(ns cheffy.recipes-tests
  (:require [clojure.test :refer :all]
            [cheffy.server :refer :all]
            [cheffy.test-system :as ts]))

(use-fixtures :once ts/token-fixture)

(def recipe-id (atom nil))

(def step-id (atom nil))

(def ingredient-id (atom nil))

(def recipe
  {:img       "https://images.unsplash.com/photo-1547516508-4c1f9c7c4ec3?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=3318&q=80"
   :prep-time 30
   :name      "My Test Recipe"})

(def step
  {:description "My Test Step"
   :sort 1})

(def ingredient
  {:amount 2
   :measure "30 grams"
   :sort 1
   :name "My test ingredient"})

(def update-recipe
  (assoc recipe :public true))

(deftest recipes-tests
  (testing "List recipes"
    (testing "with auth -- public and drafts"
      (let [{:keys [status body]} (ts/test-endpoint :get "/v1/recipes" {:auth true})]
        (is (= 200 status))
        (is (vector? (:public body)))
        (is (vector? (:drafts body)))))

    (testing "without auth -- pubic"
      (let [{:keys [status body]} (ts/test-endpoint :get "/v1/recipes" {:auth false})]
        (is (= 200 status))
        (is (vector? (:public body)))
        (is (nil? (:drafts body)))))))

(deftest recipe-tests
  (testing "Create recipe"
    (let [{:keys [status body]} (ts/test-endpoint :post "/v1/recipes" {:auth true :body recipe})]
      (reset! recipe-id (:recipe-id body))
      (is (= status 201))))

  (testing "Update recipe"
    (let [{:keys [status]} (ts/test-endpoint :put (str "/v1/recipes/" @recipe-id) {:auth true :body update-recipe})]
      (is (= status 204))))

  (testing "Favorite recipe"
    (let [{:keys [status]} (ts/test-endpoint :post (str "/v1/recipes/" @recipe-id "/favorite") {:auth true})]
      (is (= status 204))))

  (testing "Unfavorite recipe"
    (let [{:keys [status]} (ts/test-endpoint :delete (str "/v1/recipes/" @recipe-id "/favorite") {:auth true})]
      (is (= status 204))))

  (testing "Create step"
    (let [{:keys [status body]} (ts/test-endpoint :post (str "/v1/recipes/" @recipe-id "/steps")
                                  {:auth true :body step})]
      (reset! step-id (:step-id body))
      (is (= status 201))))

  (testing "Update step"
    (let [{:keys [status]} (ts/test-endpoint :put (str "/v1/recipes/" @recipe-id "/steps")
                             {:auth true :body {:step-id @step-id
                                                :sort 2
                                                :description "Updated step"}})]
      (is (= status 204))))

  (testing "Delete step"
    (let [{:keys [status]} (ts/test-endpoint :delete (str "/v1/recipes/" @recipe-id "/steps")
                             {:auth true :body {:step-id @step-id}})]
      (is (= status 204))))

  (testing "Create ingredient"
    (let [{:keys [status body]} (ts/test-endpoint :post (str "/v1/recipes/" @recipe-id "/ingredients")
                                  {:auth true :body ingredient})]
      (reset! ingredient-id (:ingredient-id body))
      (is (= status 201))))

  (testing "Update ingredient"
    (let [{:keys [status]} (ts/test-endpoint :put (str "/v1/recipes/" @recipe-id "/ingredients")
                             {:auth true :body {:ingredient-id @ingredient-id
                                                :name "My updated name"
                                                :amount 5
                                                :measure "50 grams"
                                                :sort 3}})]
      (is (= status 204))))

  (testing "Delete ingredient"
    (let [{:keys [status]} (ts/test-endpoint :delete (str "/v1/recipes/" @recipe-id "/ingredients")
                             {:auth true :body {:ingredient-id @ingredient-id}})]
      (is (= status 204))))

  (testing "Delete recipe"
    (let [{:keys [status]} (ts/test-endpoint :delete (str "/v1/recipes/" @recipe-id) {:auth true})]
      (is (= status 204)))))

(comment

  (ts/test-endpoint :get "/v1/recipes/50ae22fd-1a81-4cb8-8d4d-4e9646c14155" {:auth true})
  (ts/test-endpoint :post "/v1/recipes/2ebf903e-56a6-44d0-96da-aaabdaa56686/favorite" {:auth true})
  (ts/test-endpoint :delete "/v1/recipes/be49e960-f5da-4a2e-8375-448901401ce7" {:auth true}))