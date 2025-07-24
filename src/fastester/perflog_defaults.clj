(def ^{:no-doc true}
  perflog-defaults-docstring
  "A hash-map residing in `perflog_defaults.clj` that supplies the default
 values for the following options keys:

  * `:perflog-tests-directory`
  * `:preflog-tests-filename`
  * `:perflog-html-directory`
  * `:perflog-html-filename`
  * `:perflog-markdown-directory`
  * `:perflog-markdown-filename`
  * `:perflog-results-directory`
  * `:verbose?`
  * `:testing-thoroughness`
  * `:parallel?`
  * `:tidy-html?`
  * `:perflog-preamble`


  Override default values by associating new values into the Fastester _options_
  hash-map. See [[generate-all-perflogs]].")


(def ^{:doc perflog-defaults-docstring
       :UUIDv4 #uuid "9e50c897-a734-4fb9-b671-b924bc209a81"}
  perflog-defaults {:perflog-tests-directory "test/fastester/performance/"
                    :preflog-tests-filename "tests.clj"

                    :perflog-html-directory "doc/"
                    :perflog-html-filename "performance.html"

                    :perflog-markdown-directory "doc/"
                    :perflog-markdown-filename "perfornamce.md"

                    :perflog-results-directory "resources/performance_entries/"

                    :verbose? false
                    :testing-thoroughness :quick
                    :parallel? false

                    :tidy-html? false

                    :perflog-preamble [:div
                                       [:p "Perflog preamble..."]

                                       [:a
                                        {:href
                                         "https://example.com"}
                                        "An example link."]

                                       [:p "Example code:"]

                                       [:pre [:code "(map inc [1 2 3])"]]]})

