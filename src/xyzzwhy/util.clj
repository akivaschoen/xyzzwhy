(ns xyzzwhy.util
  (:require [clojure.string :as str]
            [typographer.core :as typo]))

(def any? (complement not-any?))

(defn append
  "Adds text to fragment's :text and returns fragment."
  [oldtext newtext]
  (if (< (+ (-> oldtext count)
            (count newtext))
         139)
    (str oldtext newtext)
    oldtext))

(defn capitalize-first
  "Capitalizes fragment's sentences."
  [fragment]
  (assoc fragment :text (-> (:text fragment)
                            (str/replace #"^[a-z]+" #(str/capitalize %1))
                            (str/replace #"(\.\s)([a-z]+)"
                                            #(str (second %1)
                                                  (str/capitalize (nth %1 2)))))))

(defn chance
  "Returns true if a randomly chosen percentile is less
  than or equal to c."
  ([]
   (chance 50))
  ([c]
   (if (<= (+ 1 (rand-int 100)) c)
     true
     false)))

(defn prefix-dot
  "Prefix's fragment's :text with a period if it begins with
  an @mention."
  [fragment]
  (update fragment :text #(str/replace % #"^(@\w+)" ".$1")))

(defn format-text
  "Applies a preposition and/or an article to a given fragment."
  [thing]
  (let [text    (:text thing)
        article (:article thing)
        prep    (:prep thing)
        config  (:config thing)]
    (cond->> text
      (and (not-empty article)
           (not-any? #(= :no-article %) config))  (str article " ")
      (and (not-empty prep)
           (not-any? #(= :no-prep %) config))     (str (rand-nth prep) " "))))

(defn optional?
  "Returns true if a follow-up is optional."
  [follow-up]
  (-> follow-up :optional? true?))

(defn smarten
  "Converts fragment's text to use typographer's quotes."
  [fragment]
  (update fragment :text #(typo/smarten %)))

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
