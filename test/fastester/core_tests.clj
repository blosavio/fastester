(ns fastester.core-tests
  (:require
   [clojure.test :refer [deftest is are testing run-tests]]
   [fastester.core-tests.core :refer :all]))


(deftest generate-version-section-test
  (are [x y] (= x y)
    (generate-version-section {:version 99
                               :date {:year 1999
                                      :month "Month"
                                      :day 0}
                               :responsible {:name "Foo Bar"
                                             :email "FooBar@example.com"}
                               :comment "Example comment"
                               :project-status :active
                               :urgency :low
                               :breaking? true
                               :changes [{:added-functions ['+]
                                          :altered-functions ['/]
                                          :deprecated-functions ['++]
                                          :moved-functions [{:fn-name '*
                                                             :old-location 'ns-1
                                                             :new-location 'ns-2}]
                                          :renamed-functions [{:old-function-name 'foo
                                                               :new-function-name 'bar}]
                                          :removed-functions ['-]
                                          :breaking? true
                                          :description "This is an example of a breaking change."
                                          :reference {:source "Issue #1"
                                                      :url "https://example.com"}}]})
    [:section
     [:h3 "version 99"]
     [:p
      "1999 Month 0" [:br]
      "Foo Bar (FooBar@example.com)" [:br]
      [:em "Description: "] "Example comment" [:br]
      [:em "Project status: "] [:a {:href "https://github.com/metosin/open-source/blob/main/project-status.md"} "active"] [:br]
      [:em "Urgency: "] "low" [:br]
      [:em "Breaking: "] "yes"]
     [:p
      [:div [:em "added functions: "] [:code "+"]]
      [:div [:em "altered functions: "] [:code "/"]]
      [:div [:em "deprecated functions: "] [:code "++"]]
      [:div [:em "moved functions: "] [:ul [:li [:code "*"] " from " [:code "ns-1"] " to " [:code "ns-2"]]]]
      [:div [:em "renamed functions: "] [:ul [:li [:code "foo"] " → " [:code "bar"]]]]
      [:div [:em "removed functions: "] [:code "-"]]]
     [:div
      [:h4 "Breaking changes"] [:ul [:li [:div [:a {:href "https://example.com"} "Issue #1"] ": " "This is an example of a breaking change."]]]
      [:h4 "Non-breaking changes"] [:ul]] [:hr]]))


(deftest changelog-md-footer-tests
  (are [x y] (= x y)
    (changelog-md-footer {:copyright-holder "Foo Bar"
                          :changelog-UUID "Example Changelog UUID"})
    (assoc [:p#page-footer
            (readmoi.core/copyright "Foo Bar") [:br]
            "Compiled by " [:a {:href "https://github.com/blosavio/chlog"} "Chlog"] " on " "<auto-insert current date>" "."
            [:span#uuid [:br]
             "Example Changelog UUID"]]
           6
           (readmoi.core/short-date))))


(run-tests)