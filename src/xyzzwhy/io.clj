(ns xyzzwhy.io
  (:require [clojure.string :as str]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [pluralex.core :as pl])
  (:import java.io.PushbackReader))

(defonce corpora-dir "resources/corpora/")

(defn filename
  [classname]
  (-> classname name (str/replace "-" "_") pl/pluralize))

(defn edn-prefix
  [s]
  (if (str/ends-with? s ".edn")
    s
    (str s ".edn")))

(defn read-file
  [file]
  (let [path (str corpora-dir (-> file filename edn-prefix))]
    (try
      (with-open [file (-> path io/reader PushbackReader.)]
        (edn/read file))
      (catch Exception e
        (println (str "OH GOD. Failed to load '" path "': " (.getLocalizedMessage e)))))))
