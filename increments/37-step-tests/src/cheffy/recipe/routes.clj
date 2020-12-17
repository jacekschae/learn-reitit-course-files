(ns cheffy.recipe.routes
  (:require [cheffy.recipe.handlers :as recipe]
            [cheffy.responses :as responses]
            [cheffy.middleware :as mw]))

(defn routes
  [env]
  (let [db (:jdbc-url env)]
    ["/recipes" {:swagger {:tags ["recipes"]}
                 :middleware [[mw/wrap-auth0]]}
     [""
      {:get  {:handler   (recipe/list-all-recipes db)
              :responses {200 {:body responses/recipes}}
              :summary   "List all recipes"}
       :post {:handler    (recipe/create-recipe! db)
              :parameters {:body {:name      string?
                                  :prep-time number?
                                  :img       string?}}
              :responses  {201 {:body {:recipe-id string?}}}
              :summary    "Create recipe"}}]
     ["/:recipe-id"
      [""
       {:get    {:handler    (recipe/retrieve-recipe db)
                 :parameters {:path {:recipe-id string?}}
                 :responses  {200 {:body responses/recipe}}
                 :summary    "Retrieve recipe"}
        :put    {:handler    (recipe/update-recipe! db)
                 :middleware [[mw/wrap-recipe-owner db]]
                 :parameters {:path {:recipe-id string?}
                              :body {:name string? :prep-time number? :public boolean? :img string?}}
                 :responses  {204 {:body nil?}}
                 :summary    "Update recipe"}
        :delete {:handler    (recipe/delete-recipe! db)
                 :middleware [[mw/wrap-recipe-owner db]]
                 :parameters {:path {:recipe-id string?}}
                 :responses  {204 {:body nil?}}
                 :summary    "Delete recipe"}}]
      ["/steps"
       {:post    {:handler    (recipe/create-step! db)
                  :parameters {:path {:recipe-id string?}
                               :body {:description string? :sort number?}}
                  :responses  {201 {:body {:step-id string?}}}
                  :summary    "Create step"}
        :put    {:handler    (recipe/update-step! db)
                 :middleware [[mw/wrap-recipe-owner db]]
                 :parameters {:path {:recipe-id string?}
                              :body {:step-id string? :description string? :sort int?}}
                 :responses  {204 {:body nil?}}
                 :summary    "Update step"}
        :delete {:handler    (recipe/delete-step! db)
                 :middleware [[mw/wrap-recipe-owner db]]
                 :parameters {:path {:recipe-id string?}
                              :body {:step-id string?}}
                 :responses  {204 {:body nil?}}
                 :summary    "Delete step"}}]
      ["/favorite"
       {:post    {:handler    (recipe/favorite-recipe! db)
                  :parameters {:path {:recipe-id string?}}
                  :responses  {204 {:body nil?}}
                  :summary    "Favorite recipe"}
        :delete {:handler    (recipe/unfavorite-recipe! db)
                 :parameters {:path {:recipe-id string?}}
                 :responses  {204 {:body nil?}}
                 :summary    "Unfavorite recipe"}}]]]))