(ns fastester.performance.benchmarks
  "Beware: When actively developing a benchmarking namespace, the registry may
  become stale. Use `clojure.core/ns-unmap` to undefine a single benchmark."
  (:require
   [clojure.math :refer :all]
   [clojure.string :as str]
   [fastester.measure :refer [defbench
                              range-pow-10]]))


;;;; 1. Testing delayed addition of one to three numbers

(def dly 10)

(defn delayed-+
  [& args]
  (do
    (Thread/sleep dly)
    (apply + args)))

(def group-1 "plus, vary number of digits in args")

(defbench add-1-arg group-1 (fn [n] (delayed-+ n))     (range-pow-10 6))
(defbench add-2-arg group-1 (fn [n] (delayed-+ n n))   (range-pow-10 6))
(defbench add-3-arg group-1 (fn [n] (delayed-+ n n n)) (range-pow-10 6))



;;;; 2. Testing regular addition with different-sized operands

(def group-2 "plus, vary number of operands")

(def seq-of-n-repeats
  (doall (reduce #(assoc %1 %2 (take %2 (repeat 64))) {} (range-pow-10 5))))

(defbench
  add-many-args
  group-2
  (fn [n] (apply + (seq-of-n-repeats n)))
  (range-pow-10 5))


;;;; See sibling namespace `benchmarks-mapping` for #3 benchmarks


;;;; 4. Comparing built-in `conj` to custom conj implemented with transients

(defn my-conj
  [coll x]
  #_(conj coll x)
  (persistent! (conj! (transient coll) x)))

(def seq-of-n-rand-ints
  (doall
   (reduce
    (fn [m k] (assoc m k (vec (repeatedly k #(rand-int 99)))))
    {}
    (range-pow-10 7))))

(defbench
  conj-onto-rands
  "custom `conj`"
  (fn [n] (my-conj (seq-of-n-rand-ints n) :tail-value))
  (range-pow-10 7))


(comment
  #_(require '[fastester.measure :refer [clear-registry!
                                         registry
                                         run-manual-benchmark
                                         run-one-defined-benchmark]])

  #_(clear-registry!)
  #_ @registry
  #_(run-manual-benchmark (fn [w] (my-conj [1 2 3] w)) :foo :lightning)
  #_(run-one-defined-benchmark
     fastester.performance.benchmarks/add-3-arg
     :lightning))

