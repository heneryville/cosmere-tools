(ns cosmere-npc.pages.home
  (:require
   [clojure.pprint :as pprint]
   [cosmere-npc.components.creature-card :refer [creature-card]]
   [cosmere-npc.components.creature-editor :refer [creature-editor]]
   [cosmere-npc.creature-library :refer [creatures]]
   [reagent.core :as r]))

(defn home-page []
  (let [creature (r/atom (first creatures))]
    (fn []
      [:div.page.home-page
       [:pre (with-out-str (pprint/pprint (select-keys @creature  [:skills :agility])))]
       [:div.editor-pane
        [:div.editor-column
         [creature-editor {:creature @creature
                           :on-change #(reset! creature %)}]]
        [:div.preview-column
         [creature-card @creature]]]])))