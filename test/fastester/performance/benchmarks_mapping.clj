(ns fastester.performance.benchmarks-mapping
  "Demonstrate multiple benchmark namespaces.

  See also [[fastester.performance.benchmarks]]."
  (:require
   [clojure.math :refer :all]
   [clojure.string :as str]
   [fastester.define :refer [defbench]]
   [fastester.measure :refer [range-pow-10]]))


;;;; 3a. Testing basic mapping over a sequence of numbers

(def range-of-length-n
  (reduce #(assoc %1 %2 (range %2)) {} (range-pow-10 5)))

(defbench
  map-inc-across-a-sequence
  "Mapping stuff"
  (fn [n] (doall (map inc (range-of-length-n n))))
  (range-pow-10 5))



;;;; 3b. Testing mapping over a sequence of strings

(def abc-cycle-of-length-n
  (reduce #(assoc %1 %2 (take %2 (cycle ["a" "b" "c"])))
          {}
          (range-pow-10 5)))

(defbench
  map-UC-over-a-cycle
  "Mapping stuff"
  (fn [n] (doall (map str/upper-case (abc-cycle-of-length-n n))))
  (range-pow-10 5))

