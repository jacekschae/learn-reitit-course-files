(ns cheffy.conversation.db
  (:require [next.jdbc.sql :as sql]
            [next.jdbc :as jdbc]))

(defmulti dispatch (fn [[name _db _data]] name))

(defmethod dispatch :find-conversation-by-uid
  [[_ db {:keys [uid]}]]
  (with-open [conn (jdbc/get-connection db)]
    (let [conn-opts (jdbc/with-options conn (:options db))
          conversations (sql/find-by-keys conn-opts :conversation {:uid uid})]
      (doall
        (for [{:conversation/keys [conversation-id] :as conversation} conversations
              :let [{:message/keys [created-at]}
                    (jdbc/execute-one! conn-opts ["SELECT created_at FROM message
                                                   WHERE conversation_id = ?
                                                   ORDER BY created_at DESC
                                                   LIMIT 1" conversation-id])
                    with
                    (jdbc/execute-one! conn-opts ["SELECT uid FROM conversation
                                                   WHERE uid != ? AND conversation_id = ?" uid conversation-id])
                    [{:account/keys [name picture]}] (sql/find-by-keys conn-opts :account with)]]
          (assoc conversation
            :conversation/updated-at created-at
            :conversation/with-name name
            :conversation/with-picture picture))))))


(defmethod dispatch :find-messages-by-conversation
  [[_ db conversation]]
  (sql/find-by-keys db :message conversation))

(comment
  (dispatch [:find-conversation-by-uid {} {}]))