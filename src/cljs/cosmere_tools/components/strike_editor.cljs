(ns cosmere-tools.components.strike-editor
  (:require
   [clojure.string :as str]
   [cosmere-tools.components.radio-buttons :refer [radio-buttons]]
   [cosmere-tools.components.toggle-button :refer [action-toggle-button]]
   [cosmere-tools.creature-constants :as const]
   [cosmere-tools.strike-library :as strikes]
   [cosmere-tools.utils :as utils :refer [prevent-default]]
   [reagent.core :as r]))

(defn damage-selector [path strike change]
  (let [[dice-count dice-type] (utils/parse-dice (:damage-base strike))]
    [:div.strike-row
     [:div.form-group
      [:label "Damage Amount"]
      [:input {:type "number"
               :min 1
               :max 20
               :value dice-count
               :on-change #(change path (str (js/parseInt (.. % -target -value)) "d" dice-type))}]]
     [:div.form-group
      [:label "Dice Type"]
      [:div.dice-buttons
       [radio-buttons
        {:options const/dice-types
         :selected dice-type
         :on-change #(change path (str dice-count "d" %))}]]]
     [:div.form-group
      [:label "Damage Type"]
      [:select {:value (:damage-type strike)
                :on-change #(change (conj (pop path) :damage-type) (.. % -target -value))}
       (for [type const/damage-types]
         ^{:key type}
         [:option {:value type} (str/capitalize type)])]]]))

(defn strike-editor [{{strike-name :name} :strike}]
  (let [name-r (r/atom strike-name)]
    (fn [{:keys [strike path on-remove changes]}]
      (let [change (fn [path value]
                    (changes [[path value]]))]
        [:div.strike
         [:div.strike-header
          [action-toggle-button
           {:value (:action-cost strike)
            :on-change #(change (conj path :action-cost) %)}]
          [:input.strike-name
           {:type "text"
            :value @name-r
            :placeholder "Strike name"
            :on-change #(reset! name-r (.. % -target -value))
            :on-blur #(change (conj path :name) @name-r)}]
          [:button.remove-strike
           {:on-click (prevent-default on-remove)}
           "×"]]

         [:div.strike-details
          [damage-selector (conj path :damage-base) strike change]
          [:div.strike-row
           [:div.form-group
            [:label "Skill"]
            [:select {:value (keyword (:skill strike))
                      :on-change #(change (conj path :skill) (.. % -target -value))}
             (for [skill-option const/combat-skills]
               ^{:key skill-option}
               [:option {:value skill-option}
                (str/capitalize (name skill-option))])]]

           [:div.form-group
            [:label "Attack Distance"]
            [:div.distance-selector
             [:div.distance-type
              [radio-buttons
               {:options ["Melee" "Ranged"]
                :selected (if (nil? (:reach strike)) "Ranged" "Melee")
                :on-change #(if (= % "Melee")
                              (changes [[(conj path :reach) 5]
                                        [(conj path :range) nil]])

                              (changes [[(conj path :reach) nil]
                                        [(conj path :range) [80 320]]]))}]]
             (if (nil? (:reach strike))
               [:div.range-inputs
                [:input {:type "number"
                         :min 0
                         :step 5
                         :value (first (:range strike))
                         :on-change #(change (conj path :range)
                                             [(js/parseInt (.. % -target -value))
                                              (second (:range strike))])}]
                [:span " / "]
                [:input {:type "number"
                         :min 0
                         :step 5
                         :value (second (:range strike))
                         :on-change #(change (conj path :range)
                                             [(first (:range strike))
                                              (js/parseInt (.. % -target -value))])}]]
               [:input {:type "number"
                        :min 5
                        :step 5
                        :value (:reach strike)
                        :on-change #(change (conj path :reach)
                                            (js/parseInt (.. % -target -value)))}])]]]]

         [:textarea.strike-description
          {:value (:description strike)
           :placeholder "On Hit"
           :rows 3
           :on-change #(change (conj path :on-hit)
                               (.. % -target -value))}]]))))

(defn strikes-editor [creature changes]
  (let [change  (fn [path value]
                  (changes [[path value]]))]
    [:div.strikes-section
     (doall (map-indexed
             (fn [idx strike]
               ^{:key (or (:name strike) idx)}
               [strike-editor {:strike strike
                               :path [:strikes idx]
                               :changes changes
                               :on-remove (fn [] (change [:strikes] (vec (remove #(= % strike) (:strikes creature)))))}])
             (:strikes creature)))

     [:div.add-strike
      [:select.strike-select
       {:value ""
        :on-change #(let [strike-name (.. % -target -value)]
                      (when (not= strike-name "")
                        (if (= strike-name "custom")
                          (change [:strikes]
                                  (strikes/consolidate-strikes
                                   (conj (vec (or (:strikes creature) []))
                                         {:name ""
                                          :description ""
                                          :action-cost "single"
                                          :skill "athletics"
                                          :reach 5
                                          :range [80 320]
                                          :damage-type "keen"
                                          :graze {:damage "1d6"}
                                          :hit {:damage "1d6+2"}})))
                          (change [:strikes]
                                  (strikes/consolidate-strikes
                                   (conj (vec (or (:strikes creature) []))
                                         (get strikes/all-strikes-by-name strike-name)))))))}
       [:option {:value ""} "Add strike..."]
       (for [[name _] (sort-by first strikes/all-strikes-by-name)]
         ^{:key name}
         [:option {:value name} name])
       [:option {:value "custom"} "Custom..."]]]]))