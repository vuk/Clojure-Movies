(ns Movies.core-test
  (:use clojure.test)
  (:require [Helpers.helpers :as h]))

(deftest movie-url-test
    (is (= true (h/urlismovie "http://www.rottentomatoes.com/m/the_wolverine_2012/")))
    (is (= false (h/urlismovie "http://www.rottentomatoes.com/m/the_wolverine_2012/")))
    (is (= true (h/urlismovie "http://www.rottentomatoes.com/m/the_wolverine_2012/forum")))
    (is (= false (h/urlismovie "http://www.rottentomatoes.com/m/the_wolverine_2012/forum"))))

(deftest url-validation 
  (is (= true (h/urlvalidator "http://www.rottentomatoes.com/m/the_wolverine_2012/")))
  (is (= false (h/urlvalidator "http://www.rottentomatoes.com/m/the_wolverine_2012/")))
  (is (= true (h/urlvalidator "http://www.rottentomatoes.com/celebrity/hugh_jackman/")))  
  (is (= false (h/urlvalidator "http://www.rottentomatoes.com/celebrity/hugh_jackman/")))
  )

(run-tests)