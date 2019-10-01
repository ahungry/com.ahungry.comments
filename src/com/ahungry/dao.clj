(ns com.ahungry.dao
  (:require
   [clojure.tools.logging :as log]
   [clojure.java.jdbc :as jdbc]
   [markdown.transformers :as mt]
   [markdown.core :as md]
   [java-time :as t]
   [xdg-rc.core :as xdg-rc]
   [crypto.password.pbkdf2 :as password]
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

(defn get-comments [href]
  (q ["SELECT * FROM comment WHERE href = ?" href]))

(defn get-user [username]
  (q ["SELECT * from user WHERE username = ? " username]))

(defn assert-password [{:keys [password]} plaintext-password]
  (when-not (password/check plaintext-password password)
    (throw (Exception. "Invalid password!"))))

(defn prettify [{:keys [message]}]
  (md/md-to-html-string
   message
   :replacement-transformers (into [escape-images escape-html] mt/transformer-vector)))

(defn save-user
  "Persist a user account into the database."
  [{:keys [username password1]}]
  (let [maybe-user (get-user username)]
    (if (> (count maybe-user) 0)
      ;; User already exists, so this is fine, treat it as a log in.
      (do
        (assert-password (first maybe-user) password1)
        (first maybe-user))
      (try
        (do (jdbc/insert! db "user"
                          {:username username
                           :password (password/encrypt password1)})
            (first (get-user username)))
        (catch Exception e (log/error (str e))
               {:error "Bad username / password, or that account already exists."
                :debug (str e)})))))

(defn save-comment
  "Persist a user account into the database."
  [{:keys [username password message href] :as m}]
  (try
    (do (jdbc/insert! db "comment"
                      {:username username
                       :message (prettify m)
                       :date (str (time-now))
                       :href href})
        (get-comments href))
    (catch Exception e (log/error (str e))
           {:error "Bad comment data, or that comment already exists."
            :debug (str e)})))
