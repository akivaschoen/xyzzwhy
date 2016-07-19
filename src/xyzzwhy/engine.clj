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
  ([tweetmap]
   (assoc tweetmap :event (fr/fragment :event nil))))

(defn event-fragment
  [tweetmap]
  (assoc tweetmap :event (fr/fragment (:event tweetmap))))

(defn tweet-text
  [tweetmap]
  (assoc tweetmap :tweet (util/pick (get-in tweetmap [:event :text]))))

(defn substitutes
  [tweetmap]
  (if (fr/sub? (:event tweetmap))
    (update-in tweetmap [:event :sub] sb/substitutions)
    tweetmap))

(defn transclude
  [tweetmap]
  (sb/transclude :event tweetmap nil))

(defn follow-up
  [tweetmap]
  (sb/follow-up tweetmap))

(def tweet-factory (comp follow-up transclude substitutes tweet-text event-fragment event))

(defn tweet
  []
  (tweet-factory tweetmap))
