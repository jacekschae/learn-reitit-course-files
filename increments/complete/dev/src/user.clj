(ns user
  (:require [integrant.repl :as ig-repl]
            [integrant.core :as ig]
            [integrant.repl.state :as state]
            [cheffy.server]
            [next.jdbc :as jdbc]
            [next.jdbc.sql :as sql]
            [clj-http.client :as http]
            [muuntaja.core :as m]))

(ig-repl/set-prep!
  (fn [] (-> "dev/resources/config.edn" slurp ig/read-string)))

(def go ig-repl/go)
(def halt ig-repl/halt)
(def reset ig-repl/reset)
(def reset-all ig-repl/reset-all)

(def app (-> state/system :cheffy/app))
(def db (-> state/system :db/postgres))

(comment
  (set! *print-namespace-maps* false)

  (go)
  (halt)
  (reset)


  (with-open [conn (jdbc/get-connection db)]
    (let [conn-opts (jdbc/with-options conn (:options db))
          conversations (sql/find-by-keys conn-opts :conversation {:uid "auth0|5fe31324a2914d006f574ceb"})]
      (mapv #(assoc % :conversation/updated-at (:message/created_at
                                                 (jdbc/execute-one!
                                                   conn-opts
                                                   [(str "SELECT created_at"
                                                      "  FROM message"
                                                      " WHERE conversation_id = ?"
                                                      " ORDER BY created_at DESC"
                                                      " LIMIT 1")
                                                    (:conversation/conversation_id %)])))
        (jdbc/execute! conn-opts
          [(str "SELECT c.*"
             "     , a.name AS with_name"
             "     , a.picture AS with_picture"
             "  FROM conversation c"
             "  JOIN conversation other"
             "    ON other.conversation_id = c.conversation_id"
             "   AND other.uid <> c.uid"
             "  JOIN account a"
             "    ON a.uid = other.uid"
             " WHERE c.uid = ?")
           "auth0|5fe31324a2914d006f574ceb"]))

      #_(doall
          (for [{:conversation/keys [conversation-id] :as conversation} conversations
                :let [{:message/keys [created-at]}
                      (jdbc/execute-one! conn-opts ["SELECT created_at FROM message
                                              WHERE conversation_id = ?
                                              ORDER BY created_at DESC
                                              LIMIT 1" conversation-id])
                      with
                      (jdbc/execute-one! conn-opts ["SELECT uid FROM conversation
                                              WHERE uid != ? AND conversation_id = ?" "auth0|5fe31324a2914d006f574ceb" conversation-id])
                      [{:account/keys [name picture]}] (sql/find-by-keys conn-opts :account with)]]
            (assoc conversation
              :conversation/updated-at created-at
              :conversation/with-name name
              :conversation/with-picture picture))))))