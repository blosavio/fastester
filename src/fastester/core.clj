(ns fastester.core
  "Run performance benchmarks and create displays of those measurements.

  High-level workflow:

  1. Write benchmarks in a dedicated namespace.
  2. Run performance benchmarks with, e.g., [[do-all-performance-tests]].
  3. Create an html document with charts and tables with, e.g.,
  [[generate-all-displays]]."
  (:require
   [fastester.measure :refer [run-all-benchmarks]]
   [fastester.display :refer [generate-documents]]))


(defn run-benchmarks-and-generate-documents
  "Run all benchmarks and generate an html document that displays comparisons to
  previous versions.

  See [[do-all-performance-tests]] and [[generate-all-displays]]"
  {:UUIDv4 #uuid "e26d0308-229f-4f2d-b24f-60ff9e59d109"}
  []
  (do
    (run-all-benchmarks)
    (generate-documents)))

