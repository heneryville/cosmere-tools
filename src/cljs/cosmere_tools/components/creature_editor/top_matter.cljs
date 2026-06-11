(ns cosmere-tools.components.creature-editor.top-matter
  (:require
   [clojure.string :as str]
   [cosmere-tools.creature-constants :as const]
   [cosmere-tools.trait-library :as traits]))

(defn handle-role-change [creature new-role change]
  (change [:role] new-role)
  (if (= "minion" new-role)
    (change [:traits] (conj (:traits creature) traits/minion))
    (change [:traits] (vec (remove #(= (:name %) "Minion") (:traits creature))))))

(defn top-matter [{:keys [creature change]}]
  [:div.top-matter
   [:div.form-group
    [:label "Name"]
    [:input {:type "text"
             :value (:name creature "")
             :on-change #(change [:name] (.. % -target -value))}]]

   [:div.form-row
    [:div.form-group {:style {:max-width 50}}
     [:label "Tier"]
     [:input {:type "number"
              :min 1
              :max 5
              :value (:tier creature 1)
              :on-change #(change [:tier] (js/parseInt (.. % -target -value)))}]]

    [:div.form-group
     [:label "Role"]
     [:select {:value (:role creature "minion")
               :on-change #(handle-role-change creature (.. % -target -value) change)}
      (for [role const/roles]
        ^{:key role}
        [:option {:value role} (str/capitalize role)])]]

    [:div.form-group
     [:label "Size"]
     [:select {:value (:size creature "medium")
               :on-change #(change [:size] (.. % -target -value))}
      (for [size const/sizes]
        ^{:key size}
        [:option {:value size} (str/capitalize size)])]]]

   [:div.form-group.type-group
    [:label "Type"]
    [:div.type-input-wrapper
     [:select.type-select
      {:value (if (some #{(:type creature)} const/preset-types)
                (:type creature)
                "custom")
       :on-change #(let [new-type (.. % -target -value)]
                     (change [:type]
                             (if (= new-type "custom")
                               ""
                               new-type)))}
      (for [type const/preset-types]
        ^{:key type}
        [:option {:value type} (str/capitalize type)])
      [:option {:value "custom"} "Custom..."]]
     (when (not (some #{(:type creature)} const/preset-types))
       [:input.type-custom
        {:type "text"
         :value (:type creature)
         :placeholder "Enter custom type..."
         :on-change #(change [:type] (.. % -target -value))}])]]])