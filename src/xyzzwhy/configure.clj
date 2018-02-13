(ns xyzzwhy.configure
  (:require [xyzzwhy.util :as util]))

(defn required? [config]
  (contains? config :required))
