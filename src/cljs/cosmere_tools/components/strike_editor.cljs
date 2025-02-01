(ns cosmere-tools.components.strike-editor
  (:require
   [clojure.string :as str]
   [cosmere-tools.components.radio-buttons :refer [radio-buttons]]
   [cosmere-tools.components.toggle-button :refer [action-toggle-button
                                                   toggle-button]]
   [cosmere-tools.creature-constants :as const]
   [cosmere-tools.strike-library :as strikes]
   [cosmere-tools.utils :as utils]))

(defn damage-selector [path strike change]
  (let [[dice-count dice-type] (utils/parse-dice (:damage-base strike))]
    [:div.strike-row
     [:div.form-group
      [:label "Damage"]
      [:input {:type "number"
               :min 1
               :max 20
               :value dice-count
               :on-change #(change path (str (js/parseInt (.. % -target -value)) "d" dice-type))}]]
     [:div.form-group
      [:label "Dice Type"]
      [radio-buttons
       {:options const/dice-types
        :selected dice-type
        :on-change #(change path (str dice-count "d" %))}]]
     [:div.form-group
      [:label "Type"]
      [:select {:value (:damage-type strike)
                :on-change #(change (conj (pop path) :damage-type) (.. % -target -value))}
       (for [type const/damage-types]
         ^{:key type}
         [:option {:value type} (str/capitalize type)])]]]))

(defn strike-editor [{:keys [strike path change on-remove]}]
  (let [{:keys [description action-cost skill reach range]
         strike-name :name
         :or {reach 5
              range [80 320]}} strike
        skill (keyword skill)]
    [:div.strike
     [:div.strike-header
      [action-toggle-button
       {:value action-cost
        :on-change #(change (conj path :action-cost) %)}]
      [:input.strike-name
       {:type "text"
        :value strike-name
        :placeholder "Strike name"
        :on-change #(change (conj path :name)
                            (.. % -target -value))}]
      [:button.remove-strike
       {:on-click on-remove}
       "×"]]

     [:div.strike-details
      [damage-selector (conj path :damage-base) strike change]
      [:div.strike-row
       [:div.form-group
        [:label "Skill"]
        [:select {:value skill
                  :on-change #(change (conj path :skill) (.. % -target -value))}
         (for [skill-option const/combat-skills]
           ^{:key skill-option}
           [:option {:value skill-option}
            (str/capitalize (name skill-option))])]]

       [:div.form-group
        [:label "Reach"]
        [:input {:type "number"
                 :min 5
                 :step 5
                 :value reach
                 :on-change #(change (conj path :reach)
                                     (js/parseInt (.. % -target -value)))}]]

       [:div.form-group
        [:label "Range"]
        [:div.range-inputs
         [:input {:type "number"
                  :min 0
                  :step 5
                  :value (first range)
                  :on-change #(change (conj path :range)
                                      [(js/parseInt (.. % -target -value))
                                       (second range)])}]
         [:span " / "]
         [:input {:type "number"
                  :min 0
                  :step 5
                  :value (second range)
                  :on-change #(change (conj path :range)
                                      [(first range)
                                       (js/parseInt (.. % -target -value))])}]]]]]

     [:textarea.strike-description
      {:value description
       :placeholder "On Hit"
       :rows 3
       :on-change #(change (conj path :on-hit)
                           (.. % -target -value))}]]))

(defn strikes-editor [creature change]
  [:div.strikes-section
   (map-indexed
    (fn [idx strike]
      ^{:key (or (:name strike) idx)}
      [strike-editor {:strike strike
                      :path [:strikes idx]
                      :change change
                      :on-remove (fn [] (change [:strikes] (vec (remove #(= % strike) (:strikes creature)))))}])
    (:strikes creature))

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
     [:option {:value "custom"} "Custom..."]]]])