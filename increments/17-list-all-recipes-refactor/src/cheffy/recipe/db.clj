(ns cheffy.recipe.db
  (:require [next.jdbc.sql :as sql]))

(defn find-all-recipes
  [db]
  (let [public (sql/find-by-keys db :recipe {:public true})]
    {:public public}))