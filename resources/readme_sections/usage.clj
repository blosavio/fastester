[:section#usage
 [:h2 "Usage"]

 [:p "Prologue: We have previously profiled some path of our code and identified
 a bottleneck. We then changed the implementation and objectively verified that
 this new implementation provides shorter execution times than the previous
 version."]
 
 [:p "After "
  [:a {:href "#setup"} "declaring and requiring"]
  " the dependency, there are four steps to using Fastester." ]

 [:ol
  [:li [:p [:a {:href "#set-options"} "Set"] " the options."]]
  [:li [:p [:a {:href "#write-benchmarks"} "Write"] " benchmarks." ]]
  [:li [:p [:a {:href "#run-benchmarks"} "Run"] " the benchmarks."]]
  [:li [:p [:a {:href "#generate"} "Generate"] " an "
        [:span.small-caps "html"]
        " document that displays the performance data."]]]

 [:p "Steps 1 and 2 are done once, and only occasionally updated as needed.
 Steps 3 and 4 are done only when a function's implementation changes with
 affects on performance."]

 [:h3#set-options "1. Set the options"]

 [:p "We must first set the options that govern how Fastester behaves. Options
 live in the file "
  [:a {:href "https://github.com/blosavio/fastester/blob/main/resources/fastester_options.edn"}
   [:code "resources/fastester_options.edn"]]
  " as a Clojure map."]

 [:p "The following options have "
  [:a {:href "https://blosavio.github.io/fastester.display.html#var-fastester-defaults"}
   "default values"]
  "."]

 (do
   (defn opts-table-row
     "Given keyword `kw` in the Fastester options hashmap, example value `ex`,
  and hiccup/html `usage`, returns a hiccup/html table row. If `ex` is not
  supplied, the default value is referred from `fastester-defaults`."
     {:UUIDv4 #uuid "e4859fa6-7061-44da-b678-ad89f4afc174"
      :no-doc true
      :implementation-note "Use trailing `nil` to soak up return value; hiccup
                            doesn't render `nil`."}
     ([kw usage] (opts-table-row kw (fastester-defaults kw) usage))
     ([kw ex usage]
      [:tr
       [:td [:code (str kw)]]
       [:td [:code (str (if (string? ex)
                          (str "\"" ex "\"")
                          ex))]]
       [:td usage]]))
   nil)

 [:table
  [:tr
   [:th "key"]
   [:th "default"]
   [:th "usage"]]

  (opts-table-row
   :benchmarks-directory
   [:p "Directory from which to load benchmark tests."])

  (opts-table-row
   :benchmarks-filenames
   [:p "Filename from which to load benchmark tests."])

  (opts-table-row
   :html-directory
   [:p "Directory to write html document."])

  (opts-table-row
   :html-filename
   [:p "Filename to write html document."])

  (opts-table-row
   :img-subdirectory
   [:p " Under "
    [:code ":html-directory"]
    ", directory to write svg image files."])

  (opts-table-row
   :markdown-directory
   [:p "Directory to write markdown files."])

  (opts-table-row
   :markdown-filename
   [:p "Filename to write markdown document."])

  (opts-table-row
   :results-url
   [:p "Base URL for where to find benchmark data. For local filesystem, use
 something like "
    [:code "\"../\""]
    "."])

  (opts-table-row
   :results-directory
   [:p "Directory to find benchmark data, appended to "
    [:code ":results-url"]
    "."])

  (opts-table-row
   :excludes
   [:p "A set of benchmark groups (strings) to skip, i.e., "
    [:code "#{}"]
    "  skips zero benchmark groups."])

  (opts-table-row
   :verbose?
   false
   [:p "A boolean that governs printing benchmark status."])

  (opts-table-row
   :testing-thoroughness
   [:p "Assigns Criterium benchmark settings. One of "
    [:code ":default"]
    ", "
    [:code ":quick"]
    ", "
    [:code ":lightning"]
    "."])

  (opts-table-row
   :parallel?
   [:div#parallel?
    [:p "A boolean that governs running benchmarks on multiple threads in
 parallel."]

    [:p "Warning: Running benchmarks in parallel results in undesirable, high
 variance. Associate to "
     [:code "true"]
     " only to quickly verify the value returned from evaluating an expression.
 Do not use for final benchmarking."]])
  

  (opts-table-row
   :n-threads
   [:p "An integer that governs the number of threads to use when "
    [:code ":parallel"]
    " is "
    [:code "true"]
    ". Recommendation: Set to one fewer than machine core count. See warning
 for "
    [:a {:href "#parallel?"} [:code ":parallel?"]]
    "."])

  (opts-table-row
   :save-benchmark-fn-results?
   [:p "When assigned "
    [:code "true"]
    ", includes the value returned from evaluating the benchmark expression.
 Useful during development to check the correctness of the benchmark expression.
 However, file sizes grow unwieldy. Setting to "
    [:code "false"]
    " replaces the data with "
    [:code ":fastester/benchmark-fn-results-elided"]])

  (opts-table-row
   :tidy-html?
   [:p "Default setting causes html to be writen to file with no line breaks.
 If set to "
    [:code "true"]
    " line breaks are inserted for readability, and for better version control."])

  (opts-table-row
   :preamble
   [:div "..."]
   [:p "A hiccup/html block inserted at the top of the results document."])]

 [:p "The following options have no defaults."]

 [:table
  [:tr
   [:th "key"]
   [:th "example"]
   [:th "usage"]]

  (opts-table-row
   :responsible
   {:name "Grace Hopper"
    :email "RDML@univac.net"}
   [:p "A hashmap with "
    [:code ":name"]
    " and "
    [:code "email"]
    " strings that report a person responsible for the report."])

  (opts-table-row
   :copyright-holder
   "Abraham Lincoln"
   [:p "Copyright holder listed in the footer of the document."])

  (opts-table-row
   :fastester-UUID
   #uuid "de280faa-ebc5-4d75-978a-0a2b2dd8558b"
   [:p "A version 4 Universally unique ID listed in the footer of the document.
 To generate a new UUID, evaluate "
    [:code "(random-uuid)"]
    ". Associate to "
    [:code "nil"]
    " to skip."])]

 
 [:h3#write-benchmarks "2. Write benchmarks"]

 [:p "Writing benchmarks follows a similar pattern to writing unit tests. We
 make a file (defaulting to " [:code
                               (str
                                (fastester-defaults :benchmarks-directory)
                                (fastester-defaults :benchmarks-filenames))] ") with a
 namespace declaration. See this "
  [:a {:href "https://github.com/blosavio/fastester/blob/main/test/fastester/performance/benchmarks.clj"}
   "example"]
  "."]

 [:p "Within the benchmarks file, we use "
  [:code "defbench"]
  " to "
  [:strong "def"]
  "ine a "
  [:strong "bench"]
  "mark. Here is its signature."]

 [:pre [:code "(defbench "
        [:em "name \"group\" fn-expression args"]
        ")"]]

 [:p "We supply "
  [:code "defbench"]
  " a name, an unquoted symbol. The benchmark name labels the three following
 arguments."]

 [:p "After the name, we supply a "
  [:em "group"] ", a string that associates this benchmark with other
 conceptually-related benchmarks. For example, if we're benchmarking "
  [:code "map"]
  ", we will want to test its performance in different ways with different types
 of arguments. The ultimate html document will gather the benchmarks sharing a
 group."]

 [:p "The real meat of defining a benchmark lies in the last two arguments. The
 function expression is a 1-arity function that demonstrates some performance
 aspect of the new version of the function. Let's pretend we updated "
  [:code "map"]
  " so that it processes elements faster. One way to test its performance is to 
 increment sequences of integers with increasing lengths. That expression might
 look like this."]

 [:pre [:code "(fn [n] (map inc (range n)))"]]

 [:p "The "
  [:code "n"]
  " argument in the expression is supplied serially by the next part of the
 benchmark definition. Increasing the length by powers of ten explores the
 range of behaviors."]

 [:pre [:code "[10 100 1000 10000 100000]"]]

 [:p "Altogether, that benchmark definition looks like this."]

 [:pre [:code "(defbench map-inc \"faster map implementation\" (fn [n] (map inc (range n))) [10 100 1000 10000 100000])"]]

 [:p "However, there's an issue with this definition. This benchmark's function
 expression includes "
  [:code "range"]
  ". If we run this benchmark as is, the evaluation time would include "
  [:code "range"]
  "'s processing time. We may want that in some cases, but in this example, it
 would be misleading. Let's move that part out."]

 [:pre
  [:code "(def range-of-length-n (reduce #(assoc %1 %2 (range %2)) {} [10 100 1000 10000 100000]))"]
  [:br]
  [:br]
  [:code "(defbench map-inc \"faster map implementation\" (fn [n] (map inc (range-of-length-n))) [10 100 1000 10000 100000])"]]

 [:p "Perhaps you spotted a remaining problem. If we were to run these
 benchmarks, we'd notice that the evaluation times were suspiciously consistent,
 no matter how many integers the sequence contained. "
  [:code "map"]
  " returns a lazy sequence. So we must force the return sequence to be
 realized so the benchmarking measures what we intend. "
  [:code "doall"]
  " is handy for that."]

 [:pre [:code "(defbench map-inc \"faster map implementation\" (fn [n] (doall (map inc (range-of-length-n)))) [10 100 1000 10000 100000])"]]
 

 

 [:h3#run-benchmarks "3. Run benchmarks"]

 [:p "Bar!"]

 [:h3#generate "4. Generate the " [:span.small-caps "html"]]

 [:p "Baz!"]

 [:h3#gotchas]

 [:p "We must be particularly careful to define our benchmarks to test what we
 intend to test. Writing a benchmark using some common Clojure idioms may mask
 the property we're interested in. For example, if we're interested in
 benchmarking mapping over a sequence, if we create the sequence in the map
 expression, Criterium will include that process in addition to the mapping.
, such as defining sequences at the location, will measure"]
 
 [:p "We must be particularly careful to define our benchmarks to test what we
 intend to test. The first problematic pattern is a direct result of Clojure's
 inherent concision. It's idiomatic to compose a sequence right at the spot
 where we require it, like this."]

 [:pre [:code "(map inc (repeatedly 99 #(rand))"]]

 [:p "However, if we were to submit this expression to Criterium, intending to
 measure how long it takes " [:code "map"]
  " to increment the sequence, we'd be "
  [:em "also"]
  " benchmarking creating the sequence, which may be a non-negligible portion of
 the evaluation time. Instead, we should hoist the sequence creation out of the
 expression."]

 [:pre
  [:code ";; *create* the sequence"]
  [:br]
  [:code "(def ninety-nine-rands (repeatedly 99 #(rand))"]
  [:br]
  [:br]
  [:code ";; *use* the pre-existing sequence"]
  [:br]
  [:code "(map inc ninety-nine-rands)"]]

 [:p "The second expression now involves mostly the "
  [:code "map"]
  " action, and is more appropriate for benchmarking."]

 [:p "But, there is another lurking problem! "
  [:code "map"]
  " (and friends) returns a lazy sequence, which is almost certainly not what we
 were intending to benchmark. We must remember to force the realization of the
 lazy sequence, conveniently done with "
  [:code "doall"]
  "."]

 [:pre [:code "(doall (map inc ninety-nine-rands))"]]

 [:p "One final "
  [:em "gotcha"]
  ", particular to Fastester itself, will be familiar to Clojurists programming
 at the "
  [:span.small-caps "repl"]
  ". During development, it's typical to define and re-define benchmarks with "
  [:code "defbench"]
  ". It's not difficult for the namespace to hold stale benchmark definitions
 that are not apparent from visual inspection of the current state of the text
 in the file. Fastester provides a pair of tools to help with this. The "
  [:code "undefbench"]
  " utility undefines a benchmark by name. "
  [:code "clear-registry!"]
  " undefines all benchmarks, and a quick re-evaluation of the entire text
 buffer redefines only the benchmarks currently expressed in the file."]


 ]

