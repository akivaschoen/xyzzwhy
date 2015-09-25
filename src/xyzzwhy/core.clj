(ns xyzzwhy.core
  (:require [xyzzwhy.engine :as e]
            [xyzzwhy.twitter :as t])
  (:gen-class))

(defn -main
  "Starts the bot up with an initial tweet and then randomly waits between
  20 and 40 minutes before tweeting again."
  [& args]
  (e/assign-corpus 'xyzzwhy.text)

  (println "xyzzwhy is ready for some magical adventures!")

  (loop []
    (let [interval (+ 1200000 (rand-int 1200000)) ;; Tweet once every 20-40 minutes
          tweet (-> (e/get-tweet) :text)]
      (try
        (t/post-to-twitter tweet)

        (catch Exception e
          (println "Caught error:" (.getMessage e))))

      ;; Logging
      (println "Tweeted:" tweet)
      (println "Next tweet in" (int (/ interval 60000)) "minutes")

      (Thread/sleep interval))

    (recur)))
