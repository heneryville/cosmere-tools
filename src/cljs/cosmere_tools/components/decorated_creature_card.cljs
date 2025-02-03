(ns cosmere-tools.components.decorated-creature-card
  (:require [cosmere-tools.components.creature-card :refer [creature-card download-json]]
            [cosmere-tools.creature-library :as creatures]
            [cosmere-tools.router :as router]))

(defn decorated-creature-card [creature]
  [:div.decorated-creature-card
   [:div.creature-controls
    [:button.edit-button
     {:on-click #(router/navigate! :edit {:id (:id creature)})}
     "Edit"]
    [:button.download-button
     {:on-click #(download-json creature)}
     "Download JSON"]
    (when-not (creatures/static-creature? creature)
      [:button.delete-button
       {:on-click #(when (js/confirm "Are you sure you want to delete this creature?")
                    (creatures/delete-creature! creature))}
       "Delete"])
    [:code.source-label
     (if (creatures/static-creature? creature)
       "predefined"
       "personal")]]
   [creature-card creature]])