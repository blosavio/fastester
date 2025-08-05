(ns fastester-generator
  "CIDER eval buffer C-c C-k loads the Fastester options and generates a
  'performance.html', defaulting to the 'resources/' directory."
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


(defn -main
  [& args]
  {:UUIDv4 #uuid "c20e4554-c4cd-4d92-912d-4e8affb1ddc9"}
  (println "TODO: Generated fastester performance documents."))

