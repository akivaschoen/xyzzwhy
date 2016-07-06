(ns xyzzwhy.engine.test.substitution
  (:require [clojure.test :refer [are deftest is testing] :as t]
            [xyzzwhy.engine.substitution :as sut]))

(deftest test-sub?
  (testing "Testing sub? check"
    (is (= (sut/sub? {:sub "Our subkey" :other "Some other key"}) true))
    (is (= (sut/sub? {:other "Some other key"}) false))))

(deftest test-gender
  (testing "Testing gender :male"
    (are [expected sex noun-case] (= (sut/gender sex noun-case) expected)
      "he" :male :subjective
      "him" :male :objective
      "his" :male :possessive
      "himself" :male :compound))
  (testing "Testing gender :female"
    (are [expected sex noun-case] (= (sut/gender sex noun-case) expected)
      "she" :female :subjective
      "her" :female :objective
      "hers" :female :possessive
      "herself" :female :compound))
  (testing "Testing gender :group"
    (are [expected sex noun-case] (= (sut/gender sex noun-case) expected)
      "they" :group :subjective
      "them" :group :objective
      "theirs" :group :possessive
      "themselves" :group :compound))
  (testing "Testing gender :default"
    (are [expected sex noun-case] (= (sut/gender sex noun-case) expected)
      "it" :object :subjective
      "it" :object :objective
      "its" :object :possessive
      "itself" :object :compound)))

#_(deftest test-sub-with
  (let [fragment {:text "You surprise %0. %1 drops %2 and runs away."
                  :sub {0 {:class :person}
                        1 {:ref 0
                           :class :gender
                           :case :subjective}
                        2 {:class :item}}}]
    (= )))
