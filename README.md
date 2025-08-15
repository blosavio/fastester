<a href="https://clojars.org/com.sagevisuals/fastester"><img src="https://img.shields.io/clojars/v/com.sagevisuals/fastester.svg" /></a><br /><a href="#setup">Setup</a><br /><a href="https://blosavio.github.io/fastester/index.html">API</a><br /><a href="https://github.com/blosavio/fastester/blob/main/changelog.md">Changelog</a><br /><a href="#intro">Introduction</a><br /><a href="#ideas">Ideas</a><br /><a href="#usage">Usage</a><br /><a href="#limits">Limitations &amp; mitigations</a><br /><a href="#alternatives">Alternatives &amp; references</a><br /><a href="#goals">Non-goals</a><br /><a href="#examples">Examples</a><br /><a href="#glossary">Glossary</a><br /><a href="https://github.com/blosavio">Contact</a><br /><h1>Fastester</h1><em>A Clojure library for measuring and displaying
  performance changes</em><br /><section id="setup"><h2>Setup</h2><h3>Leiningen/Boot</h3><pre><code>[com.sagevisuals/fastester &quot;0-SNAPSHOT0&quot;]</code></pre><h3>Clojure CLI/deps.edn</h3><pre><code>com.sagevisuals/fastester {:mvn/version &quot;0-SNAPSHOT0&quot;}</code></pre><h3>Require</h3><pre><code>(require &apos;[fastester.measure :refer [defbench run-one-registered-benchmark]])</code></pre></section><section id="intro"><h2>Introduction</h2><p>Imagine: We notice that the <code>zap</code> function of version 11 of our library is sub-optimal. We improve the
&nbsp;implementation so that <code>zap</code> executes faster.</p><p>We could mumble something like this in the version 12 changelog.</p><blockquote><em>Function <code>zap</code> is faster.</em></blockquote><p>Or, we could write this.</p><blockquote><em>Version 12 of function <code>zap</code> is 20 to 30 percent faster than version 11 for integers spanning five
&nbsp;orders of magnitude. This implementation change will improve performance for
&nbsp;the vast majority of intended use cases.</em></blockquote><p>And then show this.</p><div><img alt="Chart of synthetic performance benchmark of function `zap`,
&nbsp;comparing versions 11 and 12; version 12 demonstrates approximately 25% faster
&nbsp;performance across a specific range of arguments." src="synthetic-benchmark-chart.svg" /><table><thead><tr><th colspan="7">arg, n</th></tr><tr><th>version</th><th>1</th><th>10</th><th>100</th><th>1000</th><th>10000</th><th>100000</th></tr></thead><tr><td>11</td><td>5.2±0.0</td><td>7.1±0.3</td><td>9.8±0.3</td><td>11.9±0.0</td><td>13.3±0.0</td><td>15.8±0.2</td></tr><tr><td>12</td><td>4.2±0.2</td><td>5.5±0.2</td><td>6.9±0.1</td><td>8.3±0.2</td><td>9.9±0.2</td><td>11.7±0.2</td></tr><tr><th colspan="7">simulated time in nanoseconds <em>(mean±std)</em></th></tr></table></div><p>The Fastester library streamlines the tasks of writing benchmarks for a
&nbsp;function, objectively measuring evaluation times of different versions of that
&nbsp;function, and concisely communicating how performance changes between
&nbsp;versions.</p></section><section id="ideas"><h2>Ideas</h2><p>We ought to strive to write fast software. Fast software respects other
&nbsp;people&apos;s time and their computing resources. Fast software demonstrates skill
&nbsp;and attention to detail. And fast software is just plain cool.</p><p>But, how fast is &quot;fast&quot;? It&apos;s not terribly convincing to say <em>Our software is fast.</em> We&apos;d like some objective measure of fast. Fortunately, the <a href="https://github.com/hugoduncan/criterium">Criterium library</a> provides a handy group of benchmarking utilities that measures the
&nbsp;evaluation time of a Clojure expression. We could use Criterium to learn that <code>(zap inc [1 2 3])</code> requires 19.6±0.6 nanoseconds to evaluate.</p><p>Is…that good? Difficult to say. What we&apos;d really like to know is how
&nbsp;19.6 nanoseconds compares to some previous version. So if, for example,
&nbsp;version 12 of <code>zap</code> evaluates in 19.6 nanoseconds, whereas version 11 required
&nbsp;24.5 nanoseconds, we have good reason to believe the later implementation is
&nbsp;faster.</p><p>Beyond that, tossing out raw numbers like &quot;19.6&quot; and &quot;24.5&quot; requires
&nbsp;people to juggle arithmetic in their head. Not ideal.</p><p>A Fastester performance report intends to be objective, relative, and
&nbsp;comprehensible.</p><h3 id="bojective">Objective</h3><p>Thanks to Criterium, we can measure, in units of seconds, how long it
&nbsp;takes to evaluate a function with a particular argument, (somewhat) independent
&nbsp;of vagaries of the environment.</p><h3 id="comparative">Relative</h3><p>A single, isolated timing measurement doesn&apos;t mean much to a person, even
&nbsp;if it is <a href="#objective">objective</a>. Humans simply don&apos;t have everyday intuition for an event that occurs in a
&nbsp;few nanoseconds. So when we discuss the concept of &apos;fast&apos;, we&apos;re often
&nbsp;implicitly speaking in relative terms.</p><p>Fastester focuses on comparing the speed of a function to a previous
&nbsp;version of itself.</p><aside>(I am sorely tempted to remove all absolute units by normalizing time
&nbsp;measurements to some arbitrary reference, but I am reluctant to treat the
&nbsp;benchmark results so casually.)</aside><h3 id="comprehensible">Comprehensible</h3><p>Humans are visually-oriented, and a straightforward two-dimensional chart
&nbsp;is an excellent tool to convey relative performance changes between versions.
&nbsp;A person ought to be able glance at the performance report and immediately
&nbsp;grasp the improvements, with details available as needed.</p><p>Fastester documents consist primarily of charts with accompanying text.
&nbsp;A show/hide button reveals details as the reader desires.</p><h3><em>Et cetera</em></h3><ul><li><p>The performance document is accreting. Once version 12 is
&nbsp;benchmarked and released, that&apos;s it. Corrections are encouraged, and later
&nbsp;additional tests to compare to some new feature are also okay. The data is
&nbsp;versioned-controlled, and the <span class="small-caps">html</span>/markdown documents that are generated from the data are also under
&nbsp;version-control.</p></li><li><p>The performance data is objective, but people may interpret it to
&nbsp;suit their tastes. 19.6 nanoseconds may be fast enough for one person, but not
&nbsp;another. The accompanying commentary may express the library author&apos;s opinions.
&nbsp;That&apos;s okay. The author is merely communicating that opinion to the person
&nbsp;considering switching versions. The author may consider a particular version <em>fast</em>, but the person using the software may not.</p></li><li><p>We should probably consider a performance regression as a <em>breaking change</em>. Fastester can help estimate and communicate how much the performance
&nbsp;regressed.</p></li></ul></section><section id="usage"><h2>Usage</h2><p>Prologue: We have previously profiled some execution path of version 11 of
&nbsp;our library. We discovered a bottleneck in a function, <code>zap</code>, which just so happens to behave exactly like <code>clojure.core/map</code>: apply some function to every element of a collection.</p><pre><code>(zap inc [1 2 3]) ;; =&gt; (2 3 4)</code></pre><p>We then changed <code>zap</code>&apos;s implementation for better performance and objectively verified that this
&nbsp;new implementation for version 12 provides shorter execution times than the
&nbsp;previous version.</p><p>We&apos;re now ready to release version 12 with the updated <code>zap</code>. After <a href="#setup">declaring and requiring</a> the dependency, there are four steps to using Fastester.</p><ol><li><p><a href="#set-options">Set</a> the options.</p></li><li><p><a href="#write-benchmarks">Write</a> benchmarks.</p></li><li><p><a href="#run-benchmarks">Run</a> the benchmarks.</p></li><li><p><a href="#generate">Generate</a> an <span class="small-caps">html</span> document that displays the performance data.</p></li></ol><p>Steps 1 and 2 are done once, and only occasionally updated as needed.
&nbsp;Steps 3 and 4 are done only when a function&apos;s implementation changes with
&nbsp;measurable affects on performance.</p><h3 id="set-options">1. Set the options</h3><p>We must first set the options that govern how Fastester behaves. Options
&nbsp;live in the file <code>fastester_options.edn</code> as a Clojure map. One way to get up and running quickly is to copy-paste
&nbsp;Fastester&apos;s <a href="https://github.com/blosavio/fastester/blob/main/resources/fastester_options.edn">sample options file</a> and edit as needed.</p><p>The following options have <a href="https://blosavio.github.io/fastester.display.html#var-fastester-defaults">default values</a>.</p><table><tr><th>key</th><th>default</th><th>usage</th></tr><tr><td><code>:benchmarks-directory</code></td><td><code>&quot;test/fastester/performance/&quot;</code></td><td><p>Directory from which to load benchmark tests.</p></td></tr><tr><td><code>:benchmarks-filenames</code></td><td><code>&quot;benchmarks.clj&quot;</code></td><td><p>Set of filenames from which to load benchmark tests.</p></td></tr><tr><td><code>:html-directory</code></td><td><code>&quot;doc/&quot;</code></td><td><p>Directory to write html document.</p></td></tr><tr><td><code>:html-filename</code></td><td><code>&quot;performance.html&quot;</code></td><td><p>Filename to write html document.</p></td></tr><tr><td><code>:img-subdirectory</code></td><td><code>&quot;img/&quot;</code></td><td><p> Under <code>:html-directory</code>, directory to write svg image files.</p></td></tr><tr><td><code>:markdown-directory</code></td><td><code>&quot;doc/&quot;</code></td><td><p>Directory to write markdown files.</p></td></tr><tr><td><code>:markdown-filename</code></td><td><code>&quot;performance.md&quot;</code></td><td><p>Filename to write markdown document.</p></td></tr><tr><td><code>:results-url</code></td><td><code>&quot;https://example.com&quot;</code></td><td><p>Base URL for where to find benchmark data. For local filesystem, use
&nbsp;something like <code>&quot;../&quot;</code>.</p></td></tr><tr><td><code>:results-directory</code></td><td><code>&quot;resources/performance_entries/&quot;</code></td><td><p>Directory to find benchmark data, appended to <code>:results-url</code>.</p></td></tr><tr><td><code>:excludes</code></td><td><code>#{}</code></td><td><p>A set of benchmark groups (strings) to skip, i.e., <code>#{}</code>  skips zero benchmark groups.</p></td></tr><tr><td><code>:verbose?</code></td><td><code>false</code></td><td><p>A boolean that governs printing benchmark status.</p></td></tr><tr><td><code>:testing-thoroughness</code></td><td><code>:quick</code></td><td><p>Assigns Criterium benchmark settings. One of <code>:default</code>, <code>:quick</code>, <code>:lightning</code>.</p></td></tr><tr><td><code>:parallel?</code></td><td><code>false</code></td><td><div id="parallel?"><p>A boolean that governs running benchmarks on multiple threads in
&nbsp;parallel.</p><p>Warning: Running benchmarks in parallel results in undesirable, high
&nbsp;variance. Associate to <code>true</code> only to quickly verify the value returned from evaluating an expression.
&nbsp;Do not use for final benchmarking.</p></div></td></tr><tr><td><code>:n-threads</code></td><td><code>1</code></td><td><p>An integer that governs the number of threads to use when <code>:parallel</code> is <code>true</code>. Recommendation: Set to one fewer than machine core count. See warning
&nbsp;for <a href="#parallel?"><code>:parallel?</code></a>.</p></td></tr><tr><td><code>:save-benchmark-fn-results?</code></td><td><code>true</code></td><td><p>When assigned <code>true</code>, includes the value returned from evaluating the benchmark expression.
&nbsp;Useful during development to check the correctness of the benchmark expression.
&nbsp;However, file sizes grow unwieldy. Setting to <code>false</code> replaces the data with <code>:fastester/benchmark-fn-results-elided</code>.</p></td></tr><tr><td><code>:tidy-html?</code></td><td><code>false</code></td><td><p>Default setting causes html to be written to file with no line breaks.
&nbsp;If set to <code>true</code> line breaks are inserted for readability, and for smaller version
&nbsp;control diffs.</p></td></tr><tr><td><code>:preamble</code></td><td><code>[:div &quot;...&quot;]</code></td><td><p>A hiccup/html block inserted at the top of the results document.</p></td></tr></table><p>The following options have no defaults.</p><table><tr><th>key</th><th>example</th><th>usage</th></tr><tr><td><code>:responsible</code></td><td><code>{:name &quot;Grace Hopper&quot;, :email &quot;RDML@univac.net&quot;}</code></td><td><p>A hashmap with <code>:name</code> and <code>email</code> strings that report a person responsible for the report.</p></td></tr><tr><td><code>:copyright-holder</code></td><td><code>&quot;Abraham Lincoln&quot;</code></td><td><p>Copyright holder listed in the footer of the document.</p></td></tr><tr><td><code>:fastester-UUID</code></td><td><code>de280faa-ebc5-4d75-978a-0a2b2dd8558b</code></td><td><p>A version 4 Universally unique ID listed in the footer of the document.
&nbsp;To generate a new UUID, evaluate <code>(random-uuid)</code>. Associate to <code>nil</code> to skip.</p></td></tr></table><h3 id="write-benchmarks">2. Write benchmarks</h3><p>Writing benchmarks follows a similar pattern to writing unit tests. We
&nbsp;make a file (defaulting to <code>test/fastester/performance/benchmarks.clj</code>) with a namespace declaration. See these <a href="https://github.com/blosavio/fastester/blob/main/test/fastester/performance/benchmarks.clj">two</a> <a href="https://github.com/blosavio/fastester/blob/main/test/fastester/performance/benchmarks_mapping.clj">examples</a>. For organizing purposes, we may write more than one benchmarks file, if,
&nbsp;for example, we&apos;d like to write one benchmark file per source namespace.</p><p>Before we start bashing the keyboard, let&apos;s think a little about how we
&nbsp;want to test <code>zap</code>. We&apos;d like to demonstrate <code>zap</code>&apos;s improved performance for a variety of argument types, over a wide range of
&nbsp;argument sizes. To do so, we decide to write two benchmarks. The first
&nbsp;benchmark will measure the evaluation times of increasingly-lengthy sequences
&nbsp;of integers. The second benchmark will measure the evaluation times of
&nbsp;upper-casing ever-longer sequences of strings. We will therefore need to write
&nbsp;two benchmarks.</p><p>Within a benchmarks file, we use <code>defbench</code> to <strong>def</strong>ine a <strong>bench</strong>mark. Here is its signature.</p><pre><code>(defbench <em>name &quot;group&quot; fn-expression args</em>)</code></pre><p>For the first argument, we supply <code>defbench</code> with a <em>name</em>, an unquoted symbol. The name labels the three trailing arguments. For our
&nbsp;scenario, let&apos;s name our two benchmarks with the symbols <code>zap-inc</code> and <code>zap-uc</code>.</p><p>Note that all benchmarks will be added to a singular <a href="#registry">registry</a>, so if we write two <code>defbench</code> expressions using the same name, the later will overwrite the former
&nbsp;(any particular ordering is an implementation detail and not guaranteed). If
&nbsp;we&apos;d like to give the same name to two different benchmarks (particularly the
&nbsp;same name from two different namespaces), we can use <a href="https://clojure.org/reference/reader#_symbols">qualified symbols</a>. In the scenario of benchmarking <code>zap</code>, we&apos;ve chosen two different names, so we won&apos;t worry about overwriting.</p><p>After the name, we supply a <em>group</em>, a string that associates this benchmark with other
&nbsp;conceptually-related benchmarks. For example, while we&apos;re benchmarking <code>zap</code>, we will want to test its performance in different ways, varying the size of
&nbsp;arguments, with different types of arguments, e.g., integers, strings, etc. The
&nbsp;ultimate html document will gather into a section the benchmarks sharing a
&nbsp;group. Let&apos;s assign both our benchmarks to the <code>&quot;faster zap implementation&quot;</code> group.</p><p>The final two arguments, <code>fn-expression</code> and <code>args</code>, share the heavy lifting. The next step, <a href="#run-benchmarks"><em>running the benchmarks</em></a> will entail serially supplying elements of <code>args</code> to the function expression.</p><p>The function expression is a 1-arity function that demonstrates some
&nbsp;performance aspect of the new version of the function. We updated <code>zap</code> so that it processes elements faster. One way to demonstrate its improved
&nbsp;performance is to increment a sequence of integers. That expression might
&nbsp;look like this.</p><pre><code>(fn [n] (zap inc (range n)))</code></pre><p>&apos;Running&apos; that benchmark means that a series of <code>n</code> arguments are passed to the expression, measuring the evaluation times for
&nbsp;each <code>n</code>. The values for <code>n</code> are supplied by the final part of the benchmark definition. Let&apos;s explore
&nbsp;<code>range</code>s from ten to one-hundred thousand.</p><pre><code>[10 100 1000 10000 100000]</code></pre><p>Ratcheting <code>(range n)</code> by powers of ten stresses <code>zap&apos;s</code> performance. Roughly speaking, we&apos;ll be doing this.</p><pre><code>(benchmark (zap inc (range 10)))</code><br /><code>(benchmark (zap inc (range 100)))</code><br /><code>(benchmark (zap inc (range 1000)))</code><br /><code>(benchmark (zap inc (range 10000)))</code><br /><code>(benchmark (zap inc (range 100000)))</code></pre><p>Altogether, that benchmark definition looks like this.</p><pre><code>(defbench zap-inc
&nbsp;         &quot;faster zap implementation&quot;
&nbsp;         (fn [n] (zap inc (range n)))
&nbsp;         [10 100 1000 10000 100000])</code></pre><p>However, there&apos;s a problem. This benchmark&apos;s function expression contains <code>range</code>. If we run this benchmark as is, the evaluation time would include <code>range</code>&apos;s processing time. We may want to do so in other cases, but in this
&nbsp;scenario, it would be misleading. We want to focus solely on how fast <code>zap</code> can process its elements. Let&apos;s extract <code>range</code> to an external expression.</p><pre><code>(def range-of-length-n (reduce #(assoc %1 %2 (range %2)) {} [10 100 1000 10000 100000]))</code><br /><br /><br /><code>(defbench zap-inc
&nbsp;         &quot;faster zap implementation&quot;
&nbsp;         (fn [n] (zap inc (range-of-length-n n)))
&nbsp;         [10 100 1000 10000 100000])</code></pre><p><code>range-of-length-n</code> generates all the sequences ahead of time. With the sequences now created
&nbsp;outside of the benchmark expression, the time measurement will mainly reflect
&nbsp;the work done by <code>zap</code> itself.</p><p>Perhaps you anticipated a remaining problem if you extrapolated that <code>zap</code> behaves like <code>map</code>. If we were to run the <code>zap-inc</code> benchmarks as defined above, we&apos;d notice that the evaluation times were
&nbsp;suspiciously consistent, no matter how many integers the sequence contained. <code>zap</code>, like many core sequence functions, returns a lazy sequence. We must force
&nbsp;the return sequence to be realized so that <code>zap-inc</code> measures what we intend. <code>doall</code> is handy for that.</p><pre><code>(defbench zap-inc
&nbsp;         &quot;faster zap implementation&quot;
&nbsp;         (fn [n] (doall (zap inc (range-of-length-n n))))
&nbsp;         [10 100 1000 10000 100000])</code></pre><p>We want to define another benchmark that exercises <code>zap</code> by upper-casing strings. We picked a name earlier <code>zap-uc</code>, so now we&apos;ll define the benchmark, analogous to <code>zap-inc</code>. First, we&apos;ll pre-compute the test sequences so that running the benchmark
&nbsp;doesn&apos;t measure evaluating <code>cycle</code>.</p><pre><code>(def abc-cycle-of-length-n
&nbsp;    (reduce #(assoc %1 %2 (take %2 (cycle [&quot;a&quot; &quot;b&quot; &quot;c&quot;])))
&nbsp;            {}
&nbsp;            [10 100 1000 10000 100000]))</code></pre><p>Next, we&apos;ll <code>require</code> the string-handler, then define the benchmark.</p><pre><code>(require &apos;[clojure.string :as str])</code><br /><br /><br /><code>(defbench zap-uc
&nbsp;         &quot;faster zap implementation&quot;
&nbsp;         (fn [n] (doall (zap str/upper-case (abc-cycle-of-length-n n))))
&nbsp;         [10 100 1000 10000 10000])</code></pre><p>Since both <code>zap-inc</code> and <code>zap-uc</code>  measure <code>zap</code>, both share the same group: <code>&quot;faster zap implementation&quot;</code>. Their benchmark results will appear under that heading in the html report.</p><p>Take a moment to discuss the registry...</p><p>The registry now contains two benchmarks that will demonstrate <code>zap</code>&apos;s performance: one incrementing sequences of integers, and one upper-casing
&nbsp;sequences of strings.</p><p>Fastester provides a few helper utilities. If we want to see how a
&nbsp;benchmark would work, we can invoke <code>run-one-registered-benchmark</code>. In the course of writing benchmarks, we often need a sequence of
&nbsp;exponentially-growing integers. For that, Fastester offers <code>range-pow-10</code> and <code>range-pow-2</code>.</p><pre><code>(range-pow-10 5) ;; =&gt; (1 10 100 1000 10000 100000)</code><br /><br /><code>(range-pow-2 5) ;; =&gt; (1 2 4 8 16 32)</code></pre><p>Sometimes, we&apos;ll want to remove a defined benchmark, which we can do
&nbsp;with <code>undefbench</code>. And when we need to tear everything down and start defining from scratch,
&nbsp;we have <code>clear-registry!</code>.</p><h3 id="run-benchmarks">3. Run benchmarks</h3><p>Now that we&apos;ve written <code>zap-inc</code> and <zap-uc></zap-uc>, we can run the benchmarks in two ways. If we&apos;ve got our editor open with an
&nbsp;attached  <span class="small-caps">repl</span>, we can invoke <code>(run-all-benchmarks)</code>. If we&apos;re at the command line, invoking </p><pre><code>$ lein run :all</code></pre><p> has the same effect.</p><p>We should expect each benchmark to take about a minute with the default
&nbsp;benchmark settings. To minimize timing variance, we ought to use a multi-core
&nbsp;machine with minimal competing processes, network activity, etc.</p><p>We should see one <code>edn</code>file per benchmark. If not, double check the options hashmap to make sure our
&nbsp;benchmarks are not contained in the <code>:excludes</code> set.</p><h3 id="generate">4. Generate the <span class="small-caps">html</span></h3><p>When the benchmarks are finished running, we can generate the performance
&nbsp;report. Sometimes it&apos;s useful to have an <span class="small-caps">html</span> file to quickly view in the browser, and other times it&apos;s useful to have a
&nbsp;markdown file (i.e., to show on Github), so Fastester generates one of each.</p><p>To generate the documents, we can invoke <code>(generate-documents)</code> at the <span class="small-caps">repl</span>, or <code>$ lein run :gen</code> from the command line.</p><p>When we look at the report, there&apos;s only version 12! We wanted a <a href="#comparative">comparative</a> report which shows how the performance of version 12&apos;s <code>zap</code> has improved <em>relative</em> to version 11&apos;s <code>zap</code>. So, we use version control to roll-back to the version 11 tag, run the
&nbsp;benchmarks with version 11. Then, we roll-forward again to version 12.</p><p>Now the charts and tables show the version 12 benchmark measurements
&nbsp;side-by-side with version 11&apos;s, similar to the <a href="#intro">introduction example</a>. We can clearly see that the new <code>zap</code> implementation executes faster across a wide range of sequence lengths, both
&nbsp;for incrementing integers and upper-casing strings.</p><h3 id="gotchas">Gotchas</h3><p>We must be particularly careful to define our benchmarks to test what we
&nbsp;intend to test. Writing a benchmark using some common Clojure idioms may mask
&nbsp;the property we&apos;re interested in. For example, if we&apos;re interested in
&nbsp;benchmarking mapping over a sequence, if we create the sequence in the map
&nbsp;expression, Criterium will include that process in addition to the mapping.
, such as defining sequences at the location, will measure</p><p>We must be particularly careful to define our benchmarks to test what we
&nbsp;intend to test. The first problematic pattern is a direct result of Clojure&apos;s
&nbsp;inherent concision. It&apos;s idiomatic to compose a sequence right at the spot
&nbsp;where we require it, like this.</p><pre><code>(map inc (repeatedly 99 #(rand))</code></pre><p>However, if we were to submit this expression to Criterium, intending to
&nbsp;measure how long it takes <code>map</code> to increment the sequence, we&apos;d be <em>also</em> benchmarking creating the sequence, which may be a non-negligible portion of
&nbsp;the evaluation time. Instead, we should hoist the sequence creation out of the
&nbsp;expression.</p><pre><code>;; *create* the sequence</code><br /><code>(def ninety-nine-rands (repeatedly 99 #(rand))</code><br /><br /><code>;; *use* the pre-existing sequence</code><br /><code>(map inc ninety-nine-rands)</code></pre><p>The second expression now involves mostly the <code>map</code> action, and is more appropriate for benchmarking.</p><p>But, there is another lurking problem! <code>map</code> (and friends) returns a lazy sequence, which is almost certainly not what we
&nbsp;were intending to benchmark. We must remember to force the realization of the
&nbsp;lazy sequence, conveniently done with <code>doall</code>.</p><pre><code>(doall (map inc ninety-nine-rands))</code></pre><p>One final <em>gotcha</em>, particular to Fastester itself, will be familiar to Clojurists programming
 at the <span class="small-caps">repl</span>. During development, it&apos;s typical to define and re-define benchmarks with <code>defbench</code>. It&apos;s not difficult for the namespace to hold stale benchmark definitions
 that are not apparent from visual inspection of the current state of the text
 in the file. Fastester provides a pair of tools to help with this. The <code>undefbench</code> utility undefines a benchmark by name. <code>clear-registry!</code> undefines all benchmarks, and a quick re-evaluation of the entire text
 buffer redefines only the benchmarks currently expressed in the file.</p></section><section id="limits"><h2>Limitations &amp; mitigations</h2><p>Modern operating systems (OSes) and virtual machines (VMs) provide a
 perilous environment for accurate, reliable benchmarking. They both toss an
 uncountable number of non-deterministic confounders onto our laps. The OS may
 host other processes which contend for computing resources, interrupt for I/O
 or network events, etc. The Java VM may nondeterministically just-in-time (JIT)
 compile hot spots, making the code run faster (or slower!) after some
 unpredictable delay, and the garbage collector (GC) is quite skilled at
 messing with precise timing measurements.</p><p><strong>So we must exercise great care when running the benchmarks and be
 very conservative with our claims when reporting the benchmark results.</strong></p><p>Fastester delegates the benchmarking to Criterium, which fortunately goes
 to considerable effort to minimize this non-determinism. First, just before
 running the benchmark, Criterium forces the GC in order to minimize the chance
 of occurring during the benchmark itself. Furthermore, Criterium includes a
 warm-up period to give the JIT compiler an opportunity to optimize the
 benchmarked code so that the evaluation time are more consistent.</p><p>To try to control for other non-determinism, we run each benchmark
 multiple times (default 60), and calculate statistics on those results, which
 helps suggest whether or not our benchmark data is consistent.</p><p>Fastester, following Criterium&apos;s lead, focuses on the mean (average)
 evaluation time, not the minimum. This policy is intended to avoid
 over-emphasizing edge cases that coincidentally perform well and giving a more
 unbiased view.</p><p>If our new implementation is only a few percent &apos;faster&apos; than the old
 version, we ought to consider very carefully whether it is worth changing the
 the implementation which may or may not be an improvement.</p></section><section id="alternatives"><section id="alternatives"><h2>Alternatives &amp; References</h2><ul><li><p><a href="https://github.com/clojure-goes-fast/clj-async-profiler">clj-async-profiler</a> A single-dependency, embedded high-precision performance profiler.</p></li><li><p><a href="https://github.com/jafingerhut/clojure-benchmarks/tree/master">clojure-benchmarks</a> Andy Fingerhut&apos;s project for benchmarking programs to compare amongst
 other languages.</p></li><li><p><a href="https://clojure-goes-fast.com/">Clojure Goes Fast</a> A hub for news, docs, and tools related to Clojure and high
 performance.</p></li><li><p><a href="https://github.com/hugoduncan/criterium/">Criterium</a> Measures the computation time of an expression, addressing some of the
 pitfalls of benchmarking. Criterium provides the vital benchmarking engine of
 the Fastester library.</p></li><li><p><a href="https://github.com/openjdk/jmh">Java Microbenchmark Harness</a> (JMH) For building, running, and
 analyzing nano/micro/milli/macro benchmarks written in Java and other languages
 targeting the JVM.</p></li><li><p>Laurence Tratt&apos;s benchmarking essays.<br /><a href="https://tratt.net/laurie/blog/2019/minimum_times_tend_to_mislead_when_benchmarking.html"><em>Minimum times tend to mislead when benchmarking</em></a><br /><a href="https://soft-dev.org/pubs/html/barrett_bolz-tereick_killick_mount_tratt__virtual_machine_warmup_blows_hot_and_cold_v6/"><em>Virtual machine warmup blows hot and cold</em></a><br /><em>Why aren’t more users more happy with our VMs? </em><a href="https://tratt.net/laurie/blog/2018/why_arent_more_users_more_happy_with_our_vms_part_1.html">Part 1</a> <a href="https://tratt.net/laurie/blog/2018/why_arent_more_users_more_happy_with_our_vms_part_2.html">Part 2</a></p></li><li><p><a href="https://clojure-goes-fast.com/kb/benchmarking/time-plus/"><code>time+</code></a> A paste-and-go macro that measures an expression&apos;s evaluation time,
 useful for interactive development.</p></li></ul></section></section><section id="goals"><h2>Non-goals</h2><p>Fastester does not aspire to be:</p><ul><li><p><strong>A diagnostic profiler.</strong> Fastester will not <a href="https://github.com/clojure-goes-fast/clj-async-profiler">locate bottlenecks</a>. It is only intended to communicate performance changes when a new version
 behaves differently than a previous version. I.e., we&apos;ve already located the
 bottlenecks and made it quicker. Fastester performs a release task, not a
 dev-time task.</p></li><li><p><strong>A comparative profiler.</strong> Fastester doesn&apos;t address if <em>My Clojure function runs faster than that OCaml function</em>, and, in fact, isn&apos;t intended to demonstrate <em>My Clojure function runs faster than someone else&apos;s Clojure function.</em> Fastester focuses on comparing benchmark results of one particular
 function to a previous version of itself.</p></li><li><p><strong>A general-purpose charting facility.</strong> Apart from choosing
 whether a chart axis is linear or logarithmic, any other charting option like
 color, marker shape, etc., will not be adjustable.</p></li></ul></section><section id="examples"><h2>Examples</h2><p>Here is an <a href="https://github.io/blosavio/fastester/performance.html"> example</a> that simulates performance changes of a few <code>clojure.core</code> functions across several versions, and demonstrates many of Fastester&apos;s
 features.</p></section><section id="glossary"><h2>Glossary</h2><dl><dt id="benchmark">benchmark</dt><dd><p>An assembly of a 1-arity function expression, a sequence of
 arguments, a name, and a group. We <em>define</em> or <em>write</em> a benchmark. Criterium <em>runs</em> benchmarks.</p></dd></dl><dl><dt id="document">document</dt><dd><p>An <span class="small-caps">html</span> or markdown file that contains benchmarks results consisting of,
 charts, text, and tables.</p></dd></dl><dl><dt id="group">group</dt><dd><p>One or more conceptually-related benchmarks, e.g., all benchmarks
 that demonstrate the performance of <code>map</code>.</p></dd></dl><dl><dt id="name">name</dt><dd><p>A tag that refers to a benchmark definition. Currently a Clojure
 symbol which to which a value is associated in the registry. In a future
 version, a symbol which simply maps to a namespace var.</p></dd></dl><dl><dt id="registry">registry</dt><dd><p>Soon to be deprecated...</p></dd></dl><dl><dt id="version">version</dt><dd><p>A notable release of software, labeled by a version number.</p></dd></dl></section><br /><h2>License</h2><p><p>This program and the accompanying materials are made available under the terms of the <a href="https://opensource.org/license/MIT">MIT License</a>.</p></p><p id="page-footer">Copyright © 2024–2025 Brad Losavio.<br />Compiled by <a href="https://github.com/blosavio/readmoi">ReadMoi</a> on 2025 August 15.<span id="uuid"><br />a19c373d-6b51-428e-a99f-a8e89a37b60c</span></p>