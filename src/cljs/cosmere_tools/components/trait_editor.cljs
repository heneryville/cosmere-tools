(ns cosmere-tools.components.trait-editor
  (:require
   [clojure.string :as str]
   [cosmere-tools.trait-library :as traits]))


;; MKHTODO use templating for the owner of the trait

(defn traits-editor [creature change]
  [:div.traits-section
   (for [{:keys [name description] :as trait} (:traits creature)]
     ^{:key name}
     [:div.trait
      [:div.trait-header
       [:input.trait-name 
        {:type "text"
         :value name
         :placeholder "Trait name"
         :on-change #(change [:traits] 
                            (map (fn [t] 
                                  (if (= (:name t) name)
                                    (assoc t :name (.. % -target -value))
                                    t))
                                 (:traits creature)))}]
       [:button.remove-trait
        {:on-click #(change [:traits]
                           (remove (fn [t] (= (:name t) name)) 
                                   (:traits creature)))}
        "×"]]
      [:textarea.trait-description
       {:value description
        :placeholder "Trait description"
        :rows 3
        :on-change #(change [:traits]
                           (map (fn [t]
                                 (if (= (:name t) name)
                                   (assoc t :description (.. % -target -value))
                                   t))
                                (:traits creature)))}]])
   
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