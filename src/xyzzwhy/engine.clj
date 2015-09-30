(ns xyzzwhy.engine
  (:require [clojure.string :as str]
            [typographer.core :as typo]))

;;
;; Utilities
;;
(defn- pad
  [text]
  (str text " "))

(defn- append
  "Adds text to fragment's :text and returns fragment."
  [fragment text]
  (if (< (+ (-> fragment :text count)
            (count text))
         139)
    (update fragment :text #(str (pad %1) text))
    fragment))

(defn- capitalize*
  "Capitalizes fragment's sentences."
  [fragment]
  (assoc fragment :text (-> (:text fragment)
                            (str/replace #"^[a-z]+" #(str/capitalize %1))
                            (str/replace #"(\.\s)([a-z]+)"
                                         #(str (second %1)
                                               (str/capitalize (nth %1 2)))))))

(defn- pluralize
  [c]
  (let [c' (name c)]
    (if (.endsWith c' "s")
      c'
      (str c' "s"))))

(defn- dot-prefix
  "Prefix's fragment's :text with a period if it begins with
  an @mention."
  [fragment]
  (update fragment :text #(str/replace % #"^(@\w+)" ".$1")))

(defn- optional?
  "Returns true if a follow-up is optional."
  [follow-up]
  (-> follow-up :optional? true?))

(defn- pick
  "Chooses a random item from coll."
  [coll]
  (nth coll (rand-int (count coll))))

(defn- smarten*
  "Converts fragment's text to use typographer's quotes."
  [fragment]
  (update fragment :text #(typo/smarten %)))

(defn- chance
  "Returns true if a randomly chosen percentile is less
  than or equal toc."
  [c]
  (if (<= (+ 1 (rand-int 100)) c)
    true
    false))


;;
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

(defn- get-class
  [c]
  (-> c pluralize keyword corpus))


;;
;; Fragment Configuration
;;
(defn- article?
  [config]
  (not (contains? config :no-article)))

(defn- article
  "Returns a fragment's article."
  [fragment]
  (when-let [article (:article fragment)]
    (-> fragment :article pad)))

(defn- follow-ups?
  [fragment]
  (contains? fragment :follow-ups))

(defn- prep?
  [config]
  (not (contains? config :no-prep)))

(defn- prep
  "Returns a fragment's preposition, randomly chosen."
  [fragment]
  (when-let [preps (:preps fragment)]
    (-> preps pick pad)))

(defn- no-groups?
  [config]
  (contains? config :no-groups))

(defn- subs?
  [fragment]
  (contains? fragment :subs))


;;
;; Pronouns
;;
(defmulti ^:private gender (fn [gender _] gender))

(defmethod gender :male
  [_ c]
  (case c
    :subjective "he"
    :objective "him"
    :possessive "his"
    :compound "himself"))

(defmethod gender :female
  [_ c]
  (case c
    :subjective "she"
    :objective "her"
    :possessive "hers"
    :compound "herself"))

(defmethod gender :group
  [_ c]
  (case c
    :subjective "they"
    :objective "them"
    :possessive "theirs"
    :compound "themselves"))

(defmethod gender :default
  [_ c]
  (case c
    :subjective "it"
    :objective "it"
    :possessive "its"
    :compound "itself"))


;;
;; Main Engine
;;
(defn- interpolate
  "Replaces all substitution markers with matching text,
  returning fragment."
  ([fragment]
   (reduce #(interpolate %1 %2) fragment (:subs fragment)))
  ([fragment sub]
   (let [sub' (val sub)
         prep' (when (prep? (:config sub'))
                 (-> sub' :source prep))
         article' (when (article? (:config sub'))
                    (-> sub' :source article))
         text (str prep' article' (-> sub' :source :text))]
     (update fragment :text
             str/replace (str "%" (key sub)) text))))

(defn- choose-event
  "Returns a random event type on which a tweet is built."
  []
  (-> corpus :events pick))

;; Fragments
(defmulti ^:private get-fragment*
  "Given a class c, returns a random item
  from the corpus."
  (fn [c _] c))

(defmethod get-fragment* :actor
  [c config]
  (let [persons (get-class :persons)
        animals (get-class :animals)
        actors (apply merge persons animals)
        actors (if (no-groups? config)
                 (filter #(not= :group (-> % :gender)) actors)
                 actors)]
    (-> actors pick)))

(defmethod get-fragment* :person
  [c config]
  (let [persons (if (no-groups? config)
                  (filter #(not= :group (-> % :gender)) (get-class :persons))
                  (get-class :persons))]
    (-> persons pick)))

(defmethod get-fragment* :default
  [c config]
  (->> c pluralize keyword corpus pick))

(defn- get-fragment
  ([c]
   (get-fragment c nil))
  ([c config]
   (get-fragment* c config)))

;; Substitutions
(defn- get-sub
  "Given a reference number, returns the appropriate substitution."
  ([fragment ref]
   (get (:subs fragment) ref))
  ([fragment follow-up ref]
   (if (contains? (:subs follow-up) ref)
     (get-sub follow-up ref)
     (get-sub fragment ref))))

(defmulti ^:private substitute
  "Returns a fragment to be used for a substitution."
  (fn [_ sub] (-> sub val :class)))

(defmethod substitute :gender
  [fragment sub]
  (let [sub' (val sub)
        gender' (-> fragment :subs (find (key sub)) val :source :gender)]
    {(key sub) (assoc-in sub' [:source :text] (gender gender' (:case sub')))}))

(defmethod substitute :default
  [fragment sub]
  (let [sub' (val sub)]
    {(key sub) (assoc sub' :source (get-fragment (-> sub' :class)))}))

(letfn [(subs [fragment subs]
          (reduce #(conj %1 (substitute fragment %2)) {} subs))]
  (defn- get-subs
    "Populates fragment's possible substitutions with
    appropriate fragments."
    ([fragment]
     (let [subs (subs fragment (:subs fragment))]
       (if (empty? subs)
         fragment
         (assoc fragment :subs subs))))
    ([fragment follow-up]
     (let [subs (subs follow-up (:subs follow-up))]
       (if (empty? subs)
         follow-up
         (assoc follow-up :subs subs))))))

;; Follow-Ups
(defmulti ^:private get-follow-up*
  "Appends fragment's follow up to its :text. If follow-up
  has substitutions, those are handled first."
  (fn [_ follow-up _] (subs? follow-up)))

(defmethod get-follow-up* true
  [fragment follow-up index]
  (let [option (get-subs fragment follow-up)
        option (interpolate option)
        fragment (append fragment (:text option))]
    (assoc fragment [:follow-ups :options index] option)))

(defmethod get-follow-up* false
  [fragment follow-up _]
  (append fragment (:text follow-up)))

(defn- add-follow-up
  [fragment]
  (if (follow-ups? fragment)
    (let [options (-> fragment :follow-ups :options)
          follow-up (-> options pick)
          index (.indexOf options follow-up)]
      (get-follow-up* fragment follow-up index))
    fragment))

(defn- get-follow-up-subs
  [fragment]
  (if (subs? fragment)
    (reduce (fn [_ s]
              (if-let [follow-up (-> s val :source :follow-ups)]
                (if (and (true? (:optional? follow-up))
                         (chance 50))
                  fragment
                  (reduced (append fragment
                                   (-> follow-up :options pick :text))))
                fragment))
            fragment
            (:subs fragment))
    fragment))

;; Helper functions
(def ^:private process-fragment (comp get-follow-up-subs
                                      add-follow-up
                                      interpolate
                                      get-subs))

;; Secondary and Tertiary Events
(defn- get-tertiary
  []
  (-> (get-fragment :tertiary-event)
      process-fragment))

(defn- get-secondary
  []
  (let [s (-> (get-fragment :secondary-event)
              process-fragment)]
    (if (chance 35)
      (append s (:text (get-tertiary)))
      s)))

(defn- get-extras
  [tweet event]
  (case event
    :location-event (if (chance 75)
                      (append tweet (:text (get-secondary)))
                      tweet)
    :action-event (if (chance 25)
                    (append tweet (:text (get-tertiary)))
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
        pluralize
        keyword
        get-fragment
        process-fragment
        (get-extras event)
        capitalize*
        dot-prefix
        smarten*)))
