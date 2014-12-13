(ns xyzzwhy.core
  (:use [typographer.core]
        [xyzzwhy.twitter]
        [xyzzwhy.factory]
        [xyzzwhy.util])
  (:require [clojure.string :as string])
  (:gen-class))

(defn- combine-segments
  "Merges the text values of two segments."
  [first-segment second-segment]
  (as-> (assoc first-segment :asset second-segment) s
        (assoc s :text (str (:text s) " " (read-asset s)))))

(defn- interpolate-text 
  "Searches text for placeholder maps replaces them with appropriate things from the 
  database." 
  [segment]
  (let [matcher (re-matcher #"\{(?::\w+|\s|:\w+-\w+|\[:.+])+\}" (:text segment))]
    (loop [segment segment match (re-find matcher)]
      (if-not match
        segment
        (let [segment (as-> segment s
                          ; The new thing is stored as the current asset for later reference...
                          (assoc s :asset (get-thing (read-string match)))
                          ; ...along with its configuration...
                          (assoc-in s [:asset :config] (:config (read-string match)))
                          (assoc s :text (string/trim (string/replace-first 
                                           (:text s) 
                                           match
                                                         ; ...which is used here.
                                           (string/trim (format-text (:asset s)))))))] 
          (recur segment (re-find matcher)))))))

(defn- finalize-tweet
  "Verifies there are no remaining uninterpolated words, ensures proper capitalization 
  throughout the final tweet and, if the tweet starts with an @mention, puts a dot up 
  front so everyone can see it."
  [segment]
  (let [segment (interpolate-text segment)]
    (-> (:text segment)
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

(defn get-segment
  [type]
  (-> (create-segment type)
      interpolate-text))

(defn compile-tweet
  "Creates a tweet by combining segments in various combinations."
  []
  (let [initial-segment (get-segment :event-type)]
    (if (= (:event-type initial-segment) :location-event) 
      ; 75% chance of a location event having a secondary event
      (if (<= (rand-int 100) 75)
        (let [secondary-segment (get-segment :secondary-event)]
          ; 50% chance of that secondary event having a tertiary event
          (if (<= (rand-int 100) 35)
            (let [tertiary-segment (get-segment :tertiary-event)]
              ; 80% of merging all three events into one
              ; 20% of the teriary event replacing the secondary event
              (if (<= (rand-int 100) 80)
                (let [output (combine-segments initial-segment secondary-segment)]
                  (combine-segments output tertiary-segment))
                (combine-segments initial-segment tertiary-segment)))
            ; If no tertiary event, just use the secondary
            (combine-segments initial-segment secondary-segment)))
        ; The beginnings of the follow-up system. Hooray and stuff.
        (let [follow-up (get-follow-up (:asset initial-segment) :descriptions)]
          (if (or (empty? follow-up)
                  (> (+ (count (:text initial-segment)) (count follow-up)) 140))
            initial-segment
            (assoc initial-segment :text (str (:text initial-segment) " " follow-up)))))
      ; 25% chance of an action event having its own teriary event
      (if (<= (rand-int 100) 25)
        (let [tertiary-segment (get-segment :tertiary-event)]
          (combine-segments initial-segment tertiary-segment))
        initial-segment))))

(defn -main
  "Starts the bot up with an initial tweet and then randomly waits between
  20 and 40 minutes before tweeting again."
  [& args]
  (println "xyzzwhy is ready for some magical adventures!")
  (loop []
    (let [interval (+ 1200000 (rand-int 1200000)) ; Tweet once every 20-40 minutes
          tweet (-> (compile-tweet) finalize-tweet)]

      (println tweet)

      (comment
        (try
          (do
            (post-to-twitter tweet)

            ; Logging
            (do
              (println "Tweeted:" tweet)
              (println "Next tweet in" (int (/ interval 60000)) "minutes"))

            (Thread/sleep interval))
          (catch Exception e
            (println "Caught error:" (.getMessage e)))))
      (recur))))
