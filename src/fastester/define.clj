(ns fastester.define
  "Define benchmarks.

  Benchmarks are typically defined in a dedicated namespace, akin to defining
  unit tests.")


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

