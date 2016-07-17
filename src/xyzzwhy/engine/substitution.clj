(ns xyzzwhy.engine.substitution
  (:require [clojure.string :as str]
            [xyzzwhy.engine
             [configuration :as cf]
             [fragment :as fr]
             [interpolation :refer :all]]
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
;; Yon Substitutionarium
;; -------
(defmulti sub-with
  "Returns a fragment to be used for a substitution."
  (fn [sub] (:class sub)))

#_(defmethod sub-with :gender
    [fragment sub]
    (let [sex (-> (:sub fragment)
                  (find (:ref sub))
                  val
                  :fragment
                  :gender)]
      {(key sub) (assoc-in sub [:fragment :text] (gender sex (:case sub)))}))

(defmethod sub-with :default
  [sub]
  (merge sub (-> (fr/fragment (:class sub))
                 (update :config cf/combine (:config sub)))))

(defmulti substitute
  (fn [sub] (:class sub)))

(defmethod substitute :default
  [sub]
  (merge sub (-> (fr/fragment (:class sub))
                 (update :config cf/combine (:config sub)))))

(defn substitutions
  [subv]
  (mapv substitute subv))

;; -------
;; Yon Follow-Uppery
;; -------
(defmulti follow-up
  (fn [t _] t))

(defmethod follow-up :event
  [t tweetmap]
  (if (fr/follow-up? (:event tweetmap))
    (let [path [:event :follow-up :fragment]
          follow (util/pick-indexed (get-in tweetmap path))]
      (if (fr/sub? (val follow))
        (-> tweetmap
            (assoc-in (conj path (key follow)) (substitutions (val follow)))
            (update :tweet str (get-in tweetmap (conj path (key follow) :text))))
        (update tweetmap :tweet str (:text (val follow)))))
    tweetmap))
