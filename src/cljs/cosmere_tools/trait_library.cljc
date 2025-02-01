(ns cosmere-tools.trait-library 
  (:require
   [cosmere-tools.creature-library :refer [creatures]]))

(def all-traits-by-name
  (->> creatures
       (mapcat :traits)
       (map (fn [trait]
              [(:name trait) trait]))
       (into {})))
       
(def minion
  {:name "Minion"
   :description "The bandit's attacks can't critcally hit, and they are immediatly defeated when they suffer an injury."})

(defn consolidate-traits
  "Enforces that there can only be one trait of a given name in a list of traits.
   If multiple traits share the same name, keeps the last one in the sequence."
  [traits]
  (->> traits
       reverse  ; reverse to keep last occurrence when grouping
       (group-by :name)
       vals
       (map first)
       reverse  ; restore original order
       vec))