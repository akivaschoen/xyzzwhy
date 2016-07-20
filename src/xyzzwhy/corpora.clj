(ns xyzzwhy.corpora
  (:require [clojure.string :as str]
            [xyzzwhy.io :as io]
            [xyzzwhy.util :as util]))

(defn weighted-pick
  [c]
  (let [weight (reductions #(+ %1 %2) (vals c))
        rnd (rand-int (last weight))]
    (nth (keys c) (count (take-while #(<= % rnd) weight)))))

(defn get-config
  [classname]
  (or {:config (:config (io/read-file classname))}
      {:config #{}}))

(defn get-event
  []
  {:class (weighted-pick (:events (io/read-file "classes")))})

(defn get-fragments
  [classname]
  (:fragment (io/read-file classname)))

(defn get-fragment
  [classname]
  (util/pick (get-fragments classname)))
