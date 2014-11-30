(ns xyzzwhy-bot.data
  (:refer-clojure :exclude [count sort find])
  (:use [monger.query]
        [xyzzwhy-bot.util])
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

(defn initialize-tweet
  "Creates a new tweet object.
  
  If the type is of :event-type, caches that information for later use."
  [type]
  (let [tweet {:asset (get-random-thing [type])}]
    (if (= type :event-type)
      (as-> tweet t
            ; Store the overall event type for later reference.
            (assoc t :event-type (keyword (read-asset t)))
            ; Grab a random thing of the event type and cache it as the current asset...
            (assoc t :asset (get-random-thing (-> t
                                                  read-asset
                                                  keyword
                                                  vector))) 
            ; ... and tehn store its text in as the initial text of the tweet.
            (assoc t :text (read-asset t)))
      (assoc tweet :text (read-asset tweet)))))
