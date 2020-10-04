(ns cheffy.conversation.routes
  (:require [cheffy.conversation.handlers :as conversation]
            [cheffy.middleware :as mw]
            [cheffy.responses :as responses]))

(defn routes
  [env]
  (let [db (:db env)]
    ["/conversation" {:swagger    {:tags ["conversations"]}
                      :middleware [[mw/wrap-auth0]]}
     [""
      {:get  {:handler   (conversation/list-conversations db)
              :responses {200 {:body responses/conversation}}
              :summary   "List conversations"}
       :post {:handler    (conversation/start-conversation! db)
              :parameters {:body {:message-body string? :to string?}}
              :responses  {204 {:body nil?}}
              :summary    "Start a conversation"}}]
     ["/:conversation-id"
      {:get  {:handler    (conversation/list-conversation-messages db)
              :middleware [[mw/wrap-conversation-participant db]]
              :parameters {:path {:conversation-id string?}}
              :responses  {200 {:body responses/message}}
              :summary    "List conversation messages"}
       :post {:handler    (conversation/create-message! db)
              :middleware [[mw/wrap-conversation-participant db]]
              :parameters {:path {:conversation-id string?}
                           :body {:message-body string? :to string?}}
              :responses  {204 {:body nil?}}
              :summary    "Create message"}
       :put  {:handler    (conversation/update-notifications! db)
              :parameters {:path {:conversation-id string?}}
              :responses  {204 {:body nil?}}
              :summary    "Update notifications"}}]]))