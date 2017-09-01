(ns xyzzwhy.engine.substitution
  (:require [xyzzwhy.engine
             [configuration :as cf]
             [fragment :as fr]
             [interpolation :as in]]
            [xyzzwhy.util :as util]))



;; -------
;; Pronouns
;; -------
(defn pronoun
  "Returns a pronoun string for gender g and case c."
  [g c]
  (condp = g
    :male (case c
            :subjective "he"
            :objective "him"
            :possessive "his"
            :compound "himself")
    :female (case c
              :subjective "she"
              :objective "her"
              :possessive "hers"
              :compound "herself")
    :group (case c
             :subjective "they"
             :objective "them"
             :possessive "theirs"
             :compound "themselves")
    (case c
      :subjective "it"
      :objective "it"
      :possessive "its"
      :compound "itself")))


;; -------
;; Yon Transclusery
;; -------
(defmulti transclude (fn [_ _ t] t))

(defmethod transclude :event
  [tmap _ _]
  (let [tweet-text (reduce (fn [text sub]
                             (let [config (cf/merge-into (cf/config tmap)
                                                         (cf/config sub))]
                               (in/interpolate config text sub)))
                           (get-in tmap [:event :text])
                           (get-in tmap [:event :sub]))]
    (assoc tmap :tweet tweet-text)))

(defmethod transclude :sub
  [tmap index _]
  (let [path [:event :follow-up :fragment index]
        tweet-text (reduce (fn [text sub]
                             (let [config (cf/merge-into (cf/config tmap)
                                                         (cf/config sub))]
                               (in/interpolate config text sub)))
                           (get-in tmap (conj path :text))
                           (get-in tmap (conj path :sub)))]
    (-> tmap
        (assoc-in (conj path :text) tweet-text)
        (update :tweet util/append tweet-text))))

(defmethod transclude :default
  [tmap conf _]
  (let [text (reduce (fn [text sub]
                       (let [config (cf/merge-into (cf/config conf)
                                                   (cf/config sub))]
                         (in/interpolate config text sub)))
                     (:text tmap)
                     (:sub tmap))]
    (assoc tmap :text text)))

;; -------
;; Yon Substitutionarium
;; -------
(defmulti substitute* (fn [sub] (:class sub)))

(defmethod substitute* :gender
  [sub]
  (-> sub
      (assoc :config #{:no-article})
      #_(assoc :text (pronoun (util/pick (:gender (fr/get-ref sub subv))) (:case sub)))))

(defmethod substitute* :default
  [sub]
  (fr/fragment sub))

(defn substitute
  [subs]
  (if (empty? subs)
    []
    (mapv #(substitute* %1) subs)))

;; -------
;; Yon Follow-Uppery
;; -------
(defmulti follow-up*
  (fn [_ t] t))

(defmethod follow-up* :sub
  [tmap _]
  (reduce (fn [tmap sub]
            (let [skey (first sub)
                  sval (second sub)]
              (if (and (fr/follow-up? sval)
                       (or (and (not (cf/required? sval))
                                (util/chance))
                           (cf/required? sval)))
                (let [path [:follow-up :fragment]
                      follow (util/pick-indexed (get-in sval path))]
                  (if (fr/sub? (val follow))
                    (let [f (update (val follow) :sub substitute)
                          t (transclude f (cf/config tmap) :follow-up)
                          sval' (assoc-in sval
                                          (conj path (key follow))
                                          t)]
                      (-> tmap
                          (assoc-in [:event :sub skey] sval')
                          (update :tweet util/append (:text t))))
                    (-> tmap
                        (update :tweet util/append (:text (val follow))))))
                tmap)))
          tmap
          (rseq (vec (map-indexed vector (get-in tmap [:event :sub]))))))

(defmethod follow-up* :event
  [tmap _]
  (let [path [:event :follow-up :fragment]]
    (if-let [follow (util/pick-indexed (get-in tmap path))]
      (if (fr/sub? (val follow))
        (let [f (-> (val follow)
                    (update :sub substitute)
                    (transclude nil :follow-up))
              p (conj path (key follow))]
          (as-> tmap t
              (assoc-in t p f)
              (update t :tweet util/append (get-in t (conj p :text)))))
        (update tmap :tweet util/append (:text (val follow))))
      tmap)))

(defn follow-up
  "Returns a tmap with a follow-up possibly attached."
  [tmap]
  (if (cf/required? (get-in tmap [:event :follow-up]))
    (let [tmap' (-> tmap
                    (follow-up* :event)
                    (follow-up* :sub))]
      (if (= tmap' tmap)
        tmap
        (cf/add tmap' :no-follow-up)))
    (let [tmap' (follow-up* tmap :sub)]
      (if (= tmap' tmap)
        tmap
        (cf/add tmap' :no-follow-up)))))


;; -------
;; Yon Extra-Eventoilet
;; -------
(declare tertiary)

(defn append-event
  "Returns a tmap with an extra event attached."
  [tmap event-type]
  (let [event (-> nil
                  (fr/fragment (-> event-type
                                   name
                                   (str "-event")
                                   keyword))
                  (update :sub substitute)
                  (transclude nil nil))]
    (-> tmap
        (assoc-in [:event event-type] event)
        (update :tweet util/append (str " " (:text event)))
        (cf/add (cf/option-complement event-type)))))

(defn secondary
  "Returns a tmap with a secondary (and tertiary) event possibly attached.

  Secondary events have a default 75% chance for :location-events only,
  followed by a default 25% chance for a tertiary event."
  ([tmap]
   (secondary tmap 75 25))
  ([tmap sec-chance]
   (secondary tmap sec-chance 25))
  ([tmap sec-chance ter-chance]
   (if (and (cf/secondary? tmap)
            (= (get-in tmap [:event :class]) :location-event)
            (util/chance sec-chance))
     (tertiary (append-event tmap :secondary) ter-chance)
     tmap)))

(defn tertiary
  "Returns a tmap with a tertiary event possibly attached.

  Tertiary events have a default 35% chance of appearing for any event."
  ([tmap]
   (tertiary tmap 35))
  ([tmap chance]
   (if (and (cf/tertiary? tmap)
            (util/chance chance))
     (append-event tmap :tertiary)
     tmap)))
