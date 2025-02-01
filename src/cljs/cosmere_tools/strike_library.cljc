(ns cosmere-tools.strike-library
  (:require
   [cosmere-tools.creature-library :refer [creatures]]
   [cosmere-tools.utils :as utils]
   [cljs.math :as math]))

(def all-strikes-by-name
  (->> creatures
       (mapcat :strikes)
       (map (fn [strike]
              [(:name strike) strike]))
       (into {})))

(defn consolidate-strikes
  "Enforces that there can only be one strike of a given name in a list of strikes.
   If multiple strikes share the same name, keeps the last one in the sequence."
  [strikes]
  (->> strikes
       reverse
       (group-by :name)
       vals
       (map first)
       reverse
       vec))

(defn attack-modifier [creature strike]
  (->> creature
       :skills
       (map second)
       (keep #(get % (keyword (:skill strike))))
       first))

(defn compose-description [creature {:keys [damage-base damage-type] :as strike}]
  (let [attack-modifier (attack-modifier creature strike)
        [dice-count dice-type] (utils/parse-dice damage-base)
        graze-damage (math/round (utils/dice-avg dice-count dice-type))]
    (prn attack-modifier strike)
    (str "Attack +"  attack-modifier
         ", " (if (:reach strike)
                (str "reach " (:reach strike))
                (str "range " (first (:range strike)) "/" (second (:range strike))))
         " ft., one target. "
         "Graze: " graze-damage " (" damage-base ") " damage-type " damage. "
         "Hit: " (+ graze-damage attack-modifier) " (" damage-base "+" attack-modifier ") " damage-type " damage. ")))