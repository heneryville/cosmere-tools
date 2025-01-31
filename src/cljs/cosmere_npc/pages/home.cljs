(ns cosmere-npc.pages.home
  [:require 
   [cosmere-npc.components.creature-card :refer[creature-card]]
   [cosmere-npc.creature-library :refer [creatures]]
   
   ])

(defn home-page []
  [:div.page.home-page
   
   [:div.creatures-grid
    (doall (for [creature creatures]
             [creature-card (merge {:key (:name creature)}
                                   creature)]))]])