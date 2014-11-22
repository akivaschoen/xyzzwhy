(ns xyzzwhy-bot.core
  (:use [typographer.core]
        [xyzzwhy-bot.twitter]
        [xyzzwhy-bot.data]
        [xyzzwhy-bot.util])
  (:require [clojure.string :as string])
  (:gen-class))

(defn- combine-tweets
  "Merges the text values of two tweets."
  [first-tweet second-tweet]
  (assoc first-tweet :text (str (:text first-tweet) " " (:text second-tweet))))

(defn- interpolate-text 
  "Searches text for {{word}} and {{word-modifier}} combinations and replaces them
  with appropriate things from the database. The 'word' represents a class of possible
  responses such as {{person}} and {{garment}}."
  [tweet]
  (let [matcher (re-matcher #"\{\{(\w+(\-\w+)*)\}\}" (:text tweet))]
    (loop [tweet tweet match (re-find matcher)]
      (if-not match
        tweet
        (let [tweet (as-> tweet t 
                          (get-thing t (second match))
                          (assoc t :text (string/replace-first 
                                         (:text t) 
                                         (first match) 
                                         (format-word (:asset t)))))]
              (recur tweet (re-find matcher)))))))

(defn- finalize-tweet
  "Verifies there are no remaining uninterpolated words, ensures proper capitalization 
  throughout the final tweet and, if the tweet starts with an @mention, puts a dot up 
  front so everyone can see it."
  [tweet]
  (let [tweet (-> tweet interpolate-text)]
    (-> (:text tweet)
        smarten
        (string/replace #"^(@\w+)"        ".$1")
        (string/replace #"^[a-z]+"        #(string/capitalize %1))
        (string/replace #"(\.\s)([a-z]+)" #(str (second %1)
                                                (string/capitalize (nth %1 2)))))))

(defn- get-follow-up
  "Retrieves follow-up data from a thing. It currently returns a string but it should
  return a thing. 
  
  Currently, only rooms have follow-up data: descriptions."
  [thing k]
  (nth (k thing) (rand-int (count (k thing)))))

; This is gross, ugly, embarassing, non-standard, brutally dumb, etc. But it gets 
; the job done for now. Throw some shade but it will be refactored very soon now.
(defn create-tweet
  "Creates a tweet by combining up to three events in various combinations and/or any
  follow-ups."
  []
  (let [initial-tweet (-> (initialize-tweet)
                          interpolate-text)]
    (if (= (:event-type initial-tweet) :location-event) 
      ; 75% chance of a location event having a secondary event
      (if (<= (rand-int 100) 75)
        (let [secondary-tweet (-> (initialize-event :secondary-event) interpolate-text)]
          ; 50% chance of that secondary event having a tertiary event
          (if (<= (rand-int 100) 50)
            (let [tertiary-tweet (-> (initialize-event :tertiary-event) interpolate-text)]
              ; 80% of merging all three events into one
              ; 20% of the teriary event replacing the secondary event
              (if (<= (rand-int 100) 80)
                (let [output (combine-tweets initial-tweet secondary-tweet)]
                  (combine-tweets output tertiary-tweet))
                  (combine-tweets initial-tweet tertiary-tweet)))
            ; If no tertiary event, just use the secondary
            (combine-tweets initial-tweet secondary-tweet)))
        ; The beginnings of the follow-up system. Hooray and stuff.
        (let [follow-up (get-follow-up (:asset initial-tweet) :descriptions)]
          (if (or (empty? follow-up)
                  (> (+ (count (:text initial-tweet)) (count follow-up) 140)))
            initial-tweet
            (assoc initial-tweet :text (str (:text initial-tweet) " " follow-up)))))
      ; 25% chance of an action event having its own teriary event
      (if (<= (rand-int 100) 25)
        (let [tertiary-tweet (-> (initialize-event :tertiary-event)
                                 interpolate-text)]
          (combine-tweets initial-tweet tertiary-tweet))
        initial-tweet))))

(defn -main
  "Starts the bot up with an initial tweet and then randomly waits between
  5 and 30 minutes before tweeting again."
  [& args]
  ; This is all extremely ugly but is good enough for now.
  (println "xyzzwhy is ready for some magical adventures!")
  (loop []
    (let [interval (+ 300000 (rand-int 1500000)) ; Tweet once every 5-30 minutes
          tweet (-> (create-tweet) finalize-tweet)]
      (try
        (do
          (post-to-twitter tweet)

          ; Logging
          (println "Tweeted:" tweet)
          (println "Next tweet in" (int (/ interval 60000)) "minutes")

          (Thread/sleep interval))
        (catch Exception e
          (println "Caught error:" (.getMessage e))))
      (recur))))
