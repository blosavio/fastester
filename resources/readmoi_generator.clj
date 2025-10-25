(ns readmoi-generator
  "Script to load options and perform actions.

  CIDER eval buffer C-c C-k generates an html page and a markdown chunk."
  {:no-doc true}
  (:require
   [clojure.string :as str]
   [com.hypirion.clj-xchart :as xc]
   [criterium.core :as crit]
   [fastester.define :refer [defbench]]
   [fastester.measure :refer [*lightning-benchmark-opts*
                              range-pow-10
                              range-pow-2
                              run-benchmarks
                              run-manual-benchmark
                              run-one-defined-benchmark]]
   [fastester.options :refer [fastester-defaults]]
   [readmoi.core :refer [*project-group*
                         *project-name*
                         *project-version*
                         *wrap-at*
                         -main
                         print-form-then-eval]]))


(-main)

