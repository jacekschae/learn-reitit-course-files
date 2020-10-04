(ns cheffy.middleware
  (:require [ring.middleware.jwt :as jwt]
            [ring.util.response :as rr]
            [cheffy.recipe.db :as recipe-db]
            [next.jdbc.sql :as sql]))

(def wrap-auth0
  {:name        ::auth0
   :description "Middleware for auth0 authentication and authorization"
   :wrap        (fn [handler]
                  (jwt/wrap-jwt
                    handler
                    {:alg          :RS256
                     :jwk-endpoint "https://schae.auth0.com/.well-known/jwks.json"}))})

(def wrap-cook
  {:name        ::cook?
   :description "Middleware to check if a user can manage-recipes"
   :wrap        (fn [handler]
                  (fn [request]
                    (let [roles (get-in request [:claims "https://api.learnreitit.com/roles"])]
                      (if (some #{"manage-recipes"} roles)
                        (handler request)
                        (-> (rr/response {:message "You need to be a cook to perform this action"
                                          :data    (:uri request)
                                          :type    :authorization-required})
                            (rr/status 401))))))})

(def wrap-recipe-owner
  {:name        ::recipe-owner?
   :description "Middleware to check if a requestor is a recipe owner"
   :wrap        (fn [handler db]
                  (fn [request]
                    (let [uid (-> request :claims :sub)
                          recipe-id (-> request :parameters :path :recipe-id)
                          recipe (recipe-db/find-recipe-by-id db {:uid uid :recipe-id recipe-id})]
                      (if (= (:recipe/uid recipe) uid)
                        (handler request)
                        (-> (rr/response {:message "You need to be an owner of the recipe to perform this action"
                                          :data    (str "recipe-id " recipe-id)
                                          :type    :authorization-required})
                            (rr/status 401))))))})

(def wrap-conversation-participant
  {:name        ::conversation-participant?
   :description "Middleware to check if a requestor is an conversation participant"
   :wrap        (fn [handler db]
                  (fn [request]
                    (let [uid (-> request :claims :sub)
                          conversation-id (-> request :parameters :path :conversation-id)
                          conversation (sql/find-by-keys db :conversation {:uid uid :conversation_id conversation-id})]
                      (if (seq conversation)
                        (handler request)
                        (-> (rr/response {:message "You need to be a participant of the conversation to perform this action"
                                          :data    (str "conversation-id " conversation-id)
                                          :type    :authorization-required})
                            (rr/status 401))))))})