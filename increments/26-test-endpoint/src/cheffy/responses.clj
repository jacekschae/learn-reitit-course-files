(ns cheffy.responses
  (:require [spec-tools.data-spec :as ds]))

(def base-url "https://api.learnreitit.com")

(def step
  {:step/step-id     string?
   :step/sort        int?
   :step/description string?
   :step/recipe-id   string?})

(def ingredient
  {:ingredient/ingredient-id string?
   :ingredient/sort          int?
   :ingredient/name          string?
   :ingredient/amount        int?
   :ingredient/measure       string?
   :ingredient/recipe-id     string?})

(def recipe
  {:recipe/public               boolean?
   :recipe/favorite-count       int?
   :recipe/recipe-id            string?
   :recipe/name                 string?
   :recipe/uid                  string?
   :recipe/prep-time            number?
   :recipe/img                  string?
   (ds/opt :recipe/steps)       [step]
   (ds/opt :recipe/ingredients) [ingredient]})

(def recipes
  {:public          [recipe]
   (ds/opt :drafts) [recipe]})