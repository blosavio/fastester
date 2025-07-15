[:section#ideas
 [:h2 "Ideas"]

 [:h3 "A version number is just a number"]

 [:p "Versioning software with " [:code "major.minor.patch"] " numbers attempt to convey the notion " [:em "Yes, we can safely upgrade to such-and-such version"] ". But the granularity is poor. What if a dependency does have a breaking change, but the breaking change is in a portion of the dependency that we don't use. Version numbers ought to merely be a label to differentiate one release from another."]

 [:p "If a version number is merely a label without semantics, how would someone judge whether to switch from one version to another? A detailed, concise, regularly-formatted changelog conveys all the information necessary to make an informed decision about if there is any benefit to change versions, if changing version will require changes to on the consuming side, and if so, what changes are necessary."]

 [:p "A later version is not promised to be " [:em "better"] ", merely different. The changelog authors will provide dispassionate information about the changes, and the people using the software can decide whether it is worth switching."]
 
 [:p "Chlog is an experiment to detangle version numbers from changelog information. A version number " [:code "n"] " makes no claim other than it was released some time later than version " [:code "n-1"] "."]

 [:h3 "A changelog is data"]
 
 [:p "The " [:code "changelog.edn"] " is the single, canonical source of information. All other representations (" [:span.small-caps "html"] "/markdown, etc) are derived from that and are merely conveniences."]
 
 [:p#info "A human- and machine-readable " [:code "changelog.edn"] " will accompany each version. " [:code "changelog.edn"] " is a tail-appended file constructed from all previous releases, possibly automatically-composed of per-version " [:code "changelog-v" [:em "N"] ".edn"] " files in a sub-directory."]
 
 [:h3 "A low threshold for breakage"]

 [:p "The Chlog experiment focuses on the changelog being the sole source of information on what will happen when switching versions. For that to succeed, the entries must accurately communicate whether a change is breaking. Not every change can be objectively categorized as either breaking or non-breaking (more on that in a moment). To have empathy for other people is tricky. If all changes are claimed as breaking, the concept loses its meaning and purpose. But if a supposedly safe change ends up breaking for someone else, trust is lost."]

 [:p "Within a changelog, seeing " [:code ":breaking? false"] " indicates that switching to that version will work as it worked before with zero other changes (including changes in dependencies). Otherwise, the change is a " [:a {:href "#breaking"} [:em "breaking change"]] ", indicated by " [:code ":breaking? true"] "."]

 [:p "As a rough starting guideline, the following kinds of changes are " [:strong "probably"] " breaking."]

 [:ul
  [:li "all regressions (performance, memory, network)"]
  [:li "added or changed dependencies (see note below)"]
  [:li "removed or renamed namespaces"]
  [:li "moved, renamed, or removed functions"]
  [:li "stricter input requirements"]
  [:li "decreased return"]
  [:li "different default"]]

 [:p "Likewise, the following kinds of changes are " [:strong "probably"] " non-breaking."]

 [:ul
  [:li "all improvements (performance, memory, network)"]
  [:li "removed dependencies"]
  [:li "added or deprecated namespaces"]
  [:li "added or deprecated functions"]
  [:li "relaxed input requirements"]
  [:li "increased returns"]
  [:li "implementation"]
  [:li "source code formatting"]
  [:li "documentation"]]

 [:p "These are just starting guidelines. Careful judgment may say that a change in a function's defaults will in all cases be a non-breaking change. Or, a change in the documentation might be so severe that it's elevated to a breaking change."]
 
 [:p "One important kind of change that kinda defies categorization is bug-fixes. According to the notion that a non-breaking change must be a perfect drop-in replacement, a bug fix would classify as a breaking change. Tentative policy: Bug fixes are non-breaking changes, but it depends on the scenario."]

 [:h3 "Formal specifications state required information"]

 [:p "Each version has required information that is explicitly delineated in the " [:a {:href "https://github.com/blosavio/chlog/blob/main/src/chlog/changelog_specifications.clj"} "specifications"] ". Correctness of a changelog, or any sub-component of the changelog, may be verified by " [:a {:href "https://blosavio.github.io/chlog/chlog.changelog-validations.html"} "validating"] " the changelog against those specifications."]

 [:h3 [:em "Et cetera"]]

 [:ul
  [:li [:p "A changelog is mutable. Corrections are encouraged and additions are okay. The changelog itself is versioned-controlled data, and the " [:span.small-caps "html"] "/markdown documents that are generated from the changelog data are also under version-control."]]

  [:li [:p "Yanked or retracted releases can simply be noted by revising the changelog data."]]

  [:li [:p "Much of the changelog data is objective (e.g., dates, email), but some is merely the changelog author's opinions. That's okay. The changelog author is communicating that opinion to the person considering switching versions. The changelog author may consider a particular bug-fix " [:code ":high"] " urgency, but the person using the software may not."]]]
 
 ]