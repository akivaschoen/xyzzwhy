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
  [tweet-text sub]
  (let [text (str (prep sub)
                  (article sub)
                  (:text sub))] 
    (str/replace tweet-text (str "%" (:token sub)) text)))
