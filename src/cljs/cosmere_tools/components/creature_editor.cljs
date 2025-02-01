(ns cosmere-tools.components.creature-editor
  (:require
   [cljs.math :as math]
   [clojure.string :as str]))

(def sizes ["small" "medium" "large" "huge" "gargantuan"])
(def roles ["minion" "rival" "boss"])
(def preset-types ["animal" "humanoid" "swarm"])
(def sense-types ["sight" "smell" "hearing" "life" "investiture" "metallic"])
(def attributes [:strength :speed :intelligence :willpower :awareness :presence])
(def defenses [:physical-defense :cognitive-defense :spiritual-defense])

(def skills
  {:physical  [:agility :athletics :heavy-weapons :light-weapons :stealth :thievery]
   :cognitive [:crafting :deduction :discipline :intimidation :lore :medicine]
   :spiritual [:deception :insight :leadership :perception :persuasion :survival]})

(def skill-ranks (range 6)) ; 0 to 5

(def calculations
  [{:target [:physical-defense]
    :dependents #{[:strength] [:speed]}
    :calc-fn #(+ 10 (get % :strength 0) (get % :speed 0))}
   {:target [:cognitive-defense]
    :dependents #{[:intelligence] [:willpower]}
    :calc-fn #(+ 10 (get % :intelligence 0) (get % :willpower 0))}
   {:target [:spiritual-defense]
    :dependents #{[:awareness] [:presence]}
    :calc-fn #(+ 10 (get % :awareness 0) (get % :presence 0))}
   {:target [:health-avg]
    :dependents #{[:strength]}
    :calc-fn #(+ 10 (get % :strength 0))}
   {:target [:focus]
    :dependents #{[:willpower]}
    :calc-fn #(+ 2 (get % :willpower 0))}
   {:target [:health-min]
    :dependents #{[:health-avg]}
    :forced true
    :calc-fn #(math/round (* 0.75 (get % :health-avg)))}
   {:target [:health-max]
    :dependents #{[:health-avg]}
    :forced true
    :calc-fn #(math/round (* 1.20 (get % :health-avg)))}
   {:target [:movement]
    :dependents #{[:speed]}
    :calc-fn #(-> % :speed ({0 20 1 25 2 25 3 30 4 30 5 40 6 40 7 60 8 60} 80))}
   {:target [:sense-range]
    :dependents #{[:awareness]}
    :calc-fn #(-> % :awareness ({0 5 1 10 2 10 3 20 4 20 5 50 6 50 7 100 8 100} 1000))}])

(defn calculate-field [creature calc]
  ((:calc-fn calc) creature))

(defn is-dependent-path? [calc path]
  ((:dependents calc) path))

(defn write [prior-creature path new-value]
  ;;(prn "write" path new-value)
  (let [new-creature (assoc-in prior-creature path new-value)]
    (->> calculations
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

(defn stat-input [creature change {:keys [attr label step]}]
  (let [attr-path (if (vector? attr) attr [attr])
        calc (first (filter #(= attr-path (:target %)) calculations))
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

(defn skill-input [creature change skill-type skill]
  [:div.skill-item
   [:div.skill-label (-> skill name (str/replace "-" " ") str/capitalize)]
   [:div.skill-ranks
    (for [rank skill-ranks]
      ^{:key rank}
      [:label.radio-label
       [:input {:type "radio"
                :name (name skill)
                :value rank
                :checked (= (get-in creature [:skills skill-type skill]) rank)
                :on-change #(change [:skills skill-type skill]
                                    (js/parseInt (.. % -target -value)))}]
       rank])]])

(defn skills-column [{:keys [skill-type skills creature on-change]}]
  [:div.skills-column
   [:h3 (str/capitalize (name skill-type))]
   (for [skill skills]
     ^{:key skill}
     [skill-input creature on-change skill-type skill])])

(defn creature-editor [{:keys [creature on-change]}]
  (let [change (fn change [path value]
                 (on-change (write creature path value)))]
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
                 :on-change #(change [:role] (.. % -target -value))}
        (for [role roles]
          ^{:key role}
          [:option {:value role} (str/capitalize role)])]]

      [:div.form-group
       [:label "Size"]
       [:select {:value (:size creature "medium")
                 :on-change #(change [:size] (.. % -target -value))}
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
                       (change [:type]
                               (if (= new-type "custom")
                                 ""
                                 new-type)))}
        (for [type preset-types]
          ^{:key type}
          [:option {:value type} (str/capitalize type)])
        [:option {:value "custom"} "Custom..."]]
       (when (not (some #{(:type creature)} preset-types))
         [:input.type-custom
          {:type "text"
           :value (:type creature)
           :placeholder "Enter custom type..."
           :on-change #(change [:type] (.. % -target -value))}])]]

     [:hr]

     [:div.attributes-section
      (for [attr [:strength :physical-defense :speed
                  :intelligence :cognitive-defense :willpower
                  :awareness :spiritual-defense :presence]]
        ^{:key (name attr)}
        [stat-input creature change {:attr attr}])]

     [:hr]

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

     [:div.derived-stats-section
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
        (for [sense-type sense-types]
          ^{:key sense-type}
          [:option {:value sense-type} (str/capitalize sense-type)])]]]

     [:hr]

     [:div.skills-section
      (for [[type skills] skills]
        ^{:key type}
        [skills-column
         {:skill-type type
          :skills skills
          :creature creature
          :on-change change}])]

     [:hr]]))
