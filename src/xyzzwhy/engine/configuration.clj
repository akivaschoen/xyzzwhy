(ns xyzzwhy.engine.configuration
  (:require [clojure.string :as cstr]))

;; -------
;; DEFAULTS
;;   :article

;; -------
;; Utilities
;; -------
(defn add
  "Adds a configuration option to an event's config."
  [event option]
  (update-in event [:event :config] conj option))

(defn config
  "Returns a config set."
  [fragment]
  (or (get-in fragment [:event :config])
      (:config fragment)))

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

(defn option-complement
  [option]
  (let [opt (name option)]
    (keyword
     (cond
       (empty? opt) nil
       (cstr/starts-with? opt "no-") (cstr/replace opt #"^no-" "")
       :else
       (str "no-" opt)))))

(defn option-complement?
  [option config]
  (contains? config (option-complement option)))


;; -------
;; Queries
;; -------
(defn has?
  ([fragment option]
   (contains? (config fragment) option))
  ([fragment tmap option]
   (contains? (merge (config tmap) (config fragment)) option)))

(defn follow-up?
  ([fragment]
   (not (has? fragment :no-follow-up)))
  ([fragment tmap]
   (not (has? fragment tmap :no-follow-up))))

(defn required?
  [fragment]
  (has? fragment :required))

(defn secondary?
  [fragment]
  (not (has? fragment :no-secondary)))

(defn tertiary?
  [fragment]
  (not (has? fragment :no-tertiary)))
