(ns xyzzwhy.util
  (:require [clojure.string :as string]))

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

(defn read-asset
  "Eases the syntax required to read information from the current asset."
  ([segment]    (get-in segment [:asset :text]))
  ([segment k]  (get-in segment [:asset (keyword k)])))
