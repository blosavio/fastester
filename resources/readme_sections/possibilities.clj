[:section#possibilities
 [:h2 "Possibilities"]

 [:p "When changelog information is stored as Clojure data, it opens many intriguing possibilities."]

 [:ul
  [:li [:p "Changelog data could be used to generate formatted " [:span.small-caps "html"] " or markdown webpages for casual reading. Chlog currently implements this."]]

  [:li [:p "A " [:code "js/cljs"] " widget embedded in a webpage that presents a " [:em "current version"] " selector and a " [:em "target version"] " selector. Then, based on each selection, the utility would collapse all the intervening versions and list the breaking and non-breaking changes. Someone considering switching versions could quickly click around and compare the available versions. " [:em "Switching from version 3 to version 4 introduces one breaking change"] " whereas " [:em "Switching from version 3 to version 5 involves that same breaking change, plus another breaking change in a function we don't use."] " Therefore, switching to version 4 or version 5 is equivalent."]]

  [:li [:p "A utility that could scan the codebase and list all the functions used from a particular dependency. If we were curious about switching versions of that dependency, that list of functions would be compared to the list of functions with breaking changes. If there was no intersection of the lists, it's safe to switch versions. If the codebase " [:em "did"] " use a function with a breaking change, the changelog would communicate what changed, and how involved it would be to switch versions."]]]]