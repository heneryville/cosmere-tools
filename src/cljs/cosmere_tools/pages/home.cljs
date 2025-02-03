(ns cosmere-tools.pages.home
  (:require [cosmere-tools.creature-library :refer [creatures]]
            [cosmere-tools.components.decorated-creature-card :refer [decorated-creature-card]]))

(defn home-page []
  [:div.page.home-page
   [:div.creatures-grid
    (doall
     (for [creature creatures]
       ^{:key (:id creature)}
       [decorated-creature-card creature]))]])