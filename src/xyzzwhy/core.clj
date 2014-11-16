(ns xyzzwhy.core
  (:use [xyzzwhy.twitter]
        [xyzzwhy.data])
  (:require [clojure.string :as string]
  (:gen-class)))

(defn capitalize [tweet]
  (-> tweet
    (string/replace 
      #"^[a-z]+"
      #(string/capitalize %1))
    (string/replace
      #"(\.\s)([a-z]+)"
      #(str (second %1) (string/capitalize (nth %1 2))))))

(defn interpolate-text [text]
  (let [matcher (re-matcher #"\{\{(\w+(\-\w+)*)\}\}" text)]
    (loop [text text match (re-find matcher)]
      (if-not match
        text
        (recur 
          (string/replace-first text
                                (first match)
                                (get-word (second match)))
          (re-find matcher))))))

; Constructs the event to be tweeted.
(defn create-tweet []
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
  [& args]
  (-> (create-tweet)
      capitalize
      post-to-twitter))
