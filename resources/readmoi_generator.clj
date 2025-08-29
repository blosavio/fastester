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
                         generate-all
                         prettyfy
                         print-form-then-eval]]))


(def project-metadata (read-string (slurp "project.clj")))
(def readmoi-options (load-file "resources/readmoi_options.edn"))


(do
  (generate-all project-metadata readmoi-options)
  (let [fname "doc/readme.html"
        page (slurp fname)
        match #"doc/zap_img/group-0-fexpr-0.svg"
        replacement "zap_img/group-0-fexpr-0.svg"
        replaced (str/replace page match replacement)]
    (spit fname replaced)))


(defn -main
  [& args]
  {:UUIDv4 #uuid "053064d2-125d-48cc-8c22-1252aaffb841"}
  (println "Generated Fastester ReadMe."))

