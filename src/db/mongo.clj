(ns db.mongo
  (:require [somnium.congomongo :as m]))

(defn insertmovie
  "Insert movie into database"
  [movie]
  (m/insert! :movie
             movie)
  (println movie))