(ns cosmere-tools.utils 
  (:require
   [clojure.string :as str]))

(defn dissoc-in
  "Dissociates an entry from a nested associative structure returning a new
   structure. keys is a sequence of keys. Any empty maps that result
   will not be present in the new structure."
  [m [k & ks :as keys]]
  (if ks
    (if-let [nextmap (get m k)]
      (let [newmap (dissoc-in nextmap ks)]
        (if (seq newmap)
          (assoc m k newmap)
          (dissoc m k)))
      m)
    (dissoc m k)))

(defn parse-dice [dice-dsl]
  (map parse-long (str/split dice-dsl #"[dD]")))
  
