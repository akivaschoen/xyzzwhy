(ns xyzzwhy.engine.fragment
  (:require [xyzzwhy.engine.datastore :as data]
            [xyzzwhy.util :as util :refer [any?]]))


;;
;; Fragment Configuration
;;
(defn no-groups?
  [config]
  (any? config :no-groups))


;;
;; get-fragment (I dare you)
;;
(defmulti get-fragment*
  "Given a class c, returns a random item
  from the corpus."
  (fn [c _] c))

(defmethod get-fragment* :actor
  [c config]
  (let [persons (data/get-class :persons)
        animals (data/get-class :animals)
        actors (apply merge persons animals)
        actors (if (no-groups? config)
                 (remove #(= :group (-> % :gender)) actors)
                 actors)]
    (-> actors util/pick)))

(defmethod get-fragment* :person
  [c config]
  (let [persons (if (no-groups? config)
                  (remove #(= :group (-> % :gender)) (data/get-class :persons))
                  (data/get-class :persons))]
       (-> persons util/pick)))

(defmethod get-fragment* :default
  [c _]
  (-> c util/pluralize data/get-fragment))

(defn get-fragment
  ([c]
   (get-fragment c nil))
  ([c config]
   (get-fragment* c config)))
