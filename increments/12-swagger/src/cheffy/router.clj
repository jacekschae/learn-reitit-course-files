(ns cheffy.router
  (:require [reitit.ring :as ring]
            [cheffy.recipe.routes :as recipe]))

(defn routes
  [env]
  (ring/ring-handler
    (ring/router
      [["/v1"
        (recipe/routes env)]])))