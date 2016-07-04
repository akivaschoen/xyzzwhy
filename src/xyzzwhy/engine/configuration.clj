(ns xyzzwhy.engine.configuration
  (:require [clojure
             [string :as str]
             [set :as sets]]
            [xyzzwhy.datastore :as ds]))

(defn configure
  [classname]
  (reduce (fn [acc option]
            (conj acc (keyword option)))
          #{}
          (:config (ds/get-metadata classname))))

(declare option-complement? option-complement)

(defn combine
  "Merges c2 into c1 with c2 taking precedence."
  [c1 c2]
  (let [c (sets/union c1 c2)]
    (reduce (fn [acc option]
              (let [option' (option-complement option)]
                (if (and (str/starts-with? (name option) "no-")
                         (contains? c option'))
                  (disj acc option')
                  acc)))
              c
              c)))

(defn option-complement
  [classname]
  (let [c (name classname)]
    (if (str/starts-with? c "no-")
      (keyword (str/replace c #"^no-" ""))
      (keyword (str "no-" c)))))

(defn option-complement?
  [option config]
  (contains? config (option-complement option)))

(defn config?
  [fragment]
  (contains? fragment :config))

(defn has?
  [fragment option]
  (and (config? fragment)
       (contains? (:config fragment) option)))
