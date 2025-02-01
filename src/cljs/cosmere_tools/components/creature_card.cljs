(ns cosmere-tools.components.creature-card
  (:require
   [clojure.string :as str]))

(defn attribute-pair [[label1 value1 defense label2 value2]]
  [:div.attr-pair
   [:div.attr
    [:div.label label1]
    [:div.value value1]]
   [:div.defense
    [:div.label "DEF"]
    [:div.value defense]]
   [:div.attr
    [:div.label label2]
    [:div.value value2]]])

(defn attributes-section [{:keys [strength physical-defense speed
                                  intelligence cognitive-defense willpower
                                  awareness spiritual-defense presence]}]
  [:section.attributes
   [:header
    [:div "Physical"]
    [:div "Cognitive"]
    [:div "Spiritual"]]
   [:div.attr-values
    [attribute-pair ["STR" strength physical-defense "SPD" speed]]
    [attribute-pair ["INT" intelligence cognitive-defense "WIL" willpower]]
    [attribute-pair ["AWA" awareness spiritual-defense "PRE" presence]]]])

(defn attributes-etc [{:keys [health-avg health-min health-max focus investiture]}]
  [:section.attributes-etc
   [:div [:h3 "Health: "] (str health-avg " (" health-min "-" health-max ")")]
   [:div [:h3 "Focus: "] (str focus)]
   [:div [:h3 "Investiture: "] (str investiture)]])

(defn skills [label skills]
  (when (seq skills)
    (let [pretty-skills (for [[skill-name value] skills]
                          (str (-> skill-name name str/capitalize)  " "
                               (if (neg? value) "" "+") value))]
      [:div [:h3 label] (str/join ", " pretty-skills)])))

(defn skills-section [{:keys [movement sense-range sense-primary languages]
                       {:keys [physical cognitive spiritual]} :skills}]
  [:section.skills
   [:div [:h3 "Movement"] movement]
   [:div [:h3 "Senses"]
    sense-range " ft."
    (when sense-primary (str " (" sense-primary ")"))]
   [skills "Physical Skills" physical]
   [skills "Cognitive Skills" cognitive]
   [skills "Spiritual Skils" spiritual]
   [:div [:h3 "Languages"]
    (if (seq languages)
      (str/join ", " languages)
      "none")]])

(defn traits-section [traits]
  [:section.traits
   (for [{:keys [name description]} traits]
     ^{:key name}
     [:div
      [:h3 name]
      description])])

(defn actions-section [actions]
  [:section.actions
   (for [{:keys [action-cost name description]} actions]
     ^{:key name}
     [:div
      [:i {:class (str "icon-action-" action-cost)}]
      [:h3 name]
      description])])

(defn creature-card [{:keys [name tier role type size traits actions] :as creature}]
  [:article.npc
   [:header
    [:h1 name]
    (str "Tier" tier " " (str/capitalize role) " - " (str/capitalize size) " " (str/capitalize type))]

   [attributes-section creature]
   [attributes-etc creature]
   [:hr]
   [skills-section creature]

   [:h2 "TRAITS"]
   [:hr]
   [traits-section traits]

   [:h2 "ACTIONS"]
   [:hr]
   [actions-section actions]])
