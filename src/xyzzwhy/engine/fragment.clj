(ns xyzzwhy.engine.fragment
  (:require [xyzzwhy.engine.configuration :refer [has?] :as cf]
            [xyzzwhy.corpora :as corp]
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
  (fn [fragment] (:class fragment)))

(defmethod fragment :person
  [fragment]
  (let [persons (corp/get-fragments :person)
        s (update fragment :config cf/merge-into (cf/config (corp/get-config :person)))]
      (if (contains? (cf/config s) :no-groups)
        (merge s (dissoc (util/pick (vec (remove #(= (-> % :gender) "group") persons))) :id))
        (merge s (dissoc (util/pick persons) :id)))))

(defmethod fragment :actor
  [fragment]
  (let [classname (if (util/chance)
                    :person
                    :animal)
        actors (corp/get-fragments classname)
        s (update fragment :config cf/merge-into (cf/config (corp/get-config classname)))]
    (if (and (= classname :person)
             (contains? (cf/config s) :no-groups))
        (merge (dissoc (util/pick (vec (remove #(= (-> % :gender) "group") actors))) :id) s)
        (merge (dissoc (util/pick actors) :id) s))))

(defmethod fragment :event
  [_]
  (corp/get-event))

(defmethod fragment :default
  [fragment]
  (let [classname (:classname fragment)
        s (update fragment :config cf/merge-into (cf/config (corp/get-config classname)))]
    (merge (corp/get-fragment classname) s)))
