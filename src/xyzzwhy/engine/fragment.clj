(ns xyzzwhy.engine.fragment
  (:require [xyzzwhy.datastore :as data]
            [xyzzwhy.util :as util :refer [any? pad]]))


;;
;; Utilities
;;
(defn a-or-an
  [s]
  (if (nil? (re-find #"^[aeiou]" s))
    "a"
    "an"))

(defn has-article?
  [s]
  (if (re-find #"^(?:a|an|the)\s" s)
    true
    false))


;;
;; Fragment Configuration
;;
(defn no-groups?
  [config]
  (any? config :no-groups))

(defn article?
  [fragment]
  (not (contains? (:config fragment) :no-article)))

(defn article
  "Returns a fragment's article if specified or 'a' or 'an' as appropriate."
  [fragment]
  (let [text (-> fragment :text)]
    (if (has-article? text)
      (:text fragment)
      (if (contains? fragment :article)
        (-> (:fragment article) util/pick)
        (str (a-or-an (-> fragment :text)) " ")))))

(defn prep?
  [config]
  (not (contains? config :no-prep)))

(defn prep
  "Returns a fragment's preposition, randomly chosen."
  [fragment]
  (when-let [prep (:prep fragment)]
    (str (-> prep util/pick) " ")))


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
  (data/get-fragment c))

(defn get-fragment
  ([classname]
   (get-fragment classname nil))
  ([classname config]
   (get-fragment* classname config)))
