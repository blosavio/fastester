[:section#setup
 [:h2 "Setup"]

 [:h3 "Leiningen/Boot"]

 [:pre [:code (str "["
                   *project-group*
                   "/"
                   *project-name*
                   " \""
                   *project-version*
                   "\"]")]]

 [:h3 "Clojure CLI/deps.edn"]

 [:pre [:code (str *project-group*
                   "/"
                   *project-name*
                   " {:mvn/version \""
                   *project-version*
                   "\"}")]]

 [:h3#declare-compiler "JVM options"]

 [:pre
  [:code
   ":jvm-opts  [\"-XX:+TieredCompilation\"
            \"-XX:TieredStopAtLevel=4\"]"]]

 [:h3 "Require"]

 [:pre
  (-> "(require
 '[fastester.define :refer [defbench]]
 '[fastester.display :refer [generate-documents]]
 '[fastester.measure :refer [run-benchmarks]])"
      print-form-then-eval
      (update-in [1] #(str/replace % #";; => nil" "")))]]

