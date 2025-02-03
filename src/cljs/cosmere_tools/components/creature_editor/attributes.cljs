(ns cosmere-tools.components.creature-editor.attributes
  (:require
   [clojure.string :as str]
   [cosmere-tools.creature-constants :as const]))

(defn stat-input [creature change {:keys [attr label step]}]
  (let [attr-path (if (vector? attr) attr [attr])
        calc (first (filter #(= attr-path (:target %)) const/calculations))
        display-label (or label (-> (last attr-path) name (str/replace "-" " ") str/capitalize))]
    [:div.attribute-pair
     [:label display-label]
     [:input (cond-> {:type "number"
                      :min 0
                      :step (or step 1)
                      :value (get-in creature attr-path)
                      :on-change #(change attr-path (js/parseInt (.. % -target -value)))}
               calc (assoc :class "derived-input"))]]))

(defn attributes [{:keys [creature change]}]
  [:div.attributes-panel
   [:div.attributes-section
    (for [attr [:strength :physical-defense :speed
                :intellect :cognitive-defense :willpower
                :awareness :spiritual-defense :presence]]
      ^{:key (name attr)}
      [stat-input creature change {:attr attr}])]

   [:div.derived-stats-section
    [stat-input creature change
     {:attr :health-avg
      :label "Health"}]

    [stat-input creature change
     {:attr :focus
      :label "Focus"}]

    [stat-input creature change
     {:attr :investiture
      :label "Investiture"}]]

   [:div.deflect-row
    [:label "Deflect"]
    [:input {:type "number"
             :min 0
             :max 20
             :value (:deflect creature 0)
             :on-change #(change [:deflect] (js/parseInt (.. % -target -value)))}]
    [:input {:type "text"
             :value (:deflect-explanation creature "")
             :placeholder "Short explanation of deflect value"
             :on-change #(change [:deflect-explanation] (.. % -target -value))}]]

   [:div.form-group
    [:label "Languages: "]
    [:input {:type "text"
             :value (:languages creature "")
             :placeholder "e.g. Alethi, Azish, Shin"
             :on-change #(change [:languages] (.. % -target -value))}]]])