(ns cheffy.auth0
  (:require [clj-http.client :as http]
            [muuntaja.core :as m]))

(defn get-test-token
  []
  (->> {:content-type :json
        :cookie-policy :standard
        :body (m/encode "application/json"
                {:client_id "ts5NfJYbsIZ6rvhmbKykF9TkWz0tKcGS"
                 :audience "https://learn-reitit-playground.eu.auth0.com/api/v2/"
                 :grant_type "password"
                 :username "testing@cheffy.app"
                 :password "s#m3R4nd0m-pass"
                 :scope "openid profile email"})}
    (http/post "https://learn-reitit-playground.eu.auth0.com/oauth/token")
    (m/decode-response-body)
    :access_token))

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

(comment
  (let [uid "auth0|5fbf7db6271d5e0076903601"
        token (get-management-token)]
      (->> {:headers          {"Authorization" (str "Bearer " token)}
            :cookie-policy    :standard
            :content-type     :json
            :throw-exceptions false
            #_#_:body             (m/encode "application/json"
                                    {:roles [(get-role-id token)]})}
        (http/get (str "https://learn-reitit-playground.eu.auth0.com/api/v2/users/" uid "/roles")))))