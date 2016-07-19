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
(defn article?
  [fragment]
  (not (has? fragment :no-article)))

(defn article
  [fragment]
  (if-let [article (:article fragment)]
    (str (-> article util/pick) " ")
    (str (a-or-an (:text fragment)) " ")))

(defn follow-up?
  [fragment]
  (contains? fragment :follow-up))

(defn no-groups?
  [fragment]
  (has? fragment :no-groups))

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

(defn sub?
  [fragment]
  (contains? fragment :sub))

;; -------
;; Yon fragment fetchery
;; -------
(defmulti fragment
  "Given a classname, returns a random item from the corpus."
  (fn [classname _] classname))

(defmethod fragment :person
  [_ config]
  (let [metadata (ds/get-metadata :person)
        persons (ds/get-class :person)]
    (let [conf (cf/merge-into (cf/config config) (cf/config metadata))]
      (if (contains? conf :no-groups)
        (dissoc (util/pick (remove #(= (-> % :gender) "group") persons)) :id)
        (dissoc (util/pick persons) :id)))))

(defmethod fragment :actor
  [_ config]
  (let [classname (if (util/chance)
                    :person
                    :animal)
        metadata (ds/get-metadata classname)
        actors (ds/get-class classname)]
    (if (= classname :person)
      (let [conf (cf/merge-into (cf/config config) (cf/config metadata))]
        (if (contains? conf :no-groups)
          (dissoc (util/pick (remove #(= (-> % :gender) "group") actors)) :id)
          (dissoc (util/pick actors) :id)))
      (dissoc (util/pick actors) :id))))

(defmethod fragment :event
  [_ _]
  (ds/get-event))

(defmethod fragment :default
  [classname _]
  (merge (ds/get-metadata classname)
         (ds/get-fragment classname)))
