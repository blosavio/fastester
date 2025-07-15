(ns chlog-generator
  "CIDER eval buffer C-c C-k generates a 'changelog.md' in the project's top
  level directory and 'changelog.html' in the 'resources/' directory."
  {:no-doc true}
  (:require
   [hiccup2.core :as h2]
   [chlog.core :refer [generate-all-changelogs]]))


(def chlog-options (load-file "resources/chlog_options.edn"))


(generate-all-changelogs chlog-options)


(defn -main
  [& args]
  {:UUIDv4 }
  (println "generated fastester performance page"))