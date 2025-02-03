(ns cosmere-tools.pages.edit
  (:require
   [cosmere-tools.components.creature-card :refer [creature-card download-json]]
   [cosmere-tools.components.creature-editor :refer [creature-editor]]
   [cosmere-tools.creature-library :as creatures]
   [cosmere-tools.router :as router]
   [reagent.core :as r]))

(defn handle-file-select [creature-atom]
  (let [input (doto (js/document.createElement "input")
                (.setAttribute "type" "file")
                (.setAttribute "accept" "application/json"))]
    (.addEventListener input "change" 
                      (fn [e] (let [file (-> e .-target .-files (aget 0))
                                    reader (js/FileReader.)]
                                (set! (.-onload reader)
                                      #(let [content (-> % .-target .-result)
                                             json-data (js->clj (js/JSON.parse content) 
                                                                :keywordize-keys true)]
                                         (reset! creature-atom json-data)))
                                (.readAsText reader file))))
    (.click input)))

(defn edit-controls [creature]
  [:div.edit-controls
   [:button.load-button 
    {:on-click #(handle-file-select creature)} 
    "Load from File"]
   [:button.download-button
    {:on-click #(download-json @creature)}
    "Download"]
   [:button.save-button
    {:on-click #(creatures/save-creature! @creature)}
    (if (creatures/static-creature? @creature) 
      "Save As" 
      "Save")]])

(defn empty-creature []
  {:id (str (.getTime (js/Date.)))
   :name "New Creature"
   :tier 1
   :role "minion"
   :size "medium"
   :type "humanoid"
   ;; Physical attributes
   :strength 0
   :physical-defense 10
   :speed 0
   ;; Cognitive attributes
   :intellect 0
   :cognitive-defense 10
   :willpower 0
   ;; Spiritual attributes
   :awareness 0
   :spiritual-defense 10
   :presence 0
   ;; Derived stats
   :health-min 0
   :health-max 0
   :health-avg 0
   :focus 0
   :investiture 0
   ;; Movement and senses
   :movement 30
   :deflect 0
   :deflect-explanation ""
   :sense-range 30
   :sense-primary nil
   ;; Skills
   :skills {:physical {}
            :cognitive {}
            :spiritual {}}
   :languages ""
   ;; Special abilities
   :traits []
   :actions []
   :strikes []})

(defn edit-page []
  (let [id (get-in @router/current-route [:params :id])
        initial-creature (or (creatures/creatures-by-id id) 
                           (empty-creature))
        creature (r/atom initial-creature)]
    (fn []
      [:div.page.edit-page
       [edit-controls creature]
       [creature-editor {:creature @creature
                        :on-change #(reset! creature %)}]])))
