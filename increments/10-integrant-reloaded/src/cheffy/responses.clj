(ns cheffy.responses
  (:require [spec-tools.data-spec :as ds]))

(def base-url "http://api.learnreitit.com")

(def recipes {:public          vector?
              (ds/opt :drafts) vector?})

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

(def conversation seq?)

(def message vector?)
