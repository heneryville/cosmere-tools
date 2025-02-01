(ns cosmere-tools.pages.home
  (:require
   [clojure.pprint :as pprint]
   [cosmere-tools.components.creature-card :refer [creature-card]]
   [cosmere-tools.components.creature-editor :refer [creature-editor]]
   [cosmere-tools.creature-library :refer [creatures]]
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