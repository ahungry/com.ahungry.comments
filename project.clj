(defproject com.ahungry.comments "0.1.0-SNAPSHOT"
  :description "Comment system for sites."
  :url "https://github.com/ahungry/com.ahungry.comments"
  :license {:name "AGPLv3"
            :url ""}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 ;; Web related deps
                 [compojure "1.6.1"]
                 [cheshire "5.9.0"]
                 [http-kit "2.3.0"]
                 [ring "1.7.1"]
                 ;; Logging related
                 [ch.qos.logback/logback-classic "1.2.3"]
                 [org.clojure/tools.logging "0.5.0"]
                 ]
  :main ^:skip-aot com.ahungry.comments
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
