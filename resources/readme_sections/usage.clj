[:section#usage
 [:h2 "Usage"]

 [:p "There are four components to using Chlog."]

 [:ol
  [:li [:p "Maintain changelog data in " [:code "edn"] " files."]]
  [:li [:p [:a {:href "#setup"} "Declare and require"] " the dependency."]]
  [:li [:p "Create an " [:em "options"] " file."]]
  [:li [:p "Generate the changelog documents."]]]

 [:h3 "Changelog information is Clojure data"]

 [:p "Changelog information is stored as a series of nested collections in " [:code ".edn"] " files. Every " [:a {:href "#version"} [:em "version"]] " is represented by the following map."]

 [:pre [:code "{:version ___\n :date {:year ___\n        :month ___\n        :day ___ }\n :responsible {:name ___\n               :email ___ }\n :project-status ___\n :breaking? ___\n :urgency ___\n :comment ___\n :changes [...]}"]]

 [:p "This map (and all the following) is formally and canonically " [:a {:href "https://github.com/blosavio/chlog/blob/main/src/chlog/changelog_specifications.clj"} "specified"] " with a " [:a {:href "https://github.com/blosavio/speculoos"} "Speculoos"] " style specification."]

 [:p "Briefly, the version parts are:"]

 [:ul
  [:li [:strong "version"] " is an integer."]
  [:li [:strong "date"] " is a map of integer year, string month, and integer day"]
  [:li [:strong "responsible"] " is a map of a name string and an email string."]
  [:li [:strong "project-status"] " is one of enumerated keywords borrowed from the " [:a {:href "https://github.com/metosin/open-source/blob/main/project-status.md"} " Metosin description"] "."]
  [:li [:strong "breaking?"] " is a boolean or nil (only valid for the initial release)."]
  [:li [:strong "urgency"] " is one of " [:code ":low"] ", " [:code ":medium"] ", or " [:code ":high"] "."]
  [:li [:strong "comment"] " is a free-form string."]
  [:li [:strong "changes"] " a vector of " [:em "change"] " maps (discussed soon)."]]

 [:p "A " [:a {:href "#changelog"} "changelog"] " is a tail-appended vector of one or more such " [:em "version"] " hash-maps. Furthermore, a version hash-map may have zero or more " [:a {:href "#change"} [:em "change"]] " hash-maps associated to the " [:code ":changes"] " key. A " [:em "change"] " hash-map looks like this."]
 
 [:pre [:code "{:description ___\n :reference {:source ___\n             :url ___}\n :change-type ___\n :breaking? ___\n :altered-functions []\n :date {:year ___\n        :month ___\n        :day ___ }\n :responsible {:name ___\n               :email ___ }}"]]

 [:p "Besides a sequence of " [:code ":altered-functions"] ", a change may contain sequences of " [:code ":added-functions"] ", " [:code ":deprecated-functions"] ", " [:code ":moved-functions"] ", " [:code ":removed-functions"] ", and " [:code ":renamed-functions"] "."]

 [:p "The parts of a change hash-map are:"]

 [:ul
  [:li [:strong "date"] " analogous to the date of a version."]
  [:li [:strong "reference"] " a map of source string, optional url string, optional ticket string/uuid."]
  [:li [:strong "breaking?"] " a boolean."]
  [:li [:strong "altered-functions"] " a vector of symbols that were altered in this change."]
  [:li [:strong "responsible"] " a hash-map of a name string and an email string."]
  [:li [:strong "change-type"] " a keyword from an " [:a {:href "https://github.com/blosavio/chlog/blob/2304b2e780c23d7094872b0c58bf6a94277c77d2/src/chlog/changelog_specifications.clj#L43"} "enumerated list"] "."]
  [:li [:strong "description"] " a a free-form string."]]

 [:p "The changelog data may be manipulated and queried with any suitable Clojure function, such as " [:code "get-in"] ", " [:code "assoc-in"] ", " [:code "update-in"] ", etc. Chlog includes specifications for changelog data and utilities for performing those validations, but any validation facility, such as " [:code "clojure.spec.alpha"] ", may be used."]
 
 [:h3 "Creating an " [:em "options"] " file"]

 [:p "The " [:em "options"] " file is an " [:a {:href "https://github.com/edn-format/edn"} "edn"] " file (" [:a {:href "https://github.com/blosavio/chlog/blob/main/resources/chlog_options.edn"} "example"] ") that contains a map which supplies required information for generating a changelog. It also declares preferences for other optional settings."]

 [:p "Required keys:"]

 [:ul
  [:li [:p [:code ":project-name-formatted"] " Project name (string) to display on changelog html/markdown documents."]]
  
  [:li [:p [:code ":copyright-holder"] " Name displayed in the copyright statement in the footer of the changelog."]]
  
  [:li [:p [:code ":UUID"] " Version 4 " [:strong "U"] "niversally " [:strong "U"] "nique " [:strong "Id"] "entifier. Suggestion: eval-and-replace " [:code "(random-uuid)"] ". Default " [:code "nil"] "."]]]

 [:p "Optional keys (defaults supplied by " [:a {:href "https://github.com/blosavio/chlog/blob/main/src/chlog/chlog_defaults.edn"} [:code "chlog_defaults.edn"]] "):"]

 [:ul
  [:li [:p [:code ":changelog-entries-directory"] " Alternative directory to find changelog " [:code ".edn"] " files. Include trailing '/'. Defaults to " [:code "resources/changelog_entries/"] "."]]

  [:li [:p [:code ":changelog-data-file"] " Alternative " [:code ".edn"] " file that contains the changelog data. May include " [:code "load-file"] "-ing for easier organization and version control. Defaults to " [:code "changelog.edn"] "."]]

  [:li [:p [:code ":changelog-html-directory"] " Alternative output " [:span.small-caps "html"] " directory (string). Include trailing '/'. Defaults to 'doc/'."]]

  [:li [:p [:code ":changelog-html-filename"] " Alternative output " [:span.small-caps "html"] " filename (string). Defaults to 'changelog.html'."]]

  [:li [:p [:code ":changelog-markdown-directory"] " Alternative output markdown directory (string). Include trailing `/`. Defaults to '' (i.e., project's root directory)."]]

  [:li [:p [:code ":changelog-markdown-filename"] " Alternative output markdown filename (string). Defaults to 'changelog.md'."]]

  [:li [:p [:code ":tidy-html?"] " Indent and wrap " [:span.small-caps "html"] " and markdown files. Defaults to " [:code "false"] "."]]]
 
 [:h3 "Generating the changelog documents"]

 [:p "The Chlog library generates " [:span.small-caps "html"] " and markdown changelog files from " [:a {:href "https://github.com/weavejester/hiccup"} "hiccup"] " source. To generate the " [:span.small-caps "html"] " and markdown files. We could evaluate…"]

 [:pre [:code "(generate-all-changelogs (load-file \"resources/chlog_options.edn\"))"]]

 [:p "…in whatever namespace we loaded " [:code "generate-all-changelogs"] ". Or, we could copy " [:a {:href "https://github.com/blosavio/chlog/tree/main/resources/chlog_generator.clj"} [:code "resources/chlog_generator.clj"]] " and evaluate all forms in the namespace ("  [:span.small-caps "cider"] " command " [:code "C-c C-k"] ")."]

 [:p "Chlog produces two files. The first is a 'markdown' file that's actually plain old " [:span.small-caps "html"] ", abusing the fact that " [:span.small-caps "html"] " passes through the markdown converter. By default, this markdown file is written to the project's root directory where GitHub can find and display the changelog. We don't need a dedicated markdown converter to view this file; copy it to a " [:a {:href "https://gist.github.com/"} "GitHub gist"] " and it'll display similarly to when we view it on GitHub. The second file, by default written to the " [:code "resources/"] " directory, is a proper " [:span.small-caps "html"] " document with a " [:code (raw "&lt;head&gt;")] ", etc., that is viewable in any browser. We may want to copy over the " [:a {:href "https://github.com/blosavio/chlog/blob/main/doc/project.css"} "css file"] " for some minimal styling."]

 ]