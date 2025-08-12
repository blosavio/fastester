[:section#intro
 [:h2 "Introduction"]

 [:p "Imagine this: We notice that function "
  [:code "foo"]
  " of version 11 of our library is sub-optimal. We improve the implementation
 so that "
  [:code "foo"]
  " executes faster."]

 [:p "While writing up the changelog for version 12, instead of mumbling, "]

 [:blockquote [:em "Function " [:code "foo"] " is faster."]]

 [:p "We show this."]

 (let [n 5
       x (range-pow-10 n)
       raw-y (map #(- (* 2 %) 1) (range 3 (+ 3 (inc n))))
       y (fn [] (map #(+ % (rand)) raw-y))
       err-fn (fn [] (map #(* % (rand)) (repeat (count x) 0.3)))
       v-11 (y)
       v-12 (map #(* 0.75 %) (y))
       e-11 (err-fn)
       e-12 (err-fn)
       chart (xc/xy-chart
              {"version 11" {:x x
                             :y v-11
                             :error-bars e-11}
               "version 12" {:x x
                             :y v-12
                             :error-bars e-12}}
              {:title "evaluation times of `(foo n)`"
               :x-axis {:title "argument, n"
                        :logarithmic? true}
               :y-axis {:title "time (nanoseconds)"
                        :logarithmic? false
                        :min 0}
               :theme :xchart
               :chart {:background-color :white
                       :title {:box {:visible? false}}}
               :legend {:position :outside-e
                        :border-color :white}
               :plot {:border-visible? false
                      :border-color :white}
               :error-bars-color :match-series})
       chart-filename "synthetic-benchmark-chart.svg"
       img-alt "Chart of synthetic performance benchmark of function `foo`,
 comparing verions 4 and 5; version 5 demonstrates approximately 25% faster
 performance across a specific range of arguments."
       map-f #(vector :td (format "%2.1f±%2.1f" %1 %2))]
   (xc/spit chart chart-filename)
   (xc/spit chart (str "doc/" chart-filename))
   [:div
    (hiccup.element/image chart-filename img-alt)
    [:table
     [:thead
      [:tr [:th {:colspan (inc (count x))} "arg, n"]]
      (into [:tr [:th "version"]] (map #(vector :th %) x))]
     (into [:tr [:td "11"]] (map map-f v-11 e-11))
     (into [:tr [:td "12"]] (map map-f v-12 e-12))
     [:tr [:th {:colspan (inc (count x))} "time in nanoseconds "[:em "(mean±std)"]]]]])

 [:p "And state,"]

 [:blockquote
  [:em "Version 12 of function "
   [:code "foo"]
   " is 20 to 30 percent faster than version 11 across this particular range of
 arguments."]]

 [:p "The Fastester library streamlines the tasks of writing benchmarks for a
 function, objectively measuring evaluation times of different versions of that
 function, and concisely communicating how performance changes between
 versions."]]

