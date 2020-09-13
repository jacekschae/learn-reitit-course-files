(ns cheffy.recipes-tests
  (:require [clojure.test :refer :all]
            [cheffy.test-system :as ts]))

;(use-fixtures :once ts/system-running)

(def recipe-id (atom nil))

(def step-id (atom nil))

(def ingredient-id (atom nil))

(def recipe
  {:img       "https://images.unsplash.com/photo-1547516508-4c1f9c7c4ec3?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=3318&q=80"
   :prep-time 30
   :name      "My Test Recipe"})

(def update-recipe
  (assoc recipe
    :public true
    :img "https://res.cloudinary.com/schae/image/upload/f_auto,h_400,q_80/v1548183465/cheffy/recipes/pizza.jpg"))

(def delete-recipe
  (select-keys recipe [:recipe-id]))

(def step
  {:sort 1
   :desc "My Test Step Desc"})

(def ingredient
  {:sort    1
   :name    "My Test Ingredient"
   :amount  50
   :measure "My Test Measure"})

(deftest recipes-tests

  (testing "List recipes"
    (testing "without auth -- only public"
      (let [{:keys [status body]} (ts/test-endpoint :get "/v1/recipes"
                                                    {:auth false})]
        (is (= 200 status))
        (is (vector? (:public body)))
        (is (nil? (:drafts body)))))

    (testing "with auth -- public and drafts"
      (let [{:keys [status body]} (ts/test-endpoint :get "/v1/recipes"
                                                    {:auth true})]
        (is (= 200 status))
        (is (vector? (:public body)))
        (is (vector? (:drafts body)))))))

(deftest recipe-tests

  #_(testing "Become a cook"
      (testing "Update user role to cook"
        (let [{:keys [status body]} (ts/test-endpoint :patch "/v1/account" {:auth true})]
          (is (= 201 status)))))

  (testing "Create recipe"
    (let [{:keys [status body]} (ts/test-endpoint :post "/v1/recipes"
                                                  {:auth true :body recipe})]
      (reset! recipe-id (:recipe-id body))
      (is (= 201 status))))

  (testing "Retrieve author's draft recipe"
    (let [{:keys [status body]} (ts/test-endpoint :get (str "/v1/recipes/" @recipe-id)
                                                  {:auth true})]
      (is (= 200 status))
      (is (= (:recipe/name body) "My Test Recipe"))))

  (testing "Retrieve non-author's draft recipe"
    (let [{:keys [status]} (ts/test-endpoint :get (str "/v1/recipes/" @recipe-id)
                                             {:auth false})]
      (is (= 404 status))))

  (testing "Update author's recipe"
    (let [{:keys [status]} (ts/test-endpoint :put (str "/v1/recipes/" @recipe-id)
                                             {:auth true :body update-recipe})]
      (is (= 204 status))))

  (testing "Update non-author's recipe"
    (let [{:keys [status body]} (ts/test-endpoint :put (str "/v1/recipes/" @recipe-id)
                                                  {:auth false :body update-recipe})]
      (is (= 401 status))
      (is (= (:message body) "You need to be an owner of the recipe to perform this action"))))

  (testing "Create step"
    (let [{:keys [status body]} (ts/test-endpoint :post (str "/v1/recipes/" @recipe-id "/steps")
                                                  {:auth true :body step})]
      (reset! step-id (:step-id body))
      (is (= 201 status))))

  (testing "Update step"
    (let [{:keys [status]} (ts/test-endpoint :put (str "/v1/recipes/" @recipe-id "/steps")
                                             {:auth true :body {:step-id @step-id
                                                                :sort    2
                                                                :desc    "Updated desc"}})]
      (is (= 204 status))))

  (testing "Delete step"
    (let [{:keys [status]} (ts/test-endpoint :delete (str "/v1/recipes/" @recipe-id "/steps")
                                             {:auth true :body {:step-id @step-id}})]
      (is (= 204 status))))

  (testing "Create ingredient"
    (let [{:keys [status body]} (ts/test-endpoint :post (str "/v1/recipes/" @recipe-id "/ingredients")
                                                  {:auth true :body ingredient})]
      (reset! ingredient-id (:ingredient-id body))
      (is (= 201 status))))

  (testing "Update ingredient"
    (let [{:keys [status]} (ts/test-endpoint :put (str "/v1/recipes/" @recipe-id "/ingredients")
                                             {:auth true :body {:ingredient-id @ingredient-id
                                                                :sort          2
                                                                :name          "Updated name"
                                                                :amount        100
                                                                :measure       "Updated measure"}})]
      (is (= 204 status))))

  (testing "Delete ingredient"
    (let [{:keys [status]} (ts/test-endpoint :delete (str "/v1/recipes/" @recipe-id "/ingredients")
                                             {:auth true :body {:ingredient-id @ingredient-id}})]
      (is (= 204 status))))

  (testing "Favorite recipe"
    (let [{:keys [status body]} (ts/test-endpoint :post (str "/v1/recipes/" @recipe-id "/favorite")
                                                  {:auth true})]
      (is (= 204 status))))

  (testing "Unfavorite recipe"
    (let [{:keys [status]} (ts/test-endpoint :delete (str "/v1/recipes/" @recipe-id "/favorite")
                                             {:auth true})]
      (is (= 204 status))))

  (testing "Delete recipe"
    (let [{:keys [status]} (ts/test-endpoint :delete (str "/v1/recipes/" @recipe-id)
                                             {:auth true})]
      (is (= 204 status)))))

(comment
  (ts/test-endpoint :get "/v1/recipes" {:auth true})
  (ts/test-endpoint :get "/v1/recipes/recipe-12345" {:auth false})

  (ts/test-endpoint :post "/v1/recipes" {:auth true :body recipe})
  (ts/test-endpoint :delete "/v1/recipes/recipe-12345" {:auth true})

  (ts/test-endpoint :put "/v1/recipes/recipe-12345" {:auth true :body update-recipe})
  (ts/test-endpoint :post "/v1/recipes/recipe-12345/steps" {:auth true :body step})
  (ts/test-endpoint :post "/v1/account" {:auth true})

  (ts/test-endpoint :post "/v1/recipes/recipe-12345/favorite" {:auth true})
  (ts/test-endpoint :delete "/v1/recipes/recipe-12345/favorite" {:auth true})
  (deftest delete-recipe
    (testing "Delete recipe"
      (let [{:keys [status] :as response} (ts/test-endpoint :delete "/v1/recipes/recipe-12345" {:auth true})]
        (is (= 204 status)))))
  (go)
  (require '[shadow.cljs.devtools.server])
  (shadow.cljs.devtools.server/start!)
  (halt)
  (reset)
  (reset-all))

;; jetty - translate https requets text to jetty request
; -------
;; ring - translates jetty to clojure map
;; handler - clojure map return ring response map
;; ring - translates back
; -------
;; jetty - translates response to text