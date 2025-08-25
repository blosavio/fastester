[:section#intro
 [:h2 "Introduction"]

 [:p "Imagine: We notice that the "
  [:code "zap"]
  " function of version 11 of our library is sub-optimal. We improve the
 implementation so that "
  [:code "zap"]
  " executes faster."]

 [:p "In the version 12 changelog, we could mumble,"]

 [:blockquote [:em "Function " [:code "zap"] " is faster."]]

 [:p "Or instead, we could assert,"]

 [:blockquote
  [:em "Version 12 of function "
   [:code "zap"]
   " is 20 to 30 percent faster than version 11 for integers spanning five
 orders of magnitude. This implementation change will improve performance for
 the vast majority of intended use cases."]]
 
 [:p "And then show this."]

 (let [img-alt "Chart of synthetic performance benchmark of function `zap`,
 comparing versions 11 and 12; version 12 demonstrates approximately 25% faster
 performance across a specific range of arguments."]
   (hiccup.element/image "zap_img/group-0-fexpr-0.svg" img-alt))

 (let [page (slurp "./doc/zap_performance.html")
      start-tag "<table>"
      end-tag "</table>"
      start-tables (str/index-of page start-tag)
        end-tables (str/index-of page end-tag)]
  (hiccup.util/raw-string (subs page
                                start-tables
                                (+ end-tables (count end-tag)))))

 [:p "The Fastester library streamlines the tasks of writing benchmarks for a
 function, objectively measuring evaluation times of different versions of that
 function, and concisely communicating how performance changes between
 versions."]]

