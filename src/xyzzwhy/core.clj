(ns xyzzwhy.core
  (:require [clojure.string :as string]
            [xyzzwhy.data :refer :all])
  (:gen-class))

(defn pluralize [s]
  (str s "s"))

(def tests
  (list
    "guch"
    "puch"
    "porch"))

; This function is called to convert a {{item}} placeholder in the event
; templates with an item from a collection of the same name but pluralized.
; Ex. {{room}} = rooms collection.
(defn get-collection [coll-name] 
  (->> (pluralize coll-name) (symbol "xyzzwhy.data") resolve))

; Here's our regexp pattern, searching the templates for {{whatever}}.}
(def make-matcher
  (fn [target] 
    (re-matcher #"\{\{(\w+)\}\}" target)))

(defn get-random-thing [coll]
  (nth @coll (rand-int (count @coll))))

; Constructs the event to be tweeted.
(defn get-random-event [event-types]
  (let [event-type (get-random-thing event-types)
        event (get-random-thing (get-collection event-type))]
    ; Here we want to randomize how the events get put together.
    ; There's a 75% chance that a location event will have a secondary event.
    (if (and (= event-type "location-event")
             (< (rand-int 100) 75))
      (let [output (str event " " (get-random-thing 
                                    (get-collection "secondary-event")))]
        ; If there is a secondary event, there's a 50% chance that there
        ; will be a tertiary event.
        (if (< (rand-int 100) 50)
          (let [tertiary-event (get-random-thing 
                                 (get-collection "tertiary-event"))]
            ; And if there is a tertiary event, there's a 50/50 chance it
            ; will append the secondary event or replace it entirely.
            (if (< (rand-int 100) 50)
              (str event " " tertiary-event)
              (str output " " tertiary-event)))
          output))
      event)))

; Populates the event's placeholders with randomized results
(defn create-tweet [event match]
  (let [current-match (re-find match)]
    (if (nil? current-match)
      event
      (recur 
        (string/replace-first event
                              (first current-match)
                              (get-random-thing 
                                (get-collection (second current-match))))
        match))))

(defn -main
  [& args]
  (let [event (get-random-event (get-collection "event-type"))
        match (make-matcher event)]
    (println (create-tweet event match))))
