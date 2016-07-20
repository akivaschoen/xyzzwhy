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
  [fr]
  (not (has? fr :no-article)))

(defn article
  [fr]
  (if-let [article (:article fr)]
    (str (-> article util/pick) " ")
    (str (a-or-an (:text fr)) " ")))

(defn follow-up?
  [fr]
  (contains? fr :follow-up))

(defn no-groups?
  [fr]
  (has? fr :no-groups))

(defn prep?
  [fr]
  (and (not (has? fr :no-prep))
       (contains? fr :prep)))

(defn prep
  "Returns a fragment's preposition, randomly chosen."
  [fr]
  (if-let [prep (:prep fr)]
    (str (-> prep util/pick) " ")
    ""))

(defn sub?
  [fr]
  (contains? fr :sub))

;; -------
;; Yon fragment fetchery
;; -------
(defn pick-fragment
  [fr cname]
  (let [fragments (corp/get-fragments cname)
        fr' (update fr :config cf/merge-into (cf/config (corp/get-config cname)))]
    (if (and (= cname :person)
             (contains? (cf/config fr') :no-groups))
      (merge fr' (dissoc (util/pick (vec (remove
                                                #(= (-> % :gender) :group)
                                                fragments))) :id))
      (merge fr' (dissoc (util/pick fragments) :id)))))

(defn fragment
  ([fr]
   (fragment fr nil))
  ([fr cname]
   (let [cname (or cname
                   (:class fr)) 
         pick-fn (partial pick-fragment fr)]
     (condp = cname
       :actor (pick-fn (if (util/chance)
                         :person
                         :animal))
       :event (corp/get-event)
       (pick-fn cname)))))
