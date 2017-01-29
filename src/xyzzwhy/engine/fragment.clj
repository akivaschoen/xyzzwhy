(ns xyzzwhy.engine.fragment
  (:require [xyzzwhy.engine.configuration :refer [has?] :as cf]
            [xyzzwhy.corpora :as corp]
            [xyzzwhy.util :as util :refer [any?]]))

;; -------
;; Utilities
;; -------
(defn a-or-an
  "Returns an article to be prixed to the string s."
  [s]
  (if (nil? (re-find #"(?i)^[aeiou]" s))
    "a"
    "an"))

(defn starts-with-article?
  "Returns true if the string s already has an article."
  [s]
  (if (re-find #"(?i)^(?:a|an|the)\s" s)
    true
    false))


;; -------
;; Fragment Accessors
;; -------
(defn get-ref
  [refm subv]
  (first (filter #(= (:token %) (:ref refm)) subv)))


;; -------
;; Fragment Configuration
;; -------
(defn article?
  "Returns true if a fragment's configuration demands that
  no article be prepended to it."
  [fr]
  (not (has? fr :no-article)))

(defn article
  "If a fragment specifies an article, returns a randomly
  chosen article from that vector. Otherwise, randomly pick
  an article (either 'a' or 'an')."
  [fr]
  (if-let [article (:article fr)]
    (str (-> article util/pick) " ")
    (str (a-or-an (util/pick (:text fr))) " ")))

(defn follow-up?
  "Returns true if a fragment has follow-up fragments."
  [fr]
  (contains? fr :follow-up))

(defn no-groups?
  "Returns true if a fragment is configured to not use
  any substitutions that are designated as a :group."
  [fr]
  (has? fr :no-groups))

(defn prep?
  "Returns true if a fragment allows a preposition."
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
  "Returns true if a fragment has substitutions."
  [fr]
  (contains? fr :sub))


;; -------
;; Yon fragment fetchery
;; -------
(defn pick-fragment
  "Chooses a fragment from class cname and merges this fragment's
  :config with the parent fragment fr's :config."
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
  "Randomly chooses a fragment from class cname."
  ([fr]
   (fragment fr nil))
  ([fr cname]
   (fragment fr cname -1))
  ([fr cname index]
   (let [cname (or cname (:class fr))
         pick-fn (partial pick-fragment fr)]
     (condp = cname
       :actor (pick-fn (if (util/chance) ;; 50% chance
                         :person
                         :animal))
       :event (if (neg? index)
                (corp/get-event)
                (corp/get-event index))
       (pick-fn cname)))))
