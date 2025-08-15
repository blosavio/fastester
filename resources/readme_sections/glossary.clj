[:section#glossary
 [:h2 "Glossary"]

 [:dl
  [:dt#benchmark "benchmark"]
  [:dd [:p "An assembly of a 1-arity function expression, a sequence of
 arguments, a name, and a group. We "
        [:em "define"]
        " or "
        [:em "write"]
        " a benchmark. Criterium "
        [:em "runs"]
        " benchmarks."]]]

 [:dl
  [:dt#document "document"]
  [:dd [:p "An "
        [:span.small-caps "html"]
        " or markdown file that contains benchmarks results consisting of,
 charts, text, and tables."]]]
 
 [:dl
  [:dt#group "group"]
  [:dd [:p "One or more conceptually-related benchmarks, e.g., all benchmarks
 that demonstrate the performance of "
        [:code "map"]
        "."]]]

 [:dl
  [:dt#name "name"]
  [:dd [:p "A tag that refers to a benchmark definition. Currently a Clojure
 symbol which to which a value is associated in the registry. In a future
 version, a symbol which simply maps to a namespace var."]]]

 [:dl
  [:dt#registry "registry"]
  [:dd [:p "Soon to be deprecated..."]]]

 [:dl
  [:dt#version "version"]
  [:dd [:p "A notable release of software, labeled by a version number."]]]]

