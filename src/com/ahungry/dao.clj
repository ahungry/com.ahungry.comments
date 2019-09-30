(ns com.ahungry.dao
  (:require
   [clojure.tools.logging :as log]
   [clojure.java.jdbc :as jdbc]
   [markdown.transformers :as mt]
   [markdown.core :as md]
   [java-time :as t]
   [xdg-rc.core :as xdg-rc]
   ))

(defn data-dir-db []
  (str (xdg-rc/get-xdg-data-dir) "/ahungry-determinism.db"))

(def db
  {:classname "org.sqlite.JDBC"
   :subprotocol "sqlite"
   :subname (data-dir-db)})

(defn make-db []
  (jdbc/execute! db ["CREATE TABLE IF NOT EXISTS det (
identity, input, input_types, output, output_type, date);"]))

(defn time-now []
  (str (t/local-date-time)))

(defn q
  "Wrapper for basic querying, with logging incorporated.
  SS = jdbc interface (vec: sql + args)."
  [ss]
  (let [result (apply jdbc/query db [ss])]
    (log/debug ss)
    (log/debug result)
    result))

(defn escape-images [text state]
  [(clojure.string/replace text #"(!\[.*?\]\()(.+?)(\))" "") state])

(defn escape-html
    "Change special characters into HTML character entities."
    [text state]
    [(if-not (or (:code state) (:codeblock state))
       (clojure.string/escape
         text
         {\& "&amp;"
          \< "&lt;"
          \> "&gt;"
          \" "&quot;"
          \' "&#39;"})
       text) state])

(defn save [x]
  x)

(def *comments (atom []))

(defn prettify [{:keys [message]}]
  (md/md-to-html-string
   message
   :replacement-transformers (into [escape-images escape-html] mt/transformer-vector)))

(defn save-comment [m]
  (let [html (prettify m)
        date (java.util.Date.)]
    (swap! *comments conj (conj m {:date date
                                   :message html}))))
