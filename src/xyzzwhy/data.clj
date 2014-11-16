(ns xyzzwhy.data
  (:refer-clojure :exclude [count sort find])
  (:use [monger.query])
  (:require [clojure.string :as string]
            [monger.core :refer [get-db connect]]
            [monger.collection :refer [count]]))

(def db (get-db (connect) "xyzzwhy_test"))
  
(defn- encode-collection-name 
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

(defn- get-collection 
  "Returns a collection from the database."
  [coll]
  (let [coll (encode-collection-name coll)]

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

(defmethod get-random-thing :multi 
  "Returns a random entry from an aggregation of collections. Some collections are divided by
  category such as garments and food both of which are items but cannot be treated similarly
  (although the phrase is 'eat your hat' you generally don't literally eat your hat even though
  I once watched Werner Herzog literally eat a shoe. What was I talking about?"
  [colls]
  (let [things (get-collections (:colls colls))
        thing (nth things (rand-int (clojure.core/count things)))]

    (format-word thing)))

(defmethod get-random-thing :default 
  "Returns a random entry from a single collection. Since this is what we do most often, it's
  marked as default. Calling functions don't need to do anything special."
  [coll]
  (letfn [get-collection-count [] (count db coll)]
    (let [coll (encode-collection-name coll)]

      (with-collection db coll
        (find {})
        (limit 1)
        (skip (rand-int (get-collection-count coll)))))))

(defn- get-room-with-preposition 
  "Some locations don't work logically well with some prepositions. For example, you don't
  often find yourself 'at a ditch' but you may find yourself 'under a dump truck'. 
  
  This is just a way to get it done. It'll be replaced with a more generic function that can
  parse a placeholder and its arguments and then act upon them."
  []
  (let [room (first (get-random-thing "room"))
        prep (nth (:preps room) 
                  (rand-int (clojure.core/count (:preps room))))]

    (str prep " " (:article room) " " (:name room))))

(defn get-segment 
  "Retrieves a random thing from the database."
  [segment-type] 
  (-> (get-random-thing segment-type) 
      first
      :name))

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
    "actor" (get-random-thing {:type :multi :colls ["person" "animal"]})
    "item" (get-random-thing {:type :multi :colls ["item" "food" "book" "garment" "drink"]})
    "room-with-prep" (get-room-with-preposition)
    (-> (first (get-random-thing coll))
        format-word)))
