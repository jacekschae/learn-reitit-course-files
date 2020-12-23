(ns cheffy.conversation.db
  (:require [next.jdbc.sql :as sql]
            [next.jdbc :as jdbc]))

(defmulti dispatch (fn [[name _db _data]] name))

(defmethod dispatch :find-conversation-by-uid
  [[_ db {:keys [uid]}]]
  (with-open [conn (jdbc/get-connection db)]
    (let [conversations (sql/find-by-keys conn :conversation {:uid uid})]
      (doall
        (for [{:conversation/keys [conversation-id] :as conversation} conversations
              :let [{:message/keys [created-at]}
                    (jdbc/execute-one! conn ["SELECT created_at FROM message
                                              WHERE conversation_id = ?
                                              ORDER BY created_at DESC
                                              LIMIT 1" conversation-id] (:options db))
                    with
                    (jdbc/execute-one! conn ["SELECT uid FROM conversation
                                              WHERE uid != ? AND conversation_ID = ?" uid conversation-id])
                    [{:account/keys [name picture]}] (sql/find-by-keys conn :account with)]]
          (assoc conversation
            :conversation/updated-at created-at
            :conversation/with-name name
            :conversation/with-picture picture))))))


(comment
  (dispatch [:find-conversation-by-uid {} {}]))