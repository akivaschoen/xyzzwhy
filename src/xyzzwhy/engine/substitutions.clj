(ns xyzzwhy.engine.substitutions
  (:require [xyzzwhy.engine.fragments :as frag]))

(defn subs?
  [fragment]
  (contains? fragment :subs))

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
   (get (:subs fragment) ref))
  ([fragment follow-up ref]
   (if (contains? (:subs follow-up) ref)
     (get-sub follow-up ref)
     (get-sub fragment ref))))

(defmulti substitute
  "Returns a fragment to be used for a substitution."
  (fn [_ sub] (-> sub val :class)))

(defmethod substitute :gender
  [fragment sub]
  (let [sub' (val sub)
        gender' (-> fragment
                    :subs
                    (find (:ref sub'))
                    val
                    :source
                    :gender)]
    {(key sub) (assoc-in sub' [:source :text] (gender gender' (:case sub')))}))

(defmethod substitute :default
  [fragment sub]
  (let [sub' (val sub)]
    {(key sub) (assoc sub' :source (frag/get-fragment (-> sub' :class)))}))

(letfn [(subs [fragment subs]
          (reduce #(conj %1 (substitute fragment %2)) {} subs))]
  (defn get-subs
    "Populates fragment's possible substitutions with
    appropriate fragments."
    ([fragment]
     (let [subs (subs fragment (:subs fragment))]
       (if (empty? subs)
         fragment
         (assoc fragment :subs subs))))
    ([fragment follow-up]
     (let [subs (subs fragment (:subs follow-up))]
       (if (empty? subs)
         follow-up
         (assoc follow-up :subs subs))))))
