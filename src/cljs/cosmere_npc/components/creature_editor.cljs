(ns cosmere-npc.components.creature-editor
  (:require
   [clojure.string :as str]))

(def sizes ["small" "medium" "large" "huge" "gargantuan"])
(def roles ["minion" "rival" "boss"])
(def preset-types ["animal" "humanoid" "swarm"])
(def attributes [:strength :speed :intelligence :willpower :awareness :presence])
(def defenses [:physical-defense :cognitive-defense :spiritual-defense])

(def calculations
  [
   {:dependents [:strength :speed]
    :target :physical-defense
    :calc-fn #(+ 10 (get % :strength 0) (get % :speed 0))}
   #_{:dependents [:intelligence :willpower]
    :target :cognitive-defense
    :calc-fn #(+ 10 (get % :intelligence 0) (get % :willpower 0))}
   #_{:dependents [:awareness :presence]
    :target :spiritual-defense
    :calc-fn #(+ 10 (get % :awareness 0) (get % :presence 0))}
   #_{:dependents [:strength]
    :target :health-avg
    :calc-fn #(+ 10 (get % :strength 0))}
   #_{:dependents [:willpower]
    :target :focus
    :calc-fn #(+ 2 (get % :willpower 0))}])

(defn calculate-field [creature calc]
  ((:calc-fn calc) creature))

(defn recalculate-all-fields [existing-creature new-creature]
  (reduce (fn [c calc]
            (let [calculated-value (calculate-field c calc)
                  prior-calculated-value (calculate-field existing-creature calc)
                  prior-actual-value (get existing-creature (:target calc) 0)
                  followed-calc? (= prior-calculated-value prior-actual-value)]
              (prn "calc" (:target calc) calculated-value prior-calculated-value prior-actual-value followed-calc?)
              (if followed-calc?
                ; Only update if the field was following its calculation
                (assoc c (:target calc) calculated-value)
                c)))
          new-creature
          calculations))

(defn wrap-on-change [on-change creature]
  (fn [new-creature]
    (on-change (recalculate-all-fields creature new-creature))))

(defn attribute-input [creature on-change attr]
  (let [is-calculated (some #(= attr (:target %)) calculations)
        calc (first (filter #(= attr (:target %)) calculations))
        calculated-value (when calc (calculate-field creature calc))]
    [:div.attribute-pair
     [:label (-> attr name (str/replace "-" " ") str/capitalize)]
     [:input (cond-> {:type "number"
                      :min 0
                      :value (get creature attr 0)
                      :on-change #(on-change (assoc creature attr
                                                   (js/parseInt (.. % -target -value))))}
               is-calculated (assoc :placeholder calculated-value
                                  :class "defense-input"))]]))

(defn derived-stat-input [creature on-change {:keys [attr label]}]
  (let [calc (first (filter #(= attr (:target %)) calculations))
        calculated-value (when calc (calculate-field creature calc))]
    [:div.attribute-pair
     [:label label]
     [:input (cond-> {:type "number"
                      :min 0
                      :value (get creature attr (or calculated-value 0))
                      :on-change #(on-change (assoc creature attr
                                                   (js/parseInt (.. % -target -value))))}
               calculated-value (assoc :placeholder calculated-value
                                     :class "derived-input"))]]))

(defn creature-editor [{:keys [creature on-change]}]
  (let [wrapped-on-change (wrap-on-change on-change creature)]
    [:form.creature-editor
     [:div.form-group
      [:label "Name"]
      [:input {:type "text"
               :value (:name creature "")
               :on-change #(wrapped-on-change (assoc creature :name (.. % -target -value)))}]]

     [:div.form-row
      [:div.form-group {:style {:max-width 50}}
       [:label "Tier"]
       [:input {:type "number"
                :min 1
                :max 5
                :value (:tier creature 1)
                :on-change #(wrapped-on-change (assoc creature :tier (js/parseInt (.. % -target -value))))}]]

      [:div.form-group
       [:label "Role"]
       [:select {:value (:role creature "minion")
                 :on-change #(wrapped-on-change (assoc creature :role (.. % -target -value)))}
        (for [role roles]
          ^{:key role}
          [:option {:value role} (str/capitalize role)])]]

      [:div.form-group
       [:label "Size"]
       [:select {:value (:size creature "medium")
                 :on-change #(wrapped-on-change (assoc creature :size (.. % -target -value)))}
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
                       (wrapped-on-change (assoc creature :type
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
           :on-change #(wrapped-on-change (assoc creature :type (.. % -target -value)))}])]]

     [:hr]
     
     [:div.attributes-section
      (for [attr [:strength :physical-defense :speed
                  :intelligence :cognitive-defense :willpower
                  :awareness :spiritual-defense :presence]]
        ^{:key (name attr)}
        [attribute-input creature wrapped-on-change attr])]

     [:hr]
     
     [:div.derived-stats-section
      [derived-stat-input creature wrapped-on-change
       {:attr :health
        :label "Health"}]
      
      [derived-stat-input creature wrapped-on-change
       {:attr :focus
        :label "Focus"}]
      
      [derived-stat-input creature wrapped-on-change
       {:attr :investiture
        :label "Investiture"}]]]))
