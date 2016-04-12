(ns xyzzwhy.engine.substitution
  (:require [xyzzwhy.engine
             [configuration :as cf]
             [fragment :as fr]]
            [clojure.string :as str]
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


;;
;; Yon Follow-Uppery
;;
(declare sub transclude)

(defn follow-up?
  [fragment]
  (and (contains? fragment :follow-up)
       (not (cf/has? fragment :no-follow-up))
       #_(util/chance 25)))

(defn follow-up
  [fragment]
  (let [follow (-> fragment :follow-up :fragment util/pick)]
    (if (sb/sub? follow)
      (-> follow
          (assoc :sub (transclude follow)))
      (util/append fragment (:text follow)))))

(comment
  ([fragment follow-up ref]
   (if (contains? (:sub follow-up) ref)
     (sub follow-up ref)
     (sub fragment ref)))

  (defn handle-fu-sub
    [fragment]
    (let [f (fu/follow-up fragment)]
      (println f)
      (util/append fragment f))))




;;
;; Yon Substitutionarium
;;
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
    (follow-up? fragment) follow-up
    ))
