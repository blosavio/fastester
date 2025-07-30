(defproject com.sagevisuals/fastester "0-SNAPSHOT0"
  :description "A Clojure library for reporting performance changes."
  :url "https://github.com/blosavio/fastester"
  :license {:name "MIT License"
            :url "https://opensource.org/license/mit"
            :distribution :repo}
  :dependencies [[org.clojure/clojure "1.12.1"]
                 [com.hypirion/clj-xchart "0.2.0"]
                 [criterium "0.4.6"]]
  :repl-options {:init-ns fastester.core}
  :plugins []
  :profiles {:dev {:dependencies [[org.clojure/test.check "1.1.1"]
                                  [hiccup "2.0.0-RC3"]
                                  [zprint "1.2.9"]
                                  [com.sagevisuals/chlog "1"]
                                  [com.sagevisuals/readmoi "3"]]
                   :plugins [[dev.weavejester/lein-cljfmt "0.12.0"]
                             [lein-codox "0.10.8"]]}
             :repl {}}
  :codox {:metadata {:doc/format :markdown}
          :namespaces [#"^fastester\.(?!scratch)"]
          :target-path "doc"
          :output-path "doc"
          :doc-files []
          :source-uri "https://github.com/blosavio/fastester/blob/main/{filepath}#L{line}"
          :html {:transforms [[:div.sidebar.primary] [:append [:ul.index-link [:li.depth-1 [:a {:href "https://github.com/blosavio/fastester"} "Project home"]]]]]}
          :project {:name "Fastester" :version "version 0"}}
  :scm {:name "git" :url "https://github.com/blosavio/fastester"})
