(ns user
  (:require [integrant.repl :as ig-repl]
            [integrant.repl.state :as state]
            [integrant.core :as ig]
            [clojure.pprint]
            [cheffy.server]))

(ig-repl/set-prep!
  (fn [] (-> "dev/resources/dev.edn" slurp ig/read-string)))
  ;; dev/resources/dev.edn
(def go ig-repl/go)
(def halt ig-repl/halt)
(def reset ig-repl/reset)
(def reset-all ig-repl/reset-all)

(defn app [] (-> state/system :cheffy/app))
(defn db [] (-> state/system :db/postgres))

(comment
  (go)
  (halt)
  (reset)
  (reset-all)
  (set! *print-namespace-maps* false))
