(ns xyzzwhy.data
  (:refer-clojure :exclude [count sort find])
  (:use [monger.query]
        [xyzzwhy.util])
  (:require [clojure.string :as string]
            [environ.core :refer [env]]
            [monger.core :refer [connect-via-uri]]
            [monger.collection :refer [count]])
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

(defn- get-random-thing
  "Chooses one random item from a class."
  [classes]
  (as-> (get-classes classes) c
        (shuffle c)
        (nth c (randomize c))))

(defn get-thing 
  "Retrieves a random thing from the database. This is called during the interpolation phase."
  [placeholder]
  (let [class (:class placeholder)]
  (condp = class
    :actor (get-random-thing [:person :animal])
    :item (get-random-thing [:item :food :book :garment :drink])
    (get-random-thing [class]))))

(defn create-segment
  "Creates a new segment object.
  
  If the type is of :event-type, caches that information for later use."
  [type]
  (let [segment {:asset (get-random-thing [type])}]
    (if (= type :event-type)
      (as-> segment st
            ; Store the overall event type for later reference.
            (assoc s :event-type (keyword (read-asset s)))
            ; Grab a random thing of the event type and cache it as the current asset...
            (assoc s :asset (get-random-thing (-> s
                                                  read-asset
                                                  keyword
                                                  vector))) 
            ; ... and then store its text as the initial text of the segment.
            (assoc s :text (read-asset s)))
      (assoc segment :text (read-asset segment)))))
