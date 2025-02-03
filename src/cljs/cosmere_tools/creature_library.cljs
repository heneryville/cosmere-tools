(ns cosmere-tools.creature-library
  (:require
   [cosmere-tools.static-creatures :as static-creatures]))

(defn load-from-storage! []
  (let [storage-keys (filter #(.startsWith % "creature-")
                            (js/Object.keys js/localStorage))
        stored-creatures (map #(-> (js/localStorage.getItem %)
                                  (js/JSON.parse)
                                  (js->clj :keywordize-keys true))
                             storage-keys)]
    (if (seq stored-creatures)
      stored-creatures
      static-creatures/creatures)))

(defn save-to-storage! [creatures]
  (doseq [creature creatures]
    (let [key (str "creature-" (:id creature))
          value (-> creature
                   clj->js
                   (js/JSON.stringify nil 2))]
      (js/localStorage.setItem key value))))

;; Initialize with data from storage or fallback to static
(def creatures (concat static-creatures/creatures (load-from-storage!)))
(def creatures-by-id (into {} (map (fn [creature] [(:id creature) creature]) creatures)))

(defn save-creature! [creature]
  (let [key (str "creature-" (:id creature))
        value (-> creature
                 clj->js
                 (js/JSON.stringify nil 2))]
    (js/localStorage.setItem key value)
    ;; Refresh the app's creature list
    (set! creatures (load-from-storage!))
    (set! creatures-by-id (into {} (map (fn [c] [(:name c) c]) creatures)))))

