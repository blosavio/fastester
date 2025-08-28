(ns fastester.options
  "Options that govern how Fastester works.

  Options are supplied by an `edn` file containing a hashmap.

  Note: There are no user-facing functions in this namespace."
  (:require
   [clojure.java.io :as io]
   [clojure.xml :as xml]))


(def ^{:no-doc true}
  fastester-defaults-docstring
  "A hash-map residing in
 [`src/fastester_defaults.clj`](https://github.com/blosavio/fastester/blob/main/src/fastester/fastester_defaults.clj)
 that supplies the default values for the following option keys:

  * `:benchmarks`
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
  * `:save-benchmark-fn-results?`
  * `:tidy-html?`
  * `:preamble`

  Override default values by associating new values into the Fastester _options_
  hash-map. See [[run-benchmarks]] and [[generate-documents]].")


(def ^{:doc fastester-defaults-docstring
       :UUIDv4 #uuid "9e50c897-a734-4fb9-b671-b924bc209a81"}
  fastester-defaults
  {:benchmarks {}

   :html-directory "doc/"
   :html-filename "performance.html"
   :img-subdirectory "img/"

   :markdown-directory "doc/"
   :markdown-filename "performance.md"

   :results-url "https://example.com"
   :results-directory "resources/performance_entries/"

   :verbose? false
   :testing-thoroughness :quick
   :parallel? false
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


(defn project-version-pom-xml
  "Queries 'pom.xml' file a returns version element as a string."
  {:UUIDv4 #uuid "63bc2597-1575-48bf-b444-09bca5115df7"
   :no-doc true}
  []
  (->> (xml/parse "pom.xml")
       :content
       (filter #(= (:tag %) :version))
       first
       :content
       first))


(defn project-version-lein
  "Queries the Leiningen 'project.clj' file's `defproject` expression and
  returns a string."
  {:UUIDv4 #uuid "9d8409a9-89ad-4567-9572-65c1e456cbb0"
   :no-doc true
   :implementation-note "Would prefer to use `clojure.edn/read-string` to
 minimize security issues with reading strings, but Leiningen 'project.clj'
 files are open-ended collections, and may contain, e.g., regular expressions
 `#\"...\"` in codox's section or backslahses, which are not members of edn.
 Must presume that a user's local 'project.clj' is trustworthy."}
  []
  (let [project-metadata (read-string (slurp "project.clj"))]
    (nth project-metadata 2)))


(defn project-version
  "Given options hashmap `opt`, returns the project version as a string.

  * If `opt` declares a preference by associating either `:lein` or `:pom-xml`
  to key `:preferred-version-info`, queries only that preference.
  * If a preference is not specified and both sources exists, throws.
  * If a preference is not specified and only one source exists, returns that
  version.
  * If neither 'project.clj' nor 'pom.xml' exists, throws."
  {:UUIDv4 #uuid "c5bd643f-73d8-4bc6-9506-8e59ba32f9fe"
   :no-doc true}
  [opts]
  (case (opts :preferred-version-info)
    :lein (project-version-lein)
    :pom-xml (project-version-pom-xml)
    nil (let [exists? #(.exists (io/file %))
              lein? (exists? "project.clj")
              pom-xml? (exists? "pom.xml")
              both-err "Both 'project.clj' and 'pom.xml' exist, but `:preferred-version-info` not set in options hashmap."
              neither-err "Neither 'project.clj' nor 'pom.xml' exist."]
          (cond
            (and lein? pom-xml?) (throw (Exception. both-err))
            lein? (project-version-lein)
            pom-xml? (project-version-pom-xml)
            :default (throw (Exception. neither-err))))))


(defn load-and-merge-with-defaults
  "Loads options hashmap filename `fname` and merges with fastester defaults."
  {:UUIDv4 #uuid "6dadccc5-09f7-45b4-97a0-3bd03749064f"
   :no-doc true}
  [fname]
  (merge fastester-defaults (load-file fname)))


(defn get-options
  "With no argument, reads Fastester options hashmap from file
  'resources/fastester_options.edn', otherwise, reads from file
  `explicit-options-filename`.

  Merges with `fastester-defaults`, explicit options superceding defaults."
  {:UUIDv4 #uuid "3fe666a0-2aa4-4777-a88f-056824bbed2f"
   :no-doc true}
  ([]
   (load-and-merge-with-defaults "resources/fastester_options.edn"))
  ([explicit-options-filename]
   (if explicit-options-filename
     (load-and-merge-with-defaults explicit-options-filename)
     (get-options))))

