(ns cheffy.conversation.handlers
  (:require [ring.util.response :as rr]
            [cheffy.conversation.db :as conversation-db]
            [ring.util.response :as rr]
            [cheffy.responses :as responses])
  (:import (java.util UUID)))

(defn list-conversations
  [db]
  (fn [request]
    (let [uid (-> request :claims :sub)
          conversations (conversation-db/find-conversations-by-uid db {:uid uid})]
      (rr/response conversations))))

(defn start-conversation!
  [db]
  (fn [request]
    (let [from (-> request :claims :sub)
          {:keys [to message-body]} (-> request :parameters :body)
          message-id (str (UUID/randomUUID))
          conversation-id (conversation-db/start-conversation! db {:message-id   message-id
                                                                   :from         from
                                                                   :message-body message-body
                                                                   :to           to})]
      (rr/created (str responses/base-url "/v1/conversations/" conversation-id) {:conversation-id conversation-id}))))

(defn list-conversation-messages
  [db]
  (fn [request]
    (let [uid (-> request :claims :sub)
          conversation-id (-> request :parameters :path :conversation-id)
          messages (conversation-db/find-messages-by-conversation db {:uid uid :conversation-id conversation-id})]
      (rr/response messages))))

(defn create-message!
  [db]
  (fn [request]
    (let [conversation-id (-> request :parameters :path :conversation-id)
          {:keys [message-body to]} (-> request :parameters :body)
          message-id (str (UUID/randomUUID))
          from (-> request :claims :sub)]
      (conversation-db/insert-message! db {:message-id      message-id
                                           :conversation-id conversation-id
                                           :from            from
                                           :message-body    message-body
                                           :to              to})
      (rr/created (str responses/base-url "/v1/conversations/" conversation-id) {:conversation-id conversation-id}))))

(defn update-notifications!
  [db]
  (fn [request]
    (let [uid (-> request :claims :sub)
          conversation-id (-> request :parameters :path :conversation-id)
          update! (conversation-db/clear-notifications! db {:conversation-id conversation-id :uid uid})]
      (when update!
        (rr/status 204)))))