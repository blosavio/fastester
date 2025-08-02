(ns fastester.measure-tests
  (:require
   [clojure.test :refer [are
                         deftest
                         is
                         run-test
                         run-tests
                         testing]]
   [fastester.measure :refer :all]))


(deftest range-pow-10-tests
  (are [x y] (= x y)
    [1] (range-pow-10 0)
    [1 10] (range-pow-10 1)
    [1 10 100] (range-pow-10 2)
    [1 10 100 1000 10000 100000 1000000] (range-pow-10 6)))


(deftest range-pow-2-tests
  (are [x y] (= x y)
    [1] (range-pow-2 0)
    [1 2] (range-pow-2 1)
    [1 2 4] (range-pow-2 2)
    [1 2 4 8 16 32 64 128 256] (range-pow-2 8)))


#_(run-tests)