(ns xyzzwhy.engine.fragment
  (:require [xyzzwhy.engine.configuration :as cf]
            [xyzzwhy.datastore :as ds]
            [xyzzwhy.util :as util :refer [any?]]))

;;
;; Utilities
;;
(defn a-or-an
  [s]
  (if (nil? (re-find #"(?i)^[aeiou]" s))
    "a"
    "an"))

(defn has-article?
  [s]
  (if (re-find #"(?i)^(?:a|an|the)\s" s)
    true
    false))


;;
;; Fragment Configuration
;;
(defn no-groups?
  [fragment]
  (cf/has? fragment :no-groups))

(defn article?
  [fragment]
  (cf/has? fragment :article))

(defn article
  [fragment]
  (if-let [article (:article fragment)]
    (str (-> article util/pick) " ")
    ""))

(defn prep?
  [fragment]
  (and (not (cf/has? fragment :no-prep))
       (contains? fragment :prep)))

(defn prep
  "Returns a fragment's preposition, randomly chosen."
  [fragment]
  (if-let [prep (:prep fragment)]
    (str (-> prep util/pick) " ")
    ""))


;;
;; Yon fragment fetchery
;;
(defn- metadata
  [classname]
  (ds/get-metadata classname))

(defmulti fragment*
  "Given a classname, returns a random item from the corpus."
  (fn [classname _] classname))

(defmethod fragment* :actor
  [classname config]
  (let [persons (ds/get-class :persons)
        animals (ds/get-class :animals)
        actors (apply merge persons animals)
        actors (if (no-groups? config)
                 (remove #(= :group (-> % :gender)) actors)
                 actors)]
    (-> actors util/pick second)))

(defmethod fragment* :person
  [classname config]
  (let [persons (if (no-groups? config)
                  (remove #(= :group (-> % :gender)) (ds/get-class :persons))
                  (ds/get-class :persons))]
    (-> persons util/pick)))

(defmethod fragment* :default
  [classname _]
  (let [fragment (ds/get-fragment classname)
        config (cf/configure classname)]
    (if (empty? config)
      fragment
      (assoc fragment :config config))))

(defn fragment
  "Returns a raw adventure fragment of event classname."
  ([classname]
   (fragment classname nil))
  ([classname config]
   (fragment* classname config)))
