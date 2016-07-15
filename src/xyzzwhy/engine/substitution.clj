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
(defn follow-up*
  ([fragment]
   (follow-up* fragment util/pick))
  ([fragment pickfn]
   (-> fragment :follow-up :fragment pickfn)))

(defmulti follow-up
  (fn [fragment] (:type fragment)))

(defmethod follow-up :event
  [fragment]
  (if (fr/follow-up? fragment)
    (let [follow (follow-up* fragment util/pick-indexed)]
      (if (fr/sub? (val follow))
        (update-in [:follow-up :fragment (key follow)] (substitutions (val follow)))
        fragment))
    fragment))

  #_([fragment percentage]
   (reduce (fn [text fragment]
             (if (and (empty? text)
                      (fr/follow-up? fragment percentage))
               (follow-up fragment)
               (reduced text)))
           ""
           fragment))
