(ns xyzzwhy-bot.util
  (:require [clojure.string :as string]))

(defn randomize
  "Produces a random number within the bounds of a given collection."
  [collection]
  (rand-int (count collection)))

(defn format-text 
  "Applies an article to a word if it has one. For example, 'falafel' becomes 'a falafel' while
  'rice' becomes 'some rice'. Each word specifies its preferred articles."
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
  ([tweet]    (get-in tweet [:asset :text]))
  ([tweet k]  (get-in tweet [:asset (keyword k)])))
