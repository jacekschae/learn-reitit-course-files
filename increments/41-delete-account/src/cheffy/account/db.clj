(ns cheffy.account.db
  (:require [next.jdbc.sql :as sql]))

(defn create-account!
  [db account]
  (sql/insert! db :account account))
