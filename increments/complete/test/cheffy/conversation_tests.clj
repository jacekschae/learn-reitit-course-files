(ns cheffy.conversation-tests
  (:require [clojure.test :refer :all]
            [cheffy.test-system :as ts]
            [next.jdbc.sql :as sql]
            [integrant.repl.state :as state]))

(def db (-> state/system :db/postgres))

(def uid "auth0|5fe31324a2914d006f574ceb")

(def conversation-id (atom nil))

(defn conversation-fixture
  [f]
  (reset! ts/token (ts/get-test-token "testing@cheffy.app"))
  (f)
  (sql/delete! db :conversation {:conversation-id @conversation-id})
  (reset! ts/token nil))

(use-fixtures :once conversation-fixture)

(deftest conversation-tests

  (testing "Create message"
    (testing "without conversation"
      (let [{:keys [status body]} (ts/test-endpoint :post "/v1/conversation"
                                    {:auth true :body {:to "mike@mailinator.com"
                                                       :message-body "Test Message"}})]
        (reset! conversation-id (:conversation-id body))
        (is (= 201 status))))

    (testing "with conversation"
      (let [{:keys [status body]} (ts/test-endpoint :post (str "/v1/conversation/" @conversation-id)
                                    {:auth true :body {:to "mike@mailinator.com"
                                                       :message-body "Second Test Message"}})]
        (is (= 201 status)))))

  (testing "List user conversations"
    (let [{:keys [status body]} (ts/test-endpoint :get "/v1/conversation" {:auth true})]
      (is (= 200 status))
      (is (= (:conversation/uid (first body)) uid))))

  (testing "List conversation messages"
    (let [{:keys [status body]} (ts/test-endpoint :get (str "/v1/conversation/" @conversation-id)
                                  {:auth true})]
      (is (= 200 status))
      (is (= (:messages/conversation_id (first body) @conversation-id)))))

  (testing "Clear notifications"
    (let [{:keys [status]} (ts/test-endpoint :put (str "/v1/conversation/" @conversation-id)
                             {:auth true})]
      (is (= 204 status)))))
