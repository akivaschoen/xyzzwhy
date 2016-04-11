(ns xyzzwhy.engine.follow-up
  (:require [xyzzwhy.engine
             [config :as cf]
             [interpolation :refer :all]
             [substitution :as sb]]
            [xyzzwhy.util :as util]))

(defn follow-up?
  [fragment]
  (cf/check-config fragment :follow-up))

(defn follow-up
  [fragment]
  (-> fragment :fragment :follow-up :fragment util/pick :text))
