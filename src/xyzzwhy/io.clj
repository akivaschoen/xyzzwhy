(ns xyzzwhy.io
  (:require [clojure.string :as str]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [pluralex.core :as pl])
  (:import java.io.PushbackReader))

(defonce corpora-dir "corpora/")

(defn filename
  "Converts a keyworded class name into an acceptable filename."
  [classname]
  (-> classname name (str/replace "-" "_") pl/pluralize))

(defn edn-prefix
  "If string s does not end with '.edn', suffix s with '.edn'."
  [s]
  (if (str/ends-with? s ".edn")
    s
    (str s ".edn")))

(defn read-file
  "Reads a class file from the corpora."
  [file]
  (let [path (str corpora-dir (-> file filename edn-prefix))]
    (try
      (with-open [file (-> path io/resource io/reader PushbackReader.)]
        (edn/read file))
      (catch Exception e
        (println (str "OH GOD. Failed to load '" path "': " (.getLocalizedMessage e)))))))
