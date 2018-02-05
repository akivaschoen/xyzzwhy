(ns xyzzwhy.engine.interpolation
  (:require [clojure.string :as str]
            [xyzzwhy.engine.fragment :as fr]
            [xyzzwhy.util :as util]))

(defn prep
  "If the config c does not specify :no-prep, add a preposition
  to string s."
  [c s]
  (when (not (contains? c :no-prep))
    (fr/prep s)))

(defn article
  "If the config c does not specify :no-article, prefix string s
  with an article."
  [c s]
  (when (and (not (contains? c :no-article))
             (not (contains? c :article)))
    (fr/article s)))

(defn interpolate
  "Returns a string with a token (%x) with the appropriate
  replacement text as subtext."
  [config text sub]
  (let [subtext (str (prep config sub)
                     (article config sub)
                     (util/pick (:text sub)))]
    (str/replace text (str "%" (:token sub)) subtext)))
