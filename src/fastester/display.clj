(ns fastester.display
  "Create html performace displays."
  (:require
   [hiccup2.core :as h2]
   [hiccup.page :as page]
   [hiccup.element :as element]
   [hiccup.form :as form]
   [hiccup.util :as util]
   [clojure.inspector :refer :all]
   [clojure.java.io :as io]
   [clojure.string :as str]
   [readmoi.core :refer :all]))


(defn get-result-filenames
  "Given options hashmap `opt`, returns a sequence of filepath name strings
  of all preformance results in `:perflog-results-directory`."
  {:UUIDv4 #uuid "2bb3732e-6749-4506-9cb5-4a62ffa95b25"}
  [opt]
  (let [prefix (str (opt :perflog-results-directory)
                    "version ")
        suffix ".edn"]
    (->> (opt :perflog-results-directory)
         io/file
         file-seq
         (map #(.toString %))
         (filter #(and (str/starts-with? % prefix)
                       (str/ends-with? % suffix))))))


(defn load-results
  "Given a sequence of filepath name strings of performance results `fnames`,
  returns a vector containing the performance data, each element a hashmap."
  {:UUIDv4 #uuid "64b3edc9-1b5d-46e8-acd1-f7170c6a5e27"}
  [fnames]
  (let [read-opts {:readers {'criterium.core.OutlierCount identity}}
        custom-read-string #(clojure.edn/read-string read-opts %)]
    (map #(-> % slurp custom-read-string) fnames)))


;; Strategy: Don't try to construct a hierarchical display while ripping through
;; the flat data as produced by the benchmarking utility. Instead, re-organize
;; the data itself into a proper hierarchy, then the display will
;; straightforwardly reflect the data's natural shape.


(defn organize-data
  "Given raw data sequence from benchmarking `flat-data`, returns a hashmap
  whose first-level keys are the groups, the second-level keys are fexprs, and
  the third-level keys are the 'n' arguments. Those third level argument keys
  are associated to the original data elements."
  {:UUIDv4 #uuid "e2380cb6-9d5d-4199-875d-1b3a096af2d2"
   :no-doc true}
  [flat-data]
  (reduce (fn [m v] (assoc-in m
                              [(-> v :fastester/metadata :group)
                               (-> v :fastester/metadata :fexpr)
                               (-> v :fastester/metadata :arg)
                               (-> v :fastester/metadata :version)]
                              v))
          {}
          flat-data))


(defn link-to-original-data
  "Given `data`, `options` hashmap, and string `s`, returns a hiccup/html link
  to the original data file.

  Note: Implementation assumes rendered html is in the `doc` directory and
  the results are rooted in the `resources` directory. TODO: Upgrade
  implementation to be more generic so that links 'know' how to navigate from
  the html page to the results directory."
  {:UUIDv4 #uuid "32909a01-7b21-4633-8515-9d939a448d53"
   :no-doc true}
  [data options s]
  (let [test-idx (-> data :fastester/metadata :index)
        version (-> data :fastester/metadata :version)
        filepath (options :perflog-results-directory)
        href (str "../" filepath "version " version "/test-" test-idx ".edn")]
    [:a {:href href} s]))


(defn significant-digits
  "Given number `x`, returns a double with at most `p` significant digits. If
  `x` or `p` is zero, then `x` is returned."
  {:UUIDv4 #uuid "f54f0e90-7523-48ab-803b-99e64d36fe32"
   :no-doc true
   :reference-url "https://en.wikipedia.org/wiki/Significant_figures#Rounding_to_significant_figures"}
  [x p]
  (if (or (= x 0)
          (= p 0))
    x
    (let [n (-> x
                Math/abs
                Math/log10
                Math/floor
                inc
                (- p))
          f (Math/pow 10 n)]
      (* f (Math/round (/ x f))))))


(defn data-divs
  "Docstring..."
  {:UUIDv4 #uuid "6643ccc7-8d04-4aff-ab72-aec4bc03ff41"
   :no-doc true}
  [o-data options group group-idx fexpr fexpr-idx arg arg-idx version version-idx]
  (let [var->std #(Math/sqrt %)]
    [:div
     [:p (str "samples: " (o-data :samples))]
     [:p (str "mean±std: "
              (-> (o-data :mean)
                  first
                  (#(format "%2.1e" %)))
              "±"
              (-> (o-data :variance)
                  first
                  var->std
                  (#(format "%2.1e" %))))]
     [:p
      (str "original data: ")
      (link-to-original-data o-data options "link")]]))


(defn version-divs
  "Docstring..."
  {:UUIDv4 #uuid "8b806197-371f-42fa-a9c2-34d860e0d0bb"
   :no-doc true}
  [o-data options group group-idx fexpr fexpr-idx arg arg-idx]
  (let [o-data (into (sorted-map) o-data)
        versions (keys o-data)
        version-index #(.indexOf versions %)
        h6-fn #(keyword (str "h6#group-" group-idx
                             "-fexpr-" fexpr-idx
                             "-arg-" arg-idx
                             "-version-" (version-index %)))
        red-fn (fn [acc ky vl]
                 (conj acc
                       [(h6-fn ky) (str "version " ky)]
                       (data-divs vl
                                  options
                                  group
                                  group-idx
                                  fexpr
                                  fexpr-idx
                                  arg
                                  arg-idx
                                  ky
                                  (version-index ky))))]
    (into [:div] (reduce-kv red-fn [] o-data))))


;; The 'organized-data' at this stage has first-level keys 'args' and
;; second-level keys 'versions'. Html tables are row-oriented, with a table row
;; `[:tr ...]` containing column elements, table datums `[:td]`. However, the
;; desired table is rows of versions and columns of args. Strategy: `reduce`
;; over the organized-data as it exists, which results in rows of arg and
;; columns of versions, then transpose the vector of vectors to obtain the
;; desired rows of versions with colums of args.


(defn transpose
  "Given a vector-of-vectors `v`, returns a vector of vectors whose rows are
  `v`'s columns and whose columns are `v`'s rows.

  Example:
  ```clojure
  (transpose [[:a :b :c]
              [:d :e :f]])
  ;; => [[:a :d]
         [:b :e]
         [:c :f]]
  ```"
  {:UUIDv4 #uuid "d1b3701d-74fd-4fad-a54a-aa4d97567b12"
   :no-doc true}
  [v]
  (apply mapv vector v))


(defn col
  "Given `datum` from a Criterium benchmark run, returns a hiccup/html table
  column entry, an `[:td ...]` element."
  {:UUIDv4 #uuid "45b777fe-54c6-452f-9ae3-9d74d7a29234"
   :no-doc true}
  [datum options]
  (let [mean (first (datum :mean))
        var (first (datum :variance))
        std (Math/sqrt var)
        arg (-> datum :fastester/metadata :arg)
        version (-> datum :fastester/metadata :version)
        compact-mean-std (format "%2.1e±%2.1e" mean std)
        verbose-mean-std (str "mean±std: " compact-mean-std
                              "<br/>arg: " arg
                              "<br/>version: " version)]
    [:td (link-to-original-data
          datum
          options
          #_verbose-mean-std
          compact-mean-std)]))


(defn row
  "Given a hashmap `m` representing benchmarks for one arg, returns a single
  hiccup/html table row."
  {:UUIDv4 #uuid "cdbe757c-da33-40c0-9263-1c65fa00bbf8"
   :no-doc true}
  [m options]
  (mapv #(col (second %) options) (into (sorted-map) m)))


(defn table-rows
  "...Returns a hiccup/html table row."
  {:UUIDv4 #uuid "de8fda39-fb17-403b-91fd-52ae0516cc07"
   :no-doc true}
  [o-data options]
  (let [raw-rows (mapv #(row (second %) options) o-data)
        prepend #(vec (cons %1 %2))
        versions (mapv #(vector :td %)
                       (-> o-data
                           first
                           second
                           keys
                           sort))
        transposed-rows (transpose raw-rows)
        version-prepended-rows (mapv #(prepend %1 %2) versions transposed-rows)
        attribute-prepeneded-rows (mapv #(prepend :tr %) version-prepended-rows)]
    attribute-prepeneded-rows))


(defn table-spanner
  "...Returns a hiccup/html table header spanner."
  {:UUIDv4 #uuid "bf6ef3a6-e185-4244-8e41-13a275cdbf12"
   :no-doc true}
  [o-data]
  (let [n-cols (count o-data)]
    [:tr [:td][:th {:colspan n-cols} "arg, n"]]))

(defn table-header
  "...Returns a hiccup/html table header."
  {:UUIDv4 #uuid "8ddb1490-9df4-4f8d-b31f-691640338adc"
   :no-doc true}
  [o-data]
  (let [all-args (keys o-data)]
    [:tr [:th "version"] (map #(vector :th %) all-args)]))


(defn arg-vs-version-table
  "...Returns a hiccup/html tabulation of benchmark timings (mean±std) versus
  argument and version."
  {:UUIDv4 #uuid "6e7b31a4-c23d-4ac0-a866-5c857ed1661d"
   :no-doc true}
  [o-data options]
  (into (conj [:table [:caption "times in seconds, " [:em "mean±std"]]]
              [:thead
               (table-spanner o-data)
               (table-header o-data)])
        (table-rows o-data options)))


(defn arg-divs
  "Given ..."
  {:UUIDv4 #uuid "60d84519-d994-4c3f-993f-4c1ea962af09"
   :no-doc true}
  [o-data options group group-idx fexpr fexpr-idx]
  (let [o-data (into (sorted-map) o-data)
        args (keys o-data)
        arg-index #(.indexOf args %)
        h5-fn #(keyword (str "h5#group-" group-idx
                             "-fexpr-" fexpr-idx
                             "-arg-" (arg-index %)))
        red-fn (fn [acc ky vl]
                 (conj acc
                       [(h5-fn ky) (str "n = " ky)]
                       (version-divs vl
                                     options
                                     group
                                     group-idx
                                     fexpr
                                     fexpr-idx
                                     ky
                                     (arg-index ky))))]
    (into [:div.collapsable]      
          [#_(reduce-kv red-fn [] o-data) ;; generate flat listing
           (arg-vs-version-table o-data options)])))


(defn fexpr-divs
  "Given `group`, and organized fexpr data `o-data` for a single group,
  `options` hashmap, and the group index `group-idx` integer, returns a
  hiccup/html div with an element for each fexpr."
  {:UUIDv4 #uuid "5407930e-36d3-4742-a6bc-ca96a609e35c"
   :no-doc true}
  [o-data options group group-idx]
  (let [o-data (into (sorted-map) o-data)
        fexprs (keys o-data)
        fexpr-index #(.indexOf fexprs %)
        h4-fn #(keyword (str "h4#group-" group-idx
                             "-fexpr-" (fexpr-index %)))
        red-fn (fn [acc ky vl]
                 (conj acc
                       [(h4-fn ky) ky]
                       [:button.collapser {:type "button"} "Show details"]
                       (arg-divs vl
                                 options
                                 group
                                 group-idx
                                 ky
                                 (fexpr-index ky))))]
    (into [:div] (reduce-kv red-fn [] o-data))))


(defn main-section
  "Given `organized-data` and `options` hashmap, returns a hiccup/html section
  with `[:h3]` headers for each group, each followed by the group's comments,
  followed by testing subsections."
  {:UUIDv4 #uuid "30d5adcf-0e75-4b56-ba28-649f6c371732"
   :no-doc true}
  [organized-data options]
  (let [ogranized-data (into (sorted-map) organized-data)
        group-names (keys organized-data)
        group-index #(.indexOf group-names %)
        h3-fn #(keyword (str "h3#group-" (group-index %)))]
    (reduce-kv (fn [acc ky vl] (conj acc [(h3-fn ky) ky]
                                     (fexpr-divs vl options ky (group-index ky))
                                     ((options :display-comments) ky)))
               [:section]
               organized-data)))


(defn toc
  "Given complete `data`, returns a table-of-contents section."
  {:UUIDv4 #uuid "d449450b-d809-46a3-ab8f-da64a06a2e39"
   :no-doc true}
  [o-data]
  (let [group-names (keys o-data)
        links (map-indexed
               #(vector :a {:href (str "#group-" %1)} %2)
               group-names)]
    (into [:div] (drop-last (interleave links (repeat [:br]))))))


(defn perflog-md-footer
  "Given an options map `opt`, retruns a page footer with a copyright notice and
  a compiled on date, plus the UUID."
  {:UUIDv4 #uuid "755d14b3-215f-40fb-990c-5b748dc75ee4"
   :no-doc true}
  [opt]
  [:p#page-footer
   (copyright (opt :copyright-holder))
   [:br]
   "Compiled by " [:a {:href "https://github.com/blosavio/Fastester"}
                   "Fastester"] " on " (short-date) "."
   [:span#uuid [:br] (opt :perflog-UUID)]])


(defn inject-js
  "Given string 'html' representing html, inject javascript loading declaration
  `js-filepath` into the document immediately after the `<head>` element."
  {:UUIDv4 #uuid "52521fc7-6048-4985-b12e-59c215cc5d32"
   :no-doc true}
  [html js-filepath]
  (let [split-at #"<head>"
        [left right] (clojure.string/split html split-at)
        js-includer (-> js-filepath
                        page/include-js
                        h2/html
                        str)]
    (str left (str split-at) js-includer right)))


(defn generate-perflog-html
  "foobar"
  {:UUIDv4 #uuid "e1063a97-77e9-420e-b321-e0f31a729a7d"
   :no-doc true}
  [opt o-data]
  (spit (str (opt :perflog-html-directory) (opt :perflog-html-filename))
        (-> (page-template
             (str (opt :project-formatted-name) " library performance log")
             (opt :perflog-UUID)
             (conj [:body
                    [:h1 (str (opt :project-formatted-name)
                              " library performance log")]
                    (toc o-data)
                    (opt :perflog-preamble)
                    (main-section o-data opt)])
             (opt :copyright-holder)
             [:a {:href "https://github.com/blosavio/fastester"} "Fastester"])
            (inject-js "fastester.js")
            (inject-js "jquery-3.7.1.min.js"))))


(defn generate-perflog-markdown
  "foobar"
  {:UUIDv4 #uuid "3c43eb12-0ad8-44b5-919c-fc117b6cd1bf"
   :no-doc true}
  [opt o-data]
  (spit (str (opt :perflog-markdown-directory) (opt :perflog-markdown-filename))
        (h2/html
         (vec (-> [:body
                   [:h1 (str (opt :project-formatted-name)
                             " library performance log")]
                   (toc o-data)
                   (opt :perflog-preamble)
                   (main-section o-data opt)]
                  (conj (perflog-md-footer opt)))))))


(load "perflog_defaults")


(defn generate-all-perflogs
  "Given Fastester options `opt` hashmap, write-to-file html and markdown
  perflogs.

  See project documentation for details on the structure of the options map.

  Performance log data will be read from `resources/performace_entries/`
  unless superseded by `:perflog-tests-directory` or
  `:perflog-data-file` values in the options map.

  Defaults supplied by `src/perflog_defaults.edn`"
  {:UUIDv4 #uuid "5dda7f24-f344-4dce-96bb-51c5280c6ba9"}
  [opt]
  (let [options-n-defaults (merge perflog-defaults opt)
        perflog-data (load-results (get-result-filenames opt))
        organized-data (organize-data perflog-data)]
    (do (generate-perflog-html options-n-defaults organized-data)
        (generate-perflog-markdown options-n-defaults organized-data)
        (if (options-n-defaults :tidy-html?)
          (do (tidy-html-document
               (str (options-n-defaults :perflog-html-directory)
                    (options-n-defaults :perflog-html-filename)))
              (tidy-html-body
               (str (options-n-defaults :perflog-markdown-directory)
                    (options-n-defaults :perflog-markdown-filename))))))))

