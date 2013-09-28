(ns db.mongo
  (:require [somnium.congomongo :as m]
            [hiccup.core :refer :all]))

(def conn
  (m/make-connection "movies"
                     :host "127.0.0.1"
                     :port 27017))
(m/set-connection! conn)

(defn insertmovie
  "Insert movie into database"
  [movie]
  (m/insert! :movie
             movie)
  (println movie))

(defn echosingle
  "Echo single movie"
  [movie]
  [:div {:class "row"} 
	 [:div {:class "span3"} (get movie :name)]
	 [:div {:class "span3"} (get movie :datepublished)]
	 [:div {:class "span6"} (get movie :description)]
	 ]
  )

(defn echofirst
  "Echo single movie"
  ([movies]
  (if (not (empty? movies))
    ;;first pass - no previous response - wrap current response in container
    (do
      (echofirst (rest movies) (into [:div {:class "container"}] [(echosingle (first movies))]))
      )
    ))
  ([movies response]
    (if (not (empty? movies))
      ;;every other pass - append current response to previous one and pass it into next iteration
    (do
      (recur (rest movies) (into response [(echosingle (first movies))]))
      )
      ;;return response when list is empty
    response
    ))
  )

(defn returnall
  "Create structure for all movies"
  [movies]
  ;(println movies)
  (html (if (not (empty? movies))
          (echofirst movies)
          )
        )
  )

(defn getallmovies
  "Get movies from database"
  [offset limit]
  (def movies (m/fetch :movie
                       :skip offset
                       :limit limit))
  ;(println movies)
  (returnall movies)
  )

