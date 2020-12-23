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
  (fn [] (-> "resources/config.edn" slurp ig/read-string)))

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
    (let [conversations (sql/find-by-keys conn :conversation {:uid "auth0|5fe31324a2914d006f574ceb"})]
      (doall
        (for [{:conversation/keys [conversation-id] :as conversation} conversations
              :let [{:message/keys [created-at]}
                    (jdbc/execute-one! conn ["SELECT created_at FROM message
                                              WHERE conversation_id = ?
                                              ORDER BY created_at DESC
                                              LIMIT 1" "8d4ab926-d5cc-483d-9af0-19627ed468eb"] (:options db))
                    with
                    (jdbc/execute-one! conn ["SELECT uid FROM conversation
                                              WHERE uid != ? AND conversation_ID = ?" "auth0|5fe31324a2914d006f574ceb" "8d4ab926-d5cc-483d-9af0-19627ed468eb"])
                    [{:account/keys [name picture]}] (sql/find-by-keys conn :account with)]]
          (assoc conversation
            :conversation/updated-at created-at
            :conversation/with-name name
            :conversation/with-picture picture))))))