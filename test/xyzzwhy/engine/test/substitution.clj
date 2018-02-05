(ns xyzzwhy.engine.test.substitution
  (:require [clojure.test :refer [are deftest is testing] :as t]
            [xyzzwhy.engine.substitution :as sut]))

(deftest test-pronoun
  (testing "Testing pronoun :male"
    (are [expected gender noun-case] (= (sut/pronoun gender noun-case) expected)
      "he" :male :subjective
      "him" :male :objective
      "his" :male :possessive
      "himself" :male :compound))
  (testing "Testing pronoun :female"
    (are [expected gender noun-case] (= (sut/pronoun gender noun-case) expected)
      "she" :female :subjective
      "her" :female :objective
      "hers" :female :possessive
      "herself" :female :compound))
  (testing "Testing pronoun :group"
    (are [expected gender noun-case] (= (sut/pronoun gender noun-case) expected)
      "they" :group :subjective
      "them" :group :objective
      "theirs" :group :possessive
      "themselves" :group :compound))
  (testing "Testing pronoun :default"
    (are [expected gender noun-case] (= (sut/pronoun gender noun-case) expected)
      "it" :object :subjective
      "it" :object :objective
      "its" :object :possessive
      "itself" :object :compound)))
