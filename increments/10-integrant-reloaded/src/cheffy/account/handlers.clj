(ns cheffy.account.handlers
  (:require [cheffy.account.db :as account-db]
            [cheffy.auth0 :as auth0]
            [ring.util.response :as rr]
            [clj-http.client :as client]
            [muuntaja.core :as m]
            [ring.util.codec :as codec]))

(defn create-account!
  [db]
  (fn [request]
    (let [{:keys [sub name picture]} (-> request :claims)
          create! (account-db/create-account! db {:uid sub :name name :picture picture})]
      (when create!
        (rr/status 204)))))

(defn update-role-to-cook!
  []
  (fn [request]
    (let [uid (-> request :claims :sub)
          encoded-uid (codec/url-encode uid)]
      (client/post (str "https://schae.auth0.com/api/v2/users/" encoded-uid "/roles")
                   {:headers          {"Authorization" (str "Bearer " (auth0/get-management-token))}
                    :cookie-policy    :standard
                    :throw-exceptions false
                    :content-type     :json
                    :body             (->> {:roles [(auth0/get-role-id)]}
                                           (m/encode "application/json")
                                           slurp)}))))

(defn delete-account!
  [db]
  (fn [request]
    (let [uid (-> request :claims :sub)
          encoded-uid (codec/url-encode uid)
          delete-auth0-account! (client/delete (str "https://schae.auth0.com/api/v2/users/" encoded-uid)
                                               {:headers {"Authorization" (str "Bearer " (auth0/get-management-token))}})]
      (when (= (:status delete-auth0-account!) 204)
        (account-db/delete-account! db uid)
        (rr/status 204)))))