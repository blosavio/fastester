(ns semver.benchmarks
  "Quick benchmarks to verify sorting with `clojure.core/compare`."
  (:require
   [fastester.define :refer [defbench]]
   [fastester.display :refer [generate-documents]]
   [fastester.measure :refer [run-benchmarks]]))


(defbench
  inc-test
  "A sham group"
  (fn [x] (inc x))
  [99])


#_(run-benchmarks "./resources/semver_options.edn")
#_(generate-documents "./resources/semver_options.edn")

