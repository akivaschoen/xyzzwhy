(ns xyzzwhy.engine.test.fragment
  (:require [clojure.test :refer [are deftest is testing] :as t]
            [xyzzwhy.engine.fragment :as sut]))

(deftest test-a-or-an
  (is (= (sut/a-or-an "animal") "an")
      "Expected article 'an' with a word that begins with a vowel.")
  (is (= (sut/a-or-an "book") "a")
      "Expected article 'a' with a word that begins with a consonant."))

(deftest test-starts-with-article?
  (is (true? (sut/starts-with-article? "a book"))
      "Expected a true result with a string beginning with the 'a'.")
  (is (true? (sut/starts-with-article? "an animal"))
      "Expected a true result with a string beginning with the article 'an'.")
  (is (true? (sut/starts-with-article? "the wall"))
      "Expected a true result with a string beginning with the article 'the'.")
  (is (false? (sut/starts-with-article? "animal"))
      "Expected a false result with a string beginning with the non-article 'an'.")
  (is (false? (sut/starts-with-article? "alpaca"))
      "Expected a false result with a string beginning with the non-article 'a'.")
  (is (false? (sut/starts-with-article? "theocrat"))
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

(deftest test-sub?
  (testing "Testing sub? check"
    (is (= (sut/sub? {:sub "Our subkey" :other "Some other key"}) true))
    (is (= (sut/sub? {:other "Some other key"}) false))))
