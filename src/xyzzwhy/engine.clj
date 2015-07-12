(ns xyzzwhy.engine
  (:require [clojure.string :as str]
            [xyzzwhy.text :refer :all]))

(defn random-pick
  [coll]
  (nth coll (rand-int (count coll))))

(defn pad
  [text]
  (str text " "))

(defn article
  [fragment]
  (if (contains? fragment :article)
    (pad (:article fragment))
    nil))

(defn prep?
  [config]
  (not (contains? config :no-prep)))

(defn prep
  [fragment]
  (-> (:preps fragment) random-pick pad))

(defn no-groups?
  [config]
  (contains? config :no-groups))

(defmulti gender (fn [gender _] gender))

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

(defn append
  [fragment text]
  (update fragment :text #(str %1 " " text)))

(defn defcorpus
  [classes]
  (reduce #(conj %1 @(-> %2 symbol resolve)) {} classes))

(defn get-class
  [corpus class]
  (-> class name (str "s") keyword corpus))

(defmulti get-fragment* (fn [_ class _] class))

(defmethod get-fragment* :actor
  [corpus class config]
  (let [persons (->> :person (get-class corpus))
        animals (->> :animal (get-class corpus))
        actors (apply merge persons animals)
        actors (if (no-groups? config)
                 (filter #(not= :group (-> % :gender)) actors)
                 actors)]
    (-> actors random-pick)))

(defmethod get-fragment* :default
  [corpus class config]
  (->> class (get-class corpus) random-pick))

(defn get-fragment
  ([corpus class]
   (get-fragment* corpus class nil))
  ([corpus class config]
   (get-fragment* corpus class config)))

(defmulti get-sub (fn [_ _ sub] (-> sub val :class)))

(defmethod get-sub :gender
  [_ fragment sub]
  (let [sub' (val sub)
        sex (-> fragment :subs (find (key sub)) val :source :gender)]
    {(key sub) (assoc-in sub' [:source :text] (gender sex (:case sub')))}))

(defmethod get-sub :default
  [corpus fragment sub]
  (let [sub' (val sub)]
    {(key sub) (assoc sub' :source (get-fragment corpus (-> sub' :class)))}))

(defn get-subs
  ([corpus fragment]
   (let [subs (reduce #(conj %1 (get-sub corpus fragment %2)) {} (:subs fragment))]
     (assoc fragment :subs subs)))
  ([corpus fragment follow-up]
   (let [subs (reduce #(conj %1 (get-sub corpus fragment %2)) {} (:subs follow-up))
         follow-up' (assoc follow-up :subs subs)]
     follow-up')))

(defmulti get-follow-up* (fn [_ _ follow-up _] (contains? follow-up :subs)))

(defmethod get-follow-up* false
  [corpus fragment follow-up _]
  (append fragment (:text follow-up)))

(defmethod get-follow-up* true
  [corpus fragment follow-up index]
  (let [option (get-subs corpus fragment follow-up)
        option (interpolate option)
        fragment (append fragment (:text option))]
    (assoc-in fragment [:follow-ups :options index] option)))

(defn follow-up
  [corpus fragment]
  (let [options (-> fragment :follow-ups :options)
        follow-up (-> options random-pick)
        index (.indexOf options follow-up)]
    (get-follow-up* corpus fragment follow-up index)))

(defn interpolate
  ([fragment]
   (reduce #(interpolate %1 %2) fragment (:subs fragment)))
  ([fragment sub]
  (let [sub' (val sub)
        prep' (when (prep? (:config sub'))
                (-> sub' :source :prep random-pick))
        article' (-> sub' :source article)
        text (str prep' article' (-> sub' :source :text))]
    (assoc fragment :text
           (str/replace (:text fragment) (str "%" (key sub)) text)))))

(def construct-tweet (comp interpolate follow-up))
