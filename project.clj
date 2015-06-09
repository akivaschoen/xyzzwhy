(defproject xyzzwhy "0.8.0-SNAPSHOT"
  :description "A Twitter bot who takes you on surreal adventures."
  :url "https://github.com/akivaschoen/xyzzwhy"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :min-lein-version "2.5.0"

  :dependencies
  [[org.clojure/clojure "1.7.0-RC1"]
   [environ "1.0.0"]
   [com.novemberain/monger "2.1.0"]
   [twitter-api "0.7.8"]
   [typographer "1.1.0"]]

  :plugins [[lein-environ "1.0.0"]]

  :main xyzzwhy.core

  :target-path "target/%s"

  :profiles
  {:uberjar {:aot :all}

   :dev [:database :twitter
         {:env {:dev true}
          :jvm-opts ^:replace ["-XX:-OmitStackTraceInFastThrow"]}]})
