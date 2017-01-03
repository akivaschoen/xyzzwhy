(ns xyzzwhy.engine
  (:require [xyzzwhy.engine
             [fragment :as fr]
             [substitution :as sb]]
            [xyzzwhy.util :as util]))

(def tweetmap
  {:tweet nil
   :event nil})

(defn event
  ([]
   (event tweetmap))
  ([tmap]
   (update tmap :event fr/fragment :event)))

(defn event-fragment
  [tmap]
  (update tmap :event fr/fragment))

(defn tweet-text
  [tmap]
  (assoc tmap :tweet (util/pick (get-in tmap [:event :text]))))

(defn substitutes
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
                         substitutes
                         tweet-text
                         event-fragment
                         event))

(defn tweet
  []
  (tweet-factory tweetmap))
