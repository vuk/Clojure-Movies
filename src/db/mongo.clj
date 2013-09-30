(ns db.mongo
  (:require [somnium.congomongo :as m]
            [hiccup.core :refer :all]
            [hiccup.page :refer :all]
            [Helpers.htmlhelp :refer :all])
  (:use ring.util.codec))

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
	 [:div {:class "span3"} [:a {:href (clojure.string/join ["/movie/" (get movie :_id)])} (get movie :name)]]
	 [:div {:class "span3"} (get movie :datepublished)]
	 [:div {:class "span6"} (get movie :description)]
  [:div {:class "clearfix"}]
  [:hr]
	 ]
  )

(defn echofirst
  "Echo single movie"
  ([movies]
  (if (not (empty? movies))
    ;;first pass - no previous response - wrap current response in container
    (do
      (echofirst (rest movies) (into [:div] [(echosingle (first movies))]))
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
  (html 
    [:html
     [:head
      [:title "Naslovna"]
      (include-css "//netdna.bootstrapcdn.com/twitter-bootstrap/2.3.1/css/bootstrap-combined.min.css")
      (include-js "//ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js")
      (include-js "http://puskice.org/assets/js/is.js")
      ]
     [:body 
      [:div {:class "container"}
       (returnmenu)
       [:h1 "Movies"]
      (if (not (empty? movies))
          (echofirst movies)
          )]]]
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

(defn getsingle
  "Get single movie"
  [id]
  (def movie (m/fetch-one :movie
                          :where {:_id (m/object-id id)}))
  (println movie)
  (html 
    [:html
     [:head
      [:title (str "Movie - " (get movie :name))]
      (include-css "//netdna.bootstrapcdn.com/twitter-bootstrap/2.3.1/css/bootstrap-combined.min.css")
      (include-js "//ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js")
      (include-js "http://puskice.org/assets/js/is.js")
      ]
     [:body 
      [:div {:class "container"}
       (returnmenu)
       [:h1 (get movie :name)]
       [:div {:class "well"}
        [:div {:class "row"}
         [:div {:class "span3"}
          [:p "Duration: "
           [:i (get movie :duration)]]
          [:p "Publish date: "
           [:i (get movie :datepublished)]]
          [:img {:src (get movie :image)}]
         ]
         [:div {:class "span6"}
          [:h2 "Description"]
          [:p (get movie :description)]
          [:p [:strong "Production Company: "] (get movie :productioncompany)]]
         ]
        ]
       ]
      ]]))

(defn findmovie
  "Find movie by query"
  [query]
  (let [search (re-pattern (str "(?i).*" (url-decode query) ".*"))
        movies (m/fetch :movie :where {:$or [{:name search}, {:description search}]})]
    (returnall movies))
 )
