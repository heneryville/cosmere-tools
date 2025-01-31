(ns cosmere-npc.core
  (:require [reagent.core :as r]
            [reagent.dom :as rdom]
            [cosmere-npc.router :as router]
            [cosmere-npc.pages.home :refer [home-page]]
            [cosmere-npc.pages.library :refer [library-page]]
            [cosmere-npc.pages.about :refer [about-page]]))

(defn navbar []
  [:nav.navbar
   [:div.navbar-content
    [:a.brand {:href "#/" 
               :on-click #(router/navigate! :home)} 
     "Cosmere NPC"]
    [:div.nav-links
     [:a {:href "#/"
          :on-click #(router/navigate! :home)} "Home"]
     [:a {:href "#/"
          :on-click #(router/navigate! :library)} "Library"]
     [:a {:href "#/about"
          :on-click #(router/navigate! :about)} "About"]]]])

(defn page-container []
  [:div.page-container
   (case (:handler @router/current-route)
     :home [home-page]
     :library [library-page]
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