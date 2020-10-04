(ns cheffy.recipe.handlers
  (:require [ring.util.response :as rr]
            [cheffy.recipe.db :as recipe-db]
            [cheffy.responses :as responses])
  (:import (java.util UUID)))

(defn list-all-recipes
  [db]
  (fn [request]
    (let [uid (-> request :claims :sub)
          recipes (recipe-db/find-all-recipes db uid)]
      (rr/response recipes))))

(defn create-recipe!
  [db]
  (fn [request]
    (let [recipe-id (str (UUID/randomUUID))
          uid (-> request :claims :sub)
          {:keys [prep-time name img]} (-> request :parameters :body)]
      (recipe-db/insert-recipe! db {:recipe-id recipe-id
                                    :uid       uid
                                    :prep-time prep-time
                                    :name      name
                                    :img       img})
      (rr/created (str responses/base-url "/recipes/" recipe-id) {:recipe-id recipe-id}))))

(defn retrieve-recipe
  [db]
  (fn [request]
    (let [uid (-> request :claims :sub)
          recipe-id (-> request :parameters :path :recipe-id)
          recipe (recipe-db/find-recipe-by-id db {:uid uid :recipe-id recipe-id})]
      (if recipe
        (rr/response recipe)
        (rr/not-found {:type    "recipe-not-found"
                       :message "Recipe not found"
                       :data    (str "recipe-id " recipe-id)})))))

(defn update-recipe!
  [db]
  (fn [request]
    (let [recipe-id (-> request :parameters :path :recipe-id)
          {:keys [img prep-time name public]} (-> request :parameters :body)
          update-successful? (recipe-db/update-recipe! db {:recipe-id recipe-id
                                                           :prep-time prep-time
                                                           :img       img
                                                           :name      name
                                                           :public    public})]
      (if update-successful?
        (rr/status 204)
        (rr/not-found {:recipe-id recipe-id})))))

(defn delete-recipe!
  [db]
  (fn [request]
    (let [recipe-id (-> request :parameters :path :recipe-id)
          delete! (recipe-db/delete-recipe! db {:recipe-id recipe-id})]
      (if delete!
        (rr/status 204)
        (rr/not-found {:recipe-id recipe-id})))))

(defn create-step!
  [db]
  (fn [request]
    (let [recipe-id (-> request :parameters :path :recipe-id)
          uid (-> request :claims :sub)
          {:keys [sort desc]} (-> request :parameters :body)
          step-id (str (UUID/randomUUID))]
      (recipe-db/insert-step! db {:uid       uid
                                  :recipe-id recipe-id
                                  :step-id   step-id
                                  :sort      sort
                                  :desc      desc})
      (rr/created
        (str responses/base-url "/recipes/" recipe-id "/steps/" step-id)
        {:step-id step-id}))))

(defn update-step!
  [db]
  (fn [request]
    (let [{:keys [step-id sort desc]} (-> request :parameters :body)
          update-successful? (recipe-db/update-step! db {:step-id step-id
                                                         :sort    sort
                                                         :desc    desc})]
      (if update-successful?
        (rr/status 204)
        (rr/bad-request {:step-id step-id})))))


(defn delete-step!
  [db]
  (fn [request]
    (let [step-id (-> request :parameters :body :step-id)]
      (recipe-db/delete-step! db {:step-id step-id})
      (rr/status 204))))

(defn create-ingredient!
  [db]
  (fn [request]
    (let [uid (-> request :claims :sub)
          recipe-id (-> request :parameters :path :recipe-id)
          {:keys [sort name amount measure]} (-> request :parameters :body)
          ingredient-id (str (UUID/randomUUID))]
      (recipe-db/insert-ingredient! db {:uid           uid
                                        :recipe-id     recipe-id
                                        :ingredient-id ingredient-id
                                        :sort          sort
                                        :name          name
                                        :amount        amount
                                        :measure       measure})
      (rr/created
        (str responses/base-url "/recipes/" recipe-id "/ingredients/" ingredient-id)
        {:ingredient-id ingredient-id}))))

(defn update-ingredient!
  [db]
  (fn [request]
    (let [uid (-> request :claims :sub)
          {:keys [ingredient-id sort name amount measure]} (-> request :parameters :body)
          update-successful? (recipe-db/update-ingredient! db {:uid           uid
                                                               :ingredient-id ingredient-id
                                                               :sort          sort
                                                               :name          name
                                                               :amount        amount
                                                               :measure       measure})]
      (if update-successful?
        (rr/status 204)
        (rr/bad-request {:ingredient-id ingredient-id})))))

(defn delete-ingredient!
  [db]
  (fn [request]
    (let [ingredient-id (-> request :parameters :body :ingredient-id)]
      (recipe-db/delete-ingredient! db {:ingredient-id ingredient-id})
      (rr/status 204))))

(defn favorite-recipe!
  [db]
  (fn [request]
    (let [uid (-> request :claims :sub)
          recipe-id (-> request :parameters :path :recipe-id)]
      (recipe-db/favorite-recipe! db {:uid uid :recipe-id recipe-id})
      (rr/status 204))))

(defn unfavorite-recipe!
  [db]
  (fn [request]
    (let [uid (-> request :claims :sub)
          recipe-id (-> request :parameters :path :recipe-id)]
      (recipe-db/unfavorite-recipe! db {:uid uid :recipe-id recipe-id})
      (rr/status 204))))
