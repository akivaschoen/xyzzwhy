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
(defn pick-fragment
  [fragment classname]
  (let [fragments (corp/get-fragments classname)
        fragment' (update fragment :config cf/merge-into (cf/config (corp/get-config classname)))]
    (if (and (= classname :person)
             (contains? (cf/config fragment') :no-groups))
      (merge fragment' (dissoc (util/pick (vec (remove
                                                 #(= (-> % :gender) :group)
                                                 fragments))) :id))
      (merge fragment' (dissoc (util/pick fragments) :id)))))

(defmulti fragment
  "Given a classname, returns a random item from the corpus."
  (fn [fragment] (:class fragment)))

(defmethod fragment :actor
  [fragment]
  (pick-fragment fragment (if (util/chance)
                            :person
                            :animal)))

(defmethod fragment :event
  [_]
  (corp/get-event))

(defmethod fragment :default
  [fragment]
  (pick-fragment fragment (:classname fragment)))
