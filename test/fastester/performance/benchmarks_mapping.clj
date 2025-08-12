(ns fastester.performance.benchmarks-mapping
  "Demonstrate multiple benchmark namespaces and qualified symbols.

  See also [[fastester.performance.benchmarks]]."
  (:require
   [clojure.math :refer :all]
   [clojure.string :as str]
   [fastester.measure :refer [defbench
                              range-pow-10]]))


;;;; 3a. Testing basic mapping over a sequence of numbers

(def range-of-length-n
  (reduce #(assoc %1 %2 (range %2)) {} (range-pow-10 5)))

(defbench
  mapping/map-inc-across-a-sequence
  "mapping stuff"
  (fn [n] (doall (map inc (range-of-length-n n))))
  (range-pow-10 5))



;;;; 3b. Testing mapping over a sequence of strings

(def abc-cycle-of-length-n
  (reduce #(assoc %1 %2 (take %2 (cycle ["a" "b" "c"])))
          {}
          (range-pow-10 5)))

(defbench
  mapping/map-UC-over-a-cycle
  "mapping stuff"
  (fn [n] (doall (map str/upper-case (abc-cycle-of-length-n n))))
  (range-pow-10 5))


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

