(ns cheffy.conversation.routes
  (:require [cheffy.middleware :as mw]
            [cheffy.conversation.db :as conversation-db]
            [ring.util.response :as rr]
            [cheffy.responses :as responses])
  (:import (java.util UUID)))

(defn routes
  [env]
  (let [db (:jdbc-url env)]
    ["/conversation" {:swagger    {:tags ["conversations"]}
                      :middleware [[mw/wrap-auth0]]}
     [""
      {:get  {:handler   (fn [request]
                           (let [uid (-> request :claims :sub)]
                             (rr/response
                               (conversation-db/dispatch [:find-conversation-by-uid db {:uid uid}]))))
              :responses {200 {:body vector?}}
              :summary   "List conversations"}
       :post {:handler    (fn [request]
                            (let [message (-> request :parameters :body)
                                  message-id (str (UUID/randomUUID))
                                  from (-> request :claims :sub)
                                  conversation-id (conversation-db/dispatch
                                                    [:start-conversation db (assoc message
                                                                              :message-id message-id
                                                                              :from from)])]
                              (rr/created
                                (str responses/base-url "/v1/conversations/" conversation-id)
                                {:conversation-id conversation-id})))
              :parameters {:body {:message-body string? :to string?}}
              :responses  {201 {:body {:conversation-id string?}}}
              :summary    "Start a conversation"}}]
     ["/:conversation-id"
      {:get  {:handler    (fn [request]
                            (let [conversation-id (-> request :parameters :path :conversation-id)]
                              (rr/response
                                (conversation-db/dispatch [:find-messages-by-conversation db {:converstaion-id conversation-id}]))))
              :parameters {:path {:conversation-id string?}}
              :responses  {200 {:body vector?}}
              :summary    "List conversation messages"}
       :post {:handler    (fn [request]
                            (let [conversation-id (-> request :parameters :path :conversation-id)
                                  message (-> request :parameters :body)
                                  message-id (str (UUID/randomUUID))
                                  from (-> request :claims :sub)]
                              (conversation-db/dispatch
                                [:insert-message db (assoc message
                                                      :message-id message-id
                                                      :conversation-id conversation-id
                                                      :from from)])
                              (rr/created
                                (str responses/base-url "/v1/conversations" conversation-id)
                                {:conversation-id conversation-id})))
              :parameters {:path {:conversation-id string?}
                           :body {:message-body string? :to string?}}
              :responses  {201 {:body {:conversation-id string?}}}
              :summary    "Create message"}
       :put  {:handler    (fn [request]
                            (let [uid (-> request :claims :uid)
                                  conversation-id (-> request :parameters :path :conversation-id)
                                  updated? (conversation-db/dispatch [:clear-notifications db {:uid uid :conversation-id conversation-id}])]
                              (when updated?
                                (rr/status 204))))
              :parameters {:path {:conversation-id string?}}
              :responses  {204 {:body nil?}}
              :summary    "Update notifications"}}]]))