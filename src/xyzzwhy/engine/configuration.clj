(ns xyzzwhy.engine.configuration
  (:require [clojure
             [string :as str]
             [set :as sets]]
            [xyzzwhy.datastore :as ds]))

#_(defn configure
    [classname]
    (reduce (fn [acc option]
              (conj acc (keyword option)))
            #{}
            (:config (ds/get-metadata classname))))

(defn config
  [fragment]
  (cond
    (contains? fragment :event) (get-in fragment [:event :config])
    :else
    (:config fragment)))

(declare option-complement? option-complement)

(defn merge-into
  "Merges c2 into c1 returning a merged set."
  [c1 c2]
  (letfn [(config [c]
            (if (nil? c)
              #{}
              c))]
    (let [c1 (config c1)
          c2 (config c2)]
      (reduce (fn [conf opt]
                (let [opts (:config conf)]
                  (if (or (contains? opts opt)
                          (contains? opts (option-complement opt)))
                    conf
                    (conj conf opt))))
              c1
              c2))))

(defn combine
  [c1 c2]
  (let [c (sets/union (:config c1) (:config c2))]
    (reduce (fn [acc option]
              (let [opt (option-complement option)]
                (if (and (str/starts-with? (name option) "no-"))
                  (contains? c opt))
                (disj acc opt)
                acc))
            c
            c)))

(defn option-complement
  [option]
  (let [opt (name option)]
    (keyword
     (cond
       (empty? opt) nil
       (str/starts-with? opt "no-") (str/replace opt #"^no-" "")
       :else
       (str "no-" opt)))))

(defn option-complement?
  [option config]
  (contains? config (option-complement option)))

(defn has?
  ([fragment option]
   (contains? (config fragment) option))
  ([fragment tweetmap option]
   (contains? (merge (config tweetmap) (config fragment)) option)))

(defn optional?
  [fragment]
  (has? fragment :optional))

(defn follow-up?
  ([fragment]
   (not (has? fragment :no-follow-up)))
  ([fragment tweetmap]
   (not (has? fragment tweetmap :no-follow-up))))

(defn add
  [tweetmap opt]
  (update-in tweetmap [:event :config] conj opt))
