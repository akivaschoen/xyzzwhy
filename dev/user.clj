(ns user
  (:require [reloaded.repl :refer [system start reset stop]]
            [clojure.tools.namespace.repl :refer [refresh]]
            [xyzzwhy.bot]))

(reloaded.repl/set-init! #'xyzzwhy.bot/xyzzwhy)
(reloaded.repl/init)
