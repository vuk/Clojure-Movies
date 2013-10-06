# Movies

A Clojure application that uses itsy crawler to crawl Movie website rottentomatoes.com and store 
structured movie data according to schema.org into Mongo database.

On the other hand application has its web part that allows user to list, search and edit movie data.

##Dependencies and their usage

[itsy "0.1.1"] - used to crawl rottentomatoes.com
[enlive "1.1.4"] - used to extract itemprop attributes from html itsy retrieved
[congomongo "0.4.1"] - used to make database operations
[ring "1.2.0"] - web framework
[hiccup "1.0.4"] - used to generate dynamic HTML
[compojure "1.1.5"] - routing library for Clojure


## License

Copyright © 2013 Vuk Stankovic

Distributed under the Eclipse Public License, the same as Clojure.
