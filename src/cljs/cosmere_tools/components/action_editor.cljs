(ns cosmere-tools.components.action-editor
  (:require
   [cosmere-tools.action-library :as actions]
   [cosmere-tools.components.toggle-button :refer [action-toggle-button]]
   [cosmere-tools.utils :as utils]
   [reagent.core :as r]))

(defn action-editor [{{action-name :name} :action}]
  (let [name-r (r/atom action-name)]
    (fn [{:keys [action path change on-remove]}]
      [:div.action
       [:div.action-header
        [action-toggle-button
         {:value (:action-cost action)
          :on-change #(change (conj path :action-cost) %)}]
        [:input.action-name
         {:type "text"
          :value @name-r
          :placeholder "Action name"
          :on-change #(reset! name-r (.. % -target -value))
          :on-blur #(change (conj path :name) @name-r)}]
        [:button.remove-action
         {:on-click (utils/prevent-default on-remove)}
         "×"]]
       [:textarea.action-description
        {:value (:description action)
         :placeholder "Action description"
         :rows 3
         :on-change #(change (conj path :description)
                            (.. % -target -value))}]])))

(defn actions-editor [creature change]
  [:div.actions-section
   (doall
    (for [[idx action] (map-indexed vector (:actions creature))]
      ^{:key (or (:name action) idx)}
      [action-editor
       {:action action
        :path [:actions idx]
        :change change
        :on-remove #(change [:actions] (vec (remove (fn [a] (= a action)) (:actions creature))))}]))
   
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