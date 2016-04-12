(ns xyzzwhy.engine.config
  (:require [clojure.string :as str]
            [clojure.set :as sets]
            [xyzzwhy.datastore :as ds]))

(defn get-config
  [c]
  (reduce (fn [acc opt]
            (conj acc (keyword opt)))
          #{}
          (:config (ds/get-metadata c))))

(declare option-complement?)

(defn merge-configs
  "Merges c2 into c1 with c2 taking precedence."
  [c1 c2]
  (let [c (sets/union c1 c2)]
    (reduce (fn [acc opt]
              (let [opp (option-complement opt)]
                (if (and (str/starts-with? (name opt) "no-")
                         (contains? c opp))
                  (disj acc opp)
                  acc)))
              c
              c)))

(defn option-complement
  [c]
  (let [s (name c)]
    (if (str/starts-with? s "no-")
      (keyword (str/replace s #"^no-" ""))
      (keyword (str "no-" s)))))

(defn option-complement?
  [option config]
  (contains? config (option-complement option)))

(defn config?
  [fragment]
  (contains? fragment :config))

(defn check-config
  [fragment option]
  (and (config? fragment)
       (contains? (:config fragment) option)))
