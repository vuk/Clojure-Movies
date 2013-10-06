(ns Helpers.htmlhelp
  (:require [hiccup.core :refer :all]
            [hiccup.page :refer :all]))

(defn returnmenu 
  "Return menu"
  []
  [:div {:class "navbar"}
   [:div {:class "navbar-inner"}
    [:ul {:class "nav"}
     [:li
      [:a {:href "/"}
       "Home"]]
     ]
    [:form {:class "navbar-search pull-left" :id "search-form" :action "/search" :method "GET"}
     [:input {:type "text" 
              :class "search-query" 
              :placeholder "Search" 
              :id "search-field"
              :style "height: 30px"
              :name "query"}]
     ]
    ]
   ])