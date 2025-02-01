(ns cosmere-tools.components.toggle-button
  (:require
   [cosmere-tools.creature-constants :as const]))

(defn next-option [current-value options]
  (let [current-idx (.indexOf options current-value)]
    (nth options
         (mod (inc current-idx) (count options))
         (first options))))

(defn toggle-button [{:keys [value options on-change]}]
  (let [selected-option (or (first (filter #(= value (:value %)) options))
                            (first options))]
    [:button.toggle-button
     {:on-click #(on-change (:value (next-option selected-option options)))}
     (:ui selected-option)]))

(defn action-toggle-button [{:keys [value on-change]}]

  [toggle-button
   {:value value
    :options (for [cost const/action-costs]
               {:value cost
                :ui [:i {:class (str "icon-action-" cost)}]})
    :on-change on-change}])