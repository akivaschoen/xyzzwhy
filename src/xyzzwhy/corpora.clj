(ns xyzzwhy.corpora
  (:require [xyzzwhy.io :as io]
            [xyzzwhy.util :as util]))

(defn index-of
  [idx c]
  (some #(if (= idx (:index %)) %) c))

(defn weighted-pick
  [c]
  (let [weight (reductions #(+ %1 %2) (map :weight c))
        rnd (rand-int (last weight))]
    (nth c (count (take-while #(<= % rnd) weight)))))

(defn get-config
  [classname]
  (or {:config (:config (io/read-file classname))}
      {:config #{}}))

(defn get-event
  ([]
   {:class (weighted-pick (:events (io/read-file "classes")))})
  ([index]
   {:class (index-of index (:events (io/read-file "classes")))}))

(defn get-fragments
  [classname]
  (:fragment (io/read-file classname)))

(defn get-fragment
  [classname]
  (util/pick (get-fragments classname)))
