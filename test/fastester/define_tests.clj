(ns fastester.define-tests
  (:require
   [clojure.test :refer [are
                         deftest
                         is
                         run-test
                         run-tests
                         testing]]
   [fastester.define :refer [defbench]]))


(defbench
  test-benchmark
  "test group"
  (fn [x] (inc x))
  [1 2 3])


(deftest defbench-tests
  (testing "defined values"
    (are [x y] (= x y)
      "test group" (test-benchmark :group)
      '(fn [x] (inc x)) (test-benchmark :fexpr)
      [1 2 3] (test-benchmark :n)
      "test-benchmark" (test-benchmark :name)))
  (testing "fn object"
    (is (= 100  ((test-benchmark :f) 99)))))


#_(run-tests)

