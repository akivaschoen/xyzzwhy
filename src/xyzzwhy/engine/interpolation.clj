(ns xyzzwhy.engine.interpolation
  (:require [clojure.string :as str]
            [xyzzwhy.engine.fragment :as fr]
            [xyzzwhy.util :as util]))

(defn- prep
  [s]
  (when (fr/prep? s)
    (fr/prep s)))

(defn- article
  [s]
  (when (fr/article? s)
    (fr/article s)))

(defn interpolate
  ([fragment]
   (reduce #(interpolate %1 %2) fragment (:sub fragment)))
  ([fragment sub]
   (let [s (val sub)
         text (str (prep s)
                   (article s)
                   (:text s))] 
     (update fragment :text str/replace (str "%" (key sub)) text))))
