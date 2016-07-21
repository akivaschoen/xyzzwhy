(ns xyzzwhy.util
  (:require [clojure.string :as cstr]
            [typographer.core :as typo]))

(def any? (complement not-any?))

(defn append
  "Adds text to fragment's :text and returns fragment."
  [oldtext newtext]
  (if (< (+ (count oldtext)
            (count newtext))
         139)
    (str oldtext newtext)
    oldtext))

(defn chance
  "Returns true if a randomly chosen percentile is less
  than or equal to c."
  ([]
   (chance 50))
  ([c]
   (if (<= (+ 1 (rand-int 100)) c)
     true
     false)))

(defn smarten
  "Converts fragment's text to use typographer's quotes."
  [text]
  (typo/smarten text))

(defn pick
  "Given a vector, randomly chooses one item; if a string,
  simply returns the string."
  [c]
  (if (vector? c)
    (rand-nth c)
    c))

(defn pick-indexed
  "Given a vector, randomly chooses one item, returning a map
  of the index and the value."
  [c]
  (when (vector? c)
    (let [item (rand-nth c)]
      (first {(.indexOf c item) item}))))

(defn prefix
  "Prefix's fragment's :text with a period if it begins with
  an @mention."
  [text]
  (cstr/replace text #"^(@\w+)" ".$1"))

(defn sentence-case
  "Capitalizes fragment's sentences."
  [text]
  (-> text
      (cstr/replace #"^[a-z]+" cstr/capitalize)
      (cstr/replace #"(\.\s)([a-z]+)" #(str (second %1)
                                            (cstr/capitalize (nth %1 2))))))

(def finalize (comp smarten prefix sentence-case))
