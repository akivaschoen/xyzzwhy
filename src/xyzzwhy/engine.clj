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

(defn get-sub
  [corpus sub]
  (let [sub' (val sub)]
    {(key sub) (assoc sub' :source (get-fragment corpus (-> sub' :class)))}))

(defn get-subs
  [corpus fragment]
  (let [subs (reduce #(conj %1 (get-sub corpus %2)) {} (:subs fragment))]
    (assoc fragment :subs subs)))

(defn interpolate
  [fragment sub]
  (let [sub' (val sub)
        prep' (when (prep? (:config sub'))
                (-> sub' :source :prep random-pick))
        article' (-> sub' :source article)
        text (str prep' article' (-> sub' :source :text))]
    (assoc fragment :text
           (str/replace (:text fragment) (str "%" (key sub)) text))))
