(ns xyzzwhy.engine.follow-up
  (:require [xyzzwhy.engine
             [configuration :as cf]
             [interpolation :refer :all]
             [substitution :as sb]]
            [xyzzwhy.util :as util]))

(defn follow-up?
  [fragment]
  (and (contains? fragment :follow-up)
       (not (cf/has? fragment :no-follow-up))
       (util/chance 25)))

(defn follow-up
  [fragment]
  (let [f (-> fragment :fragment util/pick)]
    (if (sb/sub? f)
      (assoc f :sub (reduce (fn [acc item]
                              (conj acc (sb/substitute f item)))
                            {}
                            (:sub f)))
      (util/append fragment (:text f)))))

([fragment follow-up ref]
 (if (contains? (:sub follow-up) ref)
   (get-sub follow-up ref)
   (get-sub fragment ref))))

(defn handle-fu-sub
  [fragment]
  (let [f (fu/follow-up fragment)]
    (println f)
    (util/append fragment f)))
