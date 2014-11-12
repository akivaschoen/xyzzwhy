(ns xyzzwhy.core
  (:require [clojure.string :as string])
  (:gen-class))

(def event-types
  (list
    "location-event"
    "action-event"))

(def location-events
  (list
    "You have entered {{room}}."
    "You are standing {{direction}} of {{room}}."
    "You wake up from an odd dream unsure of where you are."))

(def action-events
  (list
    "You find {{item}} and slip it into your pocket, hoping {{person}} doesn't notice."
    "{{person}} drops {{item}}, looks at you carefully, then leaves."
    "Suddenly, {{person}} {{action}} you!"
    "You check your health. You are {{diagnose}}."))

(def secondary-events
  (list
    "You see {{item}} here."
    "{{person}} is here looking {{adjective}}."
    "Something smells {{scent}} here."
    "You hear the sound of {{noise}} in the distance."
    "You hear the sound of {{noise}} nearby."))

(def rooms
  (list
    "a grotto"
    "your bedroom"
    "McDonald's"
    "a dark area"))

(def items
  (list
    "a lantern"
    "an elven sword"
    "some books"))

(def directions
  (list
    "north"
    "northeast"
    "east"
    "southeast"
    "south"
    "southwest"
    "west"
    "northwest"))

(def monsters
  (list
    "orc"
    "ogre"
    "troll"))

(def persons
  (list
    "Gene Shalit"
    "Clive Chatterjee"
    "Nancy Grace"
    "Lindsay Lohan"
    "Barack Obama"
    "Abe Vigoda"
    "your mom"))

(def actions
  ; Ex. 'Suddenly, Lindsay Lohan tickles you!'}
  (list
    "attacks"
    "ignores"
    "tickles"
    "stands uncomfortably close to"
    "pets"
    "flirts with"))

(def adjectives
  (list
    "worried"
    "relieved"
    "aroused"
    "afraid"
    "sleepy"
    "hungry"
    "thirsty"
    "bored"
    "angry"))

(def scents
  (list
    "acrid"
    "sweet"
    "sour"
    "bitter"
    "smoky"
    "gross"
    "pleasant"))

(def noises
  (list
    "a foghorn"
    "laughter"
    "crying"
    "someone crying"
    "someone sneeze"
    "a sneeze"
    "wolves howling"
    "an ice cream truck"
    "a door slam"
    "a sinister chuckle"))

(def diagnoses
  ; Ex. 'You are feeling great.'
  (list
    "feeling great"
    "lightly wounded"
    "moderately wounded"
    "heavily wounded"
    "near death"
    "sleepy"
    "drunk"
    "stoned"
    "confused"
    "temporarily blind"
    "temporarily deaf"
    "covered in bees"))

(defn pluralize [s]
  (str s "s"))

(def make-matcher
  (fn [target] (re-matcher #"\{\{(\w+)\}\}" target)))

(defn get-random-thing [coll]
  (nth coll (rand-int (count coll))))

(defn get-random-event [event-types]
  (let [event-type (get-random-thing event-types)
        event (get-random-thing @(-> (pluralize event-type) 
                                     symbol 
                                     resolve))]
    (if (and (= event-type "location-event")
             (> (rand-int 100) 50))
      (str event " " (get-random-thing secondary-events))
      event)))

(defn interpolate [event match]
  (let [current-match (re-find match)]
    (if (nil? current-match)
      event
      (recur (string/replace event
                           (first current-match)
                           (get-random-thing @(-> (pluralize
                                                   (second current-match))
                                                 symbol
                                                 resolve)))
             match))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (let [event (get-random-event event-types)
        match (make-matcher event)]
    (interpolate event match)))
