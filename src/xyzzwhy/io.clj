(ns xyzzwhy.io
  (:require [clojure.string :as str]
            [clojure.edn :as edn]
            [clojure.java.io :as io])
  (:import java.io.PushbackReader))

(defonce corpora-dir "resources/corpora/")

;;
;; EDN Functions
;; (Thanks to yogthos)
;;
(defn edn-prefix
  [s]
  (if (str/ends-with? s ".edn")
    s
    (str s ".edn")))

(defn read-file
  [filename]
  (let [path (str corpora-dir (edn-prefix filename))]
    (try
      (with-open [file (-> path io/reader PushbackReader.)]
        (edn/read file))
      (catch Exception e
        (println (str "OH GOD. Failed to load '" path "': " (.getLocalizedMessage e)))))))
