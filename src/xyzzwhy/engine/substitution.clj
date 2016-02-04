(ns xyzzwhy.engine.substitution
  (:require [xyzzwhy.engine.fragment :as fr]
            [clojure.string :as str]))

;;
;; Utilities
;;
(defn sub?
  [fragment]
  (contains? fragment :sub))


;;
;; Pronouns
;;
(defmulti ^:private gender (fn [gender _] gender))

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

(defn get-sub
  "Given a reference number, returns the appropriate substitution."
  ([fragment ref]
   (get (:sub fragment) ref))
  ([fragment follow-up ref]
   (if (contains? (:sub follow-up) ref)
     (get-sub follow-up ref)
     (get-sub fragment ref))))

(defmulti get-substitution
  "Returns a fragment to be used for a substitution."
  (fn [_ sub] (-> sub val :class)))

(defmethod get-substitution :gender
  [fragment sub]
  (let [gender' (-> (:sub fragment)
                    (find (:ref sub))
                    val
                    :fragment
                    :gender)]
    {(key sub) (assoc-in sub [:fragment :text] (gender gender' (:case sub)))}))

(defmethod get-substitution :default
  [fragment sub]
  (let [sub' (val sub)]
    {(key sub) (assoc sub' :fragment (fr/get-fragment (:class sub')))}))

(defn transclude
  [fragment]
  (reduce #(conj %1 (get-substitution fragment %2)) {} (:sub fragment)))

(defn substitute
  "Populates fragment's substitutions with appropriate fragments."
  [fragment]
  (if (sub? fragment)
    (assoc fragment :sub (reduce (fn [acc item]
                                   (conj acc (get-substitution fragment item)))
                                 {}
                                 (:sub fragment)))
    fragment))
