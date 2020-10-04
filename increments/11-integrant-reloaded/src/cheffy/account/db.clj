(ns cheffy.account.db
  (:require [next.jdbc.sql :as sql]))

(defn create-account!
  [db {:keys [uid name picture]}]
  (sql/insert! db :account {:uid uid :name name :picture picture}))

(defn delete-account!
  [db uid]
  (sql/delete! db :account {:uid uid}))