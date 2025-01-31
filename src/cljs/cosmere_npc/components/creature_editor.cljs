(ns cosmere-npc.components.creature-editor
  (:require
   [clojure.string :as str]))

(def sizes ["small" "medium" "large" "huge" "gargantuan"])
(def roles ["minion" "rival" "boss"])
(def preset-types ["animal" "humanoid" "swarm"])

(defn creature-editor [{:keys [creature on-change]}]
  [:form.creature-editor
   [:div.form-group
    [:label "Name"]
    [:input {:type "text"
             :value (:name creature "")
             :on-change #(on-change (assoc creature :name (.. % -target -value)))}]]

   [:div.form-row
    [:div.form-group {:style {:max-width 50}}
     [:label "Tier"]
     [:input {:type "number"
              :min 1
              :max 5
              :value (:tier creature 1)
              :on-change #(on-change (assoc creature :tier (js/parseInt (.. % -target -value))))}]]

    [:div.form-group
     [:label "Role"]
     [:select {:value (:role creature "minion")
               :on-change #(on-change (assoc creature :role (.. % -target -value)))}
      (for [role roles]
        ^{:key role}
        [:option {:value role} (str/capitalize role)])]]

    [:div.form-group
     [:label "Size"]
     [:select {:value (:size creature "medium")
               :on-change #(on-change (assoc creature :size (.. % -target -value)))}
      (for [size sizes]
        ^{:key size}
        [:option {:value size} (str/capitalize size)])]]]

   [:div.form-group.type-group
    [:label "Type"]
    [:div.type-input-wrapper
     [:select.type-select
      {:value (if (some #{(:type creature)} preset-types)
                (:type creature)
                "custom")
       :on-change #(let [new-type (.. % -target -value)]
                     (on-change (assoc creature :type
                                       (if (= new-type "custom")
                                         ""  ; Reset to empty string for custom input
                                         new-type))))}
      (for [type preset-types]
        ^{:key type}
        [:option {:value type} (str/capitalize type)])
      [:option {:value "custom"} "Custom..."]]
     (when (not (some #{(:type creature)} preset-types))
       [:input.type-custom
        {:type "text"
         :value (:type creature)
         :placeholder "Enter custom type..."
         :on-change #(on-change (assoc creature :type (.. % -target -value)))}])]]])
