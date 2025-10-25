(ns redirect-svg-link
  "Readmoi produces two files: 'resources/readme.html' and 'README.md' in the
  project root directory. The svg links are relative to the root directory, so
  the html file needs to have its links re-pointed to the proper directory.

  This utility can't live in the same ns as the readmoi generator because it
  seems `lein run` can either

  a) eval `-main` in the context of vars loaded from `readmoi.core`, or

  b) eval a custom `-main` composed of `(do (readmoi.core/-main) (let []...)`

  but not both. Performing the `lein do` tasks serially works okay."
  (:require
   [clojure.string :as str]))


(defn -main
  "Generator script target."
  []
  (let [fname "doc/readme.html"
        page (slurp fname)
        match #"doc/zap_img/group-0-fexpr-0.svg"
        replacement "zap_img/group-0-fexpr-0.svg"
        replaced (str/replace page match replacement)]
    (spit fname replaced)))

