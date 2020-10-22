(ns cheffy.responses
  (:require [spec-tools.data-spec :as ds]))

(def recipe
  {:recipe/public         boolean?
   :recipe/favorite_count number?
   :recipe/recipe_id      string?
   :recipe/name           string?
   :recipe/uid            string?
   :recipe/prep_time      number?
   :recipe/img            string?
   :recipe/steps          any?
   :recipe/ingredients    any?})

(def recipes
  {:public [recipe]
   (ds/opt :drafts) [recipe]})
