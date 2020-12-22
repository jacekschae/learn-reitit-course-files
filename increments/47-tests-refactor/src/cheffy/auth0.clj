(ns cheffy.auth0
  (:require [clj-http.client :as http]
            [muuntaja.core :as m]))

(defn get-test-token
  [email]
  (->> {:content-type :json
        :cookie-policy :standard
        :body (m/encode "application/json"
                {:client_id "ts5NfJYbsIZ6rvhmbKykF9TkWz0tKcGS"
                 :audience "https://learn-reitit-playground.eu.auth0.com/api/v2/"
                 :grant_type "password"
                 :username email
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

(defn create-auth0-user
  [{:keys [connection email password]}]
  (->> {:headers {"Authorization" (str "Bearer " (get-management-token))}
        :throw-exceptions false
        :content-type :json
        :cookie-policy :standard
        :body (m/encode "application/json"
                {:connection connection
                 :email email
                 :password password})}
    (http/post "https://learn-reitit-playground.eu.auth0.com/api/v2/users")
    (m/decode-response-body)))

(comment
  (create-auth0-user
    {:connection "Username-Password-Authentication"
     :email "account-tests@cheffy.app"
     :password "s#m3R4nd0m-pass"})
  (http/delete "https://learn-reitit-playground.eu.auth0.com/api/v2/users/5fe110f779ac79006fa4efba")
  (->> {:headers {"Authorization" (str "Bearer " (get-management-token))}
        :throw-exceptions false
        :content-type :json
        :cookie-policy :standard}
    (http/delete "https://learn-reitit-playground.eu.auth0.com/api/v2/users/auth0|5fe110f779ac79006fa4efba"))
  (let [uid "auth0|5fbf7db6271d5e0076903601"
        token (get-management-token)]
      (->> {:headers          {"Authorization" (str "Bearer " token)}
            :cookie-policy    :standard
            :content-type     :json
            :throw-exceptions false
            #_#_:body             (m/encode "application/json"
                                    {:roles [(get-role-id token)]})}
        (http/get (str "https://learn-reitit-playground.eu.auth0.com/api/v2/users/" uid "/roles")))))