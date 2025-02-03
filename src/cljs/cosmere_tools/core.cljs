(ns cosmere-tools.core
  (:require [reagent.core :as r]
            [reagent.dom :as rdom]
            [cosmere-tools.router :as router]
            [cosmere-tools.pages.edit :refer [edit-page]]
            [cosmere-tools.pages.home :refer [home-page]]
            [cosmere-tools.pages.about :refer [about-page]]))

(defn navbar []
  [:nav.navbar
   [:div.navbar-content
    [:a.brand {:href "/home" 
               :on-click #(router/navigate! :home)} 
     "Cosmere Tools"]
    [:div.nav-links
     [:a {:href "/home"
          :on-click #(router/navigate! :home)} "Home"]
     [:a {:href "/create"
          :on-click #(router/navigate! :edit)} "Create"]  ; No params = new creature
     [:a {:href "/about"
          :on-click #(router/navigate! :about)} "About"]]]])

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