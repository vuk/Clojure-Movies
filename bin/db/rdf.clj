(ns db.rdf
  (:require [hiccup.core :refer :all]
            [hiccup.page :refer :all]
            [Helpers.htmlhelp :refer :all]
            [ring.util.response :as resp])
  (:use ring.util.codec)
  (:use ring.middleware.params))

(defn insertmovie
  "Insert movie into rdf database"
  [movie]
  (println movie)  
)