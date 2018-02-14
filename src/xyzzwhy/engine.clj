 (ns xyzzwhy.engine
   (:require [xyzzwhy.configure :as conf]
             [xyzzwhy.fragment :as fr]
             [xyzzwhy.io :as io]
             [xyzzwhy.util :as util]))

(def tweetmap
  {:tweet ""
   :group nil
   :config #{[:gender :neutral]}
   :event []
   :secondary []
   :tertiary []
   :fragments []})

(defn initialize []
  "Returns a fresh tweetmap configured with a random :group."
  (assoc tweetmap :group (-> (io/read-file :group)
                             :events
                             util/weighted-nth
                             :group)))
