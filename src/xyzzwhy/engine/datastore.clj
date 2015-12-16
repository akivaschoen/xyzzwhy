(ns xyzzwhy.engine.datastore
  (:require [environ.core :refer :all]
            [monger.core :as mg]
            [monger.collection :as mc]))

(def ^:private db nil)

(defn initialize-db-connection!
  [& {:keys [uri]}]
  (alter-var-root #'db (:db (mg/connect-via-uri (or uri (env :corpora-database-uri))))))

(defn get-class
  [c]
  (mc/find-maps db c))
