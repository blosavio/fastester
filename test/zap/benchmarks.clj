(ns zap.benchmarks
  "ReadMe follow-along demonstrating Fastester on imaginary implementation
  improvements on `zap` function."
  (:require [clojure.string :as str]
            [clojure.test :refer [are
                                  is
                                  deftest
                                  run-test
                                  run-tests
                                  testing]]
            [fastester.display :refer [generate-documents]]
            [fastester.measure :refer [defbench
                                       project-version
                                       range-pow-10
                                       run-benchmarks
                                       run-one-defined-benchmark]]))


;; Strategy: Instead of changing the implementation of `zap`, we will *simulate*
;; a performance enhancement by running the mapping function fewer times. For
;; example, if we want to give the impression that version 12 is 25% faster than
;; version 11, then during version 11 benchmarking, we'll increment each element
;; four times, and during version 12 benchmarking, we'll increment each element
;; three times, giving the illusion of a 25% speedup.


(defn do-fn-many-times
  "Returns a version of function `f`, evaluates many times (based on version),
  returning the result of the final evaluation."
  {:no-doc true}
  [f]
  (fn [& args]
    (let [ver (project-version {:preferred-version-info :lein})
          ver-to-times {"11" 1000
                        "12" 0}
          _ (dotimes [n (dec (get ver-to-times ver 100))]
              (apply f args))]
      (apply f args))))


(defn zap
  "Like `map`, but repeats `f` many times to simulate performance improvement."
  {:no-doc true}
  [f coll]
  (map (do-fn-many-times f) coll))



;; Set length of longest sequence

(def exponent 4)



;;;; Test incrementing integers

;; Define `args` outside of `defbench` function expression so that sequence
;; creation is not included in benchmark timing.

(def range-of-length-n (reduce #(assoc %1 %2 (range %2)) {} (range-pow-10 exponent)))


(defbench
  zap-inc
  "faster `zap` implementation"
  (fn [n] (doall (zap inc (range-of-length-n n))))
  (range-pow-10 exponent))



;;;; Test upper-casing integers

(def abc-cycle-of-length-i
     (reduce #(assoc %1 %2 (take %2 (cycle ["a" "b" "c"])))
             {}
             (range-pow-10 exponent)))


(defbench
  zap-uc
  "faster `zap` implementation"
  (fn [i] (doall (zap str/upper-case (abc-cycle-of-length-i i))))
  (range-pow-10 exponent))



(comment
  (def zap-options-filename "./resources/zap_options.edn")

  #_(run-one-defined-benchmark zap-inc :lightning)
  #_(run-one-defined-benchmark zap-uc :lightning)
  #_(run-benchmarks zap-options-filename)
  #_(generate-documents zap-options-filename)
  )


;; Unit test the benchmark functions

(deftest benchmark-fn-tests
  (are [x y] (= x y)
    [1 2 3 4 5 6 7 8 9 10]
    ((zap-inc :f) 10)

    ["A" "B" "C" "A" "B" "C" "A" "B" "C" "A"]
    ((zap-uc :f) 10)))

#_(run-tests)

