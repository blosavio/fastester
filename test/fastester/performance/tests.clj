(ns fastester.performance.tests
  "Beware: When actively developing a benchmarking namespace, the test registry
  may become stale. Use `fastester.measure/clear-perf-test-registry!` to return
  to empty state before re-defining perf tests."
  (:require
   [clojure.math :refer :all]
   [clojure.string :as str]
   [fastester.measure :refer [defperf
                              range-pow-10]]))



;;;; 1. Testing delayed addition of one to three numbers

(def dly 20)

(defn delayed-+
  [& args]
  (do
    (Thread/sleep dly)
    (apply + args)))

(def plus-test-name "plus, vary number of digits in args")

(defperf plus-test-name (fn [n] (delayed-+ n))     (range-pow-10 6))
(defperf plus-test-name (fn [n] (delayed-+ n n))   (range-pow-10 6))
(defperf plus-test-name (fn [n] (delayed-+ n n n)) (range-pow-10 6))



;;;; 2. Testing regular addition with different-sized operands

(def plus-test-name-2 "plus, vary number of operands")

(def seq-of-n-repeats
  (doall (reduce #(assoc %1 %2 (take %2 (repeat 64))) {} (range-pow-10 5))))

(defperf plus-test-name-2 (fn [n] (apply + (seq-of-n-repeats n))) (range-pow-10 5))



;;;; 3a. Testing basic mapping over a sequence of numbers

(def mapping-test-name "mapping stuff")

(def range-of-length-n
  (doall (reduce #(assoc %1 %2 (range %2)) {} (range-pow-10 5))))

(defperf mapping-test-name
  (fn [n] (map inc (range-of-length-n n))) (range-pow-10 5))



;;;; 3b. Testing mapping over a sequence of strings

(def abc-cycle-of-length-n
  (doall (reduce #(assoc %1 %2 (take %2 (cycle ["a" "b" "c"])))
                 {}
                 (range-pow-10 5))))

(defperf
  mapping-test-name
  (fn [n] (map str/upper-case (abc-cycle-of-length-n n))) (range-pow-10 5))



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

(defperf
  "custom `conj`"
  (fn [n] (my-conj (seq-of-n-rand-ints n) :tail-value))
  (range-pow-10 7))



(comment
  (require '[fastester.measure :refer [clear-perf-test-registry!
                                       do-all-performance-tests
                                       do-selected-performance-tests
                                       perf-test-registry]])

  #_(do-all-performance-tests)
  #_(do-selected-performance-tests)
  #_(fastester.measure/clear-perf-test-registry!)
  #_ @fastester.measure/perf-test-registry
  #_(println "\n\n\n\n\n")
  )

