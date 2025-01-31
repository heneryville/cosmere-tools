(ns cosmere-npc.creature-library
  #?(:clj
     (:require
      [clojure.java.io :as io]
      [clojure.data.json :as json]))
  #?(:cljs
     (:require-macros [cosmere-npc.creature-library :refer [import-creatures]])))


#?(:clj
   (defmacro import-creatures []
     (let [creature-dir "creatures"
           creatures (->> creature-dir
                          io/file
                          file-seq
                          (filter #(.isFile %))
                          (filter #(.endsWith (.getName %) ".json"))
                          (mapv (fn [file]
                                  (-> (slurp file)
                                      (json/read-str :key-fn keyword)))))]
       creatures)))

#?(:cljs
   (def creatures (import-creatures)))