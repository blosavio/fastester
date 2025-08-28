(ns fastester.measure-tests
  (:require
   [clojure.test :refer [are
                         deftest
                         is
                         run-test
                         run-tests
                         testing]]
   [fastester.define :refer [defbench]]
   [fastester.measure :refer :all]))


(deftest date-tests
  (is (map? (date)))
  (is (int? ((date) :year)))
  (is (< 2000 ((date) :year)))
  (is (contains? #{"January"
                   "February"
                   "March"
                   "April"
                   "May"
                   "June"
                   "July"
                   "August"
                   "September"
                   "October"
                   "November"} ((date) :month)))
  (is (int? ((date) :day)))
  (is (<= 1 ((date) :day) 31)))


(deftest dissoc-identifying-metadata-tests
  (is (= {:runtime-details {}}
         (dissoc-identifying-metadata
          {:runtime-details {:vm-version 0
                             :name "foo"
                             :java-runtime-version 0
                             :input-arguments "baz"}}))))


(deftest benchmark-nspace+syms-tests
  (are [x y] (= x y)
    (benchmark-nspace+syms {:benchmarks {}})
    []

    (benchmark-nspace+syms {:benchmarks {'nspace-1/name-1 #{}}})
    []

    (benchmark-nspace+syms {:benchmarks {'nspace-1/name-1 #{:foo}}})
    [['nspace-1/name-1 :foo]]

    (benchmark-nspace+syms {:benchmarks {'nspace-1/name-1 #{:foo
                                                            :bar
                                                            :baz}}})
    [['nspace-1/name-1 :baz]
     ['nspace-1/name-1 :bar]
     ['nspace-1/name-1 :foo]]

    (benchmark-nspace+syms {:benchmarks {'nspace-1/name-1 #{:foo
                                                            :bar
                                                            :baz}
                                         'nspace-2/name-2 #{:quz
                                                            :qux
                                                            :quq}}})
    [['nspace-1/name-1 :baz]
     ['nspace-1/name-1 :bar]
     ['nspace-1/name-1 :foo]
     ['nspace-2/name-2 :quq]
     ['nspace-2/name-2 :qux]
     ['nspace-2/name-2 :quz]]))


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


(deftest benchmark-fn-tests
  (is (= 100)
      (do
        (defbench foobar "foobar group" #(inc %) [1 2 3])
        ((foobar :f) 99))))


#_(run-tests)

