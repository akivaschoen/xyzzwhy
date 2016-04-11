(ns xyzzwhy.engine.interpolation
  (:require [clojure.string :as str]
            [xyzzwhy.engine.fragment :as fr]))

(defn- prep
  [s]
  (when (fr/prep? (:fragment s))
    (-> s :fragment fr/prep)))

(defn- article
  [s]
  (when (fr/article? (:fragment s))
    (-> s :fragment fr/article)))

(defn interpolate
  ([fragment]
   (reduce #(interpolate %1 %2) fragment (:sub fragment)))
  ([fragment sub]
   (let [s (val sub)
         text (str (prep s)
                   (article s)
                   (-> s :fragment :text))]
     (update fragment :text str/replace (str "%" (key sub)) text))))
