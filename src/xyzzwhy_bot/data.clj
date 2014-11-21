(ns xyzzwhy-bot.data
  (:refer-clojure :exclude [count sort find])
  (:use [monger.query]
        [xyzzwhy-bot.util])
  (:require [clojure.string :as string]
            [environ.core :refer [env]]
            [monger.core :refer [get-db connect-via-uri]]
            [monger.collection :refer [count]])
  (:import (java.util ArrayList Collections)))

(defn- encode-collection-name 
  "Makes the collection name compatible with MongoDB. All placeholders are singular while
  the collections are pluralized so this is handled along with converting dashes to
  underscores."
  [s]
  (-> s
      name
      (string/replace #"-" "_")
      (str "s")))

(defn- get-collection 
  "Returns a collection from the database."
  [coll]
  (let [uri (env :mongolab-uri)
        {:keys [conn db]} (connect-via-uri uri)
        coll (encode-collection-name coll)]
    (with-collection db coll
      (find {}))))

(defn- get-collections 
  "Returns a sequence of collections from the database."
  [colls]
  (loop [coll colls result {}]
    (if (empty? coll)
      result
      (recur (rest coll)
             (concat result (get-collection (first coll)))))))

(defn- shuffle-collection
  "Since the built in (rand) function isn't nearly random enough, go ahead and shuffle the
  collection before selecting a random thing from it."
  [coll]
  (let [array (ArrayList. coll)]
    (Collections/shuffle array)
    (vec array)))

(defmulti get-random-thing :type)

(defmethod get-random-thing :multi [colls]
  (as-> (get-collections (:colls colls)) c
         (shuffle-collection c)
         (nth c (rand-int (clojure.core/count c)))))

(defmethod get-random-thing :default [coll]
  (as-> (get-collection coll) c
         (shuffle-collection c)
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
    (assoc room :text (str prep " " (:text room)))))

(defn get-thing 
  "Retrieves a random word from the database. This is called during the interpolation phase."
  [tweet coll]
  (condp = coll
    ; Handle special situations. The first two aggregate collections which qualify for the
    ; asking placeholder. The third handles the situation when a segment requires a room
    ; specify how it can be used (can you be in it? can you be near it? under it? etc.))
    "actor" (assoc tweet :asset (get-random-thing 
                                  {:type :multi :colls [:person :animal]}))
    "item" (assoc tweet :asset (get-random-thing 
                                 {:type :multi :colls [:item :food :book :garment :drink]}))
    "room-with-prep" (assoc tweet :asset (get-room-with-preposition))
    (assoc tweet :asset (get-random-thing coll))))

(defn initialize-event
  [event-type]
  (as-> {:text "" :asset (get-random-thing event-type)} t 
                         (assoc t :text (read-asset t))))

(defn initialize-tweet
  []
  (as-> {:text "" :asset (get-random-thing :event-type)} t 
                         (assoc t :event-type (keyword (read-asset t)))
                         (get-thing t (read-asset t))
                         (assoc t :text (read-asset t))))
