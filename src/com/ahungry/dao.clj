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
  (str (xdg-rc/get-xdg-data-dir) "/com.ahungry.comments.db"))

(def db
  {:classname "org.sqlite.JDBC"
   :subprotocol "sqlite"
   :subname (data-dir-db)})

(defn wipe-db []
  (jdbc/execute! db ["DROP TABLE IF EXISTS user;"])
  (jdbc/execute! db ["DROP TABLE IF EXISTS comment;"]))

(defn make-db []
  (jdbc/execute! db ["
CREATE TABLE IF NOT EXISTS user (
  username PRIMARY KEY UNIQUE,
  password
);"])
  (jdbc/execute! db ["
CREATE TABLE IF NOT EXISTS comment (
  username NOT NULL,
  date NOT NULL,
  message NOT NULL,
  href NOT NULL,
  PRIMARY KEY (message, href)
);"]))

(defn reload-db []
  (wipe-db)
  (make-db))

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

(defn test-db []
  (q ["select * from user"]))

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

(defn get-users []
  (q ["SELECT * FROM user"]))

(defn get-comments []
  (q ["SELECT * FROM comment"]))

(defn get-user [username password]
  (q ["SELECT * from user WHERE username = ? AND password = ? " username password]))

(defn prettify [{:keys [message]}]
  (md/md-to-html-string
   message
   :replacement-transformers (into [escape-images escape-html] mt/transformer-vector)))

(defn save-user
  "Persist a user account into the database."
  [{:keys [username password1]}]
  (if (> (count (get-user username password1)) 0)
    ;; User already exists, so this is fine, treat it as a log in.
    :logged-in
    (try
      (do (jdbc/insert! db "user"
                        {:username username
                         :password password1})
          :created)
      (catch Exception e (log/error (str e)) false))))

(defn save-comment
  "Persist a user account into the database."
  [{:keys [username password message href]}]
  (try
    (do (jdbc/insert! db "comment"
                      {:username username
                       :message (prettify message)
                       :date (str (time-now))
                       :href href})
        :created)
    (catch Exception e (log/error (str e)) false)))
