(ns cheffy.recipe.handlers
  (:require [cheffy.recipe.db :as recipe-db]
            [ring.util.response :as rr]))

(defn list-all-recipes
  [db]
  (fn [request]
    (let [uid "auth0|5ef440986e8fbb001355fd9c"
          recipes (recipe-db/find-all-recipes db uid)]
      (rr/response recipes))))