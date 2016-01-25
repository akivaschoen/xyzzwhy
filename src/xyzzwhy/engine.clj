(ns xyzzwhy.engine
  (:require [xyzzwhy.engine
             [follow-ups :as fup]
             [fragment :as frag]
             [interpolation :refer :all]
             [substitutions :as subs]]
            [xyzzwhy.util :as util]))

(defn article?
  [fragment config]
  (and (not (contains? config :no-article))
       (contains? fragment :sub)))

(defn article
  "Returns a fragment's article."
  [fragment]
  (when-let [article (:article fragment)]
    (-> article util/pick)))

(defn prep?
  [config]
  (not (contains? config :no-prep)))

(defn prep
  "Returns a fragment's preposition, randomly chosen."
  [fragment]
  (when-let [prep (:prep fragment)]
    (-> prep util/pick)))

(defn prepend
  "Adds text to the front of another string."
  [text target]
  (str (util/pad text) target))








;; ____ Older Code Begins Here ____

;; Corpus
;;
;; Future plan is to allow the corpus to come from any source as
;; long as it can be mapped as it is here based on how each fragment
;; is formatted xyzzwhy.text.
(def ^:private corpus {})

(defn- initialize-corpus-from-namespace
  [c n]
  (reduce #(conj %1 @(-> (ns-resolve n (symbol %2)) deref future))
          c
          (-> (ns-resolve n 'classes) deref)))



;;
;; Main Engine
;;


(defn- choose-event
  "Returns a random event type on which a tweet is built."
  []
  (-> corpus :events util/pick))


;; Helper functions
#_(def ^:private process-fragment (comp fup/add-follow-up-subs
                                      fup/add-follow-up
                                      interpolate
                                      subs/get-subs))

;; Secondary and Tertiary Events
(defn- get-tertiary
  []
  (-> (frag/get-fragment :tertiary-event)
      process-fragment))

(defn- get-secondary
  []
  (let [s (-> (frag/get-fragment :secondary-event)
              process-fragment)]
    (if (util/chance 35)
      (util/append s (:text (get-tertiary)))
      s)))

(defn- get-extras
  [tweet event]
  (case event
    :location-event (if (util/chance 75)
                      (util/append tweet (:text (get-secondary)))
                      tweet)
    :action-event (if (util/chance 25)
                    (util/append tweet (:text (get-tertiary)))
                    tweet)
    "default" tweet))


;;
;; API
;;
(defn assign-corpus
  "Sets the corpus from a properly formatted source.

  See xyzzwhy.text."
  [source]
  (alter-var-root #'corpus initialize-corpus-from-namespace source))

(defn get-tweet
  "Returns a randomly generated tweet."
  []
  (let [event (choose-event)]
    (-> event
        util/pluralize
        keyword
        frag/get-fragment
        process-fragment
        (get-extras event)
        util/capitalize*
        util/dot-prefix
        util/smarten*)))
