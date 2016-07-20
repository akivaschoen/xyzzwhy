(ns xyzzwhy.engine.substitution
  (:require [clojure.string :as str]
            [xyzzwhy.engine
             [configuration :as cf]
             [fragment :as fr]
             [interpolation :as in]]
            [xyzzwhy.util :as util]
            [xyzzwhy.engine.substitution :as sb]))

;; -------
;; Pronouns
;; -------
(defn pronoun
  "Returns a pronoun string for gender g and case c."
  [g c]
  (condp = g
    :male (case c
            :subjective "he"
            :objective "him"
            :possessive "his"
            :compound "himself")
    :female (case c
              :subjective "she"
              :objective "her"
              :possessive "hers"
              :compound "herself")
    :group (case c
             :subjective "they"
             :objective "them"
             :possessive "theirs"
             :compound "themselves")
    (case c
      :subjective "it"
      :objective "it"
      :possessive "its"
      :compound "itself")))

;; -------
;; Yon Transclusery
;; -------
(defmulti transclude
  (fn [t _ _] t))

(defmethod transclude :event
  [_ tmap _]
  (let [tweet-text (reduce (fn [text sub]
                             (let [config (cf/merge-into (cf/config tmap)
                                                         (cf/config sub))]
                               (in/interpolate config text sub)))
                           (:tweet tmap)
                           (get-in tmap [:event :sub]))]
    (assoc tmap :tweet tweet-text)))

(defmethod transclude :sub
  [_ tmap index]
  (let [path [:event :follow-up :fragment index]
        tweet-text (reduce (fn [text sub]
                             (let [config (cf/merge-into (cf/config tmap)
                                                         (cf/config sub))]
                               (in/interpolate config text sub)))
                           (get-in tmap (conj path :text))
                           (get-in tmap (conj path :sub)))]
    (-> tmap
        (assoc-in (conj path :text) tweet-text)
        (update :tweet str tweet-text))))

(defmethod transclude :follow-up
  [_ fragment conf]
  (let [text (reduce (fn [text sub]
                       (let [config (cf/merge-into (cf/config conf)
                                                   (cf/config sub))]
                         (in/interpolate config text sub)))
                     (:text fragment)
                     (:sub fragment))]
    (assoc fragment :text text)))

;; -------
;; Yon Substitutionarium
;; -------
#_(defmethod sub-with :gender
    [fragment sub]
    (let [sex (-> (:sub fragment)
                  (find (:ref sub))
                  val
                  :fragment
                  :gender)]
      {(key sub) (assoc-in sub [:fragment :text] (gender sex (:case sub)))}))

(defmulti substitute
  (fn [sub] (:class sub)))

(defmethod substitute :default
  [sub]
  (fr/fragment sub))

(defn substitutions
  [subv]
  (mapv substitute subv))

;; -------
;; Yon Follow-Uppery
;; -------
(defmulti follow-up*
  (fn [t _] t))

(defmethod follow-up* :sub
  [_ tmap]
  (reduce (fn [tmap sub]
            (let [skey (first sub)
                  sval (second sub)]
              (if (and (fr/follow-up? sval)
                       (or (and (not (cf/required? sval))
                                (util/chance))
                           (cf/required? sval)))
                (let [path [:follow-up :fragment]
                      follow (util/pick-indexed (get-in sval path))]
                  (if (fr/sub? (val follow))
                    (let [f (update (val follow) :sub substitutions)
                          t (transclude :follow-up f (cf/config tmap))
                          sval' (assoc-in sval
                                          (conj path (key follow))
                                          t)]
                      (-> tmap
                          (assoc-in [:event :sub skey] sval')
                          (update :tweet str (:text t))
                          (cf/add :no-follow-up)))
                    (-> tmap
                        (update :tweet str (:text (val follow)))
                        (cf/add :no-follow-up))))
                tmap)))
          tmap
          (rseq (vec (map-indexed vector (get-in tmap [:event :sub]))))))

(defmethod follow-up* :event
  [_ tmap]
  (let [path [:event :follow-up :fragment]]
    (if-let [follow (util/pick-indexed (get-in tmap path))]
      (if (fr/sub? (val follow))
        (let [f (-> (val follow)
                    (update :sub substitutions)
                    ((partial transclude :follow-up) nil))
              p (conj path (key follow))]
          (as-> tmap tmap'
            (assoc-in tmap' p f)
            (update tmap' :tweet str (get-in tmap' (conj p :text)))
            (cf/add tmap' :no-follow-up)))
        (-> tmap
            (update :tweet str (:text (val follow)))
            (cf/add :no-follow-up)))
      tmap)))

(defn follow-up
  "Returns a tmap with a follow-up possibly attached."
  [tmap]
  (if (and (cf/follow-up? tmap)
           (cf/required? (get-in tmap [:event :follow-up :config])))
    (follow-up* :event tmap)
    (let [tmap (follow-up* :sub tmap)]
      (if (cf/follow-up? tmap)
        (follow-up* :event tmap)
        tmap))))
