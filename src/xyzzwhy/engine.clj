(ns xyzzwhy.engine
  (:require [xyzzwhy.engine
             [fragment :as fr]
             [interpolation :as in]
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
  (assoc tweetmap :event (fr/fragment :event))))

(defn event-fragment
  [tweetmap]
  (let [fragment (fr/fragment (get-in tweetmap [:event :class]))]
    (update tweetmap :event merge fragment)))

(defn tweet-text
  [tweetmap]
  (assoc tweetmap :tweet (util/pick (get-in tweetmap [:event :text]))))

(defn substitutes
  [tweetmap]
  (if (fr/sub? (:event tweetmap))
    (update-in tweetmap [:event :sub] #(mapv sb/substitute %))
    tweetmap))

(defn transclude
  [tweetmap]
  (let [tweet-text (reduce (fn [text sub]
                             (in/interpolate text sub))
                           (:tweet tweetmap)
                           (get-in tweetmap [:event :sub]))]
    (assoc tweetmap :tweet tweet-text)))

(defn follow-up
  [tweetmap]
  (sb/follow-up :event tweetmap))

(def tweet-factory (comp follow-up transclude substitutes tweet-text event-fragment event))

(defn tweet
  []
  (tweet-factory tweetmap))
