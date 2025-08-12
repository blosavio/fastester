[:section#alternatives
 [:section#alternatives
  [:h2 "Alternatives & References"]

  [:ul
   [:li
    [:p [:a {:href "https://github.com/clojure-goes-fast/clj-async-profiler"}
         "clj-async-profiler"]
     " A single-dependency, embedded high-precision performance profiler."]]

   [:li
    [:p
     [:a {:href "https://github.com/jafingerhut/clojure-benchmarks/tree/master"}
      "clojure-benchmarks"]
     " Andy Fingerhut's project for benchmarking programs to compare amongst
 other languages."]]

   [:li
    [:p [:a {:href "https://clojure-goes-fast.com/"}
         "Clojure Goes Fast"]
     " A hub for news, docs, and tools related to Clojure and high
 performance."]]

   [:li
    [:p [:a {:href "https://github.com/hugoduncan/criterium/"} "Criterium"]
     " Measures the computation time of an expression, addressing some of the
 pitfalls of benchmarking. Criterium provides the vital benchmarking engine of
 the Fastester library."]]

   [:li
    [:p [:a {:href "https://github.com/openjdk/jmh"}
         "Java Microbenchmark Harness"] " (JMH) For building, running, and
 analysing nano/micro/milli/macro benchmarks written in Java and other languages
 targeting the JVM."]]

   [:li
    [:p "Laurence Tratt's benchmarking essays."
     [:br]
     [:a {:href "https://tratt.net/laurie/blog/2019/minimum_times_tend_to_mislead_when_benchmarking.html"}
      [:em "Minimum times tend to mislead when benchmarking"]]
     [:br]
     [:a {:href "https://soft-dev.org/pubs/html/barrett_bolz-tereick_killick_mount_tratt__virtual_machine_warmup_blows_hot_and_cold_v6/"}
      [:em "Virtual machine warmup blows hot and cold"]]
     [:br]
     [:em "Why aren’t more users more happy with our VMs? "]
     [:a {:href "https://tratt.net/laurie/blog/2018/why_arent_more_users_more_happy_with_our_vms_part_1.html"}
      "Part 1"]
     " "
     [:a {:href "https://tratt.net/laurie/blog/2018/why_arent_more_users_more_happy_with_our_vms_part_2.html"}
      "Part 2"]]]

   [:li
    [:p [:a {:href "https://clojure-goes-fast.com/kb/benchmarking/time-plus/"}
         [:code "time+"]]
     " A paste-and-go macro that measures an expression's evaluation time,
 useful for interactive development."]]]]]

