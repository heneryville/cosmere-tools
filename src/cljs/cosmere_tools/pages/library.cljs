(ns cosmere-tools.pages.library
  [:require 
   [cosmere-tools.components.creature-card :refer[creature-card]]
   [cosmere-tools.creature-library :refer [creatures]]
   
   ])

(defn library-page []
  [:div.page.library-page
   
   [:div.creatures-grid
    (doall (for [creature creatures]
             [creature-card (merge {:key (:name creature)}
                                   creature)]))]])