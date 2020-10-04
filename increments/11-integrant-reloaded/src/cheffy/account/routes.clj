(ns cheffy.account.routes
  (:require [cheffy.account.handlers :as account]
            [cheffy.middleware :as mw]))

(defn routes
  [env]
  (let [db (:db env)]
    ["/account" {:swagger    {:tags ["account"]}
                 :middleware [[mw/wrap-auth0]]}
     [""
      {:post   {:handler   (account/create-account! db)
                :responses {204 {:body nil?}}
                :summary   "Create account"}
       :patch  {:handler   (account/update-role-to-cook!)
                :responses {204 {:body nil?}}
                :summary   "Update account role to cook"}
       :delete {:handler   (account/delete-account! db)
                :responses {204 {:body nil?}}
                :summary   "Delete account"}}]]))
