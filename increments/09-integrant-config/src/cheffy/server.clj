(ns cheffy.server
  (:require [reitit.ring :as ring]
            [ring.adapter.jetty :as jetty]))

(def app
  (ring/ring-handler
    (ring/router
      [["/"
        {:get {:handler (fn [req] {:staus 200
                                   :body "Hello Reiti"})}}]])))

(defn start
  []
  (jetty/run-jetty app {:port 3000 :join? false})
  (println "\n Server running on port 3000"))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))

(comment
  (start)
  (app {:request-method :get
        :uri "/"})
  (-main))