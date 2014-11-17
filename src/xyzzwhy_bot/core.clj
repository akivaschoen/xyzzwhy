(ns xyzzwhy-bot.core
  (:use [xyzzwhy-bot.twitter]
        [xyzzwhy-bot.data])
  (:require [clojure.string :as string]
  (:gen-class)))

(defn capitalize 
  "Ensures proper capitalization throughout the final tweet."
  [tweet]
  (-> tweet
    (string/replace #"^[a-z]+"        #(string/capitalize %1))
    (string/replace #"(\.\s)([a-z]+)" #(str (second %1) 
                                            (string/capitalize (nth %1 2))))))

(defn interpolate-text 
  "Searches text for {{word}} and {{word-modifier}} combinations and replaces them
  with appropriate things from the database. The 'word' represents a class of possible
  responses such as {{person}} and {{garment}}."
  [text]
  (let [matcher (re-matcher #"\{\{(\w+(\-\w+)*)\}\}" text)]
    (loop [text text match (re-find matcher)]
      (if-not match
        text
        (recur 
          (string/replace-first text
                                (first match)
                                (get-word (second match)))
          (re-find matcher))))))

(defn create-tweet 
  "Construct the tweet segment by segment."
  []
  (let [event-type (get-event-type)
        primary-segment (interpolate-text (get-segment event-type))]

    ; Here we want to randomize how the events get put together.
    ; There's a 75% chance that a location event will have a secondary event.
    (if (and (= event-type "location-event")
             (< (rand-int 100) 75))
      (let [secondary-segment (interpolate-text (get-segment "secondary-event"))]

        ; if there is a secondary event, there's a 50% chance that there
        ; will be a tertiary event.
        (if (< (rand-int 100) 50)
          (let [tertiary-segment (interpolate-text (get-segment "tertiary-event"))]

            ; And if there is a tertiary event, there's a 20% chance it
            ; will replace the secondary segment rather than append it.
            (if (< (rand-int 100) 20)
              (str primary-segment " " tertiary-segment)
              (str primary-segment " " secondary-segment " " tertiary-segment)))
          (str primary-segment " " secondary-segment)))
      primary-segment)))

(defn -main
  "Starts the bot up with an initial tweet and then randomly waits between
  5 and 30 minutes before tweeting again."
  [& args]
  ; This is all extremely ugly but is good enough for now.
  (println "xyzzwhy is ready for some magical adventures!")
  (loop []
    (let [interval (+ 300000 (rand-int 1500000))
          tweet (-> (create-tweet) capitalize)]
      (try
        (post-to-twitter tweet)
        (catch Exception e
          (Thread/sleep interval)))

      (println "Tweeted: '" tweet "'")
      (println "Next tweet in" (int (/ 1500000 60000)) "minutes")

      (Thread/sleep interval)
    (recur))))
