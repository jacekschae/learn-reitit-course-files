(ns cheffy.recipe.routes
  (:require [cheffy.recipe.handlers :as recipe]
            [cheffy.responses :as responses]))

(defn routes
  [env]
  (let [db (:jdbc-url env)]
    ["/recipes" {:swagger {:tags ["recipes"]}}
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
      {:get {:handler    (recipe/retrieve-recipe db)
             :parameters {:path {:recipe-id string?}}
             :responses  {200 {:body responses/recipe}}
             :summary    "Retrieve recipe"}
       :put {:handler    (recipe/update-recipe! db)
             :parameters {:path {:recipe-id string?}
                          :body {:name string? :prep-time int? :public boolean? :img string?}}
             :responses  {204 {:body nil?}}
             :summary    "Update recipe"}}]]))