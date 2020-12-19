(ns cheffy.account.handlers
  (:require [ring.util.response :as rr]
            [cheffy.account.db :as account-db]
            [clj-http.client :as http]))

(defn create-account!
  [db]
  (fn [request]
    (let [{:keys [sub name picture]} (-> request :claims)]
      (account-db/create-account! db {:uid sub :name name :picture picture})
      (rr/status 204))))

(defn delete-account!
  [db]
  (fn [request]
    (let [uid (-> request :claims :sub)
          delete-auth0-account! (http/delete
                                  (str "https://learn-reitit-playground.auth0.com/api/v2/users/" uid)
                                  {:headers {"Authorization" (str "Bearer " (auth0/get-managemetn-toekn))}})]
      (when (= (:status delete-auth0-account!) 204)
        (account-db/delete-account! db {:uid uid})
        (rr/status 204)))))
