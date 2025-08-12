[:section#ideas
 [:h2 "Ideas"]

 [:p "We ought to strive to write fast software. Fast software respects other
 people's time and their computing resources. Fast software demonstrates skill
 and attention to detail. And fast software is just plain cool."]

 [:p "But, how fast is \"fast\"? It's not terribly convincing to say "
  [:em "Our software is fast."]
  " We'd like some objective measure of fast. Fortunately, the "
  [:a {:href "https://github.com/hugoduncan/criterium"} "Criterium library"]
  " provides a handy group of benchmarking utilities that measures the
 evaluation time of a Clojure expression. We could use Criterium to learn that "
  [:code "(foo 99)"]
  " requires 4.1 nanoseconds to evaluate."]

 [:p "Is…that good? Difficult to say. What we'd really like to know is how
 4.1 nanoseconds compares to some previous version. So if, for example,
 version 1.12.1 of "
  [:code "foo"]
  " evaluated in 4.1 nanoseconds, whereas version 1.11.4 required
 5.4 nanoseconds, we have good reason to believe the later implementation is
 faster."]

 [:p "A performance report ought to be:"]

 [:ul
  [:li [:p [:strong "Objective"] " Thanks to Criterium, we can objectively measure
 how long it takes to evaluate a function with a particular argument,
 independent of vagaries of the environment."]]

  [:li [:p [:strong "Thorough"] " Performance tests ought to fully exercise
 invocation patterns, e.g., all the relevant, performance-sensitive arities, and
 the arguments ought to span several orders of magnitude."]]

  [:li [:p [:strong "Straighforward"] " Once the benchmarks are written, it ought to
 be effortless to run the benchmarks for any subsequent version."]]

  [:li [:p [:strong "Comprehensible"] " A person ought to be able glance at the
 performance report and immediately grasp the improvements, with details
 available as needed."]]]

 [:h3 "Objectively measure performance"]

 [:p "Thorough data. No trust required. Can see how performance changes, under
 exactly what conditions. Also, what was *not* tested."]

 [:h3 "Communicate performance changes"]

 [:p "Claiming a performance improvement requires objective measurement, wide
 range of inputs, invoking many arities/call patterns. I.e., `map` with
 different mapping functions applied to disparate types."]

 [:p "Makes people less reluctant to use our software. Remove objections, etc.
 What exactly will happen when they switch from one version to another?"]

 [:p "Particularly if a version number is merely a label without semantics.
 what information is necessary to make an informed decision about if there is
 any benefit to change versions. the people using the software can decide
 whether it is worth switching."]

 [:h3 "A low threshold for breakage"]

 [:p "Mindset: Performance regression is a "
  [:a {:href ""}
   "breaking change"] "."]

 [:h3 "Clear communication"]

 [:p "A single source of performance information helps inform what will happen
 when switching versions. Must accurately communicate, and clearly and obvious."]

 [:h3 [:em "Et cetera"]]

 [:ul
  [:li [:p "The performance document is accreting. Once version 9 is benchmarked
 and released, that's it. Corrections are encouraged, and later additional tests
 to compare to some new feature are also okay. The data is versioned-controlled,
 and the " [:span.small-caps "html"] "/markdown documents that are generated
 from the data are also under version-control."]]

  [:li [:p "The performance data is objective, but people may interpret it to
 suit their tastes. 4.1 nanoseconds may be fast enough for one person, but not
 another. The accompanying commentary may express the library author's opinions.
 That's okay. The author is merely communicating that opinion to the person
 considering switching versions. The author may consider a particular version "
 [:em "fast"] ", but the person using the software may not."]]]]

