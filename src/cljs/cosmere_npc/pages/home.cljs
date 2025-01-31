(ns cosmere-npc.pages.home
  (:require
   [cosmere-npc.components.creature-card :refer [creature-card]]
   [cosmere-npc.components.creature-editor :refer [creature-editor]]
   [cosmere-npc.creature-library :refer [creatures]]
   [reagent.core :as r]))

(defn home-page []
  (let [creature (r/atom (first creatures))]
    (fn []
      [:div.page.home-page
       [:div.editor-column
        [creature-editor {:creature @creature
                         :on-change #(reset! creature %)}]]
       [:div.preview-column
        [creature-card @creature]]])))