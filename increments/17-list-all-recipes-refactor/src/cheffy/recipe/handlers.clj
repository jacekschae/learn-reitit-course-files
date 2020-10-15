(ns cheffy.recipe.handlers
  (:require [cheffy.recipe.db :as recipe-db]
            [ring.util.response :as rr]))

(defn list-all-recipes
  [db]
  (fn [request]
    (let [recipes (recipe-db/find-all-recipes db)]
      (rr/response recipes))))