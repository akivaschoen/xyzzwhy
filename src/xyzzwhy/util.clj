(ns xyzzwhy.util)

(defn chance
  "Returns true if a randomly chosen percentile is less
  than or equal to c."
  ([]
   (chance 50))
  ([c]
   (if (<= (+ 1 (rand-int 100)) c)
     true
     false)))

(defn has? [m k]
  "Checks to see if a map contains a particular key."
  (some? (some #(= k %) (keys m))))

(def has-not?
  (complement has?))

(defn weighted-nth [coll]
  "Returns a coll by weighted randomization."
  (let [weight (reductions #(+ %1 %2) (map :weight coll))
        rnd (rand-int (last weight))]
    (nth coll (count (take-while #(<= % rnd) weight)))))
