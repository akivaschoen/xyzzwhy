(ns xyzzwhy.engine
  (:require [xyzzwhy.engine
             [fragment :as fr]
             [substitution :as sb]]
            [xyzzwhy.util :as util]))

(def tweetmap
  {:tweet nil
   :event nil})

;; Step 1. Choose event type for the tweet
(defn event
  ([]
   (event tweetmap))
  ([tmap]
   (update tmap :event fr/fragment :event))
  ([tmap index]
   (update tmap :event fr/fragment :event index)))

;; Step 2. Choose the event fragment based on event type
(defn event-fragment
  [tmap]
  (update tmap :event fr/fragment))

(defn tweet-text
  [tmap]
  (assoc tmap :tweet (util/pick (get-in tmap [:event :text]))))

;; Step 3. Pick substitutions for each item in :sub
(defn substitute
  [tmap]
  (if (fr/sub? (:event tmap))
    (update-in tmap [:event :sub] sb/substitute)
    tmap))

(defn transclude
  [tmap]
  (sb/transclude tmap nil :event))

(defn follow-up
  [tmap]
  (sb/follow-up tmap))

(defn secondary-event
  [tmap]
  (sb/secondary tmap))

(defn tertiary-event
  [tmap]
  (sb/tertiary tmap))

(defn finalize
  [tmap]
  (update tmap :tweet util/finalize))

(def tweet-factory (comp finalize
                         tertiary-event
                         secondary-event
                         follow-up
                         transclude
                         tweet-text
                         substitute
                         event-fragment
                         event))

(defn tweet
  []
  (tweet-factory tweetmap))
