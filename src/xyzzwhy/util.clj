(ns xyzzwhy.util
  (:require [clojure.string :as string]
            [typographer.core :as typo]))

(def any? (complement not-any?))

(defn pad
  [text]
  (str text " "))

(defn append
  "Adds text to fragment's :text and returns fragment."
  [oldtext newtext]
  (if (< (+ (-> oldtext count)
            (count newtext))
         139)
    (str (pad oldtext) newtext)
    oldtext))

(defn capitalize-first
  "Capitalizes fragment's sentences."
  [fragment]
  (assoc fragment :text (-> (:text fragment)
                            (string/replace #"^[a-z]+" #(string/capitalize %1))
                            (string/replace #"(\.\s)([a-z]+)"
                                            #(str (second %1)
                                                  (string/capitalize (nth %1 2)))))))

(defn chance
  "Returns true if a randomly chosen percentile is less
  than or equal to c."
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
           (not-any? #(= :no-prep %) config))     (str (nth prep (randomize prep)) " "))))

(defn pluralize
  "A simple pluralizer able really only to handle xyzzwhy's classes."
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
  "Given a vector, randomly chooses one item; if a string,
  simply returns the string."
  [c]
  (if (vector? c)
    (nth c (randomize c))
    c))

(defn- cast-values
  "Returns a map with its values converted to keywords as necessary."
  [m]
  (reduce (fn [acc item]
            (assoc acc (first item)
                   (cond
                     (= :text (first item)) (second item)
                     (= :prep (first item)) (second item)
                     (= :article (first item)) (second item)
                     (= :config (first item)) (into #{} (map keyword (second item)))
                     (string? (second item)) (keyword (second item))
                     :else
                     (second item))))
          {}
          m))

(defn fix-sub-map
  "Returns a map with its :sub entries' keys converted from keyword to
  integer.

  (RethinkDB converts them the opposite way when storing.)"
  [fragment]
  (if (contains? fragment :sub)
    (assoc fragment :sub
           (reduce (fn [acc item]
                     (assoc acc (-> (key item)
                                    name
                                    Integer/parseInt)
                            (cast-values (val item))))
                   {}
                   (:sub fragment)))
    fragment))
