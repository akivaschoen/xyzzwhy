(ns xyzzwhy.engine.interpolation
  (:require [clojure.string :as str]
            [xyzzwhy.engine.fragment :as fr]
            [xyzzwhy.util :as util]))

(defn prep
  [s]
  (when (fr/prep? s)
    (fr/prep s)))

(defn article
  [s]
  (when (fr/article? s)
    (fr/article s)))

(defn interpolate
  #_([fragment]
   (reduce #(interpolate %1 %2) fragment (:sub fragment)))
  ([fragment item]
   (let [text (str (prep item)
                   (article item)
                   (:text item))] 
     (update fragment :text str/replace (str "%" (:token item)) text))))
