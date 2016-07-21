(ns user)

(defn rl-bot [] (require '[xyzzwhy.bot :as bot] :reload))

(defn rl-cf [] (require '[xyzzwhy.engine.configuration :as cf] :reload))

(defn rl-en [] (require '[xyzzwhy.engine :as en] :reload))

(defn rl-fr [] (require '[xyzzwhy.engine.fragment :as fr] :reload))

(defn rl-in [] (require '[xyzzwhy.engine.interpolation :as in] :reload))

(defn rl-sb
  []
  (ns-unmap 'xyzzwhy.engine.substitution 'transclude)
  (ns-unmap 'xyzzwhy.engine.substitution 'sub)
  (ns-unmap 'xyzzwhy.engine.substitution 'follow-up*)
  (require '[xyzzwhy.engine.substitution :as sb] :reload))

(defn rl-util [] (require '[xyzzwhy.util :as util] :reload))

(defn init-repl
  []
  (rl-cf)
  (rl-en)
  (rl-fr)
  (rl-in)
  (rl-sb)
  (rl-util))
