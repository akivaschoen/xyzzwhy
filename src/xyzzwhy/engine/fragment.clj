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
  (fn [sub] (:class sub)))

(defmethod fragment :person
  [sub]
  (let [persons (ds/get-class :person)
        s (update sub :config cf/merge-into (cf/config (ds/get-metadata :person)))]
      (if (contains? (cf/config s) :no-groups)
        (merge s (dissoc (util/pick (vec (remove #(= (-> % :gender) "group") persons))) :id))
        (merge s (dissoc (util/pick persons) :id)))))

(defmethod fragment :actor
  [sub]
  (let [classname (if (util/chance)
                    :person
                    :animal)
        actors (ds/get-class classname)
        s (update sub :config cf/merge-into (cf/config (ds/get-metadata classname)))]
    (if (and (= classname :person)
             (contains? (cf/config s) :no-groups))
        (merge (dissoc (util/pick (vec (remove #(= (-> % :gender) "group") actors))) :id) s)
        (merge (dissoc (util/pick actors) :id) s))))

(defmethod fragment :event
  [_]
  (ds/get-event))

(defmethod fragment :default
  [sub]
  (let [classname (:class sub)
        s (update sub :config cf/merge-into (cf/config (ds/get-metadata classname)))]
    (merge (ds/get-fragment classname) s)))
