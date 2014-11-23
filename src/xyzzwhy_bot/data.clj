(ns xyzzwhy-bot.data
  (:refer-clojure :exclude [count sort find])
  (:use [monger.query]
        [xyzzwhy-bot.util])
  (:require [clojure.string :as string]
            [environ.core :refer [env]]
            [monger.core :refer [get-db connect-via-uri]]
            [monger.collection :refer [count]])
  (:import (java.util ArrayList Collections)))

(def ^:private db (:db (connect-via-uri (env :database-uri))))

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
  (letfn [(get-class [c] 
            (with-collection db (encode-classname c)
              (find {})))]
    (loop [classes classes result {}]
      (if (empty? classes)
        result
        (recur (rest classes)
               (concat result (get-class (first classes))))))))

(defn- get-random-thing
  [classes]
  (as-> (get-classes classes) c
        (shuffle c)
        (nth c (rand-int (clojure.core/count c)))))

(defn- get-room-with-preposition 
  "Some locations don't work logically well with some prepositions. For example, you don't
  often find yourself 'at a ditch' but you may find yourself 'under a dump truck'. 
  
  This is just a way to get it done. It'll be replaced with a more generic function that can
  parse a placeholder and its arguments and then act upon them."
  []
  (let [room (get-random-thing "room")
        prep (nth (:preps room) 
                  (rand-int (clojure.core/count (:preps room))))]
    (assoc room :text (str prep " " (format-text room)))))

(defn get-thing 
  "Retrieves a random word from the database. This is called during the interpolation phase."
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
      ; This is why I need better comments: this code is as clear as things which are
      ; not very clear at all in any way, form, or fashion whatsoever and then some.
      (as-> tweet t
            (assoc t :event-type (keyword (read-asset t)))
            (assoc t :asset (get-random-thing (-> t
                                                  read-asset
                                                  keyword
                                                  vector))) 
            (assoc t :text (read-asset t)))
      (assoc tweet :text (read-asset tweet)))))
