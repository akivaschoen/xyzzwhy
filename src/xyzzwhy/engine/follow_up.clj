(ns xyzzwhy.engine.follow-up
  (:require [xyzzwhy.engine
             [config :as cf]
             [interpolation :refer :all]
             [substitution :as sb]]
            [xyzzwhy.util :as util]))

(defn follow-up?
  [fragment]
  (and (contains? fragment :follow-up)
       (not (cf/check-config fragment :no-follow-up))
       (util/chance 25)))

(defn follow-up
  [fragment]
  (-> fragment :follow-up :fragment util/pick :text))
