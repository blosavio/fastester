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


(def ^{:no-doc true}
  registry-docstring
  "An atom containing a hashmap of benchmarks to run. Typically populated by
  invoking [[defbench]] or [[defbench*]], not manipulated directly.

  See also [[undefbench]] and [[clear-registry!]].")


(def ^{:doc registry-docstring}
  registry (atom (sorted-map)))


(defn clear-registry!
  "Remove all entries from the benchmark [[registry]]."
  {:UUIDv4 #uuid "d615da84-0b3b-42e3-acdb-9cec175df53e"}
  []
  (swap! registry empty))


(defn defbench*
  "Function version of `defbench` macro. Define and register a benchmark. See
  [[defbench]] documentation for full details. The two differences are that
  `name` and `f` are supplied as a quoted symbols.

  Example:
  ```clojure
  (defbench* 'add-four \"benchmarking addition\" '(fn [z] (+ z z z z) [1 10 100])
  ```"
  {:UUIDv4 #uuid "4b09fdc6-f830-40df-8f60-c454f1cca4c4"}
  [name group f n]
  (swap! registry assoc name {:group group
                              :fexpr f
                              :f (eval f)
                              :n n}))


(defmacro defbench
  "Define and register a benchmark by inserting an entry into the [[registry]].

  * `name` is an unquoted symbol that labels the benchmark.
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

  (defbench add-three \"benchmarking additon\" my-fn-obj [2 20 200 2000])
  ```

  Both examples above share the same `group` label (\"benchmarking addition\"),
  as both are concerned with measuring the performance of `+`. Putting them in
  the same group signifies that they are conceptually-related, and the html
  document will aggregate them under a single subsection. Also notice that the
  `n` sequences need not be identical.

  Note: Invoking a `defbench` expression, editing the `name`, followed by
  invoking `defbench` a second time, registers two unique performance tests.
  When developing at the REPL, be aware that the registry may become 'stale'
  with outdated tests.

  1. To remove a single, unwanted test, use [[undefbench]].
  2. To put the registry into a state that reflects only the current
  definitions, use [[clear-registry!]] and re-evaluate the namespace to invoke
  all the current `defbench`s .

  See [[defbench*]] for the function version."
  {:UUIDv4 #uuid "a02dc349-e964-41d9-b704-39f7d685109a"}
  [name group f n]
  (let [fun (nth &form 3)]
    `(defbench* '~name ~group '~fun ~n)))


(defn undefbench*
  "Function version of [[undefbench]]. Undefines a benchmark by removing quoted
  symbol `name` from the [[registry]].

  Example:
  ```clojure
  ;; define a performance test
  (defbench* 'add-four \"benchmarking addition\" '(fn [z] (+ z z z z) [1 10 100])

  ;; undefine that performance test
  (undefbench* 'add-four)
  ```"
  {:UUIDv4 #uuid "6130c03f-e8b0-4ce1-a682-ba3605fc291b"}
  [name]
  (swap! registry dissoc name))


(defmacro undefbench
  "Undefine a benchmark by removing `name` from the [[registry]].

  Example:
  ```clojure
  ;; define a performance test
  (defbench add-two \"benchmarking addition\" (fn [n] (+ n n)) [1 10 100 1000])

  ;; undefine that performance test
  (undefbench add-two)
  ```

  Undoes the results of [[defbench]]. See also [[undefbench*]]."
  {:UUIDv4 #uuid "2b9a96f7-087d-483d-bde9-d43841132eba"}
  [name]
  `(undefbench* '~name))


(defn create-results-directories
  "Create directories to contain measurement results, location declared by
  options key `:results-directory`. Consults names contained in set
  `excludes`. If zero performance tests are to be executed (i.e., all tests are
  excluded), directories are not created.

  Creates `<options-results-dir>/` then `<options-results-dir>/<version>/`."
  {:UUIDv4 #uuid "a75a0d12-150b-43e6-b3b7-6a758528a3b2"
   :no-doc true}
  [excludes]
  (let [options (get-options)
        mkdir #(.mkdir (io/file %))
        results-dirname (options :results-directory)
        version-dirname (str results-dirname
                             "/version "
                             (project-version))
        unique-test-names (distinct (map :group @registry))
        non-excluded-names (remove
                            excludes
                            unique-test-names)]
    (if (seq non-excluded-names)
      (do
        (mkdir results-dirname)
        (mkdir version-dirname)))))


(def ^{:no-doc true}
  *performance-testing-options*-docstring
  "Hashmap containing Criterium options applied during performance measuring.
  Defaults to
  [`criterium.core/*default-quick-bench-opts*`](https://github.com/hugoduncan/criterium/blob/bb10582ded6de31b4b985dc31d501db604c0e461/src/criterium/core.clj#L92).

  Any function that consults this value decides what to do with it; not settable
  by user.

  See also [`criterium.core/*default-benchmark-opts*`](https://github.com/hugoduncan/criterium/blob/bb10582ded6de31b4b985dc31d501db604c0e461/src/criterium/core.clj#L83)
  and
  [[*lightning-benchmark-opts*]] for alternate values.")



(def ^{:dynamic true
       :doc *performance-testing-options*-docstring
       :no-doc true}
  *performance-testing-options* crit/*default-quick-bench-opts*)


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


(defmacro run-one-benchmark
  "Given 1-arity, S-expression `fexpr` representing a function and argument
  `arg`, run one benchmark test using keyword `thoroughness` to designate the
  Criterium options, sending result to &ast;`out`&ast;.

  `thoroughness` is one of `:default`, `:quick`, or `:lightning`.

  Example:
  ```clojure
  (run-one-benchmark (fn [n] (+ n n)) 9 :lightning)
  ```

  See [[run-one-registered-benchmark]] and [[*lightning-benchmark-opts*]]."
  {:UUIDv4 #uuid "f7d36987-4451-4115-99e3-2fc57bee6a91"}
  [fexpr arg thoroughness]
  `(crit/benchmark (~(eval fexpr) ~arg) (thoroughnesses ~thoroughness)))


(defmacro run-one-registered-benchmark
  "Given benchmark registered by `name`, and a keyword `thoroughness` that
  designates the Criterium options, runs benchmarks. Returns a map with keys
  provided by the benchmark arguments `n` associated with the benchmark results
  for that `n`.

  `thoroughness` is one of `:default`, `:quick`, or `:lightning`.

  Example:
  ```clojure
  (defbench my-bench \"my-group\" (fn [x] (inc x)) [97 98 99])

  (run-one-registered-benchmark my-bench :lightning)
  ```

  See [[run-one-benchmark]] and [[*lightning-benchmark-opts*]]."
  {:UUIDv4 #uuid "eac060b1-2175-4cf3-a3b9-5eb57c0438cb"}
  [name thoroughness]
  `(reduce #(assoc %1
                   %2
                   (crit/benchmark
                    (~(eval (get-in @registry [name :fexpr])) %2)
                    (thoroughnesses ~thoroughness)))
           {}
           ~(get-in @registry [name :n])))


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
  "Given string `ver`, symbol `name`, string `group`, function object `f`,
  function S-expression `fexpr`, test argument `arg`, option hashmap `opts`,
  and index integer `idx`, measures the evaluation time under the current
  Criterium benchmark settings and saves results to filesystem.

  Example:
  ```clojure
  (run-save-benchmark \"77-SNAPSHOT7\"
                      'mult-two-nums
                      \"adding --- stuff\"
                      (fn [n] (* n n))
                      '(fn [n] (* n n))
                      22
                      5
                      options)
  ```

  See also [[run-one-benchmark]] and [[run-one-registered-benchmark]] for
  utilities to quickly benchmark an expression."
  {:UUIDv4 #uuid "5f87d695-9d47-4553-8596-1b9ad26f4bab"}
  [ver name group f fexpr arg idx opts]
  (let [dirname (opts :results-directory)
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
                       :name (str name)
                       :group group
                       :fexpr (str fexpr)
                       :arg arg
                       :date (date)
                       :UUIDv4 (random-uuid)
                       :testing-thoroughness (opts :testing-thoroughness)
                       :parallel? (opts :parallel?)
                       :n-threads (opts :n-threads)}
        results (assoc results :fastester/metadata test-metadata)
        results (if (opts :save-benchmark-fn-results?)
                  results
                  (assoc results
                         :results
                         :fastester/benchmark-fn-results-elided))
        results (dissoc-identifying-metadata results)]
    (pretty-print-to-file filepath results)))


;; The following license terms apply to `pmap-with`:

;; Copyright (c) Rich Hickey. All rights reserved.
;; The use and distribution terms for this software are covered by the
;; Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;; which can be found in the file epl-v10.html at the root of this distribution.
;; By using this software in any fashion, you are agreeing to be bound by
;; the terms of this license.
;; You must not remove this notice, or any other, from this software.


(defn pmap-with
  "Maps function `f` over elements of collection `coll`, using `n` threads,
  analogous to `clojure.core/pmap`. If `n` is not supplied, delegates directly
  to `pmap`, where `n` is bound to
  `(+ 2 (.. Runtime getRuntime availableProcessors))`.

  Example:
  ```clojure
  ;; in parallel, apply `inc` to elements of a vector, using two threads
  (pmap-with inc [1 2 3] 2) ;; => (2 3 4)
  ```

  Note: `pmap-with` does not provide the multi-collection arity.

  Like `map`, except `f` is applied in parallel. Semi-lazy in that the parallel
  computation stays ahead of the consumption, but doesn't realize the entire
  result unless required. Only useful for computationally intensive functions
  where the time of `f` dominates the coordination overhead."
  {:UUIDv4 #uuid "65bdd065-4ca7-4730-afad-0d76527459a8"
   :no-doc true
   :original-source "https://github.com/clojure/clojure/blob/ce55092f2b2f5481d25cff6205470c1335760ef6/src/clj/clojure/core.clj#L7079"
   :original-author "Rich Hickey"
   :license "Eclipse Public License 1.0"}
  ([f coll] (pmap f coll))
  ([f coll n]
   (let [rets (map #(future (f %)) coll)
         step (fn step [[x & xs :as vs] fs]
                (lazy-seq
                 (if-let [s (seq fs)]
                   (cons (deref x) (step xs (rest s)))
                   (map deref vs))))]
     (step rets (drop n rets)))))


(defn load-benchmarks-ns
  "Given options hashmap `opt`, `require`s the testing namespaces declared by
  the Fastester options `:benchmarks-directory` and `:benchmarks-filenames`.

  Note: Invokes `clear-registry!`."
  {:UUIDv4 #uuid "f15a8cff-88dd-4c71-81b5-dab61bfeaffc"
   :no-doc true
   :implementation-note "Don't feel great about abusing `require` like this, but
  it appears to work okay, and it seems how Leiningen gets tasks done, too. See
  `leiningen.core.utils/require-resolve`."}
  [opt]
  (do
    (clear-registry!)
    (doseq [fname (opt :benchmarks-filenames)]
      (let [filepath (str (opt :benchmarks-directory) fname)
            tests-file (clojure.string/replace filepath #"\.[\w\d]{3}$" "")]
        (if (opt :verbose?) (println "Loading tests from " tests-file))
        (require (symbol tests-file) :reload)))))


(defn run-benchmarks
  "Execute non-excluded performance tests, as governed by test names in set
  `excludes`.

  If option `:parallel?` is `true`, runs tests in parallel. Warning: Running
  tests in parallel results in inconsistent time measurements. Use
  `:parallel? true` only for sanity-checking the return values of `(f n)`, not
  for final performance measurements."
  {:UUIDv4 #uuid "68d16e2e-2ab2-4dcd-9609-e237d6991594"
   :no-doc true
   :implementation-notes
   "See
   https://clojuredocs.org/clojure.core/future#example-542692c9c026201cdc326a7b
   and
   https://clojure.atlassian.net/browse/CLJ-124
   for discussion of cleanly shutting down agents, relevant when using
   `pmap-with`."}
  [& exclude?]
  (let [options (get-options)
        _ (load-benchmarks-ns options)
        excludes (if exclude?
                   (options :excludes)
                   #{})
        reg (remove #(excludes ((val %) :group)) @registry)
        get-idx #(.indexOf %1 %2)
        r-fn (fn [v [name settings]]
               (concat v
                       (map #(assoc settings :n %1 :name name)
                            (settings :n))))
        expanded-reg (reduce r-fn [] reg)
        num-reg (dec (count expanded-reg))
        verbose (options :verbose?)
        runner-fn (fn [t]
                    (let [idx (get-idx expanded-reg t)]
                      (if verbose (println (str "Test " idx "/" num-reg)))
                      (run-save-benchmark (project-version)
                                          (t :name)
                                          (t :group)
                                          (t :f)
                                          (t :fexpr)
                                          (t :n)
                                          idx
                                          options)))
        runner ({true (fn [f coll] (pmap-with f coll (options :n-threads)))
                 false map}
                (options :parallel?))]
    (create-results-directories excludes)
    (doall (runner runner-fn expanded-reg))
    (if (and (options :parallel?)
             (not *repl*)) (shutdown-agents))
    (if verbose (println "Performance testing complete."))))


(defn run-all-benchmarks
  "Run all registered benchmarks, ignoring options key `:excludes`.

  Example:
  ```clojure
  (run-all-benchmarks)
  ```

  See also [[run-selected-benchmarks]]."
  {:UUIDv4 #uuid "50b19eef-32f1-4586-a317-21e1f20235cf"}
  []
  (run-benchmarks))


(defn run-selected-benchmarks
  "Run *some* registered benchmarks, skipping any with a name contained in
  option `:excludes`.

  Example:
  ```clojure
  (run-selected-benchmarks)
  ```

  See also [[run-all-benchmarks]]."
  {:UUIDv4 #uuid "ed3dd772-08d5-47cc-85b9-608892f8c96a"}
  []
  (run-benchmarks true))


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

