(ns cosmere-tools.components.trait-editor
  (:require
   [cosmere-tools.trait-library :as traits]
   [cosmere-tools.utils :as utils]
   [reagent.core :as r]))

(defn trait-editor [{:keys [trait]}]
  (let [name-r (r/atom (:name trait))]
    (fn [{:keys [trait path change on-remove]}]
      [:div.trait
       [:div.trait-header
        [:input.trait-name
         {:type "text"
          :value @name-r
          :placeholder "Trait name"
          :on-change #(reset! name-r (.. % -target -value))
          :on-blur #(change (conj path :name) @name-r)}]
        [:button.remove-trait
         {:on-click (utils/prevent-default on-remove)}
         "×"]]
       [:textarea.trait-description
        {:value (:description trait)
         :placeholder "Trait description"
         :rows 3
         :on-change #(change (conj path :description) (.. % -target -value))}]])))

(defn traits-editor [creature change]
  [:div.traits-section
   (doall
    (for [[idx trait] (map-indexed vector (:traits creature))]
      ^{:key (or (:name trait) idx)}
      [trait-editor
       {:trait trait
        :path [:traits idx]
        :change change
        :on-remove #(change [:traits] (vec (remove (fn [t] (= t trait)) (:traits creature))))}]))

   [:div.add-trait
    [:select.trait-select
     {:value ""
      :on-change #(let [trait-name (.. % -target -value)]
                    (when (not= trait-name "")
                      (if (= trait-name "custom")
                        (change [:traits] (traits/consolidate-traits
                                           (conj (or (:traits creature) [])
                                                 {:name "" :description ""})))
                        (change [:traits] (traits/consolidate-traits
                                           (conj (or (:traits creature) [])
                                                 (get traits/all-traits-by-name trait-name)))))))}
     [:option {:value ""} "Add trait..."]
     (for [[name _] (sort-by first traits/all-traits-by-name)]
       ^{:key name}
       [:option {:value name} name])
     [:option {:value "custom"} "Custom..."]]]])