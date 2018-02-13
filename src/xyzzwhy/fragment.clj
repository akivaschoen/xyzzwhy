(ns xyzzwhy.fragment
  (:require [xyzzwhy.io :as io]
            [xyzzwhy.configure :as conf]
            [xyzzwhy.util :as util]))

(defn follow-up? [fragment]
  "50% chance of returning a random follow-up to a fragment, should
it have any."
  (and (util/has? fragment :follow-up)
       (or (conf/required? (-> fragment :follow-up :config))
           (util/chance))))

(defn follow-up [fragment]
  (-> fragment
      :follow-up
      :fragment
      rand-nth))

(defn select-fragment [m]
  (rand-nth (:fragment m)))

(defn select-group [m]
  (-> (:group m)
      (io/read-file)))

(defn append [tmap fragment]
  (update tmap :fragments conj (first (:text fragment))))


