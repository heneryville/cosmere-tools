(ns cosmere-tools.components.decorated-creature-card
  (:require
   [cosmere-tools.components.creature-card :refer [creature-card download-json]]
   [cosmere-tools.creature-library :as creatures]
   [cosmere-tools.router :as router]
   [cosmere-tools.utils :refer [prevent-default]]))

(defn creature-tag-pills [creature]
  [:div.creature-tags
   (for [tag (:tags creature)]
     ^{:key tag}
     [:span.creature-tag tag])
   [:code.source-label
    (if (creatures/static-creature? creature)
      "Server"
      "Local")]])

(defn decorated-creature-card [creature]
  [:div.decorated-creature-card
   [:div.creature-controls
    [:button.edit-button
     {:on-click (prevent-default #(router/navigate! :edit {:id (:id creature)}))}
     "Edit"]
    [:button.download-button
     {:on-click (prevent-default #(download-json creature))}
     "Download JSON"]
    (when-not (creatures/static-creature? creature)
      [:button.delete-button
       {:on-click (prevent-default #(when (js/confirm "Are you sure you want to delete this creature?")
                                      (creatures/delete-creature! creature)))}
       "Delete"])
    [creature-tag-pills creature]]
   [creature-card creature]])
