(ns cheffy.server
  (:require [integrant.core :as ig]
            [ring.adapter.jetty :as jetty]
            [cheffy.router :as router]
            [environ.core :refer [env]]))

(defn app
  [env]
  (router/routes env))

(defmethod ig/prep-key :server/jetty [_ config]
  (merge config {:port (Integer/parseInt (env :port))}))

(defmethod ig/init-key :server/jetty [_ {:keys [handler port]}]
  (println (str "\nServer running on port " port "\n"))
  (jetty/run-jetty handler {:port port :join? false}))

(defmethod ig/init-key :cheffy/app [_ config]
  (println "\nStarted app")
  (app config))

(defmethod ig/init-key :db/postgres [_ config]
  (println (str "\nConfigured db"))
  (merge config {:db (env :jdbc-database-url)})
  (:db config))

(defmethod ig/init-key :auth0/manage-user [_ {:keys [auth0]}]
  (println "\nauht0/manage-user loaded\n")
  auth0)

(defmethod ig/halt-key! :server/jetty [_ jetty]
  (.stop jetty))

(defn -main
  [config-file]
  (let [config (-> config-file slurp ig/read-string)]
    (-> config ig/prep ig/init)))