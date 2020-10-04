(ns cheffy.auth0
  (:require [muuntaja.core :as m]
            [clj-http.client :as client]
            [environ.core :refer [env]]))

(defn decode-body
  [request]
  (m/decode "application/json" (:body request)))

; TODO: Hide client_id and client_secrets in config file and heroku config vars
(defn get-management-token
  []
  (let [options {:throw-exceptions false
                 :cookie-policy    :standard                ;https://github.com/dakrone/clj-http/issues/444
                 :content-type     :json
                 :body             (->> {:client_id     "62OtGnk7K6fKyuckHiJNO473GvkK6th0"
                                         :client_secret "cpdy4ENuEqSJI6OLYe1zy3FU9TQ5yD7vcLmOVAOPJ3587B2o1Bt--CpNPwkdepNN"
                                         :audience      "https://schae.auth0.com/api/v2/"
                                         :grant_type    "client_credentials"}
                                        (m/encode "application/json")
                                        slurp)}]
    (-> (client/post "https://schae.auth0.com/oauth/token" options)
        decode-body
        :access_token)))

(defn get-role-id
  []
  (let [options {:headers          {"Authorization" (str "Bearer " (get-management-token))}
                 :cookie-policy    :standard
                 :throw-exceptions false}]
    (-> (client/get "https://schae.auth0.com/api/v2/roles" options)
        decode-body
        first
        :id)))

; TODO: Hide client_id and client_secrets in config file and heroku config vars
(defn get-test-token
  []
  (let [options {:throw-exceptions false
                 :cookie-policy    :standard                ;https://github.com/dakrone/clj-http/issues/444
                 :content-type     :json
                 :body             (->> {:client_id     "JC95Nmw7rIVoMJKDA81gDenlBH2j1AoF"
                                         :client_secret "Z6MiL5wjcAxP9W8uy35pQvwTDFvY4i-76P_5x6-WZZaUpqPUsSynd51o_-vCEedz"
                                         :audience      "https://schae.auth0.com/api/v2/"
                                         :grant_type    "password"
                                         :username      "e2e-testing@mydomain.com"
                                         :password      "s#m3R4nd0m-pass"
                                         :scope         "openid profile email"}
                                        (m/encode "application/json")
                                        slurp)}]
    (-> (client/post "https://schae.auth0.com/oauth/token" options)
        (decode-body)
        :access_token)))

