(ns xyzzwhy.bot
  (:gen-class)
  (:require [clj-time.local :as local]
            [com.stuartsierra.component :as component]
            [xyzzwhy
             [engine :as en]
             [twitter :as tw]]
            [xyzzwhy.engine.fragment :as fr]))

(defn- log-tweet
  [tweet interval]
  (println "--")
  (println (:tweet tweet))
  (println "On:" (local/format-local-time (local/local-now) :rfc822))
  (println "Pausing:" (int (/ interval 60000)) "minutes")
  (println "--"))

(defn tweet
  []
  (let [constructor (comp en/event-fragment en/event en/tweetmap)]
    ;; Send (and log) tweet
    ))
