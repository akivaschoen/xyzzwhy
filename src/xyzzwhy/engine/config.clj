(ns xyzzwhy.engine.config
  (:require [clojure.string :as str]
            [xyzzwhy.datastore :as ds]))

(defn get-config
  [c]
  (reduce (fn [acc opt]
            (conj acc (keyword opt)))
          #{}
          (:config (ds/get-metadata c))))

(declare option-complement?)
(defn merge-configs
  "Merges c2 into c1 with c1 taking precedence."
  [c1 c2]
  (reduce (fn [acc opt]
            (if (option-complement? opt c1)
              acc
              (conj acc opt)))
          c1
          c2))

(defn option-complement
  [c]
  (let [s (name c)]
    (if (str/starts-with? s "no-")
      (keyword (str/replace s #"^no-" ""))
      (keyword (str "no-" s)))))

(defn option-complement?
  [option config]
  (contains? config (option-complement option)))

