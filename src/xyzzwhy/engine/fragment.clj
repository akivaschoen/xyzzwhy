(ns xyzzwhy.engine.fragment
  (:require [xyzzwhy.engine.configuration :refer [has?] :as cf]
            [xyzzwhy.datastore :as ds]
            [xyzzwhy.util :as util :refer [any?]]))

;; -------
;; Utilities
;; -------
(defn a-or-an
  [s]
  (if (nil? (re-find #"(?i)^[aeiou]" s))
    "a"
    "an"))

(defn starts-with-article?
  [s]
  (if (re-find #"(?i)^(?:a|an|the)\s" s)
    true
    false))

;; -------
;; Fragment Configuration
;; -------
(defn no-groups?
  [fragment]
  (has? fragment :no-groups))

(defn article?
  [fragment]
  (has? fragment :article))

(defn article
  [fragment]
  (if-let [article (:article fragment)]
    (str (-> article util/pick) " ")
    ""))

(defn prep?
  [fragment]
  (and (not (has? fragment :no-prep))
       (contains? fragment :prep)))

(defn prep
  "Returns a fragment's preposition, randomly chosen."
  [fragment]
  (if-let [prep (:prep fragment)]
    (str (-> prep util/pick) " ")
    ""))

;; -------
;; Yon fragment fetchery
;; -------
(defmulti fragment
  "Given a classname, returns a random item from the corpus."
  (fn [classname] classname))

#_(defmethod fragment :actor
    [classname config]
    (let [persons (ds/get-class :persons)
          animals (ds/get-class :animals)
          actors (apply merge persons animals)
          actors (if (no-groups? config)
                   (remove #(= :group (-> % :gender)) actors)
                   actors)]
      (-> actors util/pick second)))

#_(defmethod fragment :person
    [classname]
    (let [config (:config (ds/get-metadata classname))
          persons (if (no-groups? config)
                    (remove #(= :group (-> % :gender)) (ds/get-class :persons))
                    (ds/get-class :persons))]
      (-> persons util/pick)))

(defmethod fragment :event
  [_]
  (ds/get-event))

(defmethod fragment :default
  [classname]
  (merge (ds/get-metadata classname)
         (ds/get-fragment classname)))
