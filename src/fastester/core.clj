(ns fastester.core
  "Run performance benchmarks and create displays of those measurements.

  High-level workflow:

  1. Write benchmarks in a dedicated namespace.
  2. Run performance benchmarks with, e.g., [[run-benchmarks]].
  3. Create an html document with charts and tables with, e.g.,
  [[generate-documents]]."
  (:require
   [fastester.measure :refer [run-benchmarks]]
   [fastester.display :refer [generate-documents]]))


(defn run-benchmarks-and-generate-documents
  "Run all benchmarks and generate an html document that displays comparisons to
  previous versions.

  See [[run-benchmarks]] and [[generate-documents]]."
  {:UUIDv4 #uuid "e26d0308-229f-4f2d-b24f-60ff9e59d109"}
  ([] (do (run-benchmarks nil)
          (generate-documents nil)))
  ([explicit-options-filename]
   (do (run-benchmarks explicit-options-filename)
       (generate-documents explicit-options-filename))))


(defn -main
  "Given keyword `action`, runs a Fastester task with the following mappings:

  * `:benchmarks` executes `(run-benchmarks)`
  * `:documents` executes `(generate-docs)`.

  Examples:
  ```bash
  $ lein run :benchmarks
  $ lein run :documents
  ```

  Reads options from `./resources/fastester_options.edn` unless an options
  filename is supplied.

  Example:
  ```bash
  $ lein run :benchmarks ./foobar_options.edn
  ```"
  {:UUIDv4 #uuid "c20e4554-c4cd-4d92-912d-4e8affb1ddc9"}
  ([action] (-main action nil))
  ([action explicit-options-filename]
   (do
     (case action
       ":benchmarks" (run-benchmarks explicit-options-filename)
       ":documents" (generate-documents explicit-options-filename))
     (System/exit 0))))

