(defproject xyzzwhy "2.0.0-SNAPSHOT"
  :description "A Twitter bot who takes you on surreal adventures."
  :url "https://github.com/akivaschoen/xyzzwhy"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :min-lein-version "2.8.1"

  :dependencies [[org.clojure/clojure "1.9.0"]
                 [clj-time "0.14.2"]
                 [environ "1.1.0"]
                 [pluralex "1.0.0-SNAPSHOT"]
                 [twitter-api "1.8.0"]
                 [typographer "1.1.0"]]

  :plugins [[lein-environ "1.0.3"]]

  :main xyzzwhy.core

  :resource-paths ["resources"]
  :target-path "target/%s/"

  :profiles {:uberjar {:omit-source true
                       :aot :all}}

  :repl-options {:caught clj-stacktrace.repl/cst+})
