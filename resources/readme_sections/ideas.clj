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
  [:code "(zap inc [1 2 3])"]
  " requires 183±2 microseconds to evaluate."]

 [:p "Is…that good? Difficult to say. What we'd really like to know is how
 183 microseconds compares to some previous version. So if, for example,
 version 12 of "
  [:code "zap"]
  " evaluates in 183 microseconds, whereas version 11 required
 264 microseconds, we have good reason to believe the later implementation is
 faster."]

 [:p "Beyond that, tossing out raw numbers like \"183\" and \"264\" requires
 people to juggle arithmetic in their head. Not ideal."]

 [:p "A Fastester performance report intends to be objective, relative, and
 comprehensible."]

 [:h3#objective "Objective"]

 [:p "Thanks to Criterium, we can measure, in units of seconds, how long it
 takes to evaluate a function with a particular argument, (somewhat) independent
 of vagaries of the environment."]

 [:h3#comparative "Relative"]

 [:p "A single, isolated timing measurement doesn't mean much to a person, even
 if it is "
  [:a {:href "#objective"} "objective"]
  ". People simply don't have everyday intuition for an event that occurs in a
 nanoseconds or microseconds. So when we discuss the concept of 'fast', we're
 often implicitly speaking in relative terms."]

 [:p "Fastester focuses on comparing the speed of one function to a previous
 version of itself."]

 #_[:aside "(I am sorely tempted to remove all absolute units by normalizing time
 measurements to some arbitrary reference, but I am reluctant to treat the
 benchmark results so casually.)"]

 [:h3#comprehensible "Comprehensible"]

 [:p "Humans are visually-oriented, and a straightforward two-dimensional chart
 is an excellent tool to convey relative performance changes between versions.
 A person ought to be able to glance at the performance report and immediately
 grasp the improvements, with details available as needed."]

 [:p "Fastester documents consist primarily of charts with accompanying text.
 A show/hide button reveals details as the reader desires."]

 [:h3 [:em "Et cetera"]]

 [:ul
  [:li [:p "The performance document is accreting. Once version 12 is
 benchmarked and released, it's there for good. Corrections are encouraged, and
 later additional tests to compare to some new feature are also okay. The data
 is versioned-controlled, and the "
        [:span.small-caps "html"]
        "/markdown documents that are generated from the data are also under
 version-control."]]

  [:li [:p "The performance data is objective, but people may interpret it to
 suit their tastes. 183 microseconds may be fast enough for one person, but not
 another. The accompanying commentary may express the library author's opinions.
 That's okay. The author is merely communicating that opinion to the person
 considering switching versions. The author may consider a particular version "
        [:em "fast"]
        ", but the person using the software may not."]]

  [:li [:p "We should probably consider a performance regression as a "
        [:em "breaking change"]
        ". Fastester can help estimate and communicate how much the performance
 regressed."]]]]

