(ns xyzzwhy.data
  (:refer-clojure :exclude [count sort find])
  (:require [clojure.string :as string]
            [monger.core :refer [get-db connect]]
            [monger.collection :refer [count]]
            [monger.query :refer :all]))

(def db (get-db (connect) "xyzzwhy_test"))
  
(defn- encode-collection-name [s]
  (-> s
      (string/replace #"-" "_")
      (str "s")))

(defn- get-collection-count [coll]
  (count db coll))

(defn get-random-thing [coll]
  (let [coll (encode-collection-name coll)]
    (with-collection db coll
      (find {})
      (limit 1)
      (skip (rand-int (get-collection-count coll))))))

(defn- get-collection [coll]
  (let [coll (encode-collection-name coll)]
    (with-collection db coll
      (find {}))))

(defn- get-collections [coll]
  (loop [coll coll result {}]
    (if (empty? coll)
      result
      (recur (rest coll)
             (concat result (get-collection (first coll)))))))

(defn get-event-type []
  (->
    (get-random-thing "event-type")
    (first)
    (:type)))

(defn get-event [event-type]
  (->
    (get-random-thing event-type)
    (first)
    (:event)))

(defn get-actor []
  (let [actors (get-collections ["person" "animal"])
        actor (nth actors (rand-int (clojure.core/count actors)))]
    (if-let [article (:article actor)]
      (str article " " (:name actor))
      (:name actor))))

(defn get-room-with-preposition []
  (let [room (first (get-random-thing "room"))
        prep (nth (:preps room) 
                  (rand-int (clojure.core/count (:preps room))))]
    (str prep " " (:article room) " " (:name room))))

(defn get-word [coll]
  (cond 
    (= coll "actor") (get-actor)
    (= coll "room-with-prep") (get-room-with-preposition)
    :else 
    (let [word (first (get-random-thing coll))]
      (if-let [article (:article word)]
        (str article " " (:name word))
        (:name word)))))
