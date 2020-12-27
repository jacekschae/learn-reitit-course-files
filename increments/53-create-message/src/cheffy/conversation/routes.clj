(ns cheffy.conversation.routes
  (:require [cheffy.middleware :as mw]
            [cheffy.conversation.db :as conversation-db]
            [ring.util.response :as rr]))

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
       :post {:handler    (fn [request] request)
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
       :post {:handler    (fn [request] request)
              :parameters {:path {:conversation-id string?}
                           :body {:message-body string? :to string?}}
              :responses  {201 {:body {:conversation-id string?}}}
              :summary    "Create message"}
       :put  {:handler    (fn [request] request)
              :parameters {:path {:conversation-id string?}}
              :responses  {204 {:body nil?}}
              :summary    "Update notifications"}}]]))