(ns com.ahungry.handler
  (:require
   [clojure.tools.logging :as log]
   [com.ahungry.dao :as dao]
   ))

(defmacro with-json
  "Execute some body content with tidy json handling/wrapper around it."
  [i & r]
  `(try
     (log/debug "Input: " ~i)
     (let [res# ~@r]
       (log/debug "Output: " res#)
       {:body res#})
     (catch Throwable e#
       (log/error "Input: " ~i "\nError: " (.getMessage e#))
       {:body {:error (.getMessage e#)}
        :status 400})))

(defn assert-password [{:keys [password1 password2]}]
  (when-not (= password1 password2)
    (throw (Throwable. "Passwords do not match."))))

(defn login [m]
  (with-json m
    (do
      (assert-password m)
      (dao/save m))))

(defn assert-comment [_]
  true)

(defn comment [m]
  (with-json m
    (do
      (assert-comment m)
      (dao/save m))))
