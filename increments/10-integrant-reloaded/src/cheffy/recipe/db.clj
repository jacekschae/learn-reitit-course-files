(ns cheffy.recipe.db
  (:require [next.jdbc.sql :as sql]
            [next.jdbc :as jdbc]))

(defn find-all-recipes
  [db uid]
  (with-open [conn (jdbc/get-connection db)]
    (let [public (sql/find-by-keys conn :recipe {:public true})]
      (if uid
        (let [drafts (sql/find-by-keys conn :recipe {:public false :uid uid})]
          {:drafts drafts
           :public public})
        {:public public}))))

(defn find-recipe-by-id
  [db {:keys [uid recipe-id]}]
  (with-open [conn (jdbc/get-connection db)]
    (let [[recipe] (if uid
                     (sql/find-by-keys conn :recipe {:recipe_id recipe-id :uid uid})
                     (sql/find-by-keys conn :recipe {:recipe_id recipe-id :public true}))
          steps (sql/find-by-keys conn :step {:recipe_id recipe-id})
          ingredients (sql/find-by-keys conn :ingredient {:recipe_id recipe-id})]
      (when (seq recipe)
        (assoc recipe
          :recipe/steps steps
          :recipe/ingredients ingredients)))))

(defn insert-recipe!
  [db {:keys [recipe-id uid name prep-time img]}]
  (sql/insert! db :recipe
               {:recipe_id      recipe-id
                :uid            uid
                :name           name
                :prep_time      prep-time
                :public         false
                :img            img
                :favorite_count 0}))

(defn update-recipe!
  [db {:keys [recipe-id name prep-time img public]}]
  (-> (sql/update! db :recipe
                   {:name name :prep_time prep-time :img img :public public}
                   {:recipe_id recipe-id})
      :next.jdbc/update-count
      (pos?)))

(defn delete-recipe!
  [db {:keys [recipe-id]}]
  (-> (sql/delete! db :recipe
                   {:recipe_id recipe-id})
      :next.jdbc/update-count
      (pos?)))

(defn insert-step!
  [db {:keys [step-id sort desc recipe-id]}]
  (sql/insert! db :step
               {:step_id step-id :recipe_id recipe-id :description desc :sort sort}))

(defn update-step!
  [db {:keys [step-id sort desc]}]
  (-> (sql/update! db :step
                   {:description desc :sort sort}
                   {:step_id step-id})
      :next.jdbc/update-count
      (pos?)))

(defn delete-step!
  [db {:keys [step-id]}]
  (-> (sql/delete! db :step
                   {:step_id step-id})
      :next.jdbc/update-count
      (pos?)))

(defn insert-ingredient!
  [db {:keys [recipe-id ingredient-id sort name amount measure]}]
  (sql/insert! db :ingredient
               {:recipe_id     recipe-id
                :ingredient_id ingredient-id
                :sort          sort
                :name          name
                :amount        amount
                :measure       measure}))

(defn update-ingredient!
  [db {:keys [ingredient-id sort name amount measure]}]
  (-> (sql/update! db :ingredient
                   {:sort sort :name name :amount amount :measure measure}
                   {:ingredient_id ingredient-id})
      :next.jdbc/update-count
      (pos?)))

(defn delete-ingredient!
  [db {:keys [ingredient-id]}]
  (-> (sql/delete! db :ingredient
                   {:ingredient_id ingredient-id})
      :next.jdbc/update-count
      (pos?)))

(defn favorite-recipe!
  [db {:keys [recipe-id uid]}]
  (jdbc/with-transaction
    [tx (jdbc/get-connection db)]
    (sql/insert! tx :recipe_favorite {:uid uid :recipe_id recipe-id})
    (jdbc/execute-one! tx ["UPDATE recipe
                            SET favorite_count = favorite_count + 1
                            WHERE recipe_id = ?" recipe-id])))

(defn unfavorite-recipe!
  [db {:keys [recipe-id uid]}]
  (jdbc/with-transaction
    [tx (jdbc/get-connection db)]
    (sql/delete! tx :recipe_favorite {:uid uid :recipe_id recipe-id})
    (jdbc/execute-one! tx ["UPDATE recipe
                            SET favorite_count = favorite_count - 1
                            WHERE recipe_id = ?" recipe-id])))