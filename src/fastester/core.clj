(ns fastester.core
  "Run performance benchmarks and create displays of those measurements.

  High-level workflow:

  1. Set options.
  2. Write benchmarks with [[defbench]].
  3. Run benchmarks with [[run-benchmarks]].
  4. Generate html document with [[generate-documents]]."
  (:require
   [fastester.measure :refer [run-benchmarks]]
   [fastester.display :refer [generate-documents]])
  (:gen-class))


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

  * `:benchmarks` executes `(`[[run-benchmarks]]`)`
  * `:documents` executes `(`[[generate-documents]]`)`.

  Examples:
  ```bash
  $ lein run -m fastester.core :benchmarks
  $ lein run -m fastester.core :documents
  ```

  Read options from `./resources/fastester_options.edn` because explicit options
  filename is not supplied.

  Example:
  ```bash
  $ lein run -m fastester.core :benchmarks ./foobar_options.edn
  ```

  Reads options from explicitly-given `./foobar_options.edn`."
  {:UUIDv4 #uuid "c20e4554-c4cd-4d92-912d-4e8affb1ddc9"}
  ([action] (-main action nil))
  ([action explicit-options-filename]
   (do
     (case action
       ":benchmarks" (run-benchmarks explicit-options-filename)
       ":documents" (generate-documents explicit-options-filename))
     (System/exit 0))))

