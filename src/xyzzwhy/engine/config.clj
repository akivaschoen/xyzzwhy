(ns xyzzwhy.engine.config
  (:require [clojure.string :as str]))

(defn option-complement
  [c]
  (let [s (name c)]
    (if (str/starts-with? s "no-")
      (keyword (str/replace s #"^no-" ""))
      (keyword (str "no-" s)))))

(defn option-complement?
  [option config]
  (contains? config (option-complement option)))

(defn merge-configs
  "Merges c1 into c2 with c2 taking precedence."
  [c1 c2]
  (reduce (fn [acc opt]
            (if (option-complement? opt c2)
              acc
              (conj acc opt)))
          c2
          c1))
