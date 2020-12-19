(ns cheffy.account.routes
  (:require [cheffy.middleware :as mw]
            [cheffy.account.handlers :as account]))

(defn routes
  [env]
  (let [db (:jdbc-url env)]
    ["/account" {:swagger   {:tags ["account"]}
                 :middlware [[mw/wrap-auth0]]}
     [""
      {:post   {:handler   (account/create-account! db)
                :responses {204 {:body nil?}}
                :summary   "Create account"}
       :put    {:handler   (account/update-role-to-cook!)
                :responses {204 {:body nil?}}
                :summary   "Update user role to cook"}
       :delete {:handler   (account/delete-account! db)
                :responses {204 {:body nil?}}
                :summary   "Delete account"}}]]))