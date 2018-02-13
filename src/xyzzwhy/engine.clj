(ns xyzzwhy.engine
  (:require [xyzzwhy.io :as io]
            [xyzzwhy.util :as util]))

(def tweetmap
  {:tweet ""
   :group nil
   :config #{[:gender :neutral]}
   :fragments []})

(defn initialize []
  (assoc tweetmap :group (-> (io/read-file :group)
                             :events
                             util/weighted-nth
                             :group)))
