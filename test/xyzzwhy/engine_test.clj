(ns xyzzwhy.engine-test
  (:require [xyzzwhy.engine :as sut]
            [clojure.test :refer :all]))

(defonce events [:location-event
                 :action-event
                 :actor-event
                 :attack-event
                 :dialogue-event
                 :diagnosis-event
                 :news-event
                 :thought-event])

(defn valid-event?
  [event]
  (some? (some #(= event %) events)))

(deftest initialize
  (is (true? (valid-event? (:event (sut/initialize)))) "Proper tweetmap initialization")
  (is (false? (valid-event? (:event :fake-event))) "Unknown :event"))

