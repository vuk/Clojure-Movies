(ns Helpers.helpers
  (:require [clojure.string :as string][clojure.core :refer :all])
  (:require [net.cgrand.enlive-html :as e])
  (:import java.net.URL))

(defn ^String substring?
  "True if s contains the substring."
  [substring ^String s]
  (.contains s substring))

(defstruct movie :name :description :contentrating :duration :genre :datepublished :productioncompany :image :type :uri)

(defn urlvalidator 
  "Validate if url should be used. Only movie and actor urls are taken into account"
  [url]
  (if (or (and (substring? "http://www.rottentomatoes.com/m/" url) 
               (not (substring? "?" url)) 
               (not (substring? "/movies_like_" url)) 
               (not (substring? "/news/" url)) 
               (not (substring? "/forum" url))) 
          (and (substring? "http://www.rottentomatoes.com/celebrity/" url) 
               (not (substring? "?" url))))
    true false
  )
)

(defn urlismovie
  "Check if url is movie url on rottentomatoes.com"
  [url]
  (if (and (substring? "http://www.rottentomatoes.com/m/" url) 
               (not (substring? "?" url)) 
               (not (substring? "/movies_like_" url)) 
               (not (substring? "/news/" url)) 
               (not (substring? "/forum" url))) 
    true
    false))

(defn getitemprop 
  "Extract attribute from HTML"
  [html value]
  (def itemprop (e/select-nodes* (e/html-snippet html)
                 [(e/attr= :itemprop value) e/text-node]))
  (first itemprop))

(defn getimage
  "Extract image src from HTML"
  [html value]
  (def imgtag (e/select-nodes* (e/html-snippet html)
                   [(e/attr= :itemprop value)]))
  (get-in (first imgtag) [:attrs :src]))

(defn extractmovie
  "Extract movie data from HTML"
  [html]
  (def currentmovie (struct-map movie 
             :name (getitemprop html "name") 
             :description (string/trim-newline (string/trim (getitemprop html "description")))
             :contentrating (getitemprop html "contentrating")
             :duration (getitemprop html "duration")
             :genre (getitemprop html "genre")
             :image (getimage html "image")
             :datepublished (getitemprop html "datepublished")
             :productioncompany (getitemprop html "productioncompany")
             :type "http://schema.org/Movie"))
  currentmovie
  )







