(ns xyzzwhy.util
  (:require [clojure.string :as string]
            [typographer.core :as typo]))

(def any? (complement not-any?))

(defn pad
  [text]
  (str text " "))

(defn append
  "Adds text to fragment's :text and returns fragment."
  [fragment text]
  (if (< (+ (-> fragment :text count)
            (count text))
         139)
    (update fragment :text #(str (pad %1) text))
    fragment))

(defn capitalize
  "Capitalizes fragment's sentences."
  [fragment]
  (assoc fragment :text (-> (:text fragment)
                            (string/replace #"^[a-z]+" #(string/capitalize %1))
                            (string/replace #"(\.\s)([a-z]+)"
                                         #(str (second %1)
                                               (string/capitalize (nth %1 2)))))))

(defn chance
  "Returns true if a randomly chosen percentile is less
  than or equal toc."
  [c]
  (if (<= (+ 1 (rand-int 100)) c)
    true
    false))

(defn dot-prefix
  "Prefix's fragment's :text with a period if it begins with
  an @mention."
  [fragment]
  (update fragment :text #(string/replace % #"^(@\w+)" ".$1")))

(defn randomize
  "Produces a random number within the bounds of a given collection."
  [collection]
  (rand-int (count collection)))

(defn format-text
  "Applies a preposition and/or an article to a given thing."
  [thing]
  (let [text    (:text thing)
        article (:article thing)
        preps   (:preps thing)
        config  (:config thing)]
    (cond->> text
      (and (not-empty article)
           (not-any? #(= :no-article %) config))  (str article " ")
      (and (not-empty preps)
           (not-any? #(= :no-prep %) config))     (str (nth preps (randomize preps)) " "))))

(defn pluralize
  [c]
  (let [c' (name c)]
    (cond
      (.endsWith c' "s") c'
      (.endsWith c' "y") (-> c' (subs 0 (-> c' count dec)) (str "ies"))
      :else
      (str c' "s"))))

(defn read-asset
  "Eases the syntax required to read information from the current asset."
  ([segment]    (get-in segment [:asset :text]))
  ([segment k]  (get-in segment [:asset (keyword k)])))

(defn optional?
  "Returns true if a follow-up is optional."
  [follow-up]
  (-> follow-up :optional? true?))

(defn smarten
  "Converts fragment's text to use typographer's quotes."
  [fragment]
  (update fragment :text #(typo/smarten %)))

(defn pick
  [c]
  (loop [sel c]
    (if (vector? sel)
      (recur (nth sel (rand-int (count sel))))
      sel)))
