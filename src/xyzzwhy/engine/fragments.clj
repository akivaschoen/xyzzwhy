(ns xyzzwhy.engine.fragments
  (:require [xyzzwhy.engine.datastore :as data]
            [xyzzwhy.util :as util]))

;;
;; Fragment Configuration
;;
(defn article?
  [config]
  (not (contains? config :no-article)))

(defn article
  "Returns a fragment's article."
  [fragment]
  (when-let [article (:article fragment)]
    (-> fragment :article util/pad)))

(defn prep?
  [config]
  (not (contains? config :no-prep)))

(defn prep
  "Returns a fragment's preposition, randomly chosen."
  [fragment]
  (when-let [preps (:preps fragment)]
    (-> preps util/pick util/pad)))

(defn no-groups?
  [config]
  (contains? config :no-groups))


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
                 (filter #(not= :group (-> % :gender)) actors)
                 actors)]
    (-> actors util/pick)))

(defmethod get-fragment* :person
  [c config]
  (let [persons (if (no-groups? config)
                  (filter #(not= :group (-> % :gender)) (data/get-class :persons))
                  (data/get-class :persons))]
    (-> persons util/pick)))

(defmethod get-fragment* :default
  [c config]
  (->> c util/pluralize keyword util/pick) 
  #_(->> c util/pluralize keyword corpus util/pick))

(defn get-fragment
  ([c]
   (get-fragment c nil))
  ([c config]
   (get-fragment* c config)))
