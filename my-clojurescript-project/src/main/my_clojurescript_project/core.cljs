(ns my-clojurescript-project.core)

(defn init []
  (js/console.log "Hello, ClojureScript!"))

(defn ^:export main []
  (init))