(ns cheffy.conversations-tests
  (:require [clojure.test :refer :all]
            [cheffy.test-system :as ts]))

(def uid "auth0|5efe87e0b07e900019e880f9")

(def message
  {:to "mike@mailinator.com"
   :message-body "Test Message"})

(def second-message
  {:to "jade@mailinator.com"
   :message-body "Second Test Message"})

(def conversation-id (atom nil))

(deftest conversation-tests

  (testing "Create message"
    (testing "without conversation"
      (let [{:keys [status body]} (ts/test-endpoint :post (str "/v1/conversation") {:auth true :body message})]
        (reset! conversation-id (:conversation-id body))
        (is (= 201 status))))

    (testing "with conversation"
      (let [{:keys [status body]} (ts/test-endpoint :post (str "/v1/conversation/" @conversation-id) {:auth true :body second-message})]
        (reset! conversation-id (:conversation-id body))
        (is (= 201 status)))))

  (testing "List user conversations"
    (let [{:keys [status body]} (ts/test-endpoint :get "/v1/conversation" {:auth true})]
      (is (= 200 status))
      (is (= (:conversation/uid (first body)) uid))))

  (testing "List conversation messages"
    (let [{:keys [status body]} (ts/test-endpoint :get (str "/v1/conversation/" @conversation-id) {:auth true})]
      (is (= 200 status))
      (is (= (:messages/conversation_id (first body) @conversation-id)))))

  (testing "Clear notifications"
    (let [{:keys [status]} (ts/test-endpoint :put (str "/v1/conversation/" @conversation-id) {:auth true})]
      (is (= 204 status)))))`


(comment
  (let [response (ts/test-endpoint :get "/v1/conversation" {:auth true})]
    (clojure.pprint/pprint response))

  (time (ts/test-endpoint :get "/v1/conversation" {:auth true}))
  (ts/test-endpoint :get "/v1/conversation/8d4ab926-d5cc-483d-9af0-19627ed468eb" {:auth true})
  (ts/test-endpoint :post "/v1/conversation" {:auth true :body message})
  (ts/test-endpoint :put "/v1/conversation/362d06c7-2702-4273-bcc3-0c04d2753b6f" {:auth true}))

