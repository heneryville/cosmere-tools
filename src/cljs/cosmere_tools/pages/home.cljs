(ns cosmere-tools.pages.home
  (:require [cosmere-tools.creature-library :refer [creatures static-creature?]]
            [cosmere-tools.components.decorated-creature-card :refer [decorated-creature-card]]
            [reagent.core :as r]))

(defn all-tags []
  (concat
   (sort (distinct (mapcat :tags creatures)))
   ["Server" "Local"]))

(defn creature-matches-tags? [creature active-tags]
  (if (empty? active-tags)
    true
    (every? (fn [tag]
              (case tag
                "Server" (static-creature? creature)
                "Local"  (not (static-creature? creature))
                (contains? (set (:tags creature)) tag)))
            active-tags)))

(defn tag-filter-bar [active-tags]
  [:div.tag-filter-bar
   (for [tag (all-tags)]
     ^{:key tag}
     [:button.tag-pill
      {:class    (when (contains? @active-tags tag) "active")
       :on-click #(swap! active-tags
                         (if (contains? @active-tags tag) disj conj)
                         tag)}
      tag])
   (when (seq @active-tags)
     [:button.tag-pill.clear-pill
      {:on-click #(reset! active-tags #{})}
      "✕ Clear"])])

(defn home-page []
  (let [active-tags (r/atom #{})]
    (fn []
      (let [filtered (filter #(creature-matches-tags? % @active-tags) creatures)]
        [:div.page.home-page
         [tag-filter-bar active-tags]
         [:div.creatures-grid
          (doall
           (for [creature filtered]
             ^{:key (:id creature)}
             [decorated-creature-card creature]))]]))))