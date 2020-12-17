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

(comment
  (get-test-token))