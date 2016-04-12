(ns xyzzwhy.engine.interpolation
  (:require [clojure.string :as str]
            [xyzzwhy.engine
             [fragment :as fr]
             [follow-up :as fu]]
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
                   (:text s))
         f (update fragment :text str/replace (str "%" (key sub)) text)]
     (if (fu/follow-up? s)
       (util/append f (fu/follow-up s))
       f))))
