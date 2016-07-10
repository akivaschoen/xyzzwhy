(ns xyzzwhy.engine
  (:require [xyzzwhy.engine
             [fragment :as fr]
             [interpolation :refer :all]
             [substitution :as sb]]
            [xyzzwhy.datastore :as ds]
            [xyzzwhy.util :as util]))

(defn prepend
  "Adds text to the front of another string."
  [text target]
  (str (util/pad text) target))

(defn event
  "Returns a random event type on which a tweet is built."
  []
  (:name (ds/get-event)))
