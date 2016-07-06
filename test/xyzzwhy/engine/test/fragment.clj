(ns xyzzwhy.engine.test.fragment
  (:require [clojure.test :refer [are deftest is testing] :as t]
            [xyzzwhy.engine
             [configuration :as cf]
             [fragment :as sut]]))


(deftest test-a-or-an
  (is (= (sut/a-or-an "animal") "an")
      "Expected article 'an' with a word that begins with a vowel.")
  (is (= (sut/a-or-an "book") "a")
      "Expected article 'a' with a word that begins with a consonant."))

(deftest test-has-article?
  (is (true? (sut/has-article? "a book"))
      "Expected a true result with a string beginning with the 'a'.")
  (is (true? (sut/has-article? "an animal"))
      "Expected a true result with a string beginning with the article 'an'.")
  (is (true? (sut/has-article? "the wall"))
      "Expected a true result with a string beginning with the article 'the'.")
  (is (false? (sut/has-article? "animal"))
      "Expected a false result with a string beginning with the non-article 'an'.")
  (is (false? (sut/has-article? "alpaca"))
      "Expected a false result with a string beginning with the non-article 'a'.")
  (is (false? (sut/has-article? "theocrat"))
      "Expected a false result with a string beginning with the non-article 'the'."))

(deftest test-prep
  (let [any? (complement not-any?)]
    (is (any? #{"a " "an " "the "}
              (vector (sut/prep {:prep ["a" "an" "the"]})))
        "Expected an article plus a space.")
    (is (= (sut/prep {:prep "a"})
           "a ")
        "Expected an 'a '.")
    (is (empty? (sut/prep {:no-prep "a"}))
        "Expected an empty string.")))
