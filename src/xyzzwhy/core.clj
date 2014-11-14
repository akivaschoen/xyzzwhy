(ns xyzzwhy.core
  (:require [clojure.string :as string]
            [xyzzwhy.data :refer :all])
  (:gen-class))

; Here's our regexp pattern, searching the templates for {{whatever}}.
(def make-matcher
  (fn [target] 
    (re-matcher #"\{\{(\w+(\-\w+)*)\}\}" target)))

(defn capitalize-tweet [tweet]
  (-> tweet
    (string/replace ; first word of the tweet
      #"^[a-z]+"
      #(string/capitalize %1))
    (string/replace ; first word of sentence
      #"(\.\s)([a-z]+)"
      #(str (second %1) (string/capitalize (nth %1 2))))))

; Constructs the event to be tweeted.
(defn create-event []
  (let [event-type (get-event-type)
        event (get-event event-type)]
    ; Here we want to randomize how the events get put together.
    ; There's a 75% chance that a location event will have a secondary event.
    (if (and (= event-type "location-event")
             (< (rand-int 100) 75))
      (let [output (str event " " (get-event "secondary-event"))]
        ; If there is a secondary event, there's a 50% chance that there
        ; will be a tertiary event.
        (if (< (rand-int 100) 50)
          (let [tertiary-event (get-event "tertiary-event")]
            ; And if there is a tertiary event, there's a 50/50 chance it
            ; will append the secondary event or replace it entirely.
            (if (< (rand-int 100) 50)
              (str event " " tertiary-event)
              (str output " " tertiary-event)))
          output))
      event)))

; Populates the event's placeholders with randomized results.
(defn create-tweet [event match]
  (let [current-match (re-find match)]
    (if (nil? current-match)
      event
      (recur 
        (string/replace-first event
                              (first current-match)
                              (get-word (second current-match)))
        match))))

(defn -main
  [& args]
  (let [event (create-event)
        match (make-matcher event)]
    (-> (create-tweet event match)
        (capitalize-tweet)
        (println))))
