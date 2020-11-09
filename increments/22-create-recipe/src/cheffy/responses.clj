(ns cheffy.responses
  (:require [spec-tools.data-spec :as ds]))

(def step
  {:step/step_id     string?
   :step/sort        int?
   :step/description string?
   :step/recipe_id   string?})

(def ingredient
  {:ingredient/ingredient_id string?
   :ingredient/sort          int?
   :ingredient/name          string?
   :ingredient/amount        int?
   :ingredient/measure       string?
   :ingredient/recipe_id     string?})

(def recipe
  {:recipe/public               boolean?
   :recipe/favorite_count       int?
   :recipe/recipe_id            string?
   :recipe/name                 string?
   :recipe/uid                  string?
   :recipe/prep_time            number?
   :recipe/img                  string?
   (ds/opt :recipe/steps)       [step]
   (ds/opt :recipe/ingredients) [ingredient]})

(def recipes
  {:public          [recipe]
   (ds/opt :drafts) [recipe]})