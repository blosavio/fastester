(ns fastester-generator
  "CIDER eval buffer C-c C-k loads the Fastester options and generates a
  'performance.html', defaulting to the 'resources/' directory."
  {:no-doc true}
  (:require
   [hiccup2.core :as h2]
   [fastester.core :refer [do-tests-and-create-displays]]
   [fastester.display :refer [generate-all-perflogs]]
   [fastester.measure :refer [clear-perf-test-registry!
                              do-all-performance-tests
                              do-selected-performance-tests
                              load-tests-ns]]))


(def options (load-file "resources/fastester_options.edn"))


(load-tests-ns options)

#_ @fastester.measure/perf-test-registry
#_(do-all-performance-tests)
#_(do-selected-performance-tests)


#_(generate-all-perflogs options)


(defn -main
  [& args]
  {:UUIDv4 #uuid "c20e4554-c4cd-4d92-912d-4e8affb1ddc9"}
  (println "generated fastester performance page"))

