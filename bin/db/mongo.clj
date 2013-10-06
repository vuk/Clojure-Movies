(ns db.mongo
  (:require [somnium.congomongo :as m]
            [hiccup.core :refer :all]
            [hiccup.page :refer :all]
            [Helpers.htmlhelp :refer :all]
            [ring.util.response :as resp])
  (:use ring.util.codec)
  (:use ring.middleware.params))

(def conn
  (m/make-connection "movies"
                     :host "127.0.0.1"
                     :port 27017))
(m/set-connection! conn)

(defn insertmovie
  "Insert movie into database"
  [movie]
  (m/insert! :movie movie))

(defn echosingle
  "Echo single movie"
  [movie]
  [:div {:class "row"} 
   [:div {:class "span3"} [:a {:href (clojure.string/join ["/movie/" (get movie :_id)])} (get movie :name)]]
   [:div {:class "span3"} (get movie :datepublished)]
   [:div {:class "span6"} (get movie :description)]
   [:div {:class "clearfix"}]
   [:hr]
   ])

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
  [movies offset]
  ;(println movies)
  (html 
    [:html
     [:head
      [:title "Naslovna"]
      (include-css "http://puskice.org/assets/css/bootstrap.css")
      (include-js "//ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js")
      (include-js "http://puskice.org/assets/js/is.js")
      ]
     [:body 
      [:div {:class "container"}
       (returnmenu)
       [:h1 "Movies"]
       (if (not (empty? movies))
         (echofirst movies)
         )
       (if (< 9 offset)
         [:div {:class "row" :style "height: 60px"} 
          [:div {:class "span6"}
           [:a {:href (str "/" (- offset 10))} "<< Prethodna stranica"]
           ]
          [:div {:class "span6"}
           [:a {:href (str "/" (+ offset 10))} "Sledeca stranica >>"]
           ]
          ]
         [:div {:class "row" :style "height: 60px"}
          [:div {:class "span12"}
           [:a {:href (str "/" (+ offset 10))} "Sledeca stranica >>"]
           ]
          ])
       ]
      ]
     ]))

(defn getallmovies
  "Get movies from database"
  [offset limit]
  (def movies (m/fetch :movie
                       :skip offset
                       :limit limit))
  ;(println movies)
  (returnall movies offset)
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
      (include-css "http://puskice.org/assets/css/bootstrap.css")
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
          [:p [:strong "Production Company: "] (get movie :productioncompany)]
          [:p [:strong "Content rating: "] (get movie :contentrating)]
          [:p [:strong "Genre: "] (get movie :genre)]
          [:a {:href (str "/edit/" (get movie :_id))} [:p [:strong "Edit: "] (get movie :name)]]]
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


(defn geteditsingle
  "Get single movie"
  [id]
  (def movie (m/fetch-one :movie
                          :where {:_id (m/object-id id)}))
  (println movie)
  (html 
    [:html
     [:head
      [:title (str "Movie - " (get movie :name))]
      (include-css "http://puskice.org/assets/css/bootstrap.css")
      (include-js "//ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js")
      (include-js "http://puskice.org/assets/js/is.js")
      ]
     [:body 
      [:div {:class "container"}
       (returnmenu)
       [:h1 (get movie :name)]
       [:div {:class "well"}
        [:form {:action "/save"
                :method "post"}
         [:input {:type "hidden"
                  :name "id"
                  :value (get movie :_id)}]
         [:div {:class "row"}
          [:div {:class "span3"}
           [:p "Name: "]
           [:input {:name "name"
                    :type "text"
                    :value (get movie :name)}]
	          [:p "Duration: "]
	          [:input {:name "duration"
                    :type "text"
                    :value (get movie :duration)}]
	          [:p "Publish date: "]
	          [:input {:name "datepublished"
                    :type "text"
                    :value (get movie :datepublished)}]
	          [:img {:src (get movie :image)}]
           ]
	         [:div {:class "span6"}
	          [:h2 "Description"]
	          [:textarea {:name "description"
	                      :cols "80"
	                      :rows "10"} (get movie :description)]
	          [:p [:strong "Production Company: "]]
	          [:input {:name "productioncompany"
                    :type "text"
                    :value (get movie :productioncompany)}]
           [:p [:strong "Genre: "]]
	          [:input {:name "genre"
                    :type "text"
                    :value (get movie :genre)}]
           [:br]
           [:input {:class "btn btn-primary"
                    :type "submit"
                    :name "submit"
                    :value "Save"}]
           ]
          ]
         ]
        ]
       ]
      ]]))

(defn savechanges
  "Save changes made to movie"
  [name description id genre productioncompany datepublished]
  (def movie (m/fetch-one :movie
                          :where {:_id (m/object-id id)}))
  (m/update! :movie movie (merge movie {:name name
                                        :description description
                                        :genre genre
                                        :productioncompany productioncompany
                                        :datepublished datepublished}))
  (resp/redirect (str "/movie/" id)))