(ns cosmere-tools.components.decorated-creature-card
  (:require [cosmere-tools.components.creature-card :refer [creature-card download-json]]
            [cosmere-tools.router :as router]))

(defn decorated-creature-card [creature]
  [:div.decorated-creature-card
   [:div.creature-controls
    [:button.edit-button
     {:on-click #(router/navigate! :edit {:id (:id creature)})}
     "Edit"]
    [:button.download-button
     {:on-click #(download-json creature)}
     "Download JSON"]]
   [creature-card creature]])