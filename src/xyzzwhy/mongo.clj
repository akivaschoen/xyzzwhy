(ns xyzzwhy.mongo
  (:refer-clojure :exclude [remove sort find])
  (:use [monger.query]
        [xyzzwhy.data])
  (:require [clojure.pprint :refer [pprint]]
            [clojure.string :as string]
            [environ.core :refer [env]]
            [monger.core :refer [connect-via-uri]]
            [monger.collection :refer [insert-batch remove]]))

(defn encode-classname 
  [classname] 
  (string/replace classname #"-" "_"))

(defn depopulate-class
  "Empty a class of its entries."
  [classname]
  (let [db (:db (connect-via-uri (env :database-uri)))]
    (remove db (encode-classname classname))))

(defn depopulate-classes
  "Empty a set of classes of their documents."
  []
  (doseq [c classes] 
    (println "Removing" c "...")
    (depopulate-class c)))

(defn populate-class
  "Adds a class to the database."
  [classname]
  (let [db (:db (connect-via-uri (env :database-uri)))
        coll (get-class classname)]
    (insert-batch db 
                  (encode-classname classname) 
                  (shuffle coll))))

(defn populate-classes
  "Adds a set of classes to the database."
  []
  (doseq [c classes]
    (println "Populating" c "...")
    (populate-class c)))

(defn repopulate-class 
  "Clears out the class and adds all new entries."
  [classname]
  (depopulate-class classname)
  (populate-class classname))

(defn repopulate-classes 
  "Empties the database and adds again all of the classes."
  []
  (depopulate-classes)
  (populate-classes))

(defn read-class-from-db
  [classname]
  (let [db (:db (connect-via-uri (env :database-uri)))]
    (pprint (with-collection db (encode-classname classname)
      (find {})))))
