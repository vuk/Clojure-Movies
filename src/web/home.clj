(ns web.home
  (:use compojure.core)
  (:require [compojure.route :as route]
            [db.mongo :refer :all]
            [compojure.handler :as handler])
  (:use ring.adapter.jetty)
  (:use ring.util.codec))

(defn parse-int [s]
   (Integer. (re-find  #"\d+" s )))

(defroutes app
  (GET "/" [] (getallmovies 0 10))
  (GET "/:offset" [offset] 
       (getallmovies (parse-int offset) 10))
  (GET "/movie/:id" [id]
       (getsingle id))
  (GET "/search/:query" [query]
       (findmovie query))
  (route/not-found "<h1>Page not found</h1>"))
 
(run-jetty (handler/site app) {:port 8080})