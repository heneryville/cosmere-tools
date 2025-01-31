(ns cosmere-npc.pages.library
  [:require 
   [cosmere-npc.components.creature-card :refer[creature-card]]
   [cosmere-npc.creature-library :refer [creatures]]
   
   ])

(defn library-page []
  [:div.page.library-page
   
   [:div.creatures-grid
    (doall (for [creature creatures]
             [creature-card (merge {:key (:name creature)}
                                   creature)]))]])