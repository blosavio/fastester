(ns fastester.core
  "Run performance benchmarks and create displays of those measurements.

  High-level workflow:

  1. Write benchmarks in a dedicated namespace.
  2. Run performance benchmarks with, e.g., [[run-all-benchmarks]].
  3. Create an html document with charts and tables with, e.g.,
  [[generate-documents]]."
  (:require
   [fastester.measure :refer [run-all-benchmarks
                              run-selected-benchmarks]]
   [fastester.display :refer [generate-documents]]))


(defn run-benchmarks-and-generate-documents
  "Run all benchmarks and generate an html document that displays comparisons to
  previous versions.

  See [[run-all-benchmarks]] and [[generate-documents]]."
  {:UUIDv4 #uuid "e26d0308-229f-4f2d-b24f-60ff9e59d109"}
  []
  (do
    (run-all-benchmarks)
    (generate-documents)))


(defn -main
  "Given keyword `action`, runs a Fastester task with the following mappings:

  * `:all` executes `(run-all-benchmarks)`
  * `:selected` executes `(run-selected-benchmarks)`
  * `:gen` executes `(generate-docs)`.

  Examples:
  ```bash
  $ lein run :all
  $ lein run :selected
  $ lein run :gen
  ```

  Reads options from `./resources/fastester_options.edn` unless an explicit
  options filename is supplied.

  Example:
  ```bash
  $ lein run :all ./alternate_options.edn
  ```"
  [& [action explicit-options-filename]]
  {:UUIDv4 #uuid "c20e4554-c4cd-4d92-912d-4e8affb1ddc9"}
  (do
    (case action
      ":all" (run-all-benchmarks explicit-options-filename)
      ":selected" (run-selected-benchmarks explicit-options-filename)
      ":gen" (generate-documents explicit-options-filename))
    (System/exit 0)))

