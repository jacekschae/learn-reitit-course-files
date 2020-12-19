(ns cheffy.account.db
  (:require [next.jdbc.sql :as sql]))

(defn create-account!
  [db account]
  (sql/insert! db :account account))

(defn delete-account!
  [db uid]
  (sql/delete! db :account uid))