(ns xyzzwhy.engine.fragment
  (:require [xyzzwhy.engine.config :as cf]
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
  [config]
  (any? config :no-groups))

(defn article?
  [fragment]
  (cf/check-config fragment :article)

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
  [fragment]
  (and (cf/config? fragment)
       (or (not (contains? (:config fragment) :no-article))
           (contains? (:config fragment) :article))))

  (if (and (contains? (:config fragment) :prep)
           (contains? fragment :prep))
    true
    false))

(defn prep
  "Returns a fragment's preposition, randomly chosen."
  [fragment]
  (when-let [prep (:prep fragment)]
    (str (-> prep util/pick) " ")))


;;
;; get-fragment (I dare you)
;;
(defn- get-metadata
  [c]
  (ds/get-metadata c))

(defmulti get-fragment*
  "Given a class c, returns a random item
  from the corpus."
  (fn [c _] c))

(defmethod get-fragment* :actor
  [c config]
  (let [persons (ds/get-class :persons)
        animals (ds/get-class :animals)
        actors (apply merge persons animals)
        actors (if (no-groups? config)
                 (remove #(= :group (-> % :gender)) actors)
                 actors)]
    (-> actors util/pick)))

(defmethod get-fragment* :person
  [c config]
  (let [persons (if (no-groups? config)
                  (remove #(= :group (-> % :gender)) (ds/get-class :persons))
                  (ds/get-class :persons))]
       (-> persons util/pick)))

(defmethod get-fragment* :default
  [c _]
  (let [fragment (ds/get-fragment c)
        config (cf/get-config c)]
    (if (empty? config)
      fragment
      (assoc fragment :config config))))

(defn get-fragment
  ([classname]
   (get-fragment classname nil))
  ([classname config]
   (get-fragment* classname config)))
