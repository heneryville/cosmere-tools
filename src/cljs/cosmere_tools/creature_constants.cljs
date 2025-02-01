(ns cosmere-tools.creature-constants)

(def sizes ["small" "medium" "large" "huge" "gargantuan"])
(def roles ["minion" "rival" "boss"])
(def preset-types ["animal" "humanoid" "swarm"])
(def sense-types ["sight" "smell" "hearing" "life" "investiture" "metallic"])
(def attributes [:strength :speed :intellect :willpower :awareness :presence])
(def defenses [:physical-defense :cognitive-defense :spiritual-defense])

(def damage-types ["keen" "impact" "energy" "spirit" "vital"])
(def action-costs ["free" "reaction" "single" "double"])

(def dice-types [4 6 8 10 12 20])

(def skills
  {:physical  [[:agility :speed]
               [:athletics :strength]
               [:heavy-weapons :strength]
               [:light-weapons :speed]
               [:stealth :speed]
               [:thievery :speed]]
   :cognitive [[:crafting :intellect]
               [:deduction :intellect]
               [:discipline :willpower]
               [:intimidation :willpower]
               [:lore :intellect]
               [:medicine :intellect]]
   :spiritual [[:deception :presence]
               [:insight :awareness]
               [:leadership :presence]
               [:perception :awareness]
               [:persuasion :presence]
               [:survival :awareness]]})

(def combat-skills [:athletics :heavy-weapons :light-weapons])

(def attr-abbrev
  {:speed "spd"
   :strength "str"
   :intellect "int"
   :willpower "wil"
   :awareness "awa"
   :presence "pre"})

(def calculations
  [{:target [:physical-defense]
    :dependents #{[:strength] [:speed]}
    :calc-fn #(+ 10 (get % :strength 0) (get % :speed 0))}
   {:target [:cognitive-defense]
    :dependents #{[:intellect] [:willpower]}
    :calc-fn #(+ 10 (get % :intellect 0) (get % :willpower 0))}
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
    :calc-fn #(* 0.75 (get % :health-avg))}
   {:target [:health-max]
    :dependents #{[:health-avg]}
    :forced true
    :calc-fn #(* 1.20 (get % :health-avg))}
   {:target [:movement]
    :dependents #{[:speed]}
    :calc-fn #(-> % :speed ({0 20 1 25 2 25 3 30 4 30 5 40 6 40 7 60 8 60} 80))}
   {:target [:sense-range]
    :dependents #{[:awareness]}
    :calc-fn #(-> % :awareness ({0 5 1 10 2 10 3 20 4 20 5 50 6 50 7 100 8 100} 1000))}])