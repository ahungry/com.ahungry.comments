(ns com.ahungry.dao
  (:require
   [clojure.tools.logging :as log]
   ))

(defn save [x]
  x)

(def *comments (atom []))

(defn save-comment [m]
  (swap! *comments conj m))
