(ns xyzzwhy.test.util
  (:require [clojure.test :refer [are deftest is testing] :as t]
            [xyzzwhy.util :as sut]))

(deftest test-pick-indexed
  (is (= (sut/pick-indexed [{:sub "guch"}]) (new clojure.lang.MapEntry 0 {:sub "guch"})))
  (is (nil? (sut/pick-indexed {:sub "guch"}))))
