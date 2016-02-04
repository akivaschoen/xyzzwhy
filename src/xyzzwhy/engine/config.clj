(ns xyzzwhy.engine.config
  (:require [clojure.string :as str]))

(defn config-complement
  [c]
  (let [s (name c)]
    (if (str/starts-with? s "no-")
      (keyword (str/replace s #"^no-" ""))
      (keyword (str "no-" s)))))

(defn has-complement?
  [s c]
  (contains? v (config-complement c)))
