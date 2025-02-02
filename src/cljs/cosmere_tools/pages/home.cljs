(ns cosmere-tools.pages.home
  (:require
   [cosmere-tools.components.creature-card :refer [creature-card]]
   [cosmere-tools.components.creature-editor :refer [creature-editor]]
   [cosmere-tools.creature-library :refer [creatures]]
   [reagent.core :as r]))

(defn handle-file-upload [e creature-atom]
  (let [file (-> e .-target .-files (aget 0))
        reader (js/FileReader.)]
    (set! (.-onload reader)
          #(let [content (-> % .-target .-result)
                 json-data (js->clj (js/JSON.parse content) :keywordize-keys true)]
             (reset! creature-atom json-data)))
    (.readAsText reader file)))

(defn home-page []
  (let [creature (r/atom (first creatures))]
    (fn []
      [:div.page.home-page
       [:div.file-upload
        [:input {:type "file"
                 :accept "application/json"
                 :on-change #(handle-file-upload % creature)}]]
       [:div.editor-pane
        [:div.editor-column
         [creature-editor {:creature @creature
                           :on-change #(reset! creature %)}]]
        [:div.preview-column
         ]]])))