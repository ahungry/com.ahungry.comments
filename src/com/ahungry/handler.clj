(ns com.ahungry.handler
  (:require
   [clojure.tools.logging :as log]
   [com.ahungry.dao :as dao]
   ))

(defn assert-password [{:keys [password1 password2]}]
  (when-not (= password1 password2)
    (throw (Throwable. "Passwords do not match."))))

(defn login [m]
  (log/debug m)
  (try
    (do
      (assert-password m)
      (dao/save m)
      {:body m})
    (catch Throwable e
      {:body {:error (.getMessage e)}
       :status 400})))
