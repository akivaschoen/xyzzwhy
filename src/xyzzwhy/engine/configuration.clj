(ns xyzzwhy.engine.configuration
  (:require [clojure
             [string :as str]
             [set :as sets]]))

(defn config
  [tmap]
  (cond
    (contains? tmap :event) (get-in tmap [:event :config])
    :else
    (:config tmap)))

(declare option-complement? option-complement)

(defn merge-into
  "Merges c2 into c1 returning a merged set."
  [c1 c2]
  (letfn [(config [c]
            (if (nil? c)
              #{}
              c))]
    (let [c1 (config c1)
          c2 (config c2)]
      (reduce (fn [conf opt]
                (let [opts (:config conf)]
                  (if (or (contains? opts opt)
                          (contains? opts (option-complement opt)))
                    conf
                    (conj conf opt))))
              c1
              c2))))

(defn combine
  [c1 c2]
  (let [c (sets/union (:config c1) (:config c2))]
    (reduce (fn [acc option]
              (let [opt (option-complement option)]
                (if (and (str/starts-with? (name option) "no-"))
                  (contains? c opt))
                (disj acc opt)
                acc))
            c
            c)))

(defn option-complement
  [option]
  (let [opt (name option)]
    (keyword
     (cond
       (empty? opt) nil
       (str/starts-with? opt "no-") (str/replace opt #"^no-" "")
       :else
       (str "no-" opt)))))

(defn option-complement?
  [option config]
  (contains? config (option-complement option)))

(defn has?
  ([fr option]
   (contains? (config fr) option))
  ([fr tmap option]
   (contains? (merge (config tmap) (config fr)) option)))

(defn required?
  [fr]
  (has? fr :required))

(defn follow-up?
  ([fr]
   (not (has? fr :no-follow-up)))
  ([fr tmap]
   (not (has? fr tmap :no-follow-up))))

(defn add
  [tmap opt]
  (update-in tmap [:event :config] conj opt))
