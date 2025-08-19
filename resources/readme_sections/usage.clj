[:section#usage
 [:h2 "Usage"]

 [:p "Prologue: We have previously profiled some execution path of version 11 of
 our library. We discovered a bottleneck in a function, "
  [:code "zap"]
  ", which just so happens to behave exactly like "
  [:code "clojure.core/map"]
  ": apply some function to every element of a collection."]

 (do
   (def zap map)
   nil)

 [:pre (print-form-then-eval "(zap inc [1 2 3])")]

 [:p "We then changed "
  [:code "zap"]
  "'s implementation for better performance and objectively verified that this
 new implementation for version 12 provides shorter execution times than the
 previous version."]

 [:p "We're now ready to release version 12 with the updated "
  [:code "zap"]
  ". After "
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
 measurable affects on performance."]

 [:h3#set-options "1. Set the options"]

 [:p "We must first set the options that govern how Fastester behaves. Options
 live in the file "
  [:code "fastester_options.edn"]
  " as a Clojure map. One way to get up and running quickly is to copy-paste
 Fastester's "
  [:a {:href "https://github.com/blosavio/fastester/blob/main/resources/fastester_options.edn"}
   "sample options file"]
  " and edit as needed."]

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
   [:p "Set of filenames from which to load benchmark tests."])

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
   :save-benchmark-fn-results?
   [:p "When assigned "
    [:code "true"]
    ", includes the value returned from evaluating the benchmark expression.
 Useful during development to check the correctness of the benchmark expression.
 However, file sizes grow unwieldy. Setting to "
    [:code "false"]
    " replaces the data with "
    [:code ":fastester/benchmark-fn-results-elided"]
    "."])

  (opts-table-row
   :tidy-html?
   [:p "Default setting causes html to be written to file with no line breaks.
 If set to "
    [:code "true"]
    " line breaks are inserted for readability, and for smaller version
 control diffs."])

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
    " to skip."])

  (opts-table-row
   :preferred-version-info
   :pom-xml
   [:p "Declares preference for source of project version. If "
    [:code ":lein"]
    ", consults 'project.clj'. If "
    [:code ":pom-xml"]
    ", consults 'pom.xml'. If both files exist, a preference must be declared.
 If only one file exists, "
    [:code ":preferred-version-info"]
    " may be omitted."])]

 [:h3#write-benchmarks "2. Write benchmarks"]

 [:p "Writing benchmarks follows a similar pattern to writing unit tests. We
 make a file (defaulting to " [:code
                               (str
                                (fastester-defaults :benchmarks-directory)
                                (fastester-defaults :benchmarks-filenames))]
  ") with a namespace declaration. See these "
  [:a {:href "https://github.com/blosavio/fastester/blob/main/test/fastester/performance/benchmarks.clj"}
   "two"]
  " "
  [:a {:href "https://github.com/blosavio/fastester/blob/main/test/fastester/performance/benchmarks_mapping.clj"}
   "examples"]
  ". For organizing purposes, we may write more than one benchmarks file, if,
 for example, we'd like to write one benchmark file per source namespace."]

 [:p "Before we start bashing the keyboard, let's think a little about how we
 want to test "
  [:code "zap"]
  ". We'd like to demonstrate "
  [:code "zap"]
  "'s improved performance for a variety of argument types, over a wide range of
 argument sizes. To do so, we decide to write two benchmarks. The first
 benchmark will measure the evaluation times of increasingly-lengthy sequences
 of integers. The second benchmark will measure the evaluation times of
 upper-casing ever-longer sequences of strings. We will therefore need to write
 two benchmarks."]

 [:p "Within a benchmarks file, we use "
  [:code "defbench"]
  " to "
  [:strong "def"]
  "ine a "
  [:strong "bench"]
  "mark. Here is its signature."]

 [:pre [:code "(defbench "
        [:em "name \"group\" fn-expression args"]
        ")"]]

 [:p "For the first argument, we supply "
  [:code "defbench"]
  " with a "
  [:em "name"]
  ", an unquoted symbol. The name labels the three trailing arguments. For our
 scenario, let's name our two benchmarks with the symbols "
  [:code "zap-inc"]
  " and "
  [:code "zap-uc"]
  "."]

 [:p "Note that all benchmarks will be added to a singular "
  [:a {:href "#registry"} "registry"]
  ", so if we write two "
  [:code "defbench"]
  " expressions using the same name, the later will overwrite the former
 (any particular ordering is an implementation detail and not guaranteed). If
 we'd like to give the same name to two different benchmarks (particularly the
 same name from two different namespaces), we can use "
  [:a {:href "https://clojure.org/reference/reader#_symbols"}
   "qualified symbols"]
  ". In the scenario of benchmarking "
  [:code "zap"]
  ", we've chosen two different names, so we won't worry about overwriting."]

 [:p "After the name, we supply a "
  [:em "group"] ", a string that associates this benchmark with other
 conceptually-related benchmarks. For example, while we're benchmarking "
  [:code "zap"]
  ", we will want to test its performance in different ways, varying the size of
 arguments, with different types of arguments, e.g., integers, strings, etc. The
 ultimate html document will gather into a section the benchmarks sharing a
 group. Let's assign both our benchmarks to the "
  [:code "\"faster zap implementation\""]
  " group."]

 [:p "The final two arguments, "
  [:code "fn-expression"]
  " and "
  [:code "args"]
  ", share the heavy lifting. The next step, "
  [:a {:href "#run-benchmarks"} [:em "running the benchmarks"]]
  " will entail serially supplying elements of "
  [:code "args"]
  " to the function expression."]

 [:p  "The function expression is a 1-arity function that demonstrates some
 performance aspect of the new version of the function. We updated "
  [:code "zap"]
  " so that it processes elements faster. One way to demonstrate its improved
 performance is to increment a sequence of integers. That expression might
 look like this."]

 [:pre [:code "(fn [n] (zap inc (range n)))"]]

 [:p "'Running' that benchmark means that a series of "
  [:code "n"]
  " arguments are passed to the expression, measuring the evaluation times for
 each "
  [:code "n"]
  ". The values for "
  [:code "n"]
  " are supplied by the final part of the benchmark definition. Let's explore
 "
  [:code "range"]
  "s from ten to one-hundred thousand."]

 [:pre [:code "[10 100 1000 10000 100000]"]]

 [:p "Ratcheting "
  [:code "(range n)"]
  " by powers of ten stresses "
  [:code "zap's"]
  " performance. Roughly speaking, we'll be doing this."]

 [:pre
  [:code "(benchmark (zap inc (range 10)))"] [:br]
  [:code "(benchmark (zap inc (range 100)))"] [:br]
  [:code "(benchmark (zap inc (range 1000)))"] [:br]
  [:code "(benchmark (zap inc (range 10000)))"] [:br]
  [:code "(benchmark (zap inc (range 100000)))"]]

 [:p "Altogether, that benchmark definition looks like this."]

 [:pre [:code
        "(defbench zap-inc
          \"faster zap implementation\"
          (fn [n] (zap inc (range n)))
          [10 100 1000 10000 100000])"]]

 [:p "However, there's a problem. This benchmark's function expression contains "
  [:code "range"]
  ". If we run this benchmark as is, the evaluation time would include "
  [:code "range"]
  "'s processing time. We may want to do so in other cases, but in this
 scenario, it would be misleading. We want to focus solely on how fast "
  [:code "zap"]
  " can process its elements. Let's extract "
  [:code "range"]
  " to an external expression."]

 [:pre
  [:code "(def range-of-length-n (reduce #(assoc %1 %2 (range %2)) {} [10 100 1000 10000 100000]))"]
  [:br]
  [:br]
  [:br]
  [:code "(defbench zap-inc
          \"faster zap implementation\"
          (fn [n] (zap inc (range-of-length-n n)))
          [10 100 1000 10000 100000])"]]

 [:p [:code "range-of-length-n"]
  " generates all the sequences ahead of time. With the sequences now created
 outside of the benchmark expression, the time measurement will mainly reflect
 the work done by "
  [:code "zap"]
  " itself."]

 [:p "Perhaps you anticipated a remaining problem if you extrapolated that "
  [:code "zap"]
  " behaves like "
  [:code "map"]
  ". If we were to run the "
  [:code "zap-inc"]
  " benchmarks as defined above, we'd notice that the evaluation times were
 suspiciously consistent, no matter how many integers the sequence contained. "
  [:code "zap"]
  ", like many core sequence functions, returns a lazy sequence. We must force
 the return sequence to be realized so that "
  [:code "zap-inc"]
  " measures what we intend. "
  [:code "doall"]
  " is handy for that."]

 [:pre [:code
        "(defbench zap-inc
          \"faster zap implementation\"
          (fn [n] (doall (zap inc (range-of-length-n n))))
          [10 100 1000 10000 100000])"]]

 [:p "We want to define another benchmark that exercises "
  [:code "zap"]
  " by upper-casing strings. We picked a name earlier "
  [:code "zap-uc"]
  ", so now we'll define the benchmark, analogous to "
  [:code "zap-inc"]
  ". First, we'll pre-compute the test sequences so that running the benchmark
 doesn't measure evaluating "
  [:code "cycle"]
  "."]

 [:pre
  [:code
   "(def abc-cycle-of-length-n
     (reduce #(assoc %1 %2 (take %2 (cycle [\"a\" \"b\" \"c\"])))
             {}
             [10 100 1000 10000 100000]))"]]

 [:p "Next, we'll "
  [:code "require"]
  " the string-handler, then define the benchmark."]
 
 [:pre
  (print-form-then-eval "(require '[clojure.string :as str])")
  [:br]
  [:br]
  [:br]
  [:code
   "(defbench zap-uc
          \"faster zap implementation\"
          (fn [n] (doall (zap str/upper-case (abc-cycle-of-length-n n))))
          [10 100 1000 10000 10000])"]]

 [:p "Since both "
  [:code "zap-inc"]
  " and "
  [:code "zap-uc"]
  "  measure "
  [:code "zap"]
  ", both share the same group: "
  [:code "\"faster zap implementation\""]
  ". Their benchmark results will appear under that heading in the html report."]

 [:p "Take a moment to discuss the registry..."]


 [:p "The registry now contains two benchmarks that will demonstrate "
  [:code "zap"]
  "'s performance: one incrementing sequences of integers, and one upper-casing
 sequences of strings."]

 [:p "Fastester provides a few helper utilities. If we want to see how a
 benchmark would work, we can invoke "
  [:code "run-one-registered-benchmark"]
  ". In the course of writing benchmarks, we often need a sequence of
 exponentially-growing integers. For that, Fastester offers "
  [:code "range-pow-10"]
  " and "
  [:code "range-pow-2"]
  "."]

 [:pre
  (print-form-then-eval "(range-pow-10 5)")
  [:br]
  [:br]
  (print-form-then-eval "(range-pow-2 5)")]

 [:p "Sometimes, we'll want to remove a defined benchmark, which we can do
 with "
  [:code "undefbench"]
  ". And when we need to tear everything down and start defining from scratch,
 we have "
  [:code "clear-registry!"]
  "."]

 [:h3#run-benchmarks "3. Run benchmarks"]

 [:p "Now that we've written "
  [:code "zap-inc"]
  " and "
  ["zap-uc"]
  ", we can run the benchmarks in two ways. If we've got our editor open with an
 attached  "
  [:span.small-caps "repl"]
  ", we can invoke "
  [:code "(run-all-benchmarks)"]
  ". If we're at the command line, invoking "]

 [:pre [:code "$ lein run :all"]]

 [:p " has the same effect."]

 [:p "We should expect each benchmark to take about a minute with the default
 benchmark settings. To minimize timing variance, we ought to use a multi-core
 machine with minimal competing processes, network activity, etc."]

 [:p "We should see one "
  [:code "edn"]
  "file per benchmark. If not, double check the options hashmap to make sure our
 benchmarks are not contained in the "
  [:code ":excludes"]
  " set."]

 [:h3#generate "4. Generate the " [:span.small-caps "html"]]

 [:p "When the benchmarks are finished running, we can generate the performance
 report. Sometimes it's useful to have an "
  [:span.small-caps "html"]
  " file to quickly view in the browser, and other times it's useful to have a
 markdown file (i.e., to show on Github), so Fastester generates one of each."]

 [:p "To generate the documents, we can invoke "
  [:code "(generate-documents)"]
  " at the "
  [:span.small-caps "repl"]
  ", or "
  [:code "$ lein run :gen"]
  " from the command line."]

 [:p "When we look at the report, there's only version 12! We wanted a "
  [:a {:href "#comparative"} "comparative"]
  " report which shows how the performance of version 12's "
  [:code "zap"]
  " has improved "
  [:em "relative"]
  " to version 11's "
  [:code "zap"]
  ". So, we use version control to roll-back to the version 11 tag, run the
 benchmarks with version 11. Then, we roll-forward again to version 12."]

 [:p "Now the charts and tables show the version 12 benchmark measurements
 side-by-side with version 11's, similar to the "
  [:a {:href "#intro"} "introduction example"]
  ". We can clearly see that the new "
  [:code "zap"]
  " implementation executes faster across a wide range of sequence lengths, both
 for incrementing integers and upper-casing strings."]

 [:h3#gotchas "Gotchas"]

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
 buffer redefines only the benchmarks currently expressed in the file."]]

