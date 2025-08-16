(ns fastester.measure-tests
  (:require
   [clojure.test :refer [are
                         deftest
                         is
                         run-test
                         run-tests
                         testing]]
   [fastester.measure :refer :all]))


(deftest project-version-tests
  (is (= java.lang.String (type (project-version (get-options))))))


(deftest get-options-tests
  (let [req-options-keys #{:project-formatted-name
                           :responsible
                           :copyright-holder
                           :fastester-UUID
                           :benchmarks-directory
                           :benchmarks-filenames
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
  (is (map? @registry))
  (is (do
        (clear-registry!)
        (defbench foo "bar" (fn [q] (+ q q)) [1 2 3])
        (is (= (update-in @registry ['foo] dissoc :f)
               {'foo {:group "bar"
                      :fexpr '(fn [q] (+ q q))
                      :n [1 2 3]}}))))
  (is (do
        (clear-registry!)
        (is (empty? @registry))))
  (is (do
        (defbench baz "quz" (fn [w] (* w w)) [4 5 6])
        (undefbench baz)
        (is (empty? @registry)))))


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

