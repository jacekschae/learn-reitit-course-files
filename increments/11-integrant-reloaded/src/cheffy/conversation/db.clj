(ns cheffy.conversation.db
  (:require [next.jdbc.sql :as sql]
            [next.jdbc :as jdbc])
  (:import (java.util UUID)))

(defn find-conversations-by-uid
  [db {:keys [uid]}]
  (with-open [conn (jdbc/get-connection db)]
    (let [conversations (sql/find-by-keys conn :conversation {:uid uid})]
      (doall
        (for [{:conversation/keys [conversation_id] :as conversation} conversations
              :let [[{:message/keys [created_at]}] (sql/query conn ["SELECT created_at FROM message
                                                                     WHERE conversation_id = ?
                                                                     ORDER BY created_at DESC
                                                                     LIMIT 1" conversation_id])
                    [with] (sql/query conn ["SELECT uid FROM conversation
                                             WHERE uid != ? AND conversation_id = ?" uid conversation_id])
                    [{:account/keys [name picture]}] (sql/find-by-keys conn :account {:uid (:conversation/uid with)})]]
          (assoc conversation :conversation/updated-at created_at
                       :conversation/with-name name
                       :conversation/with-picture picture))))))

(defn find-messages-by-conversation
  [db {:keys [conversation-id]}]
  (sql/find-by-keys db :message {:conversation_id conversation-id}))

(defn start-conversation!
  [db {:keys [message-id from message-body to]}]
  (with-open [conn (jdbc/get-connection db)]
    (let [[conversing] (sql/query conn ["SELECT a.conversation_id
                                         FROM conversation a
                                         JOIN conversation b ON a.conversation_id = b.conversation_id
                                         WHERE a.uid = ? and b.uid = ?" from to])]
      (if (seq conversing)
        (let [conversation-id (:conversation/conversation_id conversing)]
          (jdbc/with-transaction [tx conn]
                                 (sql/insert! tx :message {:message_id message-id :conversation_id conversation-id :uid from :message_body message-body})
                                 (jdbc/execute-one! tx ["UPDATE conversation
                                                         SET notifications = notifications + 1
                                                         WHERE conversation_id = ? AND uid = ?" conversation-id to]))
          conversation-id)
        (let [conversation-id (str (UUID/randomUUID))]
          (jdbc/with-transaction [tx conn]
                                 (sql/insert-multi! tx :conversation
                                                    [:notifications :uid :conversation_id]
                                                    [[1 to conversation-id]
                                                     [0 from conversation-id]])
                                 (sql/insert! tx :message {:message_id message-id :conversation_id conversation-id :uid from :message_body message-body}))
          conversation-id)))))

(defn insert-message!
  [db {:keys [message-id conversation-id from message-body to]}]
  (jdbc/with-transaction [tx (jdbc/get-connection db)]
                         (sql/insert! tx :message {:message_id message-id :conversation_id conversation-id :uid from :message_body message-body})
                         (jdbc/execute-one! tx ["UPDATE conversation
                                                 SET notifications = notifications + 1
                                                 WHERE conversation_id = ? AND uid = ?" conversation-id to])
                         conversation-id))

(defn clear-notifications!
  [db {:keys [uid conversation-id]}]
  (-> (sql/update! db :conversation
                   {:notifications 0}
                   {:conversation_id conversation-id :uid uid})
      :next.jdbc/update-count
      (pos?)))