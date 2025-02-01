(ns cosmere-tools.strike-library
  (:require
   [cosmere-tools.creature-library :refer [creatures]]))

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