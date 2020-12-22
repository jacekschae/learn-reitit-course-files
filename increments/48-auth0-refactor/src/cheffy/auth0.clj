(ns cheffy.auth0
  (:require [clj-http.client :as http]
            [muuntaja.core :as m]))

(defn get-management-token
  []
  (->> {:throw-exceptions false
        :content-type :json
        :cookie-policy :standard
        :body (m/encode "application/json"
                {:client_id "0NLsiVfeEF2ZY0fstfzOk6K9AKZ1a5hP"
                 :client_secret "Pir0LuiCDE5Us-2pWo3ajk0C6LIndbcXJ1cEp96kMwVhkwurVbMlTa4I7z-jKLKB"
                 :audience "https://learn-reitit-playground.eu.auth0.com/api/v2/"
                 :grant_type "client_credentials"})}
    (http/post "https://learn-reitit-playground.eu.auth0.com/oauth/token")
    (m/decode-response-body)
    :access_token))

(defn get-role-id
  [token]
  (->> {:headers {"Authorization" (str "Bearer " token)}
        :throw-exceptions false
        :content-type :json
        :cookie-policy :standard}
    (http/get "https://learn-reitit-playground.eu.auth0.com/api/v2/roles")
    (m/decode-response-body)
    (filter (fn [role] (= (:name role) "manage-recipes")))
    (first)
    :id))