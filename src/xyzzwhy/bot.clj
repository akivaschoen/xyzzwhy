(ns xyzzwhy.bot
  (:require [clj-time.local :as local]
            [com.stuartsierra.component :as component]
            [xyzzwhy
             [engine :as en]
             [twitter :as twitter]]
            [xyzzwhy.engine.fragment :as fr])
  (:gen-class))

(defn- log-tweet
  [tweet interval]
  (println "--")
  (println (:tweet tweet))
  (println "On:" (local/format-local-time (local/local-now) :rfc822))
  (println "Pausing for" (int (/ interval 60000)) "minutes"))

(defn tweet
  []
  (let [pause-time 1200000
        interrupt (atom false)
        bot (future (while (not @interrupt)
                      (let [interval (+ pause-time (rand-int pause-time))
                            tweet (en/tweet)]
                        (twitter/update (:tweet tweet))
                        (log-tweet tweet interval)

                        (try
                          (Thread/sleep interval)
                          (catch InterruptedException e
                            (reset! interrupt true))))))]
    (println "xyzzwhy is ready for some magical adventures!")
    (println "Started at:" (local/format-local-time (local/local-now) :rfc822))
    (Thread/sleep 60000)
    bot))

(defn -main
  [& args]
  (tweet))
