(ns cosmere-tools.core
  (:require
   [cosmere-tools.pages.about :refer [about-page]]
   [cosmere-tools.pages.edit :refer [edit-page]]
   [cosmere-tools.pages.home :refer [home-page]]
   [cosmere-tools.router :as router]
   [cosmere-tools.utils :refer [prevent-default]]
   [reagent.dom :as rdom]))

(defn navbar []
  [:nav.navbar
   [:div.navbar-content
    [:a.brand {:href "/home" 
               :on-click #(router/navigate! :home)} 
     "Cosmere Tools"]
    [:div.nav-links
     [:a {:href "/home"
          :on-click (prevent-default #(router/navigate! :home))} "Home"]
     [:a {:href "/create"
          :on-click (prevent-default #(router/navigate! :edit))} "Create"]  ; No params = new creature
     [:a {:href "/about"
          :on-click (prevent-default #(router/navigate! :about))} "About"]]]])

(defn page-container []
  [:div.page-container
   (case (:handler @router/current-route)
     :home [home-page]
     :edit [edit-page]  ; Edit page will now receive route params automatically
     :about [about-page]
     [home-page])])  ; Default to home page

(defn app []
  [:div
   [navbar]
   [:main.main-content
    [:div.content-container
     [page-container]]]])

(defn ^:export init []
  (router/init!)  ; Initialize router
  (rdom/render [app]
               (.getElementById js/document "app")))

(defn ^:export ^:dev/after-load reload []
  (init))