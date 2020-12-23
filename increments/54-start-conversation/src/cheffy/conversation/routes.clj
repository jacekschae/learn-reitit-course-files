(ns cheffy.conversation.routes
  (:require [cheffy.middleware :as mw]
            [ring.util.response :as rr]
            [cheffy.conversation.db :as conversation-db]
            [cheffy.responses :as responses])
  (:import (java.util UUID)))

(defn routes
  [env]
  (let [db (:db env)]
    ["/conversation" {:swagger    {:tags ["conversations"]}
                      :middleware [[mw/wrap-auth0]]}
     [""
      {:get  {:handler   (fn [request]
                           (let [uid (-> request :claims :sub)]
                             (rr/response
                               (conversation-db/dispatch [:find-conversation-by-uid db {:uid uid}]))))
              :responses {200 {:body seq?}}
              :summary   "List conversations"}
       :post {:handler    (fn [request] request)
              :parameters {:body {:message-body string? :to string?}}
              :responses  {201 {:body nil?}}
              :summary    "Start a conversation"}}]
     ["/:conversation-id"
      {:get  {:handler    (fn [request]
                            (let [converstaion-id (-> request :parameters :path :conversation-id)]
                              (rr/response
                                (conversation-db/dispatch [:find-messages-by-conversation db {:conversation-id converstaion-id}]))))
              :parameters {:path {:conversation-id string?}}
              :responses  {200 {:body vector?}}
              :summary    "List conversation messages"}
       :post {:handler    (fn [request]
                            (let [conversation-id (-> request :parameters :path :conversation-id)
                                  message (-> request :parameters :body)
                                  message-id (str (UUID/randomUUID))
                                  from (-> request :claims :sub)]
                              (conversation-db/dispatch [:insert-message db (assoc message
                                                                              :message-id message-id
                                                                              :conversation-id conversation-id
                                                                              :from from)])
                              (rr/created (str responses/base-url "v1/convesations/" conversation-id) {:conversation-id conversation-id})))
              :parameters {:path {:conversation-id string?}
                           :body {:message-body string? :to string?}}
              :responses  {201 {:body nil?}}
              :summary    "Create message"}
       :put  {:handler    (fn [request] request)
              :parameters {:path {:conversation-id string?}}
              :responses  {204 {:body nil?}}
              :summary    "Update notifications"}}]]))