(ns fastester.performance.benchmarks
  "Beware: When actively developing a benchmarking namespace, the registry may
 become stale. Use

  1. [[undefbench]] to undefine a single benchmark, or

  2. [[clear-registry!]] to return the registry to an empty state, before
  re-evaluating the entire namespace."
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

(def plus-test-name "plus, vary number of digits in args")

(defbench add-1-arg plus-test-name (fn [n] (delayed-+ n))     (range-pow-10 6))
(defbench add-2-arg plus-test-name (fn [n] (delayed-+ n n))   (range-pow-10 6))
(defbench add-3-arg plus-test-name (fn [n] (delayed-+ n n n)) (range-pow-10 6))



;;;; 2. Testing regular addition with different-sized operands

(def plus-test-name-2 "plus, vary number of operands")

(def seq-of-n-repeats
  (doall (reduce #(assoc %1 %2 (take %2 (repeat 64))) {} (range-pow-10 5))))

(defbench
  add-many-args
  plus-test-name-2
  (fn [n] (apply + (seq-of-n-repeats n)))
  (range-pow-10 5))



;;;; 3a. Testing basic mapping over a sequence of numbers

(def range-of-length-n
  (reduce #(assoc %1 %2 (range %2)) {} (range-pow-10 5)))

(defbench
  map-inc-across-a-sequence
  "mapping stuff"
  (fn [n] (doall (map inc (range-of-length-n n))))
  (range-pow-10 5))



;;;; 3b. Testing mapping over a sequence of strings

(def abc-cycle-of-length-n
  (reduce #(assoc %1 %2 (take %2 (cycle ["a" "b" "c"])))
          {}
          (range-pow-10 5)))

(defbench
  map-UC-over-a-cycle
  "mapping stuff"
  (fn [n] (doall (map str/upper-case (abc-cycle-of-length-n n))))
  (range-pow-10 5))



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
  (require '[fastester.measure :refer [clear-registry!
                                       run-all-benchmarks
                                       run-one-registered-benchmark
                                       run-selected-benchmarks
                                       registry]])

  #_(run-all-benchmarks)
  #_(run-selected-benchmarks)
  #_(fastester.measure/clear-registry!)
  #_ @fastester.measure/registry
  #_(println "\n\n\n\n\n")
  )

