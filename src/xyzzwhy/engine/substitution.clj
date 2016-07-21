(ns xyzzwhy.engine.substitution
  (:require [xyzzwhy.engine
             [configuration :as cf]
             [fragment :as fr]
             [interpolation :as in]]
            [xyzzwhy.util :as util]))

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
        (update :tweet util/append tweet-text))))

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
(defmulti sub
  (fn [_ sub] (:class sub)))

(defmethod sub :gender
  [subv sub]
  (-> sub
      (assoc :config #{:no-article})
      (assoc :text (pronoun (:gender (fr/get-ref sub subv)) (:case sub)))))

(defmethod sub :default
  [_ sub]
  (fr/fragment sub))

(defn substitute
  [subv]
  (loop [out []
         subs (apply list subv)]
    (if (empty? subs)
      out
      (recur (conj out (sub out (peek subs)))
             (next subs)))))

;; -------
;; Yon Follow-Uppery
;; -------
(defmulti follow-up*
  (fn [_ t] t))

(defmethod follow-up* :sub
  [tmap _]
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
                    (let [f (update (val follow) :sub substitute)
                          t (transclude :follow-up f (cf/config tmap))
                          sval' (assoc-in sval
                                          (conj path (key follow))
                                          t)]
                      (-> tmap
                          (assoc-in [:event :sub skey] sval')
                          (update :tweet util/append (:text t))))
                    (-> tmap
                        (update :tweet util/append (:text (val follow))))))
                tmap)))
          tmap
          (rseq (vec (map-indexed vector (get-in tmap [:event :sub]))))))

(defmethod follow-up* :event
  [tmap _]
  (let [path [:event :follow-up :fragment]]
    (if-let [follow (util/pick-indexed (get-in tmap path))]
      (if (fr/sub? (val follow))
        (let [f (-> (val follow)
                    (update :sub substitute)
                    ((partial transclude :follow-up) nil))
              p (conj path (key follow))]
          (as-> tmap t
              (assoc-in t p f)
              (update t :tweet util/append (get-in t (conj p :text)))))
        (update tmap :tweet util/append (:text (val follow))))
      tmap)))

(defn follow-up
  "Returns a tmap with a follow-up possibly attached."
  [tmap]
  (if (cf/required? (get-in tmap [:event :follow-up]))
    (let [tmap' (-> tmap
                    (follow-up* :event)
                    (follow-up* :sub))]
      (if (= tmap' tmap)
        tmap
        (cf/add tmap' :no-follow-up)))
    (let [tmap' (follow-up* tmap :sub)]
      (if (= tmap' tmap)
        tmap
        (cf/add tmap' :no-follow-up)))))
