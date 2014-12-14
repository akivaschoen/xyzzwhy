(ns xyzzwhy.factory
  (:refer-clojure :exclude [sort find])
  (:use [monger.query]
        [xyzzwhy.data]
        [xyzzwhy.state]
        [xyzzwhy.util])
  (:require [clojure.string :as string]
            [environ.core :refer [env]]
            [monger.core :refer [connect-via-uri]])
            ;[monger.collection :refer [count]])
  (:import (java.util ArrayList Collections)))

(defn- encode-classname 
  "Takes a placeholder's keyword classname and makes it compatible with MongoDB's 
  collection naming scheme."
  [classname]
  (-> classname 
      name
      (string/replace #"-" "_")
      (str "s")))

(defn- get-classes 
  "Returns a merged sequence of classes from the database."
  [classes]
  (let [db (:db (connect-via-uri (env :database-uri)))]
    (letfn [(get-class [c] 
              (with-collection db (encode-classname c)
                (find {})))]
      (loop [classes classes result {}]
        (if (empty? classes)
          result
          (recur (rest classes)
                 (concat result (get-class (first classes)))))))))

(defn- get-next-thing
  "Chooses the next thing from a shuffled class or classes."
  [classes]
  (let [class (if (> (count classes) 1)
                [(nth classes (randomize classes))]
                classes)
        idx (:nth (get-class-state (first class)))
        thing (nth (get-classes class) idx)]
    (update-state class)
    (check-class-threshold class)
    thing))

(defn- get-random-thing
  "Chooses the next thing from a shuffled class or classes."
  [classes]
  (let [class (if (> (count classes) 1)
                [(nth classes (randomize classes))]
                classes)]
    (-> (get-classes class)
        shuffle
        first)))

(defn get-thing 
  "Retrieves a random thing from the database. This is called during the interpolation phase."
  [placeholder]
  (let [class (:class placeholder)]
  (condp = class
    :actor (get-next-thing [:person :animal])
    :item (get-next-thing [:item :food :book :garment :drink])
    (get-next-thing [class]))))

(defn create-segment
  "Creates a new segment object.
  
  If the type is of :event, caches that information for later use."
  [class]
  (let [segment {:asset (if (= class :event)
                          (get-random-thing [class])
                          (get-next-thing [class]))}]
    (if (= class :event)
      (as-> segment s
            ; Store the overall event class for later reference.
            (assoc s :event (keyword (read-asset s)))
            ; Grab a random thing of the event class and cache it as the current asset...
            (assoc s :asset (get-next-thing (-> s read-asset
                                                  keyword
                                                  vector))) 
            ; ... and then store its text as the initial text of the segment.
            (assoc s :text (read-asset s)))
      (assoc segment :text (read-asset segment)))))
