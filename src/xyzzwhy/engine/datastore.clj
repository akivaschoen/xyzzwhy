(ns xyzzwhy.engine.datastore
  (:require [clojure.string :as str]
            [environ.core :refer [env]]
            [rethinkdb.query :as r]))

(defonce db-name "xyzzwhy_corpora")

(defn ->table-name
  [classname]
  (-> classname
      name
      str
      (str/replace "-" "_")))

(defn get-class
  [classname]
    (with-open [conn (r/connect (env :xyzzwhy-corpora-db))]
      (-> (r/db db-name)
          (r/table (->table-name classname))
          (r/run conn))))

(defn get-fragment
  [classname]
    (with-open [conn (r/connect (env :xyzzwhy-corpora-db))]
      (-> (r/db db-name)
          (r/table (->table-name classname))
          (r/sample 1)
          (r/run conn)
          first)))
