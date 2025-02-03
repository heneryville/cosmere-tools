(ns cosmere-tools.components.creature-editor
  (:require
   [clojure.string :as str]
   [cosmere-tools.components.action-editor :refer [actions-editor]]
   [cosmere-tools.components.creature-card :refer [creature-card]]
   [cosmere-tools.components.strike-editor :refer [strikes-editor]]
   [cosmere-tools.components.trait-editor :refer [traits-editor]]
   [cosmere-tools.creature-constants :as const]
   [cosmere-tools.strike-library :as strikes]
   [cosmere-tools.trait-library :as traits]
   [cosmere-tools.utils :refer [dissoc-in]]
   [cosmere-tools.components.creature-editor.top-matter :refer [top-matter]]
   [cosmere-tools.components.creature-editor.attributes :refer [attributes]]))

(defn calculate-field [creature calc]
  ((:calc-fn calc) creature))

(defn is-dependent-path? [calc path]
  ((:dependents calc) path))

(defn write [prior-creature path new-value]
  ;;(prn "write" path new-value)
  (let [new-creature (if (nil? new-value)
                       (dissoc-in prior-creature path)
                       (assoc-in prior-creature path new-value))
        new-creature (update new-creature :traits traits/consolidate-traits)
        new-creature (->> const/calculations
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
                                  new-creature))
        new-creature (update new-creature :strikes (fn [strikes]
                                                     (mapv (fn [strike]
                                                             (assoc strike :description (strikes/compose-description new-creature strike)))
                                                           strikes)))]
    new-creature))

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

;; MKHTODO re-write in terms of changes
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
  (let [changes (fn [change-pairs]
                  (on-change
                   (reduce (fn [creature [path new-value]]
                             (write creature path new-value))
                           creature
                           change-pairs)))
        change  (fn [path value]
                  (changes [[path value]]))]
    [:form.creature-editor
     [:div.columns
      [:div.left-column
       [top-matter {:creature creature :change change}]
       [attributes {:creature creature :change change}]

       [:hr]
       [:h2.section-head "Traits"]
       [traits-editor creature change]

       [:hr]
       [:h2.section-head "Actions"]
       [actions-editor creature change]]

      [:div.right-column
       [creature-card creature]
       [:h2.section-head "Skills"]
       [:div.skills-section
        (for [[type skills] const/skills]
          ^{:key type}
          [skills-column
           {:skill-type type
            :skills skills
            :creature creature
            :on-change change}])]]]
     
     [:hr]
     [:h2.section-head "Strikes"]
     [strikes-editor creature changes]]))
