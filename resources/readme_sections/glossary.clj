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
        " or markdown file that contains benchmarks results consisting of
 charts, text, and tables."]]]
 
 [:dl
  [:dt#group "group"]
  [:dd [:p "One or more conceptually-related benchmarks, e.g., all benchmarks
 that demonstrate the performance of "
        [:code "map"]
        "."]]]

 [:dl
  [:dt#name "name"]
  [:dd [:p "A tag that refers to a benchmark definition. Supplied to "
        [:code "defbench"]
        " as a simple symbol. Insertion into the registry prepends the namespace
 (making it namespace-qualified), to which a benchmark definition is
 associated."]]]

 [:dl
  [:dt#registry "registry"]
  [:dd [:p "A singular collection of benchmark definitions. An atom-wrapped
 hashmap whose keys are namespace-qualified symbols ("
        [:a {:href "#name"} "names"]
        ") associated to a set of benchmark definitions ("
        [:a {:href "#group"} "group"]
        ", function expression, arguments). Any other property of the registry
 is an implementation detail an not guaranteed."]]]

 [:dl
  [:dt#version "version"]
  [:dd [:p "A notable release of software, labeled by a version number."]]]]

