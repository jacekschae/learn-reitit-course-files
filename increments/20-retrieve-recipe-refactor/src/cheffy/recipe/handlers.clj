(ns cheffy.recipe.handlers
  (:require [cheffy.recipe.db :as recipe-db]
            [ring.util.response :as rr]))

(defn list-all-recipes
  [db]
  (fn [request]
    (let [uid "auth0|5ef440986e8fbb001355fd9c"
          recipes (recipe-db/find-all-recipes db uid)]
      (rr/response recipes))))

(defn retrieve-recipe
  [db]
  (fn [request]
    (clojure.pprint/pprint request)
    #_(let [recipe-id "a3dde84c-4a33-45aa-b0f3-4bf9ac997680"
            recipe (recipe-db/find-recipe-by-id db recipe-id)]
        (if recipe
          (rr/response recipe)
          (rr/not-found {:type "recipe-not-found"
                         :message "Recipe not found"
                         :data (str "recipe-id " recipe-id)})))))