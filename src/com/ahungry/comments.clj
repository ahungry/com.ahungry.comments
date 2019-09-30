(ns com.ahungry.comments
  (:require
   [clojure.tools.logging :as log]
   [compojure.core :refer [defroutes GET POST DELETE ANY OPTIONS context] :as cc]
   [cheshire.core :as cheshire]
   [ring.middleware.params :as rmp]
   [org.httpkit.server :as server]
   [com.ahungry.handler :as handler]
   [com.ahungry.dao :as dao]
   )
  (:gen-class))

(dao/make-db)
(def ^:dynamic *port* 3001)
(defn version [req] {:body "0.0.1"})

(defroutes all-routes
  (GET "/" [] (slurp "resources/index.html"))
  (GET "/comments.css" [] {:body (slurp "resources/comments.css") :headers {"Content-Type" "text/css"}})
  (GET "/comments.js" [] (slurp "resources/comments.js"))
  (GET "/version" [] version)
  (GET "/comments" req (handler/comments req))
  (POST "/login" req (handler/login (:body-params req)))
  (POST "/comment" req (handler/comment (:body-params req)))
  )

(defn wrap-headers [handler]
  (fn [req]
    (let [res (handler req)
          content-type (get-in res [:headers "Content-Type"])]
      (log/info res)
      (if content-type
        res
        (-> res
            (assoc-in [:headers "Content-Type"] "application/json")
            )))))

(defn wrap-json [handler]
  (fn [req]
    (let [res (handler req)
          content-type (get-in res [:headers "Content-Type"])]
      (if (= "application/json" content-type)
        (-> res
            (update-in [:body] cheshire/generate-string))
        res))))

(defn wrap-cors [handler]
  (fn [req]
    (let [res (handler req)]
      (-> res
          (assoc-in [:headers "Access-Control-Allow-Credentials"] "true")
          (assoc-in [:headers "Access-Control-Allow-Methods"] "GET,HEAD,OPTIONS,POST,PUT,PATCH")
          (assoc-in [:headers "Access-Control-Allow-Headers"] "Access-Control-Allow-Headers, Authorization, Origin, Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers")
          (assoc-in [:headers "Access-Control-Allow-Origin"] "*")))))

(defn keys->keyword-keys
  "When we have string keys, force them to be kw based."
  [m]
  (let [ks (keys m)
        vs (vals m)]
    (zipmap (map keyword ks) vs)))

(defn body->body-params [{:keys [body]}]
  (when body
    (-> body slurp cheshire/parse-string keys->keyword-keys)))

(defn wrap-body-params [handler]
  (fn [req]
    (-> req
        (assoc-in [:body-params] (body->body-params req))
        handler)))

(defn kw-params [{:keys [query-params]}]
  (when query-params
    (-> query-params keys->keyword-keys)))

(defn wrap-query-params-as-kws [handler]
  (fn [req]
    (-> req
        (assoc-in [:query-params] (kw-params req))
        handler)))

;; I thought this ran top down / threaded, but somehow rmp has to come after my own...?
(def app
  (cc/routes
   (-> all-routes
       (cc/wrap-routes #'wrap-query-params-as-kws)
       (cc/wrap-routes #'wrap-body-params)
       (cc/wrap-routes #'rmp/wrap-params)
       (cc/wrap-routes #'wrap-cors)
       (cc/wrap-routes #'wrap-headers)
       (cc/wrap-routes #'wrap-json))))

(defonce server (atom nil))

(defn stop []
  (when-not (nil? @server)
    ;; graceful shutdown: wait 100ms for existing requests to be finished
    ;; :timeout is optional, when no timeout, stop immediately
    ;; (@server :timeout 100)
    (log/info "Stopping server.")
    (@server)
    (reset! server nil)))

(defn start [& _]
  ;; The #' is useful when you want to hot-reload code
  ;; You may want to take a look: https://github.com/clojure/tools.namespace
  ;; and http://http-kit.org/migration.html#reload
  (log/info "Starting server.")
  (reset! server (server/run-server #'app {:port *port*})))

(defn restart []
  (log/info "Restarting server.")
  (stop)
  (Thread/sleep 1e3)
  (start))

(defn -main [& args]
  (prn "Begin")
  (start))
