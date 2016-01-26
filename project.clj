(defproject xyzzwhy "1.0.0"
  :description "A Twitter bot who takes you on surreal adventures."
  :url "https://github.com/akivaschoen/xyzzwhy"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :min-lein-version "2.5.2"

  :dependencies
  [[org.clojure/clojure "1.8.0"]
   [clj-time "0.11.0"]
   [com.stuartsierra/component "0.3.0"]
   [org.clojure/core.async "0.1.346.0-17112a-alpha"]
   [environ "1.0.1"]
   [pluralex "1.0.0-SNAPSHOT"]
   [reloaded.repl "0.2.1"]
   [rethinkdb "0.11.0-SNAPSHOT"]
   [twitter-api "0.7.8"]
   [typographer "1.1.0"]]

  :plugins
  [[lein-autoexpect "1.6.0"]
   [lein-environ "1.0.1"]]

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
     :dependencies [[expectations "2.0.16"]
                    [leiningen "2.5.2"]]}]})
