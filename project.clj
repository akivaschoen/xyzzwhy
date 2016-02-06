(defproject xyzzwhy "2.0.0-SNAPSHOT"
  :description "A Twitter bot who takes you on surreal adventures."
  :url "https://github.com/akivaschoen/xyzzwhy"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :min-lein-version "2.6.0"

  :dependencies
  [[org.clojure/clojure "1.8.0"]
   [clj-time "0.11.0"]
   [com.stuartsierra/component "0.3.1"]
   [org.clojure/core.async "0.2.374"]
   [environ "1.0.2"]
   [pluralex "1.0.0-SNAPSHOT"]
   [reloaded.repl "0.2.1"]
   [twitter-api "0.7.8"]
   [typographer "1.1.0"]
   [xyzzwhy/datastore "1.0.0-SNAPSHOT"]]

  :plugins
  [[lein-autoexpect "1.7.0"]
   [lein-environ "1.0.1"]
   [lein-expectations "0.0.8"]]

  :main xyzzwhy.bot

  :target-path "target/%s"

  :profiles
  {:uberjar
   [:prod-config
    {:env {:production true}
     :omit-source true
     :aot :all}]

   :dev
   [:dev-config
    :twitter
    {:env {:dev true}
     :source-paths ["dev"]
     :dependencies [[expectations "2.1.4"]
                    [leiningen "2.6.0"]]}]}

  :repl-options
  {:init-ns user
   :caught clj-stacktrace.repl/pst+})
