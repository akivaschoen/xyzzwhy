(ns xyzzwhy.fragment
  (:require [xyzzwhy.io :as io]
            [xyzzwhy.configure :as conf]
            [xyzzwhy.util :as util]))

(defn select [m]
  "Returns a random item from the :fragment key in m."
  (rand-nth (:fragment m)))

(defn follow-up? [tmap & paths]
  "50% chance of returning a random follow-up to a fragment, should
it have any."
  (let [paths' (apply conj [] paths)]
    (if (util/has? (get-in tmap paths') :follow-up)
      (or (conf/required? (get-in tmap (conj paths' :follow-up :config)))
          (util/chance))
      false)))

(defn follow-up [tmap & paths]
  "Appends a random :follow-up to :fragments."
  (let [paths' (conj (apply conj [] paths) :follow-up) 
        text (select (get-in tmap paths'))]
    (update tmap :fragments #(reduce conj % (:text text)))))

(defn group [m]
  "Returns a :group's possible entries."
  (-> (:group m)
      (io/read-file)))

(defn append [tmap & paths]
  "Appends an entry on keyword paths (e.g., :event :text) to a tmap's
:fragments key."
  (let [path (apply conj [] paths)]
    (update tmap :fragments #(reduce conj % (get-in tmap path)))))

(defn select-event [tmap]
  "Configures an event for a newly initialized tweetmap and appends an
event's :text to the tmap's :fragments."
  (let [select-from-group (comp select group)
        tmap' (-> (assoc tmap :event (select-from-group tmap))
                  (append :event :text))]
    (if (follow-up? tmap' :event)
      (follow-up tmap' :event)
      tmap')))

