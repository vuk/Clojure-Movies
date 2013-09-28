(ns web.home
  (:use compojure.core)
  (:require [compojure.route :as route]
            [db.mongo :refer :all])
  (:use ring.adapter.jetty))


(defroutes app
  (GET "/" [] (getallmovies 0 10))
  (GET "/list" [] "<h1>Hello World</h1>")
  (route/not-found "<h1>Page not found</h1>"))
 
(run-jetty app {:port 8080})