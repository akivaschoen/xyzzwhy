(ns xyzzwhy-bot.util)

(defn format-word 
  "Applies an article to a word if it has one. For example, 'falafel' becomes 'a falafel' while
  'rice' becomes 'some rice'. Each word specifies its preferred articles."
  [word]
  (let [text (:text word)
        preps (:preps word)]
    (if-let [article (:article word)]
      (if (or (some #(.startsWith text %) preps)
              (.startsWith text article))
        text
        (str article " " text))
    text)))

(defn read-asset
  ([tweet]    (get-in tweet [:asset :text]))
  ([tweet k]  (get-in tweet [:asset (keyword k)])))
