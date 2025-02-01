(ns cosmere-tools.action-library
  (:require
   [cosmere-tools.creature-library :refer [creatures]]))

(def all-actions-by-name
  (->> creatures
       (mapcat :actions)
       (map (fn [action]
              [(:name action) action]))
       (into {})))

(defn consolidate-actions
  "Enforces that there can only be one action of a given name in a list of actions.
   If multiple actions share the same name, keeps the last one in the sequence."
  [actions]
  (->> actions
       reverse
       (group-by :name)
       vals
       (map first)
       reverse
       vec))