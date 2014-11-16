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

(defn- format-word [word]
  (if-let [article (:article word)]
    (str article " " (:name word))
    (:name word)))

(defn- get-collection-count [coll]
  (count db coll))

(defn- get-collection [coll]
  (let [coll (encode-collection-name coll)]
    (with-collection db coll
      (find {}))))

(defn- get-collections [colls]
  (loop [coll colls result {}]
    (if (empty? coll)
      result
      (recur (rest coll)
             (concat result (get-collection (first coll)))))))

(defmulti get-random-thing :type)

(defmethod get-random-thing :multi [colls]
  (let [things (get-collections (:colls colls))
        thing (nth things (rand-int (clojure.core/count things)))]
    (format-word thing)))

(defmethod get-random-thing :default [coll]
  (let [coll (encode-collection-name coll)]
    (with-collection db coll
      (find {})
      (limit 1)
      (skip (rand-int (get-collection-count coll))))))

(defn- get-room-with-preposition []
  (let [room (first (get-random-thing "room"))
        prep (nth (:preps room) 
                  (rand-int (clojure.core/count (:preps room))))]
    (str prep " " (:article room) " " (:name room))))

(defn get-segment [segment-type] 
  (-> (get-random-thing segment-type) 
      first
      :name))

(defn get-event-type []
  (get-segment "event-type"))

(defn get-word [coll]
  (condp = coll
    "actor" (get-random-thing {:type :multi :colls ["person" "animal"]})
    "item" (get-random-thing {:type :multi :colls ["item" "food" "book" "garment" "drink"]})
    "room-with-prep" (get-room-with-preposition)
    (-> (first (get-random-thing coll))
        format-word)))
