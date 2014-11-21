(ns xyzzwhy-bot.core
  (:use [typographer.core]
        [xyzzwhy-bot.twitter]
        [xyzzwhy-bot.data]
        [xyzzwhy-bot.util])
  (:require [clojure.string :as string])
  (:gen-class))

(defn- combine-tweets
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
  (let [tweet (-> tweet interpolate-text)])
  (-> (:text tweet)
      smarten
      (string/replace #"^(@\w+)"        ".$1")
      (string/replace #"^[a-z]+"        #(string/capitalize %1))
      (string/replace #"(\.\s)([a-z]+)" #(str (second %1)
                                              (string/capitalize (nth %1 2))))))

(defn- get-follow-up
  [thing k]
  (nth (k thing) (rand-int (count (k thing)))))

; Yes. The size of this thing is ridiculous. It's highly non-idiomatic. Refactoring
; this baby is going to be fun. And I mean that with zero sarcasm or irony.
(defn create-tweet 
  "Construct the tweet segment by segment."
  []
  (let [initial-tweet (-> (initialize-tweet)
                          interpolate-text)]

    ; Here we want to randomize how the events get put together.
    ; There's a 75% chance that a location event will have a secondary event.
    (if (and (= (:event-type initial-tweet) :location-event)
             (< (rand-int 100) 75))

      (let [secondary-tweet (-> (initialize-event :secondary-event) 
                                interpolate-text)]

        ; if there is a secondary event, there's a 50% chance that there
        ; will be a tertiary event.
        (if (< (rand-int 100) 50)
          (let [tertiary-tweet (-> (initialize-event :tertiary-event) 
                                   interpolate-text)]

            ; And if there is a tertiary event, there's a 20% chance it
            ; will replace the secondary segment rather than append it.
            (if (< (rand-int 100) 80)
              (combine-tweets (combine-tweets initial-tweet secondary-tweet) tertiary-tweet)
              (combine-tweets initial-tweet tertiary-tweet)))
          (combine-tweets initial-tweet secondary-tweet))))
    (if (= (:event-type initial-tweet) :action-event)
      (if (< (rand-int 100) 25) ; A 25% chance to add a tertiary event to an action event
        (let [tertiary-tweet (-> (initialize-event :tertiary-event)
                                 interpolate-text)]
          (combine-tweets initial-tweet tertiary-tweet))
        initial-tweet)
      ; The beginnings of the follow-up system where things, such as rooms currently, can
      ; have specialized texts which can be appended.
      (let [follow-up (get-follow-up (:asset initial-tweet) :descriptions)]
        (if (or (empty? follow-up)
                (> (+ (count (:text initial-tweet)) (count follow-up)) 140))
          initial-tweet
          (assoc initial-tweet :text (str (:text initial-tweet) " " follow-up)))))))

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
