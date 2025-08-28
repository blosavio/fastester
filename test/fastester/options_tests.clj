(ns fastester.options-tests
  (:require
   [clojure.test :refer [are
                         deftest
                         is
                         run-test
                         run-tests
                         testing]]
   [fastester.options :refer :all]))


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


#_(run-tests)

