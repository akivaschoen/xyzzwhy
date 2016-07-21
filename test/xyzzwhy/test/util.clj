(ns xyzzwhy.test.util
  (:require [clojure.test :refer [are deftest is testing] :as t]
            [xyzzwhy.util :as sut]))

(def text-90 "This is a very long tweet meant to test append so that it stays underneath 140 characters.")
(def text-57 " And this one is 60 characters long including that space.")
(def text-49 " This one is 48 characters including that space.")

(deftest test-append
  (is (= (sut/append text-90 text-49) (str text-90 text-49))
      "Expected a 139-character append to work.")
  (is (= (sut/append text-57 text-49) (str text-57 text-49))
      "Expected an append totalling less than 139 characters to work.")
  (is (= (sut/append text-90 text-57) text-90)
      "Expected a 147-character append to fail, returning the oldtext param."))

(deftest test-pick
  (let [any? (complement not-any?)]
    (is (any? #{"this" "that" "the other"}
              (vector (sut/pick ["this" "that" "the other"])))
        "Expected a random selection from the input vector."))
  (is (= "this" (sut/pick "this"))
      "Expected pick to return a non-vector 'choice'."))

(deftest test-pick-indexed
  (is (= (sut/pick-indexed [{:sub "guch"}])
         (new clojure.lang.MapEntry 0 {:sub "guch"}))
      "Expected a Map$Entry matching the input including key.")
  (is (nil? (sut/pick-indexed {:sub "guch"}))
      "Expected a non-vector to return nil."))

(deftest test-prefix
  (is (= (sut/prefix "@mention") ".@mention")
      "Expected a string starting with an @ to be prefixed by a dot.")
  (is (= (sut/prefix "no mention") "no mention")
      "Expected prefix to return the param untouched."))

(deftest test-sentence-case
  (is (= (sut/sentence-case "this is a sentence.")
         "This is a sentence.")
      "Expected text with one sentence to be sentence-cased.")
  (is (= (sut/sentence-case "this is a sentence. this is another sentence.")
         "This is a sentence. This is another sentence.")
      "Expected text with two sentences to be sentence-cased.")
  (is (= (sut/sentence-case "A sentence. This is already in sentence case.")
         "A sentence. This is already in sentence case.")
      "Expected text in sentence case to return unmodified."))
