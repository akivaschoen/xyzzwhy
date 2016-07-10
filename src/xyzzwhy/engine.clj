(ns xyzzwhy.engine
  (:require [xyzzwhy.engine
             [fragment :as fr]
             [interpolation :refer :all]
             [substitution :as sb]]
            [xyzzwhy.util :as util]))

(def tweetmap
  {:tweet nil
   :event nil})

(defn event
  [tweetmap]
  (assoc tweetmap :event (fr/fragment :event)))

(defn event-fragment
  [tweetmap]
  (let [fragment (fr/fragment (get-in tweetmap [:event :name]))]
    (update tweetmap :event merge fragment)))
