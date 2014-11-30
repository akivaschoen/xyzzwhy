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
  (as-> (assoc first-tweet :asset second-tweet) t
        (assoc t :text (str (:text t) " " (read-asset t)))))

(defn- interpolate-text 
  "Searches text for placeholder maps replaces them with appropriate things from the 
  database." 
  [tweet]
  (let [matcher (re-matcher #"\{(?::\w+|\s|:\w+-\w+|\[:.+])+\}" (:text tweet))]
    (loop [tweet tweet match (re-find matcher)]
      (if-not match
        tweet
        (let [tweet (as-> tweet t 
                          ; The new thing is stored as the current asset for later reference...
                          (assoc t :asset (get-thing (read-string match)))
                          ; ...along with its configuration...
                          (assoc-in t [:asset :config] (:config (read-string match)))
                          (assoc t :text (string/trim (string/replace-first 
                                           (:text t) 
                                           match
                                                         ; ...which is used here.
                                           (string/trim (format-text (:asset t)))))))] 
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
  (let [initial-tweet (-> (initialize-tweet :event-type)
                          interpolate-text)]
    (if (= (:event-type initial-tweet) :location-event) 
      ; 75% chance of a location event having a secondary event
      (if (<= (rand-int 100) 75)
        (let [secondary-tweet (-> (initialize-tweet :secondary-event) interpolate-text)]
          ; 50% chance of that secondary event having a tertiary event
          (if (<= (rand-int 100) 35)
            (let [tertiary-tweet (-> (initialize-tweet :tertiary-event) interpolate-text)]
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
                  (> (+ (count (:text initial-tweet)) (count follow-up)) 140))
            initial-tweet
            (assoc initial-tweet :text (str (:text initial-tweet) " " follow-up)))))
      ; 25% chance of an action event having its own teriary event
      (if (<= (rand-int 100) 25)
        (let [tertiary-tweet (-> (initialize-tweet :tertiary-event)
                                 interpolate-text)]
          (combine-tweets initial-tweet tertiary-tweet))
        initial-tweet))))

(defn -main
  "Starts the bot up with an initial tweet and then randomly waits between
  10 and 30 minutes before tweeting again."
  [& args]
  (println "xyzzwhy is ready for some magical adventures!")
  (loop []
    (let [interval (+ 900000 (rand-int 1500000)) ; Tweet once every 15-30 minutes
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
