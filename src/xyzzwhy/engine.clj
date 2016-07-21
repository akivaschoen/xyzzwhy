(ns xyzzwhy.engine
  (:require [xyzzwhy.engine
             [fragment :as fr]
             [substitution :as sb]]
            [xyzzwhy.util :as util]
            [clojure.string :as str]))

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
    (update-in tmap [:event :sub] sb/substitutions)
    tmap))

(defn transclude
  [tmap]
  (sb/transclude :event tmap nil))

(defn follow-up
  [tmap]
  (sb/follow-up tmap))

(def tweet-factory (comp follow-up transclude substitutes tweet-text event-fragment event))

(defn tweet
  []
  (tweet-factory tweetmap))
