(ns xyzzwhy.bot
  (:require [xyzzwhy.engine :as e]
            [xyzzwhy.text]
            [xyzzwhy.twitter :as t])
  (:gen-class))

(defn -main
  "Starts the bot up with an initial tweet and then randomly waits between
  20 and 40 minutes before tweeting again."
  [& args]
  (e/assign-corpus 'xyzzwhy.text)

  (println "xyzzwhy is ready for some magical adventures!")

  (loop []
    (let [interval (+ 1200000 (rand-int 1200000))
          tweet (-> (e/get-tweet) :text)]
      (try
        (t/post-to-twitter tweet)

        ;; Logging
        (println "Tweeted:" tweet)
        (println "Next tweet in" (int (/ interval 60000)) "minutes")

        (Thread/sleep interval)

        (catch Exception e
          (println "Caught error:" e)
          (Thread/sleep 60000))))

    (recur)))