(ns fastester.display
  "Generate html documents with charts and tables, communicating objective
  performance changes across versions .

  See [[fastester.measure]] for utilities to run the benchmarks and save the
  performance data."
  (:require
   [com.hypirion.clj-xchart :as xc]
   [hiccup2.core :as h2]
   [hiccup.page :as page]
   [hiccup.element :as element]
   [hiccup.form :as form]
   [hiccup.util :as util]
   [clojure.inspector :refer :all]
   [clojure.java.io :as io]
   [clojure.string :as str]
   [fastester.measure :refer [get-options]]
   [readmoi.core :refer :all]))


(defn get-result-filenames
  "Given options hashmap `opt`, returns a sequence of filepath strings of all
  preformance results in `:results-directory`."
  {:UUIDv4 #uuid "2bb3732e-6749-4506-9cb5-4a62ffa95b25"
   :no-doc true}
  [opt]
  (let [prefix (str (opt :results-directory)
                    "version ")
        suffix ".edn"]
    (->> (opt :results-directory)
         io/file
         file-seq
         (map #(.toString %))
         (filter #(and (str/starts-with? % prefix)
                       (str/ends-with? % suffix))))))


(defn load-results
  "Given a sequence of filepath strings of performance results `fnames`, returns
  a vector containing the performance data, each element a hashmap."
  {:UUIDv4 #uuid "64b3edc9-1b5d-46e8-acd1-f7170c6a5e27"
   :no-doc true}
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
  are associated to the original data elements of the flat data."
  {:UUIDv4 #uuid "e2380cb6-9d5d-4199-875d-1b3a096af2d2"
   :no-doc true}
  [flat-data]
  (reduce (fn [m v] (assoc-in m
                              [(-> v :fastester/metadata :group)
                               (-> v :fastester/metadata :fexpr)
                               (-> v :fastester/metadata :arg)
                               (-> v :fastester/metadata :version)]
                              v))
          (sorted-map)
          flat-data))


(defn link-to-original-data
  "Given benchmark result `data`, `opt` options hashmap, and link text string
  `s`, returns a hiccup/html link to the original data file, hosted somewhere
  accessible by a url or directory path.

  The link href is constructed by concatenating options `:results-url` and
  `:results-directory`. To link to a local resource, set `:results-url` to
  something like `../`."
  {:UUIDv4 #uuid "32909a01-7b21-4633-8515-9d939a448d53"
   :no-doc true}
  [data opt s]
  (let [test-idx (-> data :fastester/metadata :index)
        version (-> data :fastester/metadata :version)
        url (opt :results-url)
        filepath (opt :results-directory)
        href (str url filepath "version " version "/test-" test-idx ".edn")]
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
  "Returns a `[:p ...]` hiccup/html element with a flat sequence of benchmark
  timing results. See also [[version-divs]], [[arg-divs]], and [[fexpr-divs]]."
  {:UUIDv4 #uuid "6643ccc7-8d04-4aff-ab72-aec4bc03ff41"
   :no-doc true}
  [o-data opt group group-idx fexpr fexpr-idx arg arg-idx version version-idx]
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
      (link-to-original-data o-data opt "link")]]))


(defn version-divs
  "Returns a `[:h6 ...]` hiccup/html element and a sequence of paragraph
  elements for a single version of a single fexpr of a single group. See also
  [[data-divs]], [[arg-divs]], and [[fexpr-divs]]."
  {:UUIDv4 #uuid "8b806197-371f-42fa-a9c2-34d860e0d0bb"
   :no-doc true}
  [o-data opt group group-idx fexpr fexpr-idx arg arg-idx]
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
                                  opt
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
;; desired table would be built from rows of versions and columns of args.
;; Strategy: `reduce` over the organized-data as it exists, which results in
;; rows of arg and columns of versions, then transpose the vector of vectors to
;; obtain the desired rows of versions with colums of args.


(defn transpose
  "Given a vector-of-vectors `v`, returns a vector-of-fvector whose rows are
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
  "Given `datum` from a Criterium benchmark run, and options hashmap `opt`,
  returns a hiccup/html table column entry, an `[:td ...]` element. Note: Source
  code may be altered to return either a 'compact' (default) or 'verbose'
  display."
  {:UUIDv4 #uuid "45b777fe-54c6-452f-9ae3-9d74d7a29234"
   :no-doc true}
  [datum opt]
  (let [mean (first (datum :mean))
        var (first (datum :variance))
        std (Math/sqrt var)
        arg (-> datum :fastester/metadata :arg)
        version (-> datum :fastester/metadata :version)
        below-zero? (when (<= (- mean std) 0) " ‡")
        compact-mean-std (str (format "%2.1e±%2.1e" mean std) below-zero?)
        verbose-mean-std (str "mean±std: " compact-mean-std
                              "<br/>arg: " arg
                              "<br/>version: " version
                              below-zero?)]
    [:td (link-to-original-data
          datum
          opt
          #_verbose-mean-std
          compact-mean-std)]))


(defn row
  "Given a hashmap `m` representing benchmarks for one arg, and options hashmap
  `opt`, returns a single hiccup/html table row."
  {:UUIDv4 #uuid "cdbe757c-da33-40c0-9263-1c65fa00bbf8"
   :no-doc true}
  [m opt]
  (mapv #(col (second %) opt) (into (sorted-map) m)))


(defn version-sort-by-comparator
  "Given options hashmap `opt`, returns a comparator for sorting 'version ABC'
  and 'version XYZ'. Comparator associated to `:sort-comparator` in `opt` should
  return `true` if `ABC` comes before `XYZ`.

  Consult Clojure's [comparator guide](https://clojure.org/guides/comparators)."
  {:UUIDv4 #uuid "e1b08f92-7e05-47b9-a7cb-295431e35b17"
   :no-doc true}
  [opt]
  (let [trim #(second (clojure.string/split %  #"version "))]
    (fn [a b] ((opt :sort-comparator) (trim a) (trim b)))))


(defn sort-table-rows-by-version
  "Given version-prepended table rows of the form

  ```clojure
  [[[:td 4] ...]
   [[:td 5] ...]
   [[:td 6] ...]
  ]```

  sort according to comparator supplied by `opt` key `:sort-comparator`."
  {:UUIDv4 #uuid "9ca5ac8f-f2ce-430b-8215-6b70315062d1"
   :no-doc true}
  [t opt]
  (let [keyfn #(str "version " (-> % first second))
        cmp (version-sort-by-comparator opt)]
    (sort-by keyfn cmp t)))


(defn table-rows
  "Given organized data `o-data` and options hashmap `opt`, returns a
  hiccup/html table row for a single version of a single fexpr from a single
  group."
  {:UUIDv4 #uuid "de8fda39-fb17-403b-91fd-52ae0516cc07"
   :no-doc true}
  [o-data opt]
  (let [raw-rows (mapv #(row (second %) opt) o-data)
        prepend #(vec (cons %1 %2))
        versions (mapv #(vector :td %)
                       (-> o-data
                           first
                           second
                           keys
                           sort))
        transposed-rows (transpose raw-rows)
        version-prepended-rows (mapv #(prepend %1 %2) versions transposed-rows)
        sorted-prepended-rows (when (opt :sort-comparator)
                                (sort-table-rows-by-version
                                 version-prepended-rows
                                 opt))
        attribute-prepeneded-rows (mapv #(prepend :tr %)
                                        (or sorted-prepended-rows
                                            version-prepended-rows))]
    attribute-prepeneded-rows))


(defn table-spanner
  "Returns a hiccup/html table header spanner that labels the
  immediately-following arguments."
  {:UUIDv4 #uuid "bf6ef3a6-e185-4244-8e41-13a275cdbf12"
   :no-doc true}
  [o-data]
  (let [n-cols (count o-data)]
    [:tr [:td][:th {:colspan n-cols} "arg, n"]]))


(defn table-header
  "Returns a hiccup/html table header labelling the table's columns."
  {:UUIDv4 #uuid "8ddb1490-9df4-4f8d-b31f-691640338adc"
   :no-doc true}
  [o-data]
  (let [all-args (keys o-data)]
    [:tr [:th "version"] (map #(vector :th %) all-args)]))


(defn arg-vs-version-table
  "Returns a hiccup/html tabulation of benchmark timings (mean±std) versus
  argument and version. The location of the table's caption is governed by the
  `caption-side` css property."
  {:UUIDv4 #uuid "6e7b31a4-c23d-4ac0-a866-5c857ed1661d"
   :no-doc true}
  [o-data opt]
  (into (conj [:table [:caption "times in seconds, " [:em "mean±std"]]]
              [:thead
               (table-spanner o-data)
               (table-header o-data)])
        (table-rows o-data opt)))


(defn arg-divs
  "Given organized data `o-data`, returns a `[:div.collapsable ...]` hiccup/html
  element that contains an `[:h5 ...]` header and a table for all version and
  'n' arguments for a single fexpr. Note: Source code may be altered to
  additionally include a flat listing."
  {:UUIDv4 #uuid "60d84519-d994-4c3f-993f-4c1ea962af09"
   :no-doc true}
  [o-data opt group group-idx fexpr fexpr-idx]
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
                                     opt
                                     group
                                     group-idx
                                     fexpr
                                     fexpr-idx
                                     ky
                                     (arg-index ky))))]
    (into [:div.collapsable]
          [#_(reduce-kv red-fn [] o-data) ;; generate flat listing
           (arg-vs-version-table o-data opt)])))


(defn create-img-directory
  "Create directory to contain chart images, location declared by options keys
  `:html-directory` and `:img-subdirectory`."
  {:UUIDv4 #uuid "ec828323-9f42-4e40-a5e2-b040982a19df"
   :no-doc true}
  [opt]
  (.mkdir (io/file (opt :html-directory) (opt :img-subdirectory))))


(defn img-filepath
  "Given integers `group-idx` & `fexper-idx`, and options hashmap `opt`, returns
  a filepath string."
  {:UUIDv4 #uuid "91c71ff4-3486-4469-b79f-6a45bb1f1ba2"
   :no-doc true}
  [group-idx fexpr-idx opt]
  (str (opt :html-directory)
       (opt :img-subdirectory)
       "group-"
       group-idx
       "-fexpr-"
       fexpr-idx
       ".svg"))


(defn integer-keyed-map->vector
  "Given hashmap `m` with integer keys, returns a vector of `m`'s values. Throws
  if any of the following conditions are not satisfied:

  * All keys are integers.
  * Keys incrementally increase.
  * Zero must be a key, if any.

  Returns an empty vector if `m` is empty."
  {:UUIDv4 #uuid "e183849b-696f-4842-a46b-1ec05e6d9d68"
   :no-doc true}
  [m]
  (if (= m {})
    []
    (let [all-ints? (if (every? integer? (keys m))
                      true
                      (throw (Exception. "All keys must be integers.")))
          sorted (into (sorted-map) m)
          incremental? (if (->> (keys sorted)
                                (#(map - (next %) %))
                                (every? #(= % 1)))
                         true
                         (throw (Exception.
                                 "Integer keys must incrementally increase.")))
          contains-zero? (if (contains? sorted 0)
                           true
                           (throw (Exception. "One key must be integer zero.")))]
      (vec (vals (into (sorted-map) m))))))


(defn nested-maps->vecs
  "Convert nested maps of rearranged chart data `data` from this:

  ```clojure
  {5 {:x {0 :datum-0
          1 :datum-1
          2 :datum-2}
      :y {0 :datum-3
          1 :datum-4
          2 :datum-5}}}
  ```

  to nested vectors like this:

  ```clojure
  {5 {:x [:datum-0
          :datum-1
          :datum-2]
      :y [:datum-3
          :datum-4
          :datum-5]}}
  ```

  So that `clj-xchart` can consume the data."
  {:UUIDv4 #uuid "98581e3f-360b-4105-aea0-34a2ddfb3b04"
   :no-doc true}
  [data]
  (update-vals data #(update-vals % integer-keyed-map->vector)))


(def ^{:no-doc true} zeroed! (atom nil))


(defn rearrange-chart-data
  "Given fexpr organized data `o-data`, returns the mean±std with the following
  arrangement:

  ```clojure
  {\"version N\" {:x-data     [n values]
                  :y-data     [mean values]
                  :error-bars [std values]}
   \"version N+1\" ...
  }
  ```"
  {:UUIDv4 #uuid "724d4c77-bde4-40b2-8498-0df1021147f3"
   :no-doc true
   :implementation-note "If an axis is logarithmic, it cannot display negative
  values, but a datum±std *may* be negative. In that case, coerce std to zero
  and set the `zeroed!` flag so that a footnote on the chart can be added to
  inform the reader. See [[chart]]."}
  [o-data opt fexpr]
  (let [_ (reset! zeroed! nil)
        flattened (for [n-arg o-data
                        version (second n-arg)]
                    (let [datums (second version)]
                      {:version (str "version "(first version))
                       :n (first n-arg)
                       :mean (first (datums :mean))
                       :var (first (datums :variance))}))
        get-all #(->> flattened (map %) distinct sort)
        all-args (get-all :n)
        all-versions (get-all :version)
        log-axis? (get-in opt [:chart-settings fexpr :y-axis-logarithmic?])
        get-idx #(.indexOf %1 %2)]
    (reduce (fn [acc x]
              (let [version (x :version)
                    idx (get-idx all-args (x :n))
                    mean (x :mean)
                    n (x :n)
                    std (Math/sqrt (x :var))
                    std (if (and log-axis? ;; see implementation note above
                                 (<= (- mean std) 0))
                          (do (reset! zeroed! true)
                              0)
                          std)]
                (-> acc
                    (assoc-in [version :y idx] mean)
                    (assoc-in [version :x idx] n)
                    (assoc-in [version :error-bars idx] std))))
            (sorted-map)
            flattened)))


(def ^{:no-doc true}
  default-chart-style-dosctring
  "A hashmap containing `clj-xchart` chart options, governing theme, legend
position, plot border, etc.")


(def ^{:doc default-chart-style-dosctring}
  default-chart-style
  {:theme :xchart
   :chart {:background-color :white
           :title {:box {:visible? false}}}
   :legend {:position :outside-e
            :border-color :white}
   :plot {:border-visible? false
          :border-color :white}
   :error-bars-color :match-series})


(defn chart
  "Given organized data `o-data` for an fexpr, options hashmap `opt`, group/idx
  and fexpr/idx, uses clj-xchart wrapper lib to generate an svg chart of
  mean±std time versus `n` args for all the available versions in that fexpr.

  1. Writes svg to file
  `(options :html-directory)/(options :img-subdirectory)/`.

  2. Returns a hiccup/html image element."
  {:UUIDv4 #uuid "a0313ab9-1cf1-4fcf-aaa9-612f13409583"
   :no-doc true
   :implementation-note "If an axis is logarithmic, error bars composed of
   mean±std may be zero or less, which cannot be displayed on a log scale. In
   that case, `rearrange-chart-data` coerces the std to zero and sets the
   `zeroed!` flag.

   If an axis is not logarithmic, display error bars as usual and set the
   mininum value to zero. See [[rearrange-chart-data]]."}
  [o-data opt group group-idx fexpr fexpr-idx]
  (let [file-path (img-filepath group-idx fexpr-idx opt)
        short-path (clojure.string/replace file-path  #"doc/" "")
        x-log? (get-in opt [:chart-settings fexpr :x-axis-logarithmic?])
        y-log? (get-in opt [:chart-settings fexpr :y-axis-logarithmic?])
        this-chart-style {:title "Benchmark times"
                          :x-axis {:title "argument, n"
                                   :logarithmic? x-log?}
                          :y-axis {:title "time (sec)"
                                   :logarithmic? y-log?}}
        this-chart-style (if (not x-log?)
                           (assoc-in this-chart-style [:x-axis :min] 0)
                           this-chart-style)
        this-chart-style (if (not y-log?)
                           (assoc-in this-chart-style [:y-axis :min] 0)
                           this-chart-style)
        chart-style (merge default-chart-style
                           this-chart-style)
        sort-versions-by-comparator #(if (opt :sort-comparator)
                                       (into
                                        (sorted-map-by
                                         (version-sort-by-comparator opt)) %)
                                       %)
        chrt (-> o-data
                 (rearrange-chart-data opt fexpr)
                 sort-versions-by-comparator
                 nested-maps->vecs
                 (xc/xy-chart chart-style))
        img-alt-text (str "Benchmark measurements for expression `"
                          fexpr
                          "`, time versus 'n' arguments, comparing different versions.")]
    (do
      (xc/spit chrt file-path)
      (element/image short-path img-alt-text))))


(def zeroed-notice
  [:p.de-highlight.centered
   "Note: Some error bars are negative and can not be drawn on logarithmic scale."
   [:br]
   "See ‡ in "
   [:em "details"]
   " below."])


(defn fexpr-divs
  "Given organized data `o-data` for one fexpr, `opt` options hashmap, `group`
  string, and the group index `group-idx` as an integer, returns a hiccup/html
  div with an element for each fexpr."
  {:UUIDv4 #uuid "5407930e-36d3-4742-a6bc-ca96a609e35c"
   :no-doc true}
  [o-data opt group group-idx]
  (let [o-data (into (sorted-map) o-data)
        fexprs (keys o-data)
        fexpr-index #(.indexOf fexprs %)
        h4-fn #(keyword (str "h4#group-" group-idx
                             "-fexpr-" (fexpr-index %)))
        _ (create-img-directory opt)
        red-fn (fn [acc ky vl]
                 (conj acc
                       [(h4-fn ky) ky]
                       (chart vl opt group group-idx ky (fexpr-index ky))
                       (when @zeroed! zeroed-notice)
                       [:button.collapser {:type "button"} "Show details"]
                       (arg-divs vl
                                 opt
                                 group
                                 group-idx
                                 ky
                                 (fexpr-index ky))))]
    (into [:div] (reduce-kv red-fn [] o-data))))


(defn main-section
  "Given `organized-data` and `opt` hashmap, returns a hiccup/html section
  with `[:h3]` headers for each group, each followed by the group's comments,
  followed by testing subsections."
  {:UUIDv4 #uuid "30d5adcf-0e75-4b56-ba28-649f6c371732"
   :no-doc true}
  [organized-data opt]
  (let [ogranized-data (into (sorted-map) organized-data)
        group-names (keys organized-data)
        group-index #(.indexOf group-names %)
        h3-fn #(keyword (str "h3#group-" (group-index %)))]
    (reduce-kv (fn [acc ky vl] (conj acc
                                     [(h3-fn ky) ky]
                                     ((opt :comments) ky)
                                     (fexpr-divs vl opt ky (group-index ky))))
               [:section]
               organized-data)))


(defn toc
  "Given organized data `o-data`, returns a table-of-contents section."
  {:UUIDv4 #uuid "d449450b-d809-46a3-ab8f-da64a06a2e39"
   :no-doc true}
  [o-data]
  (let [group-names (keys o-data)
        links (map-indexed
               #(vector :a {:href (str "#group-" %1)} %2)
               group-names)]
    (into [:div] (drop-last (interleave links (repeat [:br]))))))


(defn fastester-md-footer
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
   [:span#uuid [:br] (opt :fastester-UUID)]])


(defn inject-js
  "Given string `html` representing html, inject javascript loading declaration
  `js-filepath` into the document immediately after the `<head>` element. Useful
  because `page-template` from the ReadMe lib doesn't accept args for specifying
  arbitrary js files."
  {:UUIDv4 #uuid "52521fc7-6048-4985-b12e-59c215cc5d32"
   :no-doc true}
  [html js-filepath]
  (let [split-at #"<head>"
        [left right] (clojure.string/split html split-at)
        js-includer (-> js-filepath
                        page/include-js
                        h2/html
                        str)]
    (str left split-at js-includer right)))


(defn generate-html
  "Writes html to the filesystem, suitable for a web browser."
  {:UUIDv4 #uuid "e1063a97-77e9-420e-b321-e0f31a729a7d"
   :no-doc true}
  [opt o-data]
  (spit (str (opt :html-directory) (opt :html-filename))
        (-> (page-template
             (str (opt :project-formatted-name) " library performance")
             (opt :fastester-UUID)
             (conj [:body
                    [:h1 (str (opt :project-formatted-name)
                              " library performance")]
                    (toc o-data)
                    (opt :preamble)
                    (main-section o-data opt)])
             (opt :copyright-holder)
             [:a {:href "https://github.com/blosavio/fastester"} "Fastester"])
            (inject-js "fastester.js")
            (inject-js "jquery-3.7.1.min.js"))))


(defn generate-markdown
  "Generates a markdown file, similar to [[generate-html]]."
  {:UUIDv4 #uuid "3c43eb12-0ad8-44b5-919c-fc117b6cd1bf"
   :no-doc true}
  [opt o-data]
  (spit (str (opt :markdown-directory) (opt :markdown-filename))
        (h2/html
         (vec (-> [:body
                   [:h1 (str (opt :project-formatted-name)
                             " library performance")]
                   (toc o-data)
                   (opt :preamble)
                   (main-section o-data opt)]
                  (conj (fastester-md-footer opt)))))))


(load "fastester_defaults")


(defn generate-documents
  "Write-to-file html and markdown documents that show performance measurements
  across a library's versions.

  See [[fastester-defaults]]
  and
  [Fastester project documentation](https://github.com/blosavio/fastester) for
  details on the structure of the options map."
  {:UUIDv4 #uuid "5dda7f24-f344-4dce-96bb-51c5280c6ba9"}
  []
  (let [opt (get-options)
        options-n-defaults (merge fastester-defaults opt)
        data (load-results (get-result-filenames opt))
        organized-data (organize-data data)]
    (do (generate-html options-n-defaults organized-data)
        (generate-markdown options-n-defaults organized-data)
        (if (options-n-defaults :tidy-html?)
          (do (tidy-html-document
               (str (options-n-defaults :html-directory)
                    (options-n-defaults :html-filename)))
              (tidy-html-body
               (str (options-n-defaults :markdown-directory)
                    (options-n-defaults :markdown-filename))))))))

