(ns cheffy.account-tests
  (:require [clojure.test :refer :all]
            [cheffy.test-system :as ts]))

(defn account-fixture
  [f]
  (ts/create-auth0-test-user
    {:connection "Username-Password-Authentication"
     :email "account-tests@cheffy.app"
     :password "s#m3R4nd0m-pass"})
  (reset! ts/token (ts/get-test-token "account-tests@cheffy.app"))
  (f)
  (reset! ts/token nil))

(use-fixtures :once account-fixture)

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