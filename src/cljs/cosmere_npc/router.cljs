(ns cosmere-npc.router
  (:require [bide.core :as bide]
            [reagent.core :as r]))

;; Store current route
(def current-route (r/atom nil))

;; Route definitions
(def routes
  [["/" :home]
   ["/about" :about]])

;; Create router instance
(def router
  (bide/router routes))

;; Route change handler
(defn on-navigate [name params query]
  (reset! current-route {:handler name
                        :params params
                        :query query}))

;; Initialize router
(defn init! []
  (bide/start! router {:default :home
                       :on-navigate on-navigate}))

;; Navigation helpers
(defn navigate! [route]
  (bide/navigate! router route))