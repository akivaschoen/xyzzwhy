(ns xyzzwhy.engine.substitution
  (:require [clojure.string :as str]
            [xyzzwhy.engine
             [configuration :as cf]
             [fragment :as fr]
             [interpolation :as in]]
            [xyzzwhy.util :as util]))

;; -------
;; Pronouns
;; -------
(defmulti gender (fn [gender _] gender))

(defmethod gender :male
  [_ c]
  (case c
    :subjective "he"
    :objective "him"
    :possessive "his"
    :compound "himself"))

(defmethod gender :female
  [_ c]
  (case c
    :subjective "she"
    :objective "her"
    :possessive "hers"
    :compound "herself"))

(defmethod gender :group
  [_ c]
  (case c
    :subjective "they"
    :objective "them"
    :possessive "theirs"
    :compound "themselves"))

(defmethod gender :default
  [_ c]
  (case c
    :subjective "it"
    :objective "it"
    :possessive "its"
    :compound "itself"))

;; -------
;; Yon Transclusery
;; -------
(defmulti transclude
  (fn [t _ _] t))

(defmethod transclude :event
  [_ tweetmap _]
  (let [tweet-text (reduce (fn [text sub]
                             (let [config (cf/merge-into (cf/config tweetmap)
                                                         (cf/config sub))]
                               (in/interpolate config text sub)))
                           (:tweet tweetmap)
                           (get-in tweetmap [:event :sub]))]
    (assoc tweetmap :tweet tweet-text)))

(defmethod transclude :sub
  [_ tweetmap index]
  (let [path [:event :follow-up :fragment index]
        tweet-text (reduce (fn [text sub]
                             (let [config (cf/merge-into (cf/config tweetmap)
                                                         (cf/config sub))]
                               (in/interpolate config text sub)))
                           (get-in tweetmap (conj path :text))
                           (get-in tweetmap (conj path :sub)))]
    (-> tweetmap
        (assoc-in (conj path :text) tweet-text)
        (update :tweet str tweet-text))))

(defmethod transclude :follow-up
  [_ fragment tmapconf]
  (let [text (reduce (fn [text sub]
                       (let [config (cf/merge-into (cf/config tmapconf)
                                                   (cf/config fragment))]
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
  [_ tweetmap]
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
                          t (transclude :follow-up f (cf/config tweetmap))
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
          tweetmap
          (rseq (vec (map-indexed vector (get-in tweetmap [:event :sub]))))))

(defmethod follow-up* :event
  [_ tweetmap]
  (let [path [:event :follow-up :fragment]]
    (if (nil? (get-in tweetmap path))
      tweetmap
      (let [follow (util/pick-indexed (get-in tweetmap path))]
        (if (fr/sub? (val follow))
          (let [f (substitutions (:sub (val follow)))
                p (conj path (key follow))]
            (-> tweetmap
                (assoc-in (conj p :sub) f)
                (update :tweet str (get-in tweetmap (conj p :text)))
                (cf/add :no-follow-up)))
          (-> tweetmap
              (update :tweet str (:text (val follow)))
              (cf/add :no-follow-up)))))))

(defn follow-up
  [tweetmap]
  (if (and (cf/follow-up? tweetmap)
           (util/any? #{"required"} (get-in tweetmap [:event :follow-up :config])))
    (follow-up* :event tweetmap)
    (let [tmap (follow-up* :sub tweetmap)]
      (if (cf/follow-up? tmap)
        (follow-up* :event tweetmap)
        tmap))))
