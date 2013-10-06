(ns Movies.core
  (:require [itsy.core :refer :all] 
            [Helpers.helpers :as h]
            [db.mongo :refer :all]))

(defn my-handler [{:keys [url body]}]
  ;;(println url "has a count of" (count body))
  (if (h/urlvalidator url) 
    (if (h/urlismovie url) 
      (insertmovie (h/extractmovie body)) 
      (println url)) 
    (println url)))

(def c (crawl {;; initial URL to start crawling at (required)
               :url "http://www.rottentomatoes.com/"
               ;; handler to use for each page crawled (required)
               :handler my-handler
               ;; number of threads to use for crawling, (optional,
               ;; defaults to 5)
               :workers 5
               ;; number of urls to spider before crawling stops, note
               ;; that workers must still be stopped after crawling
               ;; stops. May be set to -1 to specify no limit.
               ;; (optional, defaults to 100)
               :url-limit -1
               ;; function to use to extract urls from a page, a
               ;; function that takes one argument, the body of a page.
               ;; (optional, defaults to itsy's extract-all)
               :url-extractor extract-all
               ;; http options for clj-http, (optional, defaults to
               ;; {:socket-timeout 10000 :conn-timeout 10000 :insecure? true})
               :http-opts {}
               ;; specifies whether to limit crawling to a single
               ;; domain. If false, does not limit domain, if true,
               ;; limits to the same domain as the original :url, if set
               ;; to a string, limits crawling to the hostname of the
               ;; given url
               :host-limit true}))

;; ... crawling ensues ...

(thread-status c)
;; returns a map of thread-id to Thread.State:
{33 #<State RUNNABLE>, 34 #<State RUNNABLE>, 35 #<State RUNNABLE>,
 36 #<State RUNNABLE>, 37 #<State RUNNABLE>, 38 #<State RUNNABLE>,
 39 #<State RUNNABLE>, 40 #<State RUNNABLE>, 41 #<State RUNNABLE>,
 42 #<State RUNNABLE>}

(add-worker c)
;; adds an additional thread worker to the pool

(remove-worker c)
;; removes a worker from the pool

(stop-workers c)
;; stop-workers will return a collection of all threads it failed to
;; stop (it should be able to stop all threads unless something goes
;; very wrong)