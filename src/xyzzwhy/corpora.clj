(ns xyzzwhy.corpora
  (:require [xyzzwhy.io :as io]
            [xyzzwhy.util :as util]))

(defn weighted-pick
  "Returns a group by weighted randomization."
  [group]
  (let [weight (reductions #(+ %1 %2) (map :weight group))
        rnd (rand-int (last weight))]
    (nth group (count (take-while #(<= % rnd) weight)))))

(defn get-config
  "Returns the :config set from a group."
  [group]
  (or {:config (:config (io/read-file group))}
      {:config #{}}))

(defn get-event
  "Returns an event either based on weighted randomization or, if
  passed a recipe index, by that index."
  ([]
   {:kind (:kind (weighted-pick (:events (io/read-file "groups"))))})
  ([index]
   {:kind (:kind (util/pick index (:events (io/read-file "groups"))))}))

(defn fragment
  [fragment]
  (let [group (-> fragment :group io/read-file :fragment)]
    (util/pick group)))

(defn get-fragment
  "Returns a fragment from a group either randomly or by a given
  recipe index."
  ([group]
   (get-fragment group -1))
  ([group index]
   (let [groupf (:fragment (io/read-file group))]
     (if (neg? index)
       (util/pick groupf)
       (util/pick index groupf)))))

