(def ^{:no-doc true}
  fastester-defaults-docstring
  "A hash-map residing in
 [`src/fastester_defaults.clj`](https://github.com/blosavio/fastester/blob/main/src/fastester/fastester_defaults.clj)
 that supplies the default values for the following option keys:

  * `:benchmarks-directory`
  * `:benchmarks-filenames`
  * `:html-directory`
  * `:html-filename`
  * `:img-subdirectory`
  * `:markdown-directory`
  * `:markdown-filename`
  * `:results-url`
  * `:results-directory`
  * `:verbose?`
  * `:testing-thoroughness`
  * `:parallel?`
  * `:n-threads`
  * `:save-benchmark-fn-results?`
  * `:tidy-html?`
  * `:preamble`

  Override default values by associating new values into the Fastester _options_
  hash-map. See [[run-all-benchmarks]] and [[generate-documents]].")


(def ^{:doc fastester-defaults-docstring
       :UUIDv4 #uuid "9e50c897-a734-4fb9-b671-b924bc209a81"}
  fastester-defaults
  {:benchmarks-directory "test/fastester/performance/"
   :benchmarks-filenames "benchmarks.clj"

   :html-directory "doc/"
   :html-filename "performance.html"
   :img-subdirectory "img/"

   :markdown-directory "doc/"
   :markdown-filename "performance.md"

   :results-url "https://example.com"
   :results-directory "resources/performance_entries/"

   :excludes #{}

   :verbose? false
   :testing-thoroughness :quick
   :parallel? false
   :n-threads 1
   :save-benchmark-fn-results? true

   :tidy-html? false

   :preamble [:div
              [:p "Preamble..."]

              [:a
               {:href
                "https://example.com"}
               "An example link."]

              [:p "Example code:"]

              [:pre [:code "(map inc [1 2 3])"]]]})

