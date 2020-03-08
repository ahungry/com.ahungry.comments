(defproject com.ahungry.comments "0.1.0-SNAPSHOT"
  :description "Comment system for sites."
  :url "https://github.com/ahungry/com.ahungry.comments"
  :license {:name "AGPLv3"
            :url ""}
  :dependencies [
                 ;; Lang
                 [org.clojure/clojure "1.10.0"]
                 [clojure.java-time "0.3.2"]

                 ;; Web related deps
                 [compojure "1.6.1"]
                 [cheshire "5.9.0"]
                 [http-kit "2.3.0"]
                 [ring "1.7.1"]

                 ;; Config
                 [ahungry/xdg-rc "0.0.4"]

                 ;; Database related
                 [org.clojure/java.jdbc "0.3.5"]
                 [org.xerial/sqlite-jdbc "3.7.2"]

                 ;; Crypto
                 [crypto-password "0.2.1"]

                 ;; Extras
                 [markdown-clj "1.10.0"]

                 ;; Logging related
                 [ch.qos.logback/logback-classic "1.2.3"]
                 [org.clojure/tools.logging "0.5.0"]
                 ]
  :main ^:skip-aot com.ahungry.comments
  :target-path "target/%s"
  :repl-options {:init-ns com.ahungry.comments}
  :jvm-opts [
             ;; Memory settings
             ;; "-Xms32M"
             ;; "-Xmx64M"
             "-Dfile.encoding=UTF8"]
  :profiles {:uberjar {:aot :all}})
