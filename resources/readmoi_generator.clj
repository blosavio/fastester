(ns readmoi-generator
  "Script to load options and perform actions.

  CIDER eval buffer C-c C-k generates an html page and a markdown chunk."
  {:no-doc true}
  (:require
   [com.hypirion.clj-xchart :as xc]
   [criterium.core :as crit]
   [fastester.display :refer [fastester-defaults]]
   [fastester.measure :refer [*lightning-benchmark-opts*
                              clear-registry!
                              defbench
                              range-pow-10
                              range-pow-2
                              registry
                              run-one-benchmark
                              run-one-registered-benchmark
                              undefbench]]
   [readmoi.core :refer [*project-group*
                         *project-name*
                         *project-version*
                         *wrap-at*
                         generate-all
                         prettyfy
                         print-form-then-eval]]))


(def project-metadata (read-string (slurp "project.clj")))
(def readmoi-options (load-file "resources/readmoi_options.edn"))


(generate-all project-metadata readmoi-options)


(defn -main
  [& args]
  {:UUIDv4 #uuid "053064d2-125d-48cc-8c22-1252aaffb841"}
  (println "Generated Fastester ReadMe."))

