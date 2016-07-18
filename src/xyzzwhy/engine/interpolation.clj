(ns xyzzwhy.engine.interpolation
  (:require [clojure.string :as str]
            [xyzzwhy.engine.fragment :as fr]
            [xyzzwhy.util :as util]))

(defn prep
  [c s]
  (when (not (contains? c :no-prep))
    (fr/prep s)))

(defn article
  [c s]
  (when (not (contains? c :no-article))
    (fr/article s)))

(defn interpolate
  [config text sub]
  (let [subtext (str (prep config sub)
                     (article config sub)
                     (util/pick (:text sub)))]
    (str/replace text (str "%" (:token sub)) subtext)))
