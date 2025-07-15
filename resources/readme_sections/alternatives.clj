[:section#alternatives
 [:h2 "Alternatives"]

 [:ul
  [:li [:p [:strong "Classic markdown/" [:span.small-caps "html"] " files"] " Discussed above."]]
  
  [:li [:p [:strong "Changelog generated from version control commit log"] " At first glance, a changelog generated from a version control commit log seems natural. It is certainly easy, and there are many concepts in common. However, the purpose of a changelog is different enough to merit its own focus."]
   
   [:p "Version control logs communicate information about the development history to developers of that software. Changelogs communicate the details of the released versions to people who consume the software. Related, but subtly different."]

   [:p "A commit message notes fine-grained changes to the software, somewhat like a diary of software development. It would feel a bit oppressive to have to consider how every commit message would appear in a public changelog. The evolution of software is noisy. Commit messages may involve false starts, mistakes, and dead ends. They are meant to be read by people " [:em "developing"] " the software itself. Plus, writing commit messages that also serve as a changelog entry would require some kind of standards or specifications, or heroic discipline by the authors. Finally, commit messages typically do not concern themselves about whether the change is breaking for the people using the software."]

   [:p "Changelogs, on the other hand, should clearly and concisely communicate, to people " [:em "using"] " the software, the differences between one version and another. It could be fine- or coarse-grained, but the freedom to decide should be independent of the version control commit log. Authoring a changelog requires care, judgment, and empathy for people ultimately using the software, and is a task somewhat different from wrangling version control commit messages."]]]

 [:p "Even at this early stage of its life, Chlog can alleviate most of that labor. Keep the changelog as " [:code ".edn"] " data, and Chlog will take care of the " [:span.small-caps "html"] "/markdown."]]