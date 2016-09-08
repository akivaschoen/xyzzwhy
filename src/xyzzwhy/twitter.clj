(ns xyzzwhy.twitter
  (:require [environ.core :refer [env]]
            [twitter.api.restful :refer :all]
            [twitter.oauth :refer :all])
  (:import (twitter.callbacks.protocols SyncSingleCallback)))

(def credentials
  (make-oauth-creds
    (env :twitter-consumer-key)
    (env :twitter-consumer-secret)
    (env :twitter-user-access-token)
    (env :twitter-user-access-token-secret)))

(defn update
  "This, uh, posts to Twitter."
  [status-text]
  (try
    (statuses-update :oauth-creds credentials :params {:status status-text})
    (catch Throwable t
      (println "Something went wrong when tweeting:" t))))
