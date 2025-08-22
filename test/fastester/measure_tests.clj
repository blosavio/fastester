(ns fastester.measure-tests
  (:require
   [clojure.test :refer [are
                         deftest
                         is
                         run-test
                         run-tests
                         testing]]
   [fastester.measure :refer :all]))


(deftest unambiguous-key?-tests
  (testing "map's keys not qualified symbols"
    (is (thrown? Exception (unambiguous-key? {:foo 101} :bar)))
    (is (thrown? Exception (unambiguous-key? {'foo 101} 'foo))))
  (testing "argument key not a symbol"
    (is (thrown? Exception (unambiguous-key? {'qual-1/foo 101} :bar))))
  (testing "empty map"
    (is (= [false [] {}])(unambiguous-key? {} 'foo)))
  (testing "unambiguous, unqualified arguemnt key"
    (is (= (unambiguous-key? {'qual-1/foo 99
                              'qual-1/bar 88
                              'qual-2/baz 77}
                             'foo)
           [true ['qual-1/foo] {'qual-1/foo 99
                                'qual-1/bar 88
                                'qual-2/baz 77}])))
  (testing "unambiguous, qualified argument key"
    (is (=
         (unambiguous-key? {'qual-1/foo 99
                            'qual-1/bar 88
                            'qual-2/foo 77} 'qual-2/foo)
         [true ['qual-2/foo] {'qual-1/foo 99
                              'qual-1/bar 88
                              'qual-2/foo 77}])))
  (testing "ambiguous argument key"
    (is (= (unambiguous-key? {'qual-1/foo 99
                              'qual-1/bar 88
                              'qual-2/foo 77} 'foo)
           [false ['qual-1/foo 'qual-2/foo] {'qual-1/foo 99
                                             'qual-1/bar 88
                                             'qual-2/foo 77}]))))


(deftest project-version-tests
  (is (= java.lang.String (type (project-version (get-options))))))


(deftest get-options-tests
  (let [req-options-keys #{:project-formatted-name
                           :responsible
                           :copyright-holder
                           :fastester-UUID
                           :benchmarks
                           :html-directory
                           :html-filename
                           :img-subdirectory
                           :markdown-directory
                           :markdown-filename
                           :results-url
                           :results-directory
                           :verbose?
                           :testing-thoroughness
                           :parallel?
                           :save-benchmark-fn-results?}]
    (is (every? (set (keys (get-options))) req-options-keys))))


;; mutable items can be tricky to test because `lein test` executes
;; asynchronously

(deftest registry-tests
  (testing "registry type"
    (is (map? @registry)))
  (testing "defining a benchmark"
    (is (do
          (clear-registry!)
          (defbench foo/baz "bar" (fn [q] (+ q q)) [1 2 3])
          (is (= (update-in @registry ['foo/baz] dissoc :f :ns)
                 {'foo/baz {:name "baz"
                            :group "bar"
                            :fexpr '(fn [q] (+ q q))
                            :n [1 2 3]}})))))
  (testing "clearing registry"
    (is (do
          (clear-registry!)
          (is (empty? @registry)))))
  (testing "undefining a benchmark"
    (is (do
          (defbench foo/baz "quz" (fn [w] (* w w)) [4 5 6])
          (undefbench foo/baz)
          (is (empty? @registry)))))
  (testing "undefining failure due to name ambiguity"
    (is (thrown? Exception (do
                             (clear-registry!)
                             (defbench foo/baz "quz" (fn [x] (- x)) [1 2 3])
                             (defbench bar/baz "qua" (fn [y] (- y)) [4 5 6])
                             (undefbench baz))))))


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


#_(run-tests)

