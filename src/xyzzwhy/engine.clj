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
  [tweetmap]
  (assoc tweetmap :event (fr/fragment :event)))

(defn event-fragment
  [tweetmap]
  (let [fragment (fr/fragment (get-in tweetmap [:event :name]))]
    (update tweetmap :event merge fragment)))

(defn tweet-text
  [tweetmap]
  (assoc tweetmap :tweet (get-in tweetmap [:event :text])))

(defn substitutes
  [tweetmap]
  (update-in tweetmap [:event :sub] #(mapv sb/substitute %)))

(defn transclude
  [tweetmap]
  (let [tweet-text (reduce (fn [text sub]
                             (in/interpolate text sub))
                           (:tweet tweetmap)
                           (get-in tweetmap [:event :sub]))]
    (assoc tweetmap :tweet tweet-text)))

(defn follow-up
  [tweetmap]
  (let [text (sb/find-follow-up (get-in tweetmap [:event :sub]))
        text (if (empty? text)
               (sb/find-follow-up (vector (:event tweetmap)))
               text)]
    (if (empty? text)
      tweetmap
      (-> tweetmap
          (update :tweet str " " text)
          (update-in [:event :config] conj :no-follow-up)))))
