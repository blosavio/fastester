(ns fastester.performance.tests
  (:require
   [clojure.string :as str]
   [fastester.measure :refer [defperf
                              do-selected-performance-tests
                              do-all-performance-tests]]))


(defn log-range-fn
  [i]
  (map #(long (clojure.math/pow 10 %)) (range 0 i)))


(def log-range-5 (log-range-fn 5))
(def log-range-4 (log-range-fn 4))
(def log-range-3 (log-range-fn 3))


(def dly 0)


(defn delayed-+
  [& args]
  (do
    (Thread/sleep dly)
    (apply + args)))


(defn my-conj
  [coll x]
  #_(conj coll x)
  (persistent! (conj! (transient coll) x)))


(def plus-test-name "plus, vary number of digits in args")

(defperf plus-test-name (fn [n] (delayed-+ n))     log-range-5)
(defperf plus-test-name (fn [n] (delayed-+ n n))   log-range-5)
(defperf plus-test-name (fn [n] (delayed-+ n n n)) log-range-5)


(def plus-test-name-2 "plus, vary number of operands")

(defperf plus-test-name-2 (fn [n] (apply + (take n (repeat 64)))) log-range-5)


(def mapping-test-name "mapping stuff")

(defperf mapping-test-name (fn [n] (map inc (range n))) log-range-4)

(defperf
  mapping-test-name
  (fn [n] (map
           str/upper-case
           (take n (cycle ["a" "b" "c"]))))
  log-range-3)


(defperf
  "custom `conj`"
  (fn [n] (my-conj
           (vec (repeatedly n #(rand-int 99)))
           :tail-value))
  log-range-5)


#_(do-all-performance-tests)
#_(do-selected-performance-tests)
#_(fastester.measure/clear-perf-test-registry!)
#_(fastester.measure/create-results-directories #{})
#_ @fastester.measure/perf-test-registry
#_ fastester.measure/options
#_(println "\n\n\n\n\n")
#_*clojure-version*

