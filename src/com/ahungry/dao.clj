(ns com.ahungry.dao
  (:require
   [clojure.tools.logging :as log]
   [markdown.transformers :as mt]
   [markdown.core :as md]
   ))

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
