(ns xyzzwhy.engine.test.configuration
  (:require [clojure.test :refer [are deftest is testing] :as t]
            [xyzzwhy.engine.configuration :as sut]))

(deftest test-option-complement
  (is (= (sut/option-complement :article)
         :no-article)
      "Expected a keyword beginning with 'no-' for an option not beginning with 'no-'.")
  (is (= (sut/option-complement :no-article)
         :article)
      "Expected a keyword not beginning with 'no-' for an option beginning with 'no-'.")
  (is (empty? (sut/option-complement ""))
      "Expected an empty string when given an empty string."))

(deftest test-has?
  (is (true? (sut/has? {:config #{:article}} :article))
      "Expected true for a :config containing :article.")
  (is (false? (sut/has? {:config #{:article}} :no-article))
      "Expected false for a :config not containing :no-article."))
