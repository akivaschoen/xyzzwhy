(ns xyzzwhy.state
  (:use [xyzzwhy.data]
        [xyzzwhy.mongo])
  (:require [clojure.string :as string])
  (:import (java.lang String)))

(def state (atom {:classes {}}))

(defn keywordize
  [classname]
  (let [classname (if (not (.endsWith (name classname) "s"))
                    (str (name classname) "s")
                    classname)]
    (keyword classname)))

(defn initialize-state
  []
  (letfn [(create-class-entry [classname] 
            {(keyword classname) {:nth 0 :count (count (get-class classname))}})]
  (let [classlist (into {} (map #(create-class-entry %) classes))]
    (swap! state assoc :classes classlist))))

(defn get-class-state
  [classname]
  (get-in @state [:classes (keywordize classname)]))

(defn reset-class-state
  [classname]
  (swap! state assoc-in [:classes (keywordize classname) :nth] 0)
  (repopulate-class classname))

(defn check-class-threshold
  [classname]
  (let [class (get-class-state (first classname))]
    (if (> (:nth class) (* (:count class) 0.75))
      (reset-class-state (first classname)))))

(defn update-state
  [classname]
  (let [classname (keywordize (first classname))]
    (swap! state update-in [:classes classname :nth] inc)))
