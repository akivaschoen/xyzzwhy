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

(defn defcorpus
  [classes]
  (reduce #(conj %1 @(-> %2 symbol resolve)) {} classes))

(defn get-fragment
  [corpus class]
  (let [class' (-> class name (str "s") keyword corpus)]
    (random-pick class')))

(defn get-sub
  [corpus sub]
  (let [sub' (val sub)]
    {(key sub) (assoc sub' :source (get-text corpus (-> sub' :class)))}))

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
