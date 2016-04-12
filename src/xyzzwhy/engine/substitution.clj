(ns xyzzwhy.engine.substitution
  (:require [xyzzwhy.engine
             [configuration :as cf]
             [follow-up :as fu]
             [fragment :as fr]]
            [clojure.string :as str]
            [xyzzwhy.engine.follow-up :as fu]
            [xyzzwhy.util :as util]))

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

(defn sub
  [fragment ref]
  (get (:sub fragment) ref))

(defmulti sub-with
  "Returns a fragment to be used for a substitution."
  (fn [item _] (-> item val :class)))

(defmethod sub-with :gender
  [item fragment]
  (let [gender' (-> (:sub fragment)
                    (find (:ref item))
                    val
                    :fragment
                    :gender)]
    {(key item) (assoc-in item [:fragment :text] (gender gender' (:case item)))}))

(defmethod sub-with :default
  [item fragment]
  (let [itemval (val item)
        newitem (fr/fragment (:class itemval))
        newitem (update newitem :config cf/combine (:config itemval))]
    {(key sub) (merge itemval newitem)}))

(defn transclude
  [fragment]
  (reduce (fn [acc item]
            (conj acc (sub-with fragment item)))
          {}
          (:sub fragment)))

(defn substitute
  "Populates fragment's substitutions with appropriate fragments."
  [fragment]
  (cond-> fragment
    (sub? fragment) (assoc :sub (transclude fragment))
    (fu/follow-up? fragment) fu/follow-up
    ))
