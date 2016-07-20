(ns xyzzwhy.corpora
  (:require [clojure.string :as str]
            [xyzzwhy.io :as io]
            [xyzzwhy.util :as util]))

(defn weighted-pick
  [c]
  (let [weight (reductions #(+ %1 %2) (vals c))
        rnd (rand-int (last weight))]
    (nth (keys c) (count (take-while #(<= % rnd) weight)))))

(defn get-event
  []
  (weighted-pick (:events (io/read-file "classes"))))

(defn get-fragment
  [classname]
  (util/pick (:fragment (io/read-file classname))))
