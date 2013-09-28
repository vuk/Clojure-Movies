(ns web.home
  (:use compojure.core)
  (:require [compojure.route :as route]
            [db.mongo :refer :all])
  (:use ring.adapter.jetty))

(defn parse-int [s]
   (Integer. (re-find  #"\d+" s )))

(defroutes app
  (GET "/" [] (getallmovies 0 10))
  (GET "/:offset" [offset] 
       (getallmovies (parse-int offset) 10))
  (GET "/movie/:id" [id]
       (getsingle id))
  (GET "/list" [] "<h1>Hello World</h1>")
  (route/not-found "<h1>Page not found</h1>"))
 
(run-jetty app {:port 8080})