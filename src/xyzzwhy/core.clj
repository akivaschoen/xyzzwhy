(ns xyzzwhy.core
  (:require [clojure.string :as string]
            [xyzzwhy.data :refer :all])
  (:gen-class))

(defn pluralize [s]
  (str s "s"))

(def make-matcher
  (fn [target] (re-matcher #"\{\{(\w+)\}\}" target)))

(defn get-random-thing [coll]
  (nth coll (rand-int (count coll))))

(defn get-random-event [event-types]
  (let [event-type (get-random-thing event-types)
        event (get-random-thing 
                @(-> (pluralize event-type) 
                     symbol 
                     resolve))]
    (if (and (= event-type "location-event")
             (< (rand-int 100) 75))
      (do
        (let [output (str event " " (get-random-thing secondary-events))]
          (if (< (rand-int 100) 50)
            (if (< (rand-int 100) 50)
              (str event " " (get-random-thing tertiary-events))
              (str output " " (get-random-thing tertiary-events)))
            output)))
      event)))

(defn interpolate [event match]
  (let [current-match (re-find match)]
    (if (nil? current-match)
      event
      (recur 
        (string/replace-first event
                        (first current-match)
                        (get-random-thing 
                          @(-> (pluralize (second current-match)) 
                               symbol 
                               resolve)))
        match))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (let [event (get-random-event event-types)
        match (make-matcher event)]
    (interpolate event match)))
