(ns cosmere-tools.components.creature-editor
  (:require
   [cljs.math :as math]
   [clojure.string :as str]
   [cosmere-tools.components.trait-editor :refer [trait-editor]]
   [cosmere-tools.creature-constants :as const]
   [cosmere-tools.trait-library :as traits]
   [cosmere-tools.utils :refer [dissoc-in]]))

(def skill-ranks (range 6)) ; 0 to 5

(defn calculate-field [creature calc]
  ((:calc-fn calc) creature))

(defn is-dependent-path? [calc path]
  ((:dependents calc) path))

(defn write [prior-creature path new-value]
  ;;(prn "write" path new-value)
  (let [new-creature (if (nil? new-value)
                       (dissoc-in prior-creature path)
                       (assoc-in prior-creature path new-value))
        new-creature (update new-creature :traits traits/consolidate-traits)]
    (->> const/calculations
         (filter #(is-dependent-path? % path))
         (reduce (fn [c calc]
                   (let [target (:target calc)
                         calculated-value (calculate-field c calc)
                         prior-calculated-value (calculate-field prior-creature calc)
                         prior-actual-value (get-in prior-creature (:target calc) 0)
                         use-recalc (and
                                     (not= path target)
                                     (or (:forced calc)
                                         (= prior-calculated-value prior-actual-value)))]
                     ;;(prn "calc" target prior-actual-value prior-calculated-value calculated-value use-recalc)
                     (if use-recalc
                       (assoc-in c target calculated-value)
                       c)))
                 new-creature))))

(defn write-and-notify [creature path new-value on-change]
  (on-change (write creature path new-value)))

(defn stat-input [creature change {:keys [attr label step]}]
  (let [attr-path (if (vector? attr) attr [attr])
        calc (first (filter #(= attr-path (:target %)) const/calculations))
        calculated-value (when calc (calculate-field creature calc))
        display-label (or label (-> (last attr-path) name (str/replace "-" " ") str/capitalize))]
    [:div.attribute-pair
     [:label display-label]
     [:input (cond-> {:type "number"
                      :min 0
                      :step (or step 1)
                      :value (get-in creature attr-path (or calculated-value 0))
                      :on-change #(change attr-path (js/parseInt (.. % -target -value)))}
               calculated-value (assoc :placeholder calculated-value
                                       :class "derived-input"))]]))

(defn get-attr-value [creature attr]
  (get creature attr 0))

(defn skill-input [creature change skill-type [skill attr]]
  (let [attr-value (get-attr-value creature attr)
        max-rank 5
        ranks (range attr-value (+ attr-value max-rank 1))
        current-value (get-in creature [:skills skill-type skill])]
    [:div.skill-item
     [:div.skill-label
      [:span (-> skill name (str/replace "-" " ") str/capitalize)]
      [:span.skill-attr (str " (" (const/attr-abbrev attr) ")")]]
     [:div.skill-ranks
      [:label.radio-label.clear
       [:input {:type "radio"
                :name (name skill)
                :value ""
                :checked (nil? current-value)
                :on-change #(change [:skills skill-type skill] nil)}]
       " ⃠"]
      (for [rank ranks]
        ^{:key rank}
        [:label.radio-label
         [:input {:type "radio"
                  :name (name skill)
                  :value rank
                  :checked (= current-value rank)
                  :on-change #(change [:skills skill-type skill]
                                      (js/parseInt (.. % -target -value)))}]
         rank])]]))

(defn skills-column [{:keys [skill-type skills creature on-change]}]
  [:div.skills-column
   [:h3 (str/capitalize (name skill-type))]
   (for [skill skills]
     ^{:key skill}
     [skill-input creature on-change skill-type skill])])

(defn handle-role-change [creature new-role on-change]
  (-> creature
      (write [:role] new-role)
      ((fn [creature]
         (if (= "minion" new-role)
         ;; MKHTODO consolidate traits
           (write creature [:traits] (conj (:traits creature)
                                           traits/minion))
           (write creature [:traits] (remove #(= (:name %) "Minion") (:traits creature))))))
      (on-change)))

(defn creature-editor [{:keys [creature on-change]}]
  (let [change (fn change [path value]
                 (write-and-notify creature path value on-change))]
    [:form.creature-editor
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
                 :on-change #(let [new-role (.. % -target -value)]
                               (handle-role-change creature new-role on-change))}
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
           :on-change #(change [:type] (.. % -target -value))}])]]

     [:hr]

     [:div.attributes-section
      (for [attr [:strength :physical-defense :speed
                  :intellect :cognitive-defense :willpower
                  :awareness :spiritual-defense :presence]]
        ^{:key (name attr)}
        [stat-input creature change {:attr attr}])]

     [:hr]

     #_[:div.derived-stats-section
        [stat-input creature change
         {:attr :health-avg
          :label "Health"}]

        [stat-input creature change
         {:attr :focus
          :label "Focus"}]

        [stat-input creature change
         {:attr :investiture
          :label "Investiture"}]]

     #_[:div.derived-stats-section
        [stat-input creature change
         {:attr :movement
          :step 5
          :label "Movement"}]

        [stat-input creature change
         {:attr :sense-range
          :label "Sense Range"}]

        [:div.attribute-pair
         [:label "Primary Sense"]
         [:select {:value (:sense-primary creature "sight")
                   :on-change #(change [:sense-primary] (.. % -target -value))}
          (for [sense-type const/sense-types]
            ^{:key sense-type}
            [:option {:value sense-type} (str/capitalize sense-type)])]]]

     [:hr]

     #_[:div.skills-section
        (for [[type skills] const/skills]
          ^{:key type}
          [skills-column
           {:skill-type type
            :skills skills
            :creature creature
            :on-change change}])]

     [:div.form-group
      [:label "Languages: "]
      [:input {:type "text"
               :value (:languages creature "")
               :placeholder "e.g. Alethi, Azish, Shin"
               :on-change #(change [:languages] (.. % -target -value))}]]

     [:hr]
     [:h2 "Traits"]
     [trait-editor creature change]]))
