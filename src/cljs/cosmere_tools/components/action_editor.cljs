(ns cosmere-tools.components.action-editor
  (:require
   [cosmere-tools.action-library :as actions]
   [cosmere-tools.components.toggle-button :refer [action-toggle-button
                                                   toggle-button]]))

(defn actions-editor [creature change]
  [:div.actions-section
   (map-indexed
    (fn [idx {:keys [name description action-cost]}]
      ^{:key (or name idx)}
      [:div.action
       [:div.action-header
        [action-toggle-button
         {:value action-cost
          :on-change #(change [:actions idx :action-cost] %)}]
        [:input.action-name
         {:type "text"
          :value name
          :placeholder "Action name"
          :on-change #(change [:actions idx :name]
                              (.. % -target -value))}]
        [:button.remove-action
         {:on-click #(change [:actions]
                             (vec (concat
                                   (take idx (:actions creature))
                                   (drop (inc idx) (:actions creature)))))}
         "×"]]
       [:textarea.action-description
        {:value description
         :placeholder "Action description"
         :rows 3
         :on-change #(change [:actions idx :description]
                            (.. % -target -value))}]])
    (:actions creature))
   
   [:div.add-action
    [:select.action-select
     {:value ""
      :on-change #(let [action-name (.. % -target -value)]
                    (when (not= action-name "")
                      (if (= action-name "custom")
                        (change [:actions] 
                               (actions/consolidate-actions
                                 (conj (vec (or (:actions creature) []))
                                       {:name ""
                                        :description ""
                                        :action-cost "single"})))
                        (change [:actions]
                               (actions/consolidate-actions
                                 (conj (vec (or (:actions creature) []))
                                       (get actions/all-actions-by-name action-name)))))))}
     [:option {:value ""} "Add action..."]
     (for [[name _] (sort-by first actions/all-actions-by-name)]
       ^{:key name}
       [:option {:value name} name])
     [:option {:value "custom"} "Custom..."]]]])