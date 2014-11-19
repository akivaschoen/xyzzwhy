(ns xyzzwhy-bot.data
  (:refer-clojure :exclude [count sort find])
  (:use [monger.query])
  (:require [clojure.string :as string]
            [environ.core :refer [env]]
            [monger.core :refer [get-db connect-via-uri]]
            [monger.collection :refer [count]])
  (:import (java.util ArrayList Collections)))

;(def db (get-db (connect) "xyzzwhy_test"))

(defn encode-collection-name 
  "Makes the collection name compatible with MongoDB. All placeholders are singular while
  the collections are pluralized so this is handled along with converting dashes to
  underscores."
  [s]
  (-> s
      (string/replace #"-" "_")
      (str "s")))

(defn- format-word 
  "Applies an article to a word if it has one. For example, 'falafel' becomes 'a falafel' while
  'rice' becomes 'some rice'. Each word specifies its preferred articles."
  [word]
  (if-let [article (:article word)]
    (str article " " (:name word))
    (:name word)))

(defn shuffle-collection
  "Since the built in (rand) function isn't nearly random enough, go ahead and shuffle the
  collection before selecting a random thing from it."
  [coll]
  (let [array (ArrayList. coll)]
    (Collections/shuffle array)
    (vec array)))

(defn get-collection 
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

(defmulti get-random-thing :type)

(defmethod get-random-thing :multi [colls]
  (let [things (-> (get-collections (:colls colls))
                   shuffle-collection)] 
    (nth things (rand-int (clojure.core/count things)))))
;    (first things)))

(defmethod get-random-thing :default [coll]
  (let [things (-> (get-collection coll)
                   shuffle-collection)]
    (nth things (rand-int (clojure.core/count things)))))
;    (first things)))

(defn- get-room-with-preposition 
  "Some locations don't work logically well with some prepositions. For example, you don't
  often find yourself 'at a ditch' but you may find yourself 'under a dump truck'. 
  
  This is just a way to get it done. It'll be replaced with a more generic function that can
  parse a placeholder and its arguments and then act upon them."
  []
  (let [room (get-random-thing "room")
        prep (nth (:preps room) 
                  (rand-int (clojure.core/count (:preps room))))]
    (str prep " " (format-word room))))

(defn get-segment 
  "Retrieves a random thing from the database."
  [segment-type] 
  (:name (get-random-thing segment-type)))

(defn get-event-type 
  "Do I need specific language for segment requests? Probably not. This will probably go
  away."
  []
  (get-segment "event-type"))

(defn get-word 
  "Retrieves a random word from the database. This is called during the interpolation phase."
  [coll]
  (condp = coll
    ; Handle special situations. The first two aggregate collections which qualify for the
    ; asking placeholder. The third handles the situation when a segment requires a room
    ; specify how it can be used (can you be in it? can you be near it? under it? etc.))
    "actor" (format-word (get-random-thing {:type :multi :colls ["person" "animal"]}))
    "item" (format-word (get-random-thing {:type :multi :colls ["item" "food" "book" "garment" "drink"]}))
    "room-with-prep" (get-room-with-preposition)
    (format-word (get-random-thing coll))))
