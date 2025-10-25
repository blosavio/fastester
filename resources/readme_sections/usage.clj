[:section#usage
 [:h2 "Usage"]

 [:p "Let's review our imaginary scenario. We have previously profiled some
 execution path of version 11 of our library. We discovered a bottleneck in a
 function, "
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
  ". When we're writing the changelog/release notes, we want to include a
 performance report that demonstrates the improvement. After "
  [:a {:href "#setup"} "declaring and requiring"]
  " the dependency, there are four steps to using Fastester." ]

 [:ol
  [:li [:p [:a {:href "#set-options"} "Set"] " the options."]]
  [:li [:p [:a {:href "#write-benchmarks"} "Write"] " benchmarks." ]]
  [:li [:p [:a {:href "#run-benchmarks"} "Run"] " the benchmarks."]]
  [:li [:p [:a {:href "#generate"} "Generate"] " an "
        [:span.small-caps "html"]
        " document that displays the performance data."]]]

 [:p "Keep in mind we don't need to do these steps for every function for every
 release, but only when a function's implementation changes with measurable
 affects on performance."]

 [:p "Follow along with this "
  [:a {:href "https://github.com/blosavio/fastester/blob/main/resources/zap_options.edn"}
   "example options file"]
  " and this "
  [:a {:href "https://github.com/blosavio/fastester/blob/main/test/zap/benchmarks.clj"}
   "example benchmark definition file"]
  "."]

 [:h3#set-options "1. Set the options"]

 [:p "We must first set the options that govern how Fastester behaves. Options
 live in a file (defaulting to "
  [:code "resources/fastester_options.edn"]
  ") as a Clojure map. One way to get up and running quickly is to copy-paste
 a "
  [:a {:href "https://github.com/blosavio/fastester/blob/main/resources/zap_options.edn"}
   "sample options file"]
  " and edit as needed."]

 [:p "The following options have "
  [:a {:href "https://blosavio.github.io/fastester/fastester.options.html#var-fastester-defaults"}
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
   :benchmarks
   [:div
    [:p "Hashmap arranging a hierarchy of namespaces and benchmark
 definitions. Keys (quoted symbols) represent namespaces. Values (sets of quoted
 symbols) represent benchmark names. See "
     [:a {:href "#hierarchy"} "discussion"]
     "."]

    [:p "Note: This setting only affects running benchmarks. It does not affect
 which data sets are used to generate the "
     [:span.small-caps "html"]
     " documents."]])

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
    ", directory to write svg image files. If a project's benchmark definitions
 are split among multiple files/namespaces, be sure to give each file/namespace
 their own dedicated image subdirectory."])

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
    ". Note: Every "
    [:code "edn"]
    " file in this directory will be used to create a datum in the document. Errant data files will produce confusing entries in charts and tables."])

  (opts-table-row
   :verbose?
   false
   [:p "A boolean that governs printing benchmark status."])

  (opts-table-row
   :testing-thoroughness
   [:p "Assigns Criterium benchmark settings. One of "
    [:code
     [:a
      {:href "https://github.com/hugoduncan/criterium/blob/bb10582ded6de31b4b985dc31d501db604c0e461/src/criterium/core.clj#L83"}
      ":default"]]
    ", "
    [:code
     [:a
      {:href "https://github.com/hugoduncan/criterium/blob/bb10582ded6de31b4b985dc31d501db604c0e461/src/criterium/core.clj#L92"}
      ":quick"]]
    ", "
    [:code
     [:a
      {:href "https://github.com/blosavio/fastester/blob/d1fccf5e3acfb056ecd9d2e775d62d91b55b04c8/src/fastester/measure.clj#L61"}
      ":lightning"]]
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
    ", the results file will include the value returned from evaluating the
 benchmark expression. Useful during development to check the correctness of the
 benchmark expression. However, file sizes grow unwieldy. Setting to "
    [:code "false"]
    " replaces the data with "
    [:code ":fastester/benchmark-fn-results-elided"]
    "."])

  (opts-table-row
   :tidy-html?
   [:p "Default setting causes "
    [:span.small-caps "html"]
    " to be written to file with no line breaks. If set to "
    [:code "true"]
    ", line breaks are inserted for readability, and for smaller version
 control diffs."])

  (opts-table-row
   :preamble
   [:div "..."]
   [:p "A hiccup/"
    [:span.small-caps "html"]
    " block inserted at the top of the results document."])]

 [:p "The following options have no defaults."]

 [:table
  [:tr
   [:th "key"]
   [:th "example"]
   [:th "usage"]]

  (opts-table-row
   :title
   "Taffy Yo-yo Library performance"
   [:p "A string providing the title for the performance document."])

  (opts-table-row
   :responsible
   {:name "Grace Hopper"
    :email "univac@example.com"}
   [:p "A hashmap with "
    [:code ":name"]
    " and "
    [:code ":email"]
    " strings that report a person responsible for the report."])

  (opts-table-row
   :copyright-holder
   "Abraham Lincoln"
   [:p "Copyright holder listed in the footer of the document."])

  (opts-table-row
   :fastester-UUID
   #uuid "de280faa-ebc5-4d75-978a-0a2b2dd8558b"
   [:p "A version 4 Universally Unique ID listed in the footer of the document.
 To generate a new UUID, evaluate "
    [:code "(random‑uuid)"]
    ". Associate to "
    [:code "nil"]
    " to skip."])

  (opts-table-row
   :preferred-version-info
   :pom-xml
   [:p "Declares preference for source of project version. If "
    [:code ":lein"]
    ", consults 'project.clj'. If "
    [:code ":pom‑xml"]
    ", consults 'pom.xml'. If both files exist, a preference must be declared.
 If only one file exists, "
    [:code ":preferred‑version‑info"]
    " may be omitted."])

  (opts-table-row
   :sort-comparator
   'clojure.core/compare
   [:div
    [:p "Comparator used for sorting versions in the performance document chart
 legends and table rows. Comparator must accept two strings representing
 version entries extracted from either a Leiningen 'project.clj' or a
 'pom.xml'."]

    [:p [:code "clojure.core/compare"]
     " "
     [:a {:href "https://github.com/blosavio/fastester/blob/92bc7634007b2fad8c3ee5f2c738a318688cf005/resources/semver_options.edn#L30"}
      "will"]
     " "
     [:a {:href "https://blosavio.github.io/fastester/semver_performance.html"}
      "sort"]
     " simple sematic versions of
 the form "
     [:code "int.int.int"] " provided they do not have trailing modifiers such
 as "
     [:code "‑SNAPSHOT"]
     ", "
     [:code "‑RC"]
     ", etc."]

    [:p [:a {:href "https://clojure.org/guides/comparators#_mistakes_to_avoid"}
         "Write custom comparators with caution"]
     "."]
    ])]

 [:h3#write-benchmarks "2. Write benchmarks"]

 [:p "Before we start bashing the keyboard, let's think a little about how we
 want to test "
  [:code "zap"]
  ". We'd like to demonstrate "
  [:code "zap"]
  "'s improved performance for a variety of argument types, over a wide range of
 argument sizes. To do that, we'll write two benchmarks."]

 [:ol
  [:li
   [:p "The first benchmark will measure the evaluation times of incrementing
 increasingly-lengthy sequences of integers."]
   [:pre
    [:code "(benchmark (zap inc [1]))"] [:br]
    [:code "(benchmark (zap inc [1 2]))"] [:br]
    [:code "(benchmark (zap inc [1 2 3]))"] [:br]
    [:code "(benchmark (zap inc [1 2 3 ...]))"]]
   [:p "We'll label this series of benchmark runs with the name "
    [:code "zap-inc"]
    " ."]]

  [:li
   [:p "The second benchmark will measure the evaluation times of upper-casing
 ever-longer sequences of strings."]
   [:pre
    [:code "(benchmark (zap str/uppercase [\"a\"]))"] [:br]
    [:code "(benchmark (zap str/uppercase [\"a\" \"b\"]))"] [:br]
    [:code "(benchmark (zap str/uppercase [\"a\" \"b\" \"c\"]))"] [:br]
    [:code "(benchmark (zap str/uppercase [\"a\" \"b\" \"c\" ...]))"] [:br]]
   [:p "We'll label this series of benchmark runs with the name "
    [:code "zap-uc"]
    "."]]]

 [:p "Writing benchmarks follows a similar pattern to writing unit tests. We
 create a file, perhaps named "
  [:a {:href "https://github.com/blosavio/fastester/blob/main/test/zap/benchmarks.clj"}
   "benchmarks.clj"]
  ", topped with a namespace declaration. For organizing purposes, we may write
 more than one benchmarks file if, for example, we'd like to write one benchmark
 file per source namespace."]

 [:p "Within our benchmarks file, we use "
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
  ", an unquoted symbol. The name resolves the benchmark definition in a
 namespace. We've chosen "
  [:code "zap-inc"]
  " and "
  [:code "zap-uc"]
  ". The names don't have any functional significance. We could have named the
 benchmarks "
  [:code "Romeo"]
  " and "
  [:code "Juliet"]
  " without affecting the measurements, but like any other Clojure symbol, it's
 nice if the names have some semantic meaning."]

 [:p "So far, we have the following two incomplete benchmark definitions: "
  [:code "defbench"]
  " followed by a name."]

 [:pre
  [:code "(defbench zap-inc ...)"] [:br]
  [:code "(defbench zap-uc ...)"]]

 [:p "When we evaluate a "
  [:code "defbench"]
  " expression, Fastester binds a hashmap to the "
  [:em "name"]
  " in the namespace where we evaluated the expression. If two expressions use
 the same name in the same namespace, the later-evaluated definition will
 overwrite the earlier. If we'd like to give the same name to two different
 benchmarks, we could isolate the definitions into two different namespaces. For
 this demonstration benchmarking "
  [:code "zap"]
  ", we've chosen two different names, so we won't worry about overwriting."]

 [:p "After the name, we supply a "
  [:em "group"] ", a string that associates one benchmark with other
 conceptually-related benchmarks. Later, while generating the "
  [:span.small-caps "html"]
  " results document, Fastester will aggregate benchmarks sharing a group. For "
  [:code "zap"]
  ", we have our two related benchmarks. Let's assign both of those benchmarks
 to the "
  [:code "\"faster zap implementation\""]
  " group."]

 [:p "Now, we have the following two incomplete benchmark definitions, with the
 addition of the group."]

 [:pre
  [:code "(defbench zap-inc \"faster zap implementation\" ...)"] [:br]
  [:code "(defbench zap-uc \"faster zap implementation\" ...)"]]

 [:p "The final two arguments, "
  [:em  "fn-expression"]
  " and "
  [:em "args"]
  ", do the heavy lifting. The next step of the workflow, "
  [:a {:href "#run-benchmarks"} [:em "running the benchmarks"]]
  ", involves serially supplying elements of "
  [:code "args"]
  " to the function expression."]

 [:p "The function expression is a 1-arity function that demonstrates some
 performance aspect of the new version of the function. We updated "
  [:code "zap"]
  " so that it processes elements faster. One way to demonstrate its improved
 performance is to increment a sequence of integers with "
  [:code "inc"]
  ". That particular function expression looks like this."]

 [:pre [:code "(fn [n] (zap inc (range n)))"]]

 [:p "In addition to incrementing integers, we wanted to demonstrate
 upper-casing strings. Clojure's "
  [:code "clojure.string/upper-case"] "
 performs that operation on a single string."]

 [:pre (print-form-then-eval "(require '[clojure.string :as str])")]

 [:p "To create sequence of strings, we can use "
  [:code "cycle"]
  ", and "
  [:code "take"]
  " the number of elements we desire."]

 [:pre
  (print-form-then-eval "(take 1 (cycle [\"a\" \"b\" \"c\"]))") [:br]
  (print-form-then-eval "(take 2 (cycle [\"a\" \"b\" \"c\"]))") [:br]
  (print-form-then-eval "(take 3 (cycle [\"a\" \"b\" \"c\"]))")]

 [:p "Our second function expression looks like this."]

 [:pre [:code "(fn [i] (zap str/upper-case (take i (cycle [\"a\" \"b\" \"c\"]))))"]]

 [:p "And with the addition of their respective function expressions, our two
 almost-complete benchmark definitions look like this."]

 [:pre
  [:code "(defbench zap-inc \"faster zap implementation\" (fn [n] (zap inc (range n))) ...)"]
  [:br]
  [:br]
  [:code "(defbench zap-uc \"faster zap implementation\" (fn [i] (zap str/upper-case (take i (cycle [\"a\" \"b\" \"c\"])))) ...)"]]

 [:p "Note that there is nothing special about the function expression's
 parameter. "
  [:code "zap-inc"]
  " uses "
  [:code "n"]
  ", while "
  [:code "zap-uc"]
  " uses "
  [:code "i"]
  "."]

 [:p "'Running' a benchmark with those function expressions means that arguments
 are serially passed to the expression, measuring the evaluation times for each.
 The arguments are supplied by the final component of the benchmark definition,
 a sequence. For "
  [:code "zap-inc"]
  ", let's explore "
  [:code "range"]
  "s from ten to one-hundred thousand."]

 [:p "An "
  [:em "args"]
  " sequence of five integers like this…"]

 [:pre [:code "[10 100 1000 10000 100000]"]]

 [:p "…declares a series of five maximum values, producing the following series
 of five sequences to feed to "
  [:code "zap"]
  " for benchmarking."]

 [:pre
  [:code "[0 ... 9]"] [:br]
  [:code "[0 ... 99]"] [:br]
  [:code "[0 ... 999]"] [:br]
  [:code "[0 ... 9999]"] [:br]
  [:code "[0 ... 99999]"]]

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

 [:p "Similarly, we'd like "
  [:code "zap-uc"]
  " to exercise a span of strings."]

 [:pre
  [:code "(benchmark (zap str/upper-case (take 10 (cycle \"a\" \"b\" \"c\"))))"] [:br]
  [:code "(benchmark (zap str/upper-case (take 100 (cycle \"a\" \"b\" \"c\"))))"] [:br]
  [:code "(benchmark (zap str/upper-case (take 1000 (cycle \"a\" \"b\" \"c\"))))"] [:br]
  [:code "(benchmark (zap str/upper-case (take 10000 (cycle \"a\" \"b\" \"c\"))))"] [:br]
  [:code "(benchmark (zap str/upper-case (take 100000 (cycle \"a\" \"b\" \"c\"))))"] [:br]]

 [:p "That completed benchmark definition looks like this."]

 [:pre [:code
        "(defbench zap-uc
          \"faster zap implementation\"
          (fn [i] (zap str/upper-case (take i (cycle [\"a\" \"b\" \"c\"]))))
          [10 100 1000 10000 100000])"]]

 [:p#hoist "However, there's a problem. The function expressions contain "
  [:code "range"]
  " and "
  [:code "cycle"]
  ". If we run these benchmarks as is, the evaluation times would include "
  [:code "range"]
  "'s and "
  [:code "cycle"]
  "'s processing times. We might want to do that in some other scenario, but in
 this case, it would be misleading. We want to focus solely on how fast "
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
  " itself. "]

 [:p "If you extrapolated that "
  [:code "zap"]
  " behaves like "
  [:code "map"]
  ", perhaps you anticipated a remaining problem. If we were to run the "
  [:code "zap-inc"]
  " benchmarks as defined above, we'd notice that the evaluation times were
 suspiciously consistent, no matter how many integers the sequence contained. "
  [:code "zap"]
  ", like many core sequence functions, returns a lazy sequence. We must force
 the return sequence to be realized so that "
  [:code "zap-inc"]
  " measures "
  [:code "zap"]
  " actually doing work. "
  [:code "doall"]
  " is handy for that."]

 [:pre [:code
        "(defbench zap-inc
          \"faster zap implementation\"
          (fn [n] (doall (zap inc (range-of-length-n n))))
          [10 100 1000 10000 100000])"]]

 [:p "We handle "
  [:code "zap-uc"]
  " similarly. First, we'll pre-compute the test sequences so that running the
 benchmark doesn't measure "
  [:code "cycle"]
  ". Then we'll wrap the "
  [:code "zap"]
  " expression in a "
  [:code "doall"]
  "."]

 [:pre
  [:code
   "(def abc-cycle-of-length-n
     (reduce #(assoc %1 %2 (take %2 (cycle [\"a\" \"b\" \"c\"])))
             {}
             [10 100 1000 10000 100000]))"]
  [:br]
  [:br]
  [:br]
  [:code
   "(defbench zap-uc
          \"faster zap implementation\"
          (fn [n] (doall (zap str/upper-case (abc-cycle-of-length-n n))))
          [10 100 1000 10000 10000])"]]

 [:p "So what happens when we evaluate a "
  [:code "defbench"]
  " expression? It binds the benchmark name to a hashmap of group, function
 expression, arguments, and some metadata. Let's evaluate the name "
  [:code "zap-inc"]
  "."]

 [:pre
  [:code
   "zap-inc ;; => {:fexpr (fn [n] (zap inc (range-of-length-n n)))
               :group \"faster zap implementation\"
               :ns \"zap.benchmarks\"
               :name \"zap-inc\"
               :n [10 100 1000 10000 100000]
               :f #function[fn--8882]}"]]

 [:p "Yup. We can see an everyday Clojure hashmap containing all of the
 arguments we supplied to "
  [:code "defbench"]
  " (some stringified), plus the namespace and the "
  [:span.small-caps "repl"]
  "'s rendering of the function object."]

 [:p "Soon, in the "
  [:a {:href "#run-benchmarks"} "run benchmarks"]
  " step, Fastester will rip through the benchmark names declared in the options
 hashmap key "
  [:code ":benchmarks"]
  " and run a Criterium benchmark for every name."]

 [:p "Once we evaluate the two "
  [:code "defbench"]
  " expressions, the namespace contains two benchmark definitions that will
 demonstrate "
  [:code "zap"]
  "'s performance: one incrementing sequences of integers, named "
  [:code "zap-inc"]
  ", and one upper-casing sequences of strings, named "
  [:code "zap-uc"]
  "."]

 [:h4 "Helper utilities"]

 [:p "Fastester provides a few helper utilities. If we want to see how a
 benchmark would work, we can invoke "
  [:code "run-one-defined-benchmark"]
  "."]

 [:pre
  [:code "(require '[fastester.measure :refer [run-one-defined-benchmark]])"]
  [:br]
  [:br]
  [:code "(run-one-defined-benchmark zap-inc :quick)"]
  [:br]
  [:code ";; => ...output elided for brevity..."]]

 [:p  "In the course of writing benchmarks, we often need a sequence of
 exponentially-growing integers. For that, Fastester offers "
  [:code "range‑pow‑10"]
  " and "
  [:code "range‑pow‑2"]
  "."]

 [:pre
  [:code "(require '[fastester.measure :refer [range-pow-2 range-pow-10]])"]
  [:br]
  [:br]
  (print-form-then-eval "(range-pow-10 5)")
  [:br]
  (print-form-then-eval "(range-pow-2 5)")]

 [:p "Sometimes, we'll want to remove a defined benchmark, which we can do
 with "
  [:code "clojure.core/ns-unmap"]
  "."]

 [:pre [:code "(ns-unmap *ns* 'zap-something-else)"]]

 [:h4 "Final checklist"]

 [:p "Before we go to the next step, running the benchmarks, let's do some
 double-checks."]

 [:ul
  [:li
   [:p [:strong "Benchmark expressions contain ancillary computations?"]
    " We should double-check that our benchmark definitions include only
 computations we want to measure, e.g, extract "
    [:a {:href "#hoist"} "creating the sequences"]
    " to the surrounding context."]]

  [:li
   [:p#hierarchy [:strong "Verify the benchmark namespace and names in the
 options."]
    " We need Fastester to find our two benchmark definitions, so we must correctly set "
    [:code ":benchmarks"]
    ". This options key is associated to a hashmap."]

   [:p "That nested hashmap's keys are symbols indicating the namespace. In our
 example, we have one namespace, and therefore one key, "
    [:code "'zap.benchmarks"]
    ". Associated to that one key is a set of simple symbols indicating the
 benchmark names, in our example, "
    [:code "'zap-inc"]
    " and "
    [:code "'zap-uc"]
    ". Altogether, that section of the options looks like this."]

   [:pre [:code
          ":benchmarks {'zap.benchmarks #{'zap-inc
                               'zap-uc}}"]]]

  [:li
   [:p [:strong "Save the benchmark expression results?"]
    " We should also be on guard: saving "
    [:code "zap"]
    "'s results (e.g., one-hundred-thousand incremented integers) blows up the
 file sizes, so let's set "
    [:code ":save-benchmark-fn-results?"]
    " to "
    [:code "false"]
    "."]]

  [:li
   [:p [:strong "Prepare the benchmarking environment."]
    " Set the JVM for the desired level of "
    [:a {:href "#declare-compiler"} "tiered compilation"]
    " and "
    [:a {:href "#affinity"} "pin"]
    " the benchmark process to a particular CPU."]]]

 [:h3#run-benchmarks "3. Run benchmarks"]

 [:p "Now that we've written "
  [:code "zap-inc"]
  " and "
  [:code "zap-uc"]
  ", we can run the benchmarks in two ways. If we've got our editor open with an
 attached  "
  [:span.small-caps "repl"]
  ", we can invoke "
  [:code "(run-benchmarks)"]
  ". If we're at the command line, invoking "]

 [:pre [:code "$ lein run -m fastester.core :benchmarks"]]

 [:p " has the same effect. "
  [:a {:href "#affinity"} "Later"]
  ", we'll discuss a modification of this invocation that attempts to address a
 possible issue with contemporary CPUs."]

 [:p "We should expect each benchmark to take about a minute with the default
 benchmark settings. To minimize timing variance, we ought to use a multi-core
 machine with minimal competing processes, network activity, etc."]

 [:p "Inside the "
  [:code ":results-directory"]
  ", we should see ten "
  [:code "edn"]
  " files, one per function-expression/argument pairing. If not, double check
 the options hashmap to make sure all the namespace and name symbols within  "
  [:code ":benchmarks"]
  " are complete and correct."]

 [:h3#generate "4. Generate the " [:span.small-caps "html"]]

 [:p "When the benchmarks are finished running, we can generate the performance
 report. Sometimes it's useful to have an "
  [:span.small-caps "html"]
  " file to quickly view in the browser, and other times it's useful to have a
 markdown file (i.e., to show on GitHub), so Fastester generates one of each."]

 [:p "To generate the documents, we can invoke "
  [:code "(generate‑documents)"]
  " at the "
  [:span.small-caps "repl"]
  ", or "]

 [:pre [:code "$ lein run -m fastester.core :documents"]]

 [:p " from the command line."]

 [:p "Note: Fastester uses all data files in the directory set by the options "
  [:code ":results-directory"]
  ". The "
  [:code ":benchmarks"]
  " setting has no affect on generating the documents."]

 [:p "When we look at the report, there's only version 12! We wanted a "
  [:a {:href "#comparative"} "comparative"]
  " report which shows how the performance of version 12's "
  [:code "zap"]
  " has improved "
  [:em "relative"]
  " to version 11's "
  [:code "zap"]
  ". To fix this, we use our version control system to roll-back to the
 version 11 tag, and then we run the benchmarks with version 11. Once done, we
 roll-forward again to version 12."]

 [:p "After a followup "
  [:code "generate-documents"]
  " invocation, the charts and tables show the version 12 benchmark measurements
 side-by-side with version 11's, similar to the "
  [:a {:href "#intro"} "introduction example"]
  ". We can clearly see that the new "
  [:code "zap"]
  " implementation executes faster across a wide range of sequence lengths, both
 for incrementing integers and upper-casing strings."]

 [:p "The charts and tables present strong evidence, but a morsel of explanatory
 text enhances our story. Fastester provides two opportunities to add text. Near
 the top, between the table of contents and the first group's section, we can
 insert some introductory text by associating a hiccup/"
  [:span.small-caps "html"]
  " block to the "
  [:code ":preamble"]
  " key in the options hashmap."]

 [:p "Also, we can insert text after each group's section heading by creating an
 entry in the "
  [:code ":comments"]
  " part of the options hashmap. The comments option is a nested hashmap whose
 keys are the group (a string) and the values are hiccup/"
  [:span.small-caps "html"]
  " blocks."]

 [:p "For example, what we read in the "
  [:a {:href "https://blosavio.github.io/fastester/zap_performance.html"}
   [:code "zap"]
   " performance document"]
  " derives from the "
  [:code ":preamble"]
  " and "
  [:code ":comments"]
  " options defined roughly like this."]

 [:pre
  [:code
   ":preamble [:div
            [:p \"This page follows the \"
              [:code \"zap\"]
              \" function benchmark example from the \"
              [:a {:href \"https://github.com/blosavio/fastester\"}
               \"Fastester ReadMe\"]
              ...]]

:comments {\"faster `zap` implementation\" [:div
                                           [:p \"This is the comments section... \"
                                             [:em \"group\"]
                                             \", 'faster `zap` implementation'...\"
                                             [:code \"zap-inc\"]
                                             \" and \"]
                                             ...]}"]]

 [:p "For both the preamble and group comments, we can insert more than one "
  [:span.small-caps "html"]
  " element by wrapping them with a "
  [:code "[:div ...]"]
  "."]

 [:h3#gotchas "Gotchas"]

 [:p "We must be particularly careful to define our benchmarks to test exactly
 and only what we intend to test. One danger is idiomatic Clojure patterns
 polluting our time measurements. It's typical to compose a sequence right at
 the spot where we require it, like this."]

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
  (print-form-then-eval "(def ninety-nine-rands (repeatedly 99 #(rand)))")
  [:br]
  [:br]
  [:code ";; *use* the pre-existing sequence"]
  [:br]
  [:code "(map inc ninety-nine-rands)"]]

 [:p "The second expression now involves mostly the "
  [:code "map"]
  " action, and is more appropriate for benchmarking."]

 [:p "Another danger is that while we may be accurately timing an expression,
 the expression isn't calculating what we'd like to measure. "
  [:code "map"]
  " (and friends) returns a lazy sequence, which is almost certainly not what we
 were intending to benchmark. We must remember to force the realization of the
 lazy sequence, conveniently done with "
  [:code "doall"]
  "."]

 [:pre [:code "(doall (map inc ninety-nine-rands))"]]

 [:p "Regarding Fastester itself, three final "
  [:em "gotchas"]
  "  will be familiar to Clojurists programming
 at the "
  [:span.small-caps "repl"]
  ". During development, it's typical to define and re-define benchmarks with "
  [:code "defbench"]
  ". It's not difficult for the namespace to get out of sync with the visual
 appearance of the text represented in the file. Maybe we renamed a benchmark,
 and the old benchmark is still floating around invisibly. Such an orphaned
 definition won't hurt anything because Fastester will only run benchmarks
 explicitly listed in the option's "
  [:code ":benchmarks"]
  ". If we want to actively remove the benchmark, we can use "
  [:code "clojure.core/ns-unmap"]
  "."]

 [:p "Perhaps more dangerous, maybe we edited a "
  [:code "defbench"]
  "'s textual expression, but failed to re-evaluate it. What we see with our
 eyes won't accurately reflect the benchmark definition that Fastester actually
 runs. To fix this problem, a quick re-evaluation of the entire text buffer
 redefines all the benchmarks currently expressed in the namespace."]

 [:p "Finally, we need to remember that when running from the command line,
 Fastester consults only the options and benchmark definitions from the file
contents "
  [:strong "as they exist on disk"]
  ". A "
  [:span.small-caps "repl"]
  "-attached editor with unsaved options or definitions, even with a
 freshly-evaluated namespace, will not affect the results from a command line
 invocation. Saving the files to disk synchronizes what we see in the editor
 and what is consumed by command line-initiated actions."]

 [:p "When displaying relative performance comparisons, it's crucial to
 hold the environment as consistent as possible. If a round of benchmarks are
 run when the CPU, RAM, operating system, Java version, or Clojure version are
 changed, we need to re-run "
  [:strong "all"]
  " previous benchmarks. Or, maybe better, we ought to make a new options file
 and generate a completely different performance document, while keeping the old
 around."]

 [:p#affinity [:em "Unresolved: "]
  "Contemporary systems often use multiple, heterogeneous CPU cores, i.e.,
 X efficiency cores running light tasks at low power and Y high-performance
 cores running intense tasks. Linux provides a utility, "
  [:a {:href "https://manpages.debian.org/bookworm/util-linux/taskset.1.en.html"}
   [:code "taskset"]]
  ", that explicitly sets CPU affinity. Invoking"]

 [:pre [:code "$ taskset --cpu-list 3 lein run -m fastester.core :benchmarks"]]

 [:p "from the command line pins the benchmark process to the fourth CPU.
 Fastester does not provide a turn-key solution for setting CPU affinity for
 other operating systems such as Windows or MacOS."]]

