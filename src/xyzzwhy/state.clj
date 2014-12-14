(ns xyzzwhy.state
  (:use [xyzzwhy.data]
        [xyzzwhy.mongo]))

(def state (atom {:classes {}}))

(defn- pluralize-class
  [class]
  (-> (name class)
      (str "s")
      keyword))

(defn initialize-state
  []
  (letfn [(create-class-entry [class] 
            {(keyword class) {:nth 0 :count (count (get-class class))}})]
  (let [classlist (into {} (map #(create-class-entry %) classes))]
    (swap! state assoc :classes classlist))))

(defn get-class-state
  [class]
  (get-in @state [:classes (pluralize-class (name class))]))

(defn check-class-threshold
  [classname]
  (let [class (get-class-state (first classname))]
    (if (> (:nth class) (* (:count class) 0.75))
      (repopulate-class classname))))

(defn update-state
  [class]
  (let [class (pluralize-class (name (first class)))]
    (swap! state update-in [:classes class :nth] inc)))
