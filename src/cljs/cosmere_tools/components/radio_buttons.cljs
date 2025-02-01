(ns cosmere-tools.components.radio-buttons)

(defn radio-buttons [{:keys [options selected on-change class-prefix]}]
  [:div.radio-buttons
   (for [option options]
     ^{:key option}
     [:button.radio-button
      {:class [(when (= option selected) "active")
               (when class-prefix (str class-prefix option))]
       :on-click (fn [e]
                   (.preventDefault e)
                   (on-change option))}
      (if class-prefix
        [:span.visually-hidden option]
        option)])])