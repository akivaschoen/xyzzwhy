(ns xyzzwhy.engine
  (:require [clojure.string :as str]
            [typographer.core :as typo]
            [xyzzwhy.text :refer :all]))


;;
;; Utilities
;;
(defn- append
  "Adds text to fragment's :text and returns fragment."
  [fragment text]
  (if (< (+ (-> fragment :text count)
            (count text))
         140)
    (update fragment :text #(str %1 " " text))
    fragment))

(defn capitalize*
  "Capitalizes fragment's sentences."
  [fragment]
  (assoc fragment :text (-> (:text fragment)
                            (str/replace #"^[a-z]+" #(str/capitalize %1))
                            (str/replace #"(\.\s)([a-z]+)"
                                         #(str (second %1)
                                               (str/capitalize (nth %1 2)))))))

(defn- pluralize
  [c]
  (let [c' (name c)]
    (if (.endsWith c' "s")
      c'
      (str c' "s"))))

(defn dot-prefix
  "Prefix's fragment's :text with a period of it begins with
  an @mention."
  [fragment]
  (update fragment :text #(str/replace % #"^(@\w+)" ".$1")))

(defn- optional?
  [follow-up]
  (-> follow-up :optional? true?))

(defn- pad
  [text]
  (str text " "))

(defn- random-pick
  "Chooses a random item from coll."
  [coll]
  (nth coll (rand-int (count coll))))

(defn smarten*
  "Converts fragment's text to use typographer's quotes."
  [fragment]
  (update fragment :text #(typo/smarten %)))


;;
;; Corpus
;;
(def ^:private corpus)

(defn- defcorpus
  [classes]
  (reduce #(conj %1 @(-> %2 symbol resolve)) {} classes))

(defn- get-class
  [c]
  (-> c pluralize keyword corpus))


;;
;; Fragment Configuration
;;
(defn- article?
  [config]
  (not (contains? config :no-article)))

(defn- article
  "Returns a fragment's article."
  [fragment]
  (when-let [article (:article fragment)]
    (-> fragment :article pad)))

(defn- prep?
  [config]
  (not (contains? config :no-prep)))

(defn- prep
  "Returns a fragment's preposition, randomly chosen."
  [fragment]
  (when-let [preps (:preps fragment)]
    (-> preps random-pick pad)))

(defn- no-groups?
  [config]
  (contains? config :no-groups))


;;
;; Pronouns
;;
(defmulti ^:private gender (fn [gender _] gender))

(defmethod gender :male
  [_ c]
  (case c
    :subjective "he"
    :objective "him"
    :possessive "his"))

(defmethod gender :female
  [_ c]
  (case c
    :subjective "she"
    :objective "her"
    :possessive "hers"))

(defmethod gender :neutral
  [_ c]
  (case c
    :subjective "it"
    :objective "it"
    :possessive "its"))

(defmethod gender :group
  [_ c]
  (case c
    :subjective "they"
    :objective "them"
    :possessive "theirs"))


;;
;; Main Engine
;;
(defn- interpolate
  "Replaces all substitution markers with matching text,
  returning fragment."
  ([fragment]
   (reduce #(interpolate %1 %2) fragment (:subs fragment)))
  ([fragment sub]
   (let [sub' (val sub)
         prep' (when (prep? (:config sub'))
                 (-> sub' :source prep))
         article' (when (article? (:config sub'))
                    (-> sub' :source article))
         text (str prep' article' (-> sub' :source :text))]
     (assoc fragment :text
            (str/replace (:text fragment) (str "%" (key sub)) text)))))

(defn- choose-event
  "Returns a random event type on which a tweet is built."
  []
  (-> corpus :events random-pick))


;; Fragments
(defmulti ^:private get-fragment*
  "Given a class c, returns a random item
  from the corpus."
  (fn [c _] c))

(defmethod get-fragment* :actor
  [c config]
  (let [persons (get-class :persons)
        animals (get-class :animals)
        actors (apply merge persons animals)
        actors (if (no-groups? config)
                 (filter #(not= :group (-> % :gender)) actors)
                 actors)]
    (-> actors random-pick)))

(defmethod get-fragment* :default
  [c config]
  (->> c pluralize keyword corpus random-pick))

(defn- get-fragment
  ([c]
   (get-fragment c nil))
  ([c config]
   (get-fragment* c config)))


;; Substitutions
(defmulti ^:private get-sub
  "Returns a fragment to be used for a substitution."
  (fn [_ sub] (-> sub val :class)))

(defmethod get-sub :gender
  [fragment sub]
  (let [sub' (val sub)
        gender' (-> fragment :subs (find (key sub)) val :source :gender)]
    {(key sub) (assoc-in sub' [:source :text] (gender gender' (:case sub')))}))

(defmethod get-sub :default
  [fragment sub]
  (let [sub' (val sub)]
    {(key sub) (assoc sub' :source (get-fragment (-> sub' :class)))}))

(letfn [(subs* [fragment subs]
          (reduce #(conj %1 (get-sub fragment %2)) {} subs))]
  (defn- subs
    "Populates fragment's possible substitutions which
    appropriate fragments."
    ([fragment]
     (let [subs (subs* fragment (:subs fragment))]
       (assoc fragment :subs subs)))
    ([fragment follow-up]
     (let [subs (subs* fragment (:subs follow-up))]
       (assoc follow-up :subs subs)))))


;; Follow-Ups
(defmulti ^:private get-follow-up*
  "Appends fragment's follow up to its :text. If follow-up
  has substitutions, those are handled first."
  (fn [_ follow-up _] (contains? follow-up :subs)))

(defmethod get-follow-up* true
  [fragment follow-up index]
  (let [option (subs fragment follow-up)
        option (interpolate option)
        fragment (append fragment (:text option))]
    (assoc-in fragment [:follow-ups :options index] option)))

(defmethod get-follow-up* false
  [fragment follow-up _]
  (append fragment (:text follow-up)))

(defn- follow-ups
  [fragment]
  (if (contains? fragment :follow-ups)
    (let [options (-> fragment :follow-ups :options)
          follow-up (-> options random-pick)
          index (.indexOf options follow-up)]
      (get-follow-up* fragment follow-up index))
    fragment))

(defn- sub-follow-ups
  [fragment]
  (if (contains? fragment :subs)
    (reduce (fn [_ s]
              (when-let [follow-up (-> s val :source :follow-ups)]
                (if (and (true? (:optional? follow-up))
                         (< 50 (+ 1 (rand-int 99))))
                  fragment
                  (reduced (append fragment
                                   (-> follow-up :options random-pick :text))))))
            fragment
            (:subs fragment))))

;; Actions
(def initialize-tweet (comp get-fragment keyword pluralize choose-event))
(def process-tweet (comp sub-follow-ups follow-ups interpolate subs))
(def finalize-tweet (comp smarten* dot-prefix capitalize*))
