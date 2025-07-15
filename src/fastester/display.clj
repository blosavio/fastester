(ns fastester.display
  "Create html performace displays."
  (:require
   [hiccup2.core :as h2]
   [hiccup.page :as page]
   [hiccup.element :as element]
   [hiccup.form :as form]
   [hiccup.util :as util]
   [readmoi.core :refer :all]))




(defn change-details
  "Given a sequence of `changes`, return a hiccup/html unordered list that lists
  the changes."
  {:UUIDv4 
   :no-doc true}
  [changes]
  (let [grouped-changes (group-by #(:breaking? %) changes)
        breaking-changes (grouped-changes true)
        non-breaking-changes (concat (grouped-changes false)
                                     (grouped-changes nil))
        issue-reference #(if (:reference %) [:a {:href (:url (:reference %))} (:source (:reference %))] nil)
        issue-reference-seperator #(if (:reference %) ": " nil)]
    [:div
     [:h4 "Breaking changes"]
     (into [:ul] (map (fn [v] [:li [:div (issue-reference v) (issue-reference-seperator v) (str (:description v))]])) breaking-changes)
     [:h4 "Non-breaking changes"]
     (into [:ul] (map (fn [v] [:li [:div (issue-reference v) (issue-reference-seperator v) (str (:description v))]])) non-breaking-changes)]))


(defn generate-version-section
  "Given a map `m` that contains data on a single changelog version, generate
  hiccup/html for a section that displays that info."
  {:UUIDv4 
   :no-doc true}
  [m]
  (let [changed-function-div (fn [label change-type] (let [something-ized-fn (something-ed-fns (m :changes) change-type)]
                                                       (if (empty? something-ized-fn)
                                                         nil
                                                         (into [:div [:em (str label " functions: ")]] something-ized-fn))))]
    [:section
     [:h3 (str "version " (:version m))]
     [:p
      (str (:year (:date m)) " "
           (:month (:date m)) " "
           (:day (:date m))) [:br]
      (str (:name (:responsible m)) " (" (:email (:responsible m)) ")") [:br]
      [:em "Description: "] (str (:comment m)) [:br]
      [:em "Project status: "] [:a {:href "https://github.com/metosin/open-source/blob/main/project-status.md"} (name (:project-status m))] [:br]
      [:em "Urgency: "] (name (:urgency m)) [:br]
      [:em "Breaking: "] (if (:breaking? m) "yes" "no")]
     [:p
      (changed-function-div "added" :added-functions)
      (changed-function-div "altered" :altered-functions)
      (changed-function-div "deprecated" :deprecated-functions)
      (let [possible-moves (something-ed-fns (m :changes) :moved-functions)]
        (if (= [[:ul]]  possible-moves)
          nil
          (into [:div [:em "moved functions: "]] possible-moves)))
      (let [possible-renames (something-ed-fns (m :changes) :renamed-functions)]
        (if (= [[:ul]] possible-renames)
          nil
          (into [:div [:em "renamed functions: "]] possible-renames)))
      (changed-function-div "removed" :removed-functions)]
     (change-details (m :changes))
     [:hr]]))


(defn changelog-md-footer
  "Given an options map `opt`, retruns a page footer with a copyright notice and a
  compiled on date, plus the changelog UUID."
  {:UUIDv4 
   :no-doc true}
  [opt]
  [:p#page-footer
   (copyright (opt :copyright-holder))
   [:br]
   "Compiled by " [:a {:href "https://github.com/blosavio/chlog"} "Chlog"] " on " (short-date) "."
   [:span#uuid [:br] (opt :changelog-UUID)]])


(defn generate-chlog-html
  "foobar"
  {:UUIDv4 
   :no-doc true}
  [opt changelog-data]
  (spit (str (opt :changelog-html-directory) (opt :changelog-html-filename))
        (page-template
         (str (opt :project-formatted-name) " library changelog")
         (opt :changelog-UUID)
         (conj [:body
                [:h1 (str (opt :project-formatted-name) " library changelog")]
                (opt :changelog-policies-section)]
               (into (map #(generate-version-section %) (reverse changelog-data))))
         (opt :copyright-holder)
         [:a {:href "https://github.com/blosavio/chlog"} "Chlog"])))


(defn generate-chlog-markdown
  "foobar"
  {:UUIDv4 
   :no-doc true}
  [opt changelog-data]
  (spit (str (opt :changelog-markdown-directory) (opt :changelog-markdown-filename))
        (h2/html
         (vec (-> [:body
                   [:h1 (str (opt :project-formatted-name) " library changelog")]
                   (opt :changelog-policies-section)]
                  (into (map #(generate-version-section %) (reverse changelog-data)))
                  (conj (changelog-md-footer opt)))))))


(load "chlog_defaults")


(defn generate-all-changelogs
  "Given Chlog options `opt`, write-to-file html and markdown changeloges.

  See project documentation for details on the structure of the options map.

  Changelog data will be read from `resources/changelog_entries/changelog.edn`
  unless superseded by `:changelog-entries-directory` or
  `:changelog-data-file` values in the options map.

  Defaults supplied by `src/chlog_defaults.edn`"
  {:UUIDv4 }
  [opt]
  (let [options-n-defaults (merge chlog-defaults opt)
        changelog-data (load-file (str (options-n-defaults :changelog-entries-directory) (options-n-defaults :changelog-data-file)))]
    (do (generate-chlog-html options-n-defaults changelog-data)
        (generate-chlog-markdown options-n-defaults changelog-data)
        (if (options-n-defaults :tidy-html?)
          (do (tidy-html-document (str (options-n-defaults :changelog-html-directory) (options-n-defaults :changelog-html-filename)))
              (tidy-html-body (str (options-n-defaults :changelog-markdown-directory) (options-n-defaults :changelog-markdown-filename))))))))
