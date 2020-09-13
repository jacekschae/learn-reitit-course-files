(ns cheffy.test-system
  (:require [clojure.test :refer :all]
            [integrant.core :as ig]
            [muuntaja.core :as m]
            [integrant.repl.state :as state]
            [ring.mock.request :as mock]
            [cheffy.auth0 :as auth0]))

(defonce system (atom nil))

(defn halt
  []
  (swap! system #(when % (ig/halt! %))))

(defn config
  []
  (-> "dev/resources/test.edn"
      slurp
      ig/read-string))

(defn go
  []
  (halt)
  (reset! system (ig/init (config))))

(defn system-running
  [f]
  (try
    (go)
    (f)
    (finally
      (halt))))

(defn port [] (-> @system :server/jetty .getConnectors first .getPort))
(defn app [] (-> @system :cheffy/app))
(defn db [] (-> @system :db/postgres))

(defn test-endpoint
  ([method uri]
   (test-endpoint method uri nil))
  ([method uri request]
   (let [app (-> state/system :cheffy/app)
         request (app (-> (mock/request method uri)
                          (cond-> (:auth request) (mock/header :authorization (str "Bearer " (auth0/get-test-token)))
                                  (:body request) (mock/json-body (:body request)))))]
     (if (instance? java.io.InputStream (:body request))
       (update request :body (partial m/decode "application/json"))
       request))))

(comment
  (app (mock/request :get "http://localhost:3000/v1/recipes")))
