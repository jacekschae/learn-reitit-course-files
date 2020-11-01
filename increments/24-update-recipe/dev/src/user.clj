(ns user
  (:require [integrant.repl :as ig-repl]
            [integrant.core :as ig]
            [integrant.repl.state :as state]
            [cheffy.server]
            [next.jdbc :as jdbc]
            [next.jdbc.sql :as sql]))

(ig-repl/set-prep!
  (fn [] (-> "resources/config.edn" slurp ig/read-string)))

(def go ig-repl/go)
(def halt ig-repl/halt)
(def reset ig-repl/reset)
(def reset-all ig-repl/reset-all)

(def app (-> state/system :cheffy/app))
(def db (-> state/system :db/postgres))

(comment

  (-> (app {:request-method :get
            :uri "/v1/recipes/1234-recipe"})
    :body
    (slurp))

  (-> (app {:request-method :post
            :uri "/v1/recipes"
            :body-params {:name "my recipe"
                          :prep-time 49
                          :img "image-url"}})
    :body
    (slurp))

  (go)
  (halt)
  (reset))