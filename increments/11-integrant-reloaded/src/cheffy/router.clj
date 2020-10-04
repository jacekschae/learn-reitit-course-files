(ns cheffy.router
  (:require [reitit.ring :as ring]
            [reitit.swagger-ui :as swagger-ui]
            [reitit.swagger :as swagger]
            [reitit.ring.middleware.dev :as dev] ;used for pretty diffs
            [reitit.ring.middleware.parameters :as parameters]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.middleware.exception :as exception]
            [reitit.ring.coercion :as coercion]
            [reitit.ring.spec :as spec]
            [reitit.dev.pretty :as pretty]
            [spec-tools.spell :as spell]
            [muuntaja.core :as m]
            [cheffy.recipe.routes :as recipe]
            [cheffy.conversation.routes :as conversation]
            [cheffy.account.routes :as account]
            [reitit.coercion.spec :as coercion-spec]))

(def swagger
  ["/swagger.json"
   {:get
    {:no-doc  true
     :swagger {:basePath "/"
               :info     {:title       "Cheffy API Refernce"
                          :description "The Cheffy API is organized around REST. Returns JSON, Transit (msgpack, json), or EDN  encoded responses."
                          :version     "1.0.0"}}
     :handler (swagger/create-swagger-handler)}}])

(def router-config
  {:validate         spec/validate                          ; enable spec validation for route data
   ;:reitit.middleware/transform                             dev/print-request-diffs ; pretty diffs
   :reitit.spec/wrap spell/closed                           ; strict top-level validation
   :exception        pretty/exception                       ; pretty exceptions
   :data             {:coercion   coercion-spec/coercion    ; spec
                      :muuntaja   m/instance                ; default content negotiation
                      :middleware [swagger/swagger-feature  ; swagger-documentation for routes
                                   parameters/parameters-middleware ; query-params & form-params
                                   muuntaja/format-negotiate-middleware ; content-negotiation
                                   muuntaja/format-response-middleware ; encoding response body
                                   exception/exception-middleware ; handle exceptions
                                   muuntaja/format-request-middleware ; decoding request body
                                   coercion/coerce-response-middleware ; coercing request parameters
                                   coercion/coerce-request-middleware]}}) ; coercing request parameters

(defn routes
  [env]
  (ring/ring-handler
    (ring/router
      [swagger
       ["/v1"
        (recipe/routes env)
        (conversation/routes env)
        (account/routes env)]]
      router-config)
    (ring/routes
      (ring/redirect-trailing-slash-handler)
      (swagger-ui/create-swagger-ui-handler {:path "/"})
      (ring/create-default-handler))))