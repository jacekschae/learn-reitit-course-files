(ns cheffy.account-tests
  (:require [clojure.test :refer :all]
            [cheffy.test-system :as ts]))

(use-fixtures :once ts/account-fixture)

(deftest account-tests

 (testing "Create user account"
   (let [{:keys [status]} (ts/test-endpoint :post "/v1/account" {:auth true})]
     (is (= status 201))))

 (testing "Update user role"
   (let [{:keys [status]} (ts/test-endpoint :put "/v1/account" {:auth true})]
     (is (= status 204))))

 (testing "Delete user account"
   (let [{:keys [status]} (ts/test-endpoint :delete "/v1/account" {:auth true})]
     (is (= status 204)))))