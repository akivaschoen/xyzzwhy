(ns xyzzwhy.engine
  (:require [clojure.string :as str]
            [typographer.core :as typo]
            [xyzzwhy.text :refer :all]))

(def corpus)

(defn random-pick
  [coll]
  (nth coll (rand-int (count coll))))

(defn pad
  [text]
  (str text " "))

(defn article?
  [config]
  (not (contains? config :no-article)))

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
  (if-let [preps (contains? fragment :preps)]
    (-> preps random-pick pad)))

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
  (update fragment :text #(str %1 text)))

(defn defcorpus
  [classes]
  (reduce #(conj %1 @(-> %2 symbol resolve)) {} classes))

(defn interpolate
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

(defn classname
  [c]
  (let [c' (name c)]
    (if (.endsWith c' "s")
      (keyword c')
      (keyword (str c' "s")))))

(defn get-class
  [c]
  (-> c classname corpus))

(defn choose-event
  []
  (-> corpus :events random-pick))

(defmulti get-fragment* (fn [c _] c))

(defmethod get-fragment* :actor
  [c config]
  (println c)
  (let [persons (get-class :persons)
        animals (get-class :animals)
        actors (apply merge persons animals)
        actors (if (no-groups? config)
                 (filter #(not= :group (-> % :gender)) actors)
                 actors)]
    (-> actors random-pick)))

(defmethod get-fragment* :default
  [c config]
  (println c)
  (->> c classname corpus random-pick))

(defn get-fragment
  ([c]
   (get-fragment c nil))
  ([c config]
   (get-fragment* c config)))

(defmulti get-sub (fn [_ sub] (-> sub val :class)))

(defmethod get-sub :gender
  [fragment sub]
  (let [sub' (val sub)
        gender' (-> fragment :subs (find (key sub)) val :source :gender)]
    {(key sub) (assoc-in sub' [:source :text] (gender gender' (:case sub')))}))

(defmethod get-sub :default
  [fragment sub]
  (let [sub' (val sub)]
    {(key sub) (assoc sub' :source (get-fragment (-> sub' :class)))}))

(defn get-subs
  [fragment subs]
  (reduce #(conj %1 (get-sub fragment %2)) {} subs))

(defn subs
  ([fragment]
   (let [subs (get-subs fragment (:subs fragment))]
     (assoc fragment :subs subs)))
  ([fragment follow-up]
   (let [subs (get-subs fragment (:subs follow-up))
         follow-up' (assoc follow-up :subs subs)]
     follow-up')))

(defmulti get-follow-up* (fn [_ follow-up _] (contains? follow-up :subs)))

(defmethod get-follow-up* false
  [fragment follow-up _]
  (append fragment (:text follow-up)))

(defmethod get-follow-up* true
  [fragment follow-up index]
  (let [option (get-subs fragment follow-up)
        option (interpolate option)
        fragment (append fragment (:text option))]
    (assoc-in fragment [:follow-ups :options index] option)))

(defn follow-ups
  [fragment]
  (if (contains? fragment :follow-ups)
    (let [options (-> fragment :follow-ups :options)
          follow-up (-> options random-pick)
          index (.indexOf options follow-up)]
      (get-follow-up* fragment follow-up index))
    fragment))

(defn dot-prefix
  [fragment]
  (update fragment :text #(str/replace % #"^(@\w+)" ".$1")))

(defn capitalize*
  [fragment]
  (assoc fragment :text (-> (:text fragment)
                            (str/replace #"^[a-z]+" #(str/capitalize %1))
                            (str/replace #"(\.\s)([a-z]+)"
                                            #(str (second %1)
                                                  (str/capitalize (nth %1 2)))))))

(defn smarten*
  [fragment]
  (update fragment :text #(typo/smarten %)))

(def initialize-tweet (comp get-fragment classname choose-event))
(def process-tweet (comp interpolate follow-ups subs))
(def finalize-tweet (comp smarten* dot-prefix capitalize*))
