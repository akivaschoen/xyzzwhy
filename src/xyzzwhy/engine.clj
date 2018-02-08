(ns xyzzwhy.engine
  (:require [xyzzwhy.io :as io]))

(def tweetmap
  {:tweet ""
   :event nil
   :config #{[:gender :neutral]}
   :fragments []})

(defn weighted-pick [group]
  "Returns a group by weighted randomization."
  (let [weight (reductions #(+ %1 %2) (map :weight group))
        rnd (rand-int (last weight))]
    (nth group (count (take-while #(<= % rnd) weight)))))

(defn add-fragments [tmap]
  "Adds a randomly chosen text fragment to :fragments"
  (let [group (-> (io/read-file :event)
                  :fragment

    ])
  (update tmap :fragments conj tmap ))
(defn initialize []
  (assoc tweetmap :event (-> (io/read-file :group)
                             :events
                             weighted-pick
                             :group)))
