(ns fastester.measure
  "Define and run benchmarks that objectively measure a function's evaluation
  time.

  The general idea is to run the benchmark suite once per release. Any
  performance improvement/regression is objectively measured and included in the
  changelog/release notes.

  The [Criterium](https://github.com/hugoduncan/criterium/) library does all the
  actual performance measurements.

  See [[fastester.display]] for utilities that generate an html document with
  charts and tables that communicate those results."
  (:require
   [clojure.edn :as edn]
   [clojure.java.io :as io]
   [clojure.math :as math]
   [clojure.pprint :as pp]
   [clojure.string :as str]
   [clojure.xml :as xml]
   [criterium.core :as crit]))


(defn project-version-pom-xml
  "Queries 'pom.xml' file a returns version element as a string."
  {:UUIDv4 #uuid "63bc2597-1575-48bf-b444-09bca5115df7"
   :no-doc true}
  []
  (->> (xml/parse "pom.xml")
       :content
       (filter #(= (:tag %) :version))
       first
       :content
       first))


(defn project-version-lein
  "Queries the Leiningen 'project.clj' file's `defproject` expression and
  returns a string."
  {:UUIDv4 #uuid "9d8409a9-89ad-4567-9572-65c1e456cbb0"
   :no-doc true
   :implementation-note "Would prefer to use `clojure.edn/read-string` to
 minimize security issues with reading strings, but Leiningen 'project.clj'
 files are open-ended collections, and may contain, e.g., regular expressions
 `#\"...\"` in codox's section or backslahses, which are not members of edn.
 Must presume that a user's local 'project.clj' is trustworthy."}
  []
  (let [project-metadata (read-string (slurp "project.clj"))]
    (nth project-metadata 2)))


(defn project-version
  "Given options hashmap `opt`, returns the project version as a string.

  * If `opt` declares a preference by associating either `:lein` or `:pom-xml`
  to key `:preferred-version-info`, queries only that preference.
  * If a preference is not specified and both sources exists, throws.
  * If a preference is not specified and only one source exists, returns that
  version.
  * If neither 'project.clj' nor 'pom.xml' exists, throws."
  {:UUIDv4 #uuid "c5bd643f-73d8-4bc6-9506-8e59ba32f9fe"
   :no-doc true}
  [opts]
  (case (opts :preferred-version-info)
    :lein (project-version-lein)
    :pom-xml (project-version-pom-xml)
    nil (let [exists? #(.exists (io/file %))
              lein? (exists? "project.clj")
              pom-xml? (exists? "pom.xml")
              both-err "Both 'project.clj' and 'pom.xml' exist, but `:preferred-version-info` not set in options hashmap."
              neither-err "Neither 'project.clj' nor 'pom.xml' exist."]
          (cond
            (and lein? pom-xml?) (throw (Exception. both-err))
            lein? (project-version-lein)
            pom-xml? (project-version-pom-xml)
            :default (throw (Exception. neither-err))))))


(defn get-options
  "With no argument, reads Fastester options hashmap from file
  'resources/fastester_options.edn', otherwise, reads from file
  `explicit-options-filename`."
  {:UUIDv4 #uuid "3fe666a0-2aa4-4777-a88f-056824bbed2f"
   :no-doc true}
  ([] (load-file "resources/fastester_options.edn"))
  ([explicit-options-filename] (if explicit-options-filename
                                 (load-file explicit-options-filename)
                                 (get-options))))


(defmacro defbench
  "Define a benchmark by binding a hashmap containing a function, arguments
  sequence, and group to a name.

  * `name` is a symbol that labels the benchmark.
  * `group` is a string, shared between multiple conceptually-related
  benchmarks.
  * `f` is a 1-arity function that measures some performance aspect. Its single
  argument is the \"n\" in *big-O* notation. `f` may be supplied as an
  S-expression or a function object. Supplying `f` as an S-expression has the
  advantage that the definition may later convey some meaning, e.g.,
  `(fn [n] (+ n n))`, in the html charts and tables, whereas a function object
  will render less meaningfully, e.g.,
  `#function[fastester.measure/eval11540/fn--11541]`.
  * `n` is a sequence of one or more arguments. During performance testing,
  elements of `n` will be individually supplied to the benchmarking utility.

  Example, supplying `f` as an S-expression:
  ```clojure
  (defbench add-two \"benchmarking addition\" (fn [n] (+ n n)) [1 10 100 1000])
  ```

  Example, supplying `f` as a function object:
  ```clojure
  (defn my-fn-obj [q] (+ q q q))

  (defbench add-three \"benchmarking addition\" my-fn-obj [2 20 200 2000])
  ```

  Both examples above share the same `group` label (\"benchmarking addition\"),
  as both are concerned with measuring the performance of `+`. Putting them in
  the same group signifies that they are conceptually-related, and the html
  document will aggregate them under a single subsection. Also notice that the
  `n` sequences need not be identical.

  Note: Invoking a `defbench` expression, editing the `name`, followed by
  invoking `defbench` a second time, defines two unique benchmarks.
  When developing at the REPL, be aware that the namespace may become 'stale'
  with orphaned or otherwise outdated benchmarks. Fastester will run benchmarks
  only explicity associated to the `:benchmarks` options key, defined as the
  namespace exists in the file on the disk.

  To undefine a single, unwanted test, use
  [`clojure.core/ns-unmap`](https://clojure.github.io/clojure/clojure.core-api.html#clojure.core/ns-unmap)."
  {:UUIDv4 #uuid "a02dc349-e964-41d9-b704-39f7d685109a"}
  [name group f n]
  `(def ~name {:group ~group
               :fexpr '~f
               :f ~(eval f)
               :n ~n
               :name ~(str name)
               :ns ~(str *ns*)}))


(defn create-results-directories
  "Create directories to contain measurement results. If sequence `benchmarks`
  contains zero tests to be executed, directories are not created.

  Creates `<options-results-dir>/` then `<options-results-dir>/<version>/`."
  {:UUIDv4 #uuid "a75a0d12-150b-43e6-b3b7-6a758528a3b2"
   :no-doc true}
  [benchmarks explicit-options-filename]
  (let [options (get-options explicit-options-filename)
        mkdir #(.mkdir (io/file %))
        results-dirname (options :results-directory)
        version-dirname (str results-dirname
                             "/version "
                             (project-version options))]
    (if (seq benchmarks)
      (do
        (mkdir results-dirname)
        (mkdir version-dirname)))))


(def ^{:no-doc true}
  *lightning-benchmark-opts*-docstring
  "Criterium options with extremely minimal samples, etc. Use only for quick,
 proof-of-concept runs.

 Example:
 ```clojure
   (criterium.core/benchmark (+ 1 2) *lightning-benchmark-opts*)
 ```

 See also
 [`criterium.core/*default-benchmark-opts*`](https://github.com/hugoduncan/criterium/blob/bb10582ded6de31b4b985dc31d501db604c0e461/src/criterium/core.clj#L83)
 and
 [`criterium.core/*default-quick-bench-opts*`](https://github.com/hugoduncan/criterium/blob/bb10582ded6de31b4b985dc31d501db604c0e461/src/criterium/core.clj#L92).")


(def ^{:dynamic true
       :doc *lightning-benchmark-opts*-docstring}
  *lightning-benchmark-opts*
  (reduce (fn [m k] (update m k #(/ % 2)))
          crit/*default-quick-bench-opts*
          [:max-gc-attempts
           :samples
           :target-execution-time
           :warmup-jit-period]))


(def ^{:no-doc true}
  thoroughnesses {:default crit/*default-benchmark-opts*
                  :quick crit/*default-quick-bench-opts*
                  :lightning *lightning-benchmark-opts*})


(defmacro run-manual-benchmark
  "Given 1-arity S-expression `fexpr` representing a function and one argument
  `arg`, run one benchmark test using keyword `thoroughness` to designate the
  Criterium options, sending result to &ast;`out`&ast;.

  `thoroughness` is one of `:default`, `:quick`, or `:lightning`.

  Example:
  ```clojure
  (run-manual-benchmark (fn [n] (+ n n)) 9 :lightning)
  ```

  See [[run-one-defined-benchmark]] and [[*lightning-benchmark-opts*]]."
  {:UUIDv4 #uuid "f7d36987-4451-4115-99e3-2fc57bee6a91"}
  [fexpr arg thoroughness]
  `(crit/benchmark (~(eval fexpr) ~arg) (thoroughnesses ~thoroughness)))


(defn date
  "Returns hashmap of `{:year YYYY :month \"M...\" :day DD}`.

  Example:
  ```clojure
  (date) ;; => {:year 2025, :month \"July\", :day 16}
  ```"
  {:UUIDv4 #uuid "a9d4df42-9cb8-4812-a996-0f080535183e"
   :no-doc true}
  []
  (let [instant (java.util.Date.)
        formatted-instant (.format
                           (java.text.SimpleDateFormat. "yyyy LLLL dd") instant)
        [year month day] (str/split formatted-instant #" ")]
    {:year (edn/read-string year)
     :month month
     :day (edn/read-string (str/replace day #"^0" ""))}))


(defn dissoc-identifying-metadata
  "Given `metadata` hashmap  generated by Criterium benchmarking, dissociate
  key-vals from metadata that might be personally identifying information or
  enable fingerprinting, etc."
  {:UUIDv4 #uuid "eb6de892-6749-4092-8f0a-5e0d0852e94f"
   :no-doc true}
  [metadata]
  (let [remove-paths [[:runtime-details :vm-version]
                      [:runtime-details :name]
                      [:runtime-details :java-runtime-version]
                      [:runtime-details :input-arguments]]
        dissoc-in (fn [m p] (update-in m (butlast p) dissoc (last p)))]
    (reduce dissoc-in metadata remove-paths)))


(defn pretty-print-to-file
  "Writes `content` to file `filepath` with Clojure's pretty-printer."
  {:UUIDv4 #uuid "c205183a-fa6b-4581-bd58-3e22a66d44b0"
   :no-doc true}
  [filepath content]
  (binding [pp/*print-pretty* true
            pp/*print-miser-width* 40
            pp/*print-right-margin* 72
            *print-length* nil]
    (with-open [writer (clojure.java.io/writer filepath)]
      (pp/pprint content writer))))


(defn run-save-benchmark
  "Given string `ver`, symbol `name`, namespace `namespace`, string `group`,
  function object `f`, function S-expression `fexpr`, test argument `arg`,
  index integer `idx`, and option hashmap `opts`, measures the evaluation time
  according to the option's `:testing-thoroughness` for Criterium benchmark
  settings and saves results to file system.

  Example:
  ```clojure
  (run-save-benchmark \"77-SNAPSHOT7\"
                      'mult-two-nums
                      'benchmark-namespace
                      \"adding --- stuff\"
                      (fn [n] (* n n))
                      '(fn [n] (* n n))
                      22
                      5
                      options)
  ```

  See also [[run-manual-benchmark]] and [[run-one-defined-benchmark]] for
  utilities to quickly benchmark an expression."
  {:UUIDv4 #uuid "5f87d695-9d47-4553-8596-1b9ad26f4bab"}
  [ver name namespace group f fexpr arg idx opts]
  (let [dirname (opts :results-directory)
        filepath (str dirname
                      "version "
                      ver
                      "/test-"
                      idx
                      ".edn")
        results (crit/benchmark (f arg)
                                (thoroughnesses (opts :testing-thoroughness)))
        test-metadata {:version ver
                       :index idx
                       :name (str name)
                       :ns (str namespace)
                       :group group
                       :fexpr (str fexpr)
                       :arg arg
                       :date (date)
                       :UUIDv4 (random-uuid)
                       :testing-thoroughness (opts :testing-thoroughness)
                       :parallel? (opts :parallel?)}
        results (assoc results :fastester/metadata test-metadata)
        results (if (opts :save-benchmark-fn-results?)
                  results
                  (assoc results
                         :results
                         :fastester/benchmark-fn-results-elided))
        results (dissoc-identifying-metadata results)]
    (pretty-print-to-file filepath results)))


(defn load-benchmarks-ns
  "Given options hashmap `opt`, `require`s the testing namespaces declared by
  the Fastester options `:benchmarks`."
  {:UUIDv4 #uuid "f15a8cff-88dd-4c71-81b5-dab61bfeaffc"
   :no-doc true
   :implementation-note "Use `require` to affect compiling the namespaces. Don't
                         feel great about abusing `require` like this, but it
                         appears to work okay, and it seems how Leiningen gets
                         tasks done, too. See
                         `leiningen.core.utils/require-resolve`."}
  [opt]
  (doseq [fname (keys (opt :benchmarks))]
    (if (opt :verbose?) (println "Loading tests from " fname))
    (require fname :reload)))


(defn benchmark-nspace+syms
  "Given options hashmap `opts`, returns a sequence of `[ns sym]`
  bound to vars previously defined by `defbench`."
  {:UUIDv4 #uuid "076a046c-95b5-4c47-b938-bf9ad306cb6e"
   :no-doc true}
  [opts]
  (reduce (fn [acc [nspace vrs]] (concat acc (map #(vector nspace %) vrs)))
          []
          (opts :benchmarks)))


(defn sym-sym
  "Given symbols `sym-1` and `sym-2`, returns a compound, qualified symbol
  composed of both.

  2-arity form of `clojure.core/symbol` accepts only strings, so must convert
  both to strings first."
  {:UUIDv4 #uuid "326bf527-9183-4148-8570-096c209c6603"
   :no-doc true}
  [sym-1 sym-2]
  (symbol (str sym-1) (str sym-2)))


(defn benchmark-defs
  "Given sequence `s` of namespace+symbol 2-tuples, returns a sequence of
  benchmark hashmap definitions,
  `{:name ... :ns ... :group ... :fexpr ... :f ... :n ...}`."
  {:UUIDv4 #uuid "01ae3707-ba31-4f89-a222-e85c0a7b804d"
   :no-doc true}
  [s]
  (map (fn [[nspace sym]] @(requiring-resolve (sym-sym nspace sym))) s))


(defn run-benchmarks
  "Execute benchmarks associated to option key `:benchmarks`, saving results to
  a version-specific directory. If `explicit-options-filename` is not supplied,
  defaults to `./resources/fastester_options.edn`.

  Upon invocation, reads options file and any benchmarks definition namespace
  files from the disk. Ensure that the state of the file on disk and that same
  file opened in a REPL-attached editor is what you intend.

  If option `:parallel?` is `true`, runs tests in parallel. Warning: Running
  tests in parallel results in inconsistent time measurements. Use
  `:parallel? true` only for sanity-checking the return values of `(f n)`, not
  for final performance measurements."
  {:UUIDv4 #uuid "68d16e2e-2ab2-4dcd-9609-e237d6991594"
   :implementation-notes
   "See
   https://clojuredocs.org/clojure.core/future#example-542692c9c026201cdc326a7b
   and
   https://clojure.atlassian.net/browse/CLJ-124
   for discussion of cleanly shutting down agents, relevant when using `pmap`."}
  ([] (run-benchmarks nil))
  ([explicit-options-filename]
   (let [options (get-options explicit-options-filename)
         _ (load-benchmarks-ns options)
         get-idx #(.indexOf %1 %2)
         benchmarks (-> options
                        benchmark-nspace+syms
                        benchmark-defs)
         expander-fn (fn [v bm]
                       (concat v (map #(assoc bm :n %1) (bm :n))))
         expanded-benchmarks (reduce expander-fn [] benchmarks)
         num-reg (dec (count expanded-benchmarks))
         verbose (options :verbose?)
         runner-fn (fn [bm]
                     (let [idx (get-idx expanded-benchmarks bm)]
                       (if verbose (println (str "Benchmark " idx "/" num-reg)))
                       (run-save-benchmark (project-version options)
                                           (bm :name)
                                           (bm :ns)
                                           (bm :group)
                                           (bm :f)
                                           (bm :fexpr)
                                           (bm :n)
                                           idx
                                           options)))
         runner ({true pmap
                  false map}
                 (options :parallel?))]
     (create-results-directories benchmarks explicit-options-filename)
     (do
       (when verbose (println "Estimating overhead"))
       (crit/estimated-overhead!))
     (doall (runner runner-fn expanded-benchmarks))
     (if (and (options :parallel?)
              (not *repl*)) (shutdown-agents))
     (if verbose (println "Performance testing complete.")))))


(defn range-pow-n
  "Subfunction to generate a sequence of numbers raised to a power `n`."
  {:UUIDv4 #uuid "700ac8f2-2bda-4692-9136-c985b3fa4f77"
   :no-doc true}
  [n]
  (fn [i] (map #(long (math/pow n %)) (range 0 (inc i)))))


(defn range-pow-10
  "Given integer `i`, returns a sequence of `[10^0...10^i]`, inclusive. Useful
  for supplying to benchmarking functions.

  Examples:
  ```clojure
  (range-pow-10 3) ;; => (1 10 100 1000)
  (range-pow-10 6) ;; => (1 10 100 1000 10000 100000 1000000)
  ```

  See also [[range-pow-2]]."
  {:UUIDv4 #uuid "8f0b944e-0381-4dd3-961f-4eddd19cc73b"}
  [i]
  ((range-pow-n 10) i))


(defn range-pow-2
  "Given integer `i`, returns a sequence of `[2^0...2^i]`, inclusive. Useful
  for supplying to benchmarking functions.

  Example:
  ```clojure
  (range-pow-2 8) ;; (1 2 4 8 16 32 64 128 256)
  ```

  See also [[range-pow-10]]."
  {:UUIDv4 #uuid "15a59bb1-9e00-459b-b40d-8b8f0d0011c9"}
  [i]
  ((range-pow-n 2) i))


(defn run-one-defined-benchmark
  "Given defined `benchmark`, an unquoted symbol, and keyword `thoroughness`
  that designates the Criterium options, runs a benchmark for each defined
  argument. Returns a hashmap whose keys are arguments `n`, associated to values
  that are the benchmark results for that `n`.

  `thoroughness` is one of `:default`, `:quick`, or `:lightning`.

  Example:
  ```clojure
  ;; define a benchmark
  (defbench my-bench \"my-group\" (fn [x] (inc x)) [97 98 99])

  (run-one-defined-benchmark my-bench :lightning)
  ;; => {97 {:mean ... :var ...}
  ;;     98 {:mean ... :var ...}
  ;;     99 {:mean ... :var ...}}
  ```
  The above example returns a hashmap with three key+vals: keys `97`, `98`, and
  `99`, each associated with its respective benchmark result.

  See also [[run-manual-benchmark]] and [[*lightning-benchmark-opts*]]."
  {:UUIDv4 #uuid "c2b4860d-3a46-44be-8ea1-ab97b282dfae"}
  [benchmark thoroughness]
  (let [f (benchmark :f)
        th (thoroughnesses thoroughness)]
    (reduce #(assoc %1
                    %2
                    (crit/benchmark (f %2) th))
            {}
            (benchmark :n))))


