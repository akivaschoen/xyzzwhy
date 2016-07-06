(ns xyzzwhy.engine.substitution
  (:require [clojure.string :as str]
            [xyzzwhy.engine
             [configuration :as cf]
             [fragment :as fr]
             [interpolation :refer :all]]
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


;;
;; Yon Follow-Uppery
;;
(declare sub-with transclude)

(defn follow-up?
  [fragment]
  (letfn [(pred [f] (and (contains? f :follow-up)
                         #_(not (cf/has? fragment :no-follow-up))
                         #_(util/chance 25)))]
    (if (= (type fragment) clojure.lang.MapEntry)
      (pred (val fragment))
      (pred fragment))))

(defn follow-up
  [fragment]
  (-> fragment :follow-up :fragment util/pick :text))


;;
;; Yon Substitutionarium
;;
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

(defn transclude
  [fragment sub]
  ;; (assoc fragment :sub (mapv #(sb/sub-with fragment %) (:sub fragment)))
  (let [sub' (sub-with fragment sub)
        follow (for [f (:sub fragment)]
                 (when (follow-up? f)
                   (follow-up f)))]
    (update fragment :text util/append follow)))

(defn substitute
  "Populates fragment's substitutions with appropriate fragments."
  [fragment]
  (cond-> fragment
    sub? transclude
    true interpolate))
