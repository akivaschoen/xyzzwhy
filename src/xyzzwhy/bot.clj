(ns xyzzwhy.bot
  (:gen-class)
  (:require [clj-time.local :as local]
            [com.stuartsierra.component :as component]
            [xyzzwhy
             [engine :as e]
             [twitter :as t]]))

(defn- log-tweet
  [tweet interval]
  (println "--")
  (println tweet)
  (println "On:" (local/format-local-time (local/local-now) :rfc822))
  (println "Pausing:" (int (/ interval 60000)) "minutes")
  (println "--"))
