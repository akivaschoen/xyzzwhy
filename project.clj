(defproject xyzzwhy "2.0.0-SNAPSHOT"
  :description "A Twitter bot who takes you on surreal adventures."
  :url "https://github.com/akivaschoen/xyzzwhy"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  :min-lein-version "2.5.0"

  :dependencies
  [[org.clojure/clojure "1.7.0"]
   [environ "1.0.0"]
   [com.novemberain/monger "3.0.0-rc2"]
   [twitter-api "0.7.8"]
   [typographer "1.1.0"]]

  :plugins [[lein-autoexpect "1.6.0"]
            [lein-environ "1.0.0"]]

  :main xyzzwhy.core

  :target-path "target/%s"

  :profiles
  {:uberjar {:env {:production true}
             :omit-source true
             :aot :all}

   :prod [:prod-config :twitter
          {:env {:production true}
           :omit-source true
           :aot :all}]

   :dev [:dev-config :twitter
         {:env {:dev true}

          :dependencies
          [[expectations "2.0.16"]
           [leiningen "2.5.1"]]

          :jvm-opts ^:replace ["-XX:-OmitStackTraceInFastThrow"]}]})
