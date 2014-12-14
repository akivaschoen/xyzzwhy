(ns xyzzwhy.mongo
  (:refer-clojure :exclude [remove sort find])
  (:use [monger.query]
        [xyzzwhy.data])
  (:require [clojure.pprint :refer [pprint]]
            [clojure.string :as string]
            [environ.core :refer [env]]
            [monger.core :refer [connect-via-uri disconnect]]
            [monger.collection :refer [insert-batch remove]])
  (:import (java.lang String)))

(defn- mongoize 
  "Takes a placeholder's keyword classname and makes it compatible with MongoDB's 
  collection naming scheme."
  [classname]
  (let [classname 
        (cond-> classname
          (keyword? classname) name
          (not (.endsWith (name classname) "s")) (str "s"))]
    (string/replace classname #"-" "_")))

(defn depopulate-class
  "Empty a class of its entries."
  [classname]
  (let [{:keys [conn db]} (connect-via-uri (env :database-uri))]
    (remove db (mongoize classname))
    (disconnect conn)))

(defn depopulate-classes
  "Empty a set of classes of their documents."
  []
  (doseq [c classes] 
    (depopulate-class c)))

(defn populate-class
  "Adds a class to the database."
  [classname]
  (let [{:keys [conn db]} (connect-via-uri (env :database-uri))
        coll (get-class classname)]
    (insert-batch db 
                  (mongoize classname) 
                  (shuffle coll))
    (disconnect conn)))

(defn populate-classes
  "Adds a set of classes to the database."
  []
  (doseq [c classes]
    (populate-class c)))

(defn repopulate-class 
  "Clears out the class and adds all new entries."
  [classname]
  (depopulate-class classname)
  (populate-class classname))

(defn repopulate-classes 
  "Empties the database and adds again all of the classes."
  []
  (doseq [c classes]
    (depopulate-class c)
    (populate-class c)))

(defn get-class-from-db
  [classname]
  (let [{:keys [conn db]} (connect-via-uri (env :database-uri))]
    (with-collection db (mongoize classname)
      (find {}))))

(defn read-class-from-db
  [classname]
  (let [{:keys [conn db]} (connect-via-uri (env :database-uri))]
    (pprint (with-collection db (mongoize classname)
      (find {})))))
