(ns xyzzwhy-bot.util)

(defn format-word 
  "Applies an article to a word if it has one. For example, 'falafel' becomes 'a falafel' while
  'rice' becomes 'some rice'. Each word specifies its preferred articles."
  [word]
  (if-let [article (:article word)]
    (str article " " (:text word))
    (:text word)))

(defn read-asset
  ([tweet]    (get-in tweet [:asset :text]))
  ([tweet k]  (get-in tweet [:asset (keyword k)])))
