(defproject xyzzwhy "2.0.0-SNAPSHOT"
  :description "A Twitter bot who takes you on surreal adventures."
  :url "https://github.com/akivaschoen/xyzzwhy"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :min-lein-version "2.6.1"

  :dependencies
  [[org.clojure/clojure "1.8.0"]
   [clj-time "0.13.0"]
   [com.stuartsierra/component "0.3.2"]
   [environ "1.1.0"]
   [pluralex "1.0.0-SNAPSHOT"]
   [reloaded.repl "0.2.3"]
   [twitter-api "0.7.9"]
   [typographer "1.1.0"]]

  :plugins
  [[lein-environ "1.0.3"]]

  :main xyzzwhy.bot

  :resource-paths ["resources"]
  :target-path "target/%s"

  :profiles
  {:uberjar
   {:env {:production "true"}
    :omit-source true
    :aot :all}

   :dev
   [:twitter
    {:env {:dev "true"}
     :source-paths ["dev"]}]}

  :repl-options
  {:caught clj-stacktrace.repl/pst+})
