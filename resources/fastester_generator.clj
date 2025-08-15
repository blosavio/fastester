(ns fastester-generator
  "CIDER eval buffer C-c C-k loads the Fastester options and generates a
  'performance.html', defaulting to the 'doc/' directory."
  {:no-doc true}
  (:require
   [hiccup2.core :as h2]
   [fastester.core :refer [run-benchmarks-and-generate-documents]]
   [fastester.display :refer [generate-documents]]
   [fastester.measure :refer [run-all-benchmarks
                              run-selected-benchmarks]]))


#_(run-all-benchmarks)
#_(run-selected-benchmarks)
#_(generate-documents)

