(ns cheffy.account-tests
  (:require [clojure.test :refer :all]
            [cheffy.test-system :as ts]))

(deftest account-tests

  (testing "Create user account"
    (let [{:keys [status]} (ts/test-endpoint :post "/v1/account" {:auth true})]
      (is (= 201 status))))

  (testing "Update user role to cook"
    (let [{:keys [status]} (ts/test-endpoint :patch "/v1/account" {:auth true})]
      (is (= 201 status)))))

  ;(testing "Delete user account"
  ;  (let [{:keys [status]} (ts/test-endpoint :delete "/v1/account" {:auth true})]
  ;    (is (= 204 status)))))

(comment
  (ts/test-endpoint :post "/v1/account" {:auth true})
  (ts/test-endpoint :patch "/v1/account" {:auth true}))