(ns xyzzwhy.engine.fragment
  (:require [xyzzwhy.engine.configuration :refer [has?] :as cf]
            [xyzzwhy.corpora :as corp]
            [xyzzwhy.util :as util :refer [any?]]
            [xyzzwhy.io :as io]))

;; -------
;; Utilities
;; -------
(defn a-or-an
  "Returns an article to be prixed to the string text."
  [text]
  (if (nil? (re-find #"(?i)^[aeiou]" text))
    "a"
    "an"))

(defn starts-with-article?
  "Returns true if the string text already has an article."
  [text]
  (if (re-find #"(?i)^(?:a|an|the)\s" text)
    true
    false))


;; -------
;; Fragment Accessors
;; -------
(defn backref
  [refnum sub]
  (first (filter #(= (:token %) (:ref refnum)) sub)))


;; -------
;; Fragment Configuration
;; -------
(defn no-article?
  "Returns true if a fragment's configuration demands that
  no article be prepended to it."
  [fragment]
  (has? fragment :no-article))

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

(defn article
  [chunk]
  (if-let [article (:article chunk)]
    (str (-> article util/pick) " ")
    (str (a-or-an (util/pick (:text chunk))) " ")))

;; -------
;; Yon fragment fetchery
;; -------
(defn merge-configs
  "Chooses a fragment from class cname and merges this fragment's
  :config with the parent fragment fr's :config."
  [fr cname]
  (let [fragments (corp/fragment cname)
        fr' (update fr :config cf/merge-into (cf/config (corp/config cname)))]
    (if (and (= cname :person)
             (contains? (cf/config fr') :no-groups))
      (merge fr' (dissoc (util/pick (vec (remove
                                          #(= (-> % :gender) :group)
                                          fragments))) :id))
      (merge fr' (dissoc (util/pick fragments) :id)))))

(defn decorate
  [chunk]
  )
(defn substitute
  [chunk]
  (condp = (:group chunk)
    :actor (if (util/chance) ;; 50% chance
             (-> :person io/read-file :fragment util/pick) 
             (-> :animal io/read-file :fragment util/pick))
    (-> (:group chunk) io/read-file :fragment util/pick)))

(defn parse-chunk
  [chunk]
  (cond
    (true? (string? chunk)) chunk
    (true? (map? chunk)) (-> chunk substitute article)
    :else "[REDACTED]"))

(defn set-event
  [fragment]
  (let [events (-> fragment :event io/read-file :fragment)]
    (assoc fragment :fragment (util/pick events))))

(defn set-output
  [fragment]
  (apply str (get-in fragment [:fragment :text])))

(defn initialize
  []
  (assoc {} :event (-> (io/read-file :groups)
                       :events
                       corp/weighted-pick
                       :group)))

(defn follow-up?
  [fragment]
  (contains? fragment :follow-up))

(defn follow-up
  [fragment]
  (first(map parse (rand-nth (get-in fragment [:follow-up :fragment])))))
