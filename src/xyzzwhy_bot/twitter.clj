(ns xyzzwhy-bot.twitter
  (:use 
    [twitter.oauth]
    [twitter.api.restful])
  (:require [environ.core :refer [env]])
  (:import (twitter.callbacks.protocols SyncSingleCallback)))

(def credentials
  (make-oauth-creds
    (env :twitter-consumer-key)
    (env :twitter-consumer-secret)
    (env :twitter-user-access-token)
    (env :twitter-user-access-token-secret)))

(defn post-to-twitter 
  "This, uh, posts to Twitter."
  [status-text]
  (statuses-update :oauth-creds credentials :params {:status status-text}))
