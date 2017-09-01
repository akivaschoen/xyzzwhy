(ns xyzzwhy.twitter
  (:require [environ.core :refer [env]]
            [twitter.api.restful :refer :all]
            [twitter.oauth :refer :all])
  (:import [twitter.callbacks.protocols SyncSingleCallback]))

(def credentials
  (make-oauth-creds
    (env :xyzzwhy-twitter-consumer-key)
    (env :xyzzwhy-twitter-consumer-secret)
    (env :xyzzwhy-twitter-user-access-token)
    (env :xyzzwhy-twitter-user-access-token-secret)))

(defn update-status
  "This, uh, posts to Twitter."
  [status-text]
  (try
    (statuses-update :oauth-creds credentials :params {:status status-text})
    (catch Throwable t
      (println "Something went wrong when tweeting:" t))))
