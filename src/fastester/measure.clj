(ns fastester.measure
  "Execute performance tests.

  Concept: Run performace test suite once per version."
  (:require
   [clojure.java.io :as io]
   [criterium.core :as crit]))


(defn project-version
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


(defn get-options
  "Reads Fastester options hashmap from file 'resources/fastester_options.edn'."
  {:UUIDv4 #uuid "3fe666a0-2aa4-4777-a88f-056824bbed2f"
   :no-doc true}
  []
  (load-file "resources/fastester_options.edn"))


(def perf-test-registry (atom #{}))


(defn clear-perf-test-registry!
  "Remove all entries from [[perf-test-registry]]."
  {:UUIDv4 #uuid "d615da84-0b3b-42e3-acdb-9cec175df53e"}
  []
  (swap! perf-test-registry empty))


(defmacro defperf
  "Define and register a performance test.

  * `group` is a string that links conceptually-related performance tests.

  * `f` is a 1-arity function that exercises some performance aspect. It's
  single argument is the \"n\" in *big-O* notation. `f` may be supplied as an
  S-expression or a function object. Supplying `f` as an S-expression has the
  advantage that the definition will be later convey some meaning, e.g.,
  `(fn [x] (+ x x))`, whereas a function object will render less meaningfully,
  e.g., `#function[fastester.measure/eval11540/fn--11541]`.

  * `n` is a sequence of one or more arguments. During performace testing,
  elements of `n` will be individually supplied to the benchmarking utility.

  Example, supplying `f` as an S-expression:
  ```clojure
  (defperf \"benchmarking addition\" (fn [n] (+ n n)) [1 10 100 1000])
  ```

  Example, supplying `f` as a function object:
  ```clojure
  (defn my-fn-obj [q] (+ q q q))

  (defperf \"benchmarking additon\" my-fn-obj [2 20 200 2000])
  ```

  Both examples above share the same `group` label, \"benchmarking addition\",
  therefore the benchmarking results may be later conceptually associated, in
  this example both involving measuring the performance of `+`. Also note that
  the `n` sequences need not be identical.

  Note 1: The registry ensures that every entry is unique, so repeated `defperf`
  invocations with identical arguments have no additional affect beyond the
  initial registration.

  Note 2: Invoking `defperf` with one sequence of arguments, then editing the
  expression and invoking `defperf` again registers two unique performance
  tests. When developing at the REPL, be aware that the registry may become
  'stale' with outdated tests. See [[clear-perf-test-registry!]] or edit with,
  e.g., `disj`."
  {:UUIDv4 #uuid "a02dc349-e964-41d9-b704-39f7d685109a"}
  [group f n]
  (let [fun (nth &form 2)]
    `(swap! perf-test-registry conj {:group ~group
                                     :fexpr '~fun
                                     :f ~f
                                     :n ~n})))


(defn create-results-directories
  "Create directories to contain benchmarking results, location declared by
  options key `:perflog-results-directory`. Consults names contained in set
  `excludes`. If zero performance tests are to be executed (i.e., all tests are
  excluded), directories are not created.

  Creates `<options-results-dir>/` then `<options-results-dir>/<version>/`."
  {:UUIDv4 #uuid "a75a0d12-150b-43e6-b3b7-6a758528a3b2"}
  [excludes]
  (let [options (get-options)
        mkdir #(.mkdir (io/file %))
        results-dirname (options :perflog-results-directory)
        version-dirname (str results-dirname
                             "/version "
                             (project-version))
        unique-test-names (distinct (map :group @perf-test-registry))
        non-excluded-names (remove
                            excludes
                            unique-test-names)]
    (if (seq non-excluded-names)
      (do
        (mkdir results-dirname)
        (mkdir version-dirname)))))


(def ^{:no-doc true}
  *performance-testing-options*-docstring
  "Hashmap containing Criterium benchmarking options. Defaults to
  `criterium.core/*default-quick-bench-opts*`. Create a new binding evironment
  with `binding`.

  ```clojure
  (binding [*performance-testing-options* criterium.core/*default-benchmark-opts*]
    (do-one-performance-test (+ 1 2)))
  ```

  See also `criterium.core/*default-benchmark-opts*` for alternate values.")


(def ^{:dynamic true
       :doc *performance-testing-options*-docstring}
  *performance-testing-options* crit/*default-quick-bench-opts*)


(def ^:dynamic
  *lightning-benchmark-opts*
  (reduce (fn [m k] (update m k #(/ % 2)))
          crit/*default-quick-bench-opts*
          [:max-gc-attempts
           :samples
           :target-execution-time
           :warmup-jit-period]))


(defmacro run-one-test-subroutine
  "Given 1-arity function S-expression `fexpr` and argument `arg`, run one
  benchmark test using \"lightning\" thoroughness, sending result to *out*.

  Example:
  ```clojure
  (run-one-test-subroutine '(fn [n] (+ n n)) 9)
  ```"
  {:UUIDv4 #uuid "f7d36987-4451-4115-99e3-2fc57bee6a91"}
  [fexpr arg]
  `(binding [*performance-testing-options*
             *lightning-benchmark-opts*]
     (crit/benchmark (~(eval fexpr) ~arg) *performance-testing-options*)))


(defn date
  "Returns hashmap of `{:year YYYY :month M... :day DD}`.

  Example:
  ```clojure
  (date) ;; => {:year 2025, :month \"July\", :day 16}
  ```"
  {:UUIDv4 #uuid "a9d4df42-9cb8-4812-a996-0f080535183e"}
  []
  (let [instant (java.util.Date.)
        formatted-instant (.format
                           (java.text.SimpleDateFormat. "yyyy LLLL dd") instant)
        [year month day] (clojure.string/split formatted-instant #" ")]
    {:year (clojure.edn/read-string year)
     :month month
     :day (clojure.edn/read-string day)}))


(defn dissoc-identifying-metadata
  "Given `metadata` hashmap  generated by Criterium benchmarking, dissociate
  key-vals from metadata that might be personally identifying information or
  enable fingerprinting, etc."
  {:UUIDv4 #uuid "eb6de892-6749-4092-8f0a-5e0d0852e94f"}
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
  (binding [clojure.pprint/*print-pretty* true
            clojure.pprint/*print-miser-width* 40
            clojure.pprint/*print-right-margin* 72]
    (with-open [writer (clojure.java.io/writer filepath)]
      (clojure.pprint/pprint content writer))))


(defn run-one-test
  "Given version string `ver`, test group string `group`, function
  S-expression `f`, test argument `arg`, option hashmap `opts`, and index
  integer `idx`, executes the benchmark under the current settings and saves
  results to filesystem.

  Example:
  ```clojure
  (run-one-test \"77-SNAPSHOT7\"
                \"mapping --- stuff\"
                '(fn [n] (* n n))
                22
                5
                options)

  See [[run-one-test-subroutine]].
  ```"
  {:UUIDv4 #uuid "5f87d695-9d47-4553-8596-1b9ad26f4bab"}
  [ver group f fexpr arg idx opts]
  (let [dirname (opts :perflog-results-directory)
        filepath (str dirname
                      "version "
                      ver
                      "/test-"
                      idx
                      ".edn")
        benchmark-options ({:lightning *lightning-benchmark-opts*
                            :quick crit/*default-quick-bench-opts*
                            :standard crit/*default-benchmark-opts*}
                           (opts :testing-thoroughness))
        results (binding [*performance-testing-options*
                          benchmark-options]
                  (crit/benchmark (f arg) *performance-testing-options*))
        test-metadata {:version ver
                       :index idx
                       :group group
                       :fexpr (str fexpr)
                       :arg arg
                       :date (date)
                       :UUIDv4 (random-uuid)
                       :testing-thoroughness (opts :testing-thoroughness)
                       :parallel? (opts :parallel?)}
        results (assoc results :fastester/metadata test-metadata)
        results (dissoc-identifying-metadata results)]
    (pretty-print-to-file filepath results)))


(defn do-tests
  "Execute non-excluded performance tests, as governed by test names in set
  `excludes`. If option `:parallel?` is `true`, runs tests in parallel."
  {:UUIDv4 #uuid "68d16e2e-2ab2-4dcd-9609-e237d6991594"
   :no-doc true
   :implementation-notes
   "See
   https://clojuredocs.org/clojure.core/future#example-542692c9c026201cdc326a7b
   and
   https://clojure.atlassian.net/browse/CLJ-124
   for discussion of cleanly shutting down agents, relevant when using `pmap`."}
  [excludes]
  (let [options (get-options)
        reg (sort-by :group (vec @perf-test-registry))
        reg (remove #(excludes (% :group)) reg)
        get-idx #(.indexOf %1 %2)
        r-fn (fn [v m] (concat v (map #(assoc m :n %1) (m :n))))
        expanded-reg (reduce r-fn [] reg)
        num-reg (dec (count expanded-reg))
        runner-fn (fn [t]
                    (let [idx (get-idx expanded-reg t)]
                      (println (str "Test " idx "/" num-reg))
                      (run-one-test (project-version)
                                    (t :group)
                                    (t :f)
                                    (t :fexpr)
                                    (t :n)
                                    idx
                                    options)))
        verbose (options :verbose?)
        runner ({true pmap
                 false map}
                (options :parallel?))]
    (create-results-directories excludes)
    (doall (runner runner-fn expanded-reg))
    (if (and (options :parallel?)
             (not *repl*)) (shutdown-agents))
    (if verbose (println "Performance testing complete."))))


(defn do-all-performance-tests
  "Execute all performance tests, ignoring options key `:excludes`."
  {:UUIDv4 #uuid "50b19eef-32f1-4586-a317-21e1f20235cf"}
  []
  (do-tests #{}))


(defn do-selected-performance-tests
  "Execute performance tests, skipping any tests with name contained in option
  `:excludes`."
  {:UUIDv4 #uuid "ed3dd772-08d5-47cc-85b9-608892f8c96a"}
  []
  (do-tests ((get-options) :excludes)))


(defn load-tests-ns
  "Given options hashmap `opt`, `require`s the testing namespace declared by the
  Fastester options `:perflog-tests-directory` and `:perflog-tests-filename`."
  {:UUIDv4 #uuid "f15a8cff-88dd-4c71-81b5-dab61bfeaffc"
   :no-doc true
   :implementation-note "Don't feel great about abusing `require` like this, but
  it appears to work okay, and it seems how Leiningen gets tasks done, too. See
  `leiningen.core.utils/require-resolve`."}
  [opt]
  (let [filepath (str (opt :perflog-tests-directory)
                      (opt :perflog-tests-filename))
        tests-file (clojure.string/replace filepath #"\.[\w\d]{3}$" "")]
    (require (symbol tests-file) :reload)))

