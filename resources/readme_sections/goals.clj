[:section#goals
 [:h2 "Non-goals"]

 [:p "Fastester does not aspire to be:"]

 [:ul
  [:li 
   [:p [:strong "A diagnostic profiler."]
    " Fastester will not "
    [:a {:href "https://github.com/clojure-goes-fast/clj-async-profiler"}
     "locate bottlenecks"]
    ". It is only intended to communicate performance changes when a new version
 behaves differently than a previous version. I.e., we've already located the
 bottlenecks and made it quicker. Fastester is a release task, not a dev-time
 tool."]]

  [:li
   [:p [:strong "A comparative profiler."]
    " Fastester doesn't address if "
    [:em "My Clojure function runs faster than that OCaml function"]
    ", and, in fact, isn't intended to demonstrate "
    [:em "My Clojure function runs faster than someone else's Clojure function."]
    " Fastester focuses on comparing benchmark results of one particular
 function to a previous version of itself."]]

  [:li
   [:p [:strong "A general-purpose charting facility."] " Apart from choosing
 whether a chart axis is linear or logarithmic, any other charting option like
 color, marker shape, etc., will not be adjustable."]]]]

