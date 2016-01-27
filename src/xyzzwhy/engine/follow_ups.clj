(ns xyzzwhy.engine.follow-ups
  (:require [xyzzwhy.engine.interpolation :refer :all]
            [xyzzwhy.engine.substitutions :as subs]
            [xyzzwhy.util :as util]))

(defn follow-up?
  [fragment]
  (contains? fragment :follow-up))

(defmulti get-follow-up
  "Appends fragment's follow up to its :text. If follow-up
  has substitutions, those are handled first."
  (fn [_ follow-up _] (subs/subs? follow-up)))

(defmethod get-follow-up true
  [fragment follow-up index]
  (let [option (subs/get-subs fragment follow-up)
        option (interpolate option)
        fragment (util/append fragment (:text option))]
    (assoc fragment [:follow-ups :options index] option)))

(defmethod get-follow-up false
  [fragment follow-up _]
  (util/append fragment (:text follow-up)))


(defn add-follow-up
  [fragment]
  (if (follow-up? fragment)
    (let [options (-> fragment :follow-ups :options)
          follow-up (-> options util/pick)
          index (.indexOf options follow-up)]
      (get-follow-up fragment follow-up index))
    fragment))

(defn add-follow-up-subs
  [fragment]
  (if (subs/subs? fragment)
    (reduce (fn [_ s]
              (if-let [follow-up (-> s val :source :follow-ups)]
                (if (and (true? (:optional? follow-up))
                         (util/chance 50))
                  fragment
                  (reduced (util/append fragment (-> follow-up
                                                :options
                                                util/pick
                                                subs/get-subs
                                                interpolate
                                                :text))))
                fragment))
            fragment
            (:subs fragment))
    fragment))
