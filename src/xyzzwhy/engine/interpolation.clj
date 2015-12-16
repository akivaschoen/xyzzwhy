(ns xyzzwhy.engine.interpolation
  (:require [clojure.string :as string]
            [xyzzwhy.engine.fragments :as frag]))

(defn interpolate
  "Replaces all substitution markers with matching text,
  returning fragment."
  ([fragment]
   (reduce #(interpolate %1 %2) fragment (:subs fragment)))
  ([fragment sub]
   (let [sub' (val sub)
         prep (when (frag/prep? (:config sub'))
                 (-> sub' :source frag/prep))
         article (when (frag/article? (:config sub'))
                    (-> sub' :source frag/article))
         text (str prep article (-> sub' :source :text))]
     (update fragment :text
             string/replace (str "%" (key sub)) text))))
