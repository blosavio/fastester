(ns fastester.measure-tests
  (:require
   [clojure.test :refer [are
                         deftest
                         is
                         run-test
                         run-tests
                         testing]]
   [fastester.measure :refer :all]))


(deftest log-range-tests
  (are [x y] (= x y)
    [1] (log-range 0)
    [1 10] (log-range 1)
    [1 10 100] (log-range 2)
    [1 10 100 1000 10000 100000 1000000] (log-range 6)))


#_(run-tests)