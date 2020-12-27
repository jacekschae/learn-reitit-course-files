(ns cheffy.conversation.routes
  (:require [cheffy.middleware :as mw]))

(defn routes
  [env]
  (let [db (:jdbc-url env)]
    ["/conversation" {:swagger    {:tags ["conversations"]}
                      :middleware [[mw/wrap-auth0]]}
     [""
      {:get  {:handler   (fn [request] request)
              :responses {200 {:body vector?}}
              :summary   "List conversations"}
       :post {:handler    (fn [request] request)
              :parameters {:body {:message-body string? :to string?}}
              :responses  {201 {:body {:conversation-id string?}}}
              :summary    "Start a conversation"}}]
     ["/:conversation-id"
      {:get  {:handler    (fn [request] request)
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