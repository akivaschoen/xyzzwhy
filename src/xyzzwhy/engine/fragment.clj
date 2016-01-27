(ns xyzzwhy.engine.fragment
  (:require [xyzzwhy.datastore :as data]
            [xyzzwhy.util :as util :refer [any?]]))


;;
;; Fragment Configuration
;;
(defn no-groups?
  [config]
  (any? config :no-groups))

(defn article?
  [fragment config]
  (and (not (contains? config :no-article))
       (contains? fragment :sub)))

(defn article
  "Returns a fragment's article."
  [fragment]
  (when-let [article (:article fragment)]
    (-> article util/pick)))

(defn prep?
  [config]
  (not (contains? config :no-prep)))

(defn prep
  "Returns a fragment's preposition, randomly chosen."
  [fragment]
  (when-let [prep (:prep fragment)]
    (-> prep util/pick)))


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
