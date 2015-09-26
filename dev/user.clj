(ns user
  (:require [reloaded.repl :refer [system reset stop]]
            [xyzzwhy.bot]))

(reloaded.repl/set-init! #'xyzzwhy.bot/create-system)
