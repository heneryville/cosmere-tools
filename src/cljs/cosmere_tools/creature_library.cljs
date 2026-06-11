(ns cosmere-tools.creature-library
  (:require
   [cosmere-tools.static-creatures :as static-creatures]))

(defn static-creature? [creature]
  (some #(= (:id creature) (:id %)) static-creatures/creatures))

(defn load-from-storage! []
  (let [storage-keys (filter #(.startsWith % "creature-")
                             (js/Object.keys js/localStorage))
        stored-creatures (map #(-> (js/localStorage.getItem %)
                                   (js/JSON.parse)
                                   (js->clj :keywordize-keys true))
                              storage-keys)]
    stored-creatures))

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
  (let [new-creature (if (static-creature? creature)
                      (assoc creature :id (str (.getTime (js/Date.))))
                      creature)
        key (str "creature-" (:id new-creature))
        value (-> new-creature
                 clj->js
                 (js/JSON.stringify nil 2))]
    (js/localStorage.setItem key value)
    ;; Refresh the app's creature list
    (set! creatures (load-from-storage!))
    (set! creatures-by-id (into {} (map (fn [c] [(:id c) c]) creatures)))
    ;; Return the saved creature
    new-creature))


(defn delete-creature! [creature]
  (when-not (static-creature? creature)
    (let [key (str "creature-" (:id creature))]
      (js/localStorage.removeItem key)
      ;; Refresh the app's creature list
      (set! creatures (load-from-storage!))
      (set! creatures-by-id (into {} (map (fn [c] [(:id c) c]) creatures))))))

