
  <body>
    <a href="https://clojars.org/com.sagevisuals/fastester"><img src="https://img.shields.io/clojars/v/com.sagevisuals/fastester.svg"></a><br>
    <a href="#setup">Setup</a><br>
    <a href="https://blosavio.github.io/fastester/index.html">API</a><br>
    <a href="https://github.com/blosavio/fastester/blob/main/changelog.md">Changelog</a><br>
    <a href="#intro">Introduction</a><br>
    <a href="#ideas">Ideas</a><br>
    <a href="#usage">Usage</a><br>
    <a href="#limits">Limitations &amp; mitigations</a><br>
    <a href="#alternatives">Alternatives &amp; references</a><br>
    <a href="#goals">Non-goals</a><br>
    <a href="#examples">Examples</a><br>
    <a href="#glossary">Glossary</a><br>
    <a href="https://github.com/blosavio">Contact</a><br>
    <h1>
      Fastester
    </h1><em>A Clojure library for measuring and displaying performance changes</em><br>
    <section id="setup">
      <h2>
        Setup
      </h2>
      <h3>
        Leiningen/Boot
      </h3>
      <pre><code>[com.sagevisuals/fastester &quot;0-SNAPSHOT0&quot;]</code></pre>
      <h3>
        Clojure CLI/deps.edn
      </h3>
      <pre><code>com.sagevisuals/fastester {:mvn/version &quot;0-SNAPSHOT0&quot;}</code></pre>
      <h3>
        Require
      </h3>
      <pre><code>(require &apos;[fastester.measure :refer [defbench run-one-registered-benchmark]])</code></pre>
    </section>
    <section id="intro">
      <h2>
        Introduction
      </h2>
      <p>
        Imagine this: We notice that function <code>foo</code> of version&nbsp;11 of our library is sub-optimal. We improve the implementation &nbsp;so that
        <code>foo</code> executes faster.
      </p>
      <p>
        While writing up the changelog for version&nbsp;12, instead of mumbling,
      </p>
      <blockquote>
        <em>Function <code>foo</code> is faster.</em>
      </blockquote>
      <p>
        We show this.
      </p>
      <div>
        <img alt=
        "Chart of synthetic performance benchmark of function `foo`, &nbsp;comparing verions 4 and 5; version 5 demonstrates approximately 25% faster &nbsp;performance across a specific range of arguments."
        src="synthetic-benchmark-chart.svg">
        <table>
          <thead>
            <tr>
              <th colspan="7">
                arg, n
              </th>
            </tr>
            <tr>
              <th>
                version
              </th>
              <th>
                1
              </th>
              <th>
                10
              </th>
              <th>
                100
              </th>
              <th>
                1000
              </th>
              <th>
                10000
              </th>
              <th>
                100000
              </th>
            </tr>
          </thead>
          <tr>
            <td>
              11
            </td>
            <td>
              5.8±0.1
            </td>
            <td>
              7.4±0.1
            </td>
            <td>
              9.3±0.2
            </td>
            <td>
              11.4±0.2
            </td>
            <td>
              13.5±0.0
            </td>
            <td>
              15.2±0.2
            </td>
          </tr>
          <tr>
            <td>
              12
            </td>
            <td>
              4.4±0.1
            </td>
            <td>
              5.8±0.1
            </td>
            <td>
              6.9±0.2
            </td>
            <td>
              8.4±0.1
            </td>
            <td>
              10.2±0.2
            </td>
            <td>
              12.0±0.0
            </td>
          </tr>
          <tr>
            <th colspan="7">
              time in nanoseconds <em>(mean±std)</em>
            </th>
          </tr>
        </table>
      </div>
      <p>
        And state,
      </p>
      <blockquote>
        <em>Version&nbsp;12 of function <code>foo</code> is 20 to 30&nbsp;percent faster than version&nbsp;11 across this particular range of
        &nbsp;arguments.</em>
      </blockquote>
      <p>
        The Fastester library streamlines the tasks of writing benchmarks for a &nbsp;function, objectively measuring evaluation times of different versions of
        that &nbsp;function, and concisely communicating how performance changes between &nbsp;versions.
      </p>
    </section>
    <section id="ideas">
      <h2>
        Ideas
      </h2>
      <p>
        We ought to strive to write fast software. Fast software respects other &nbsp;people&apos;s time and their computing resources. Fast software
        demonstrates skill &nbsp;and attention to detail. And fast software is just plain cool.
      </p>
      <p>
        But, how fast is &quot;fast&quot;? It&apos;s not terribly convincing to say <em>Our software is fast.</em> We&apos;d like some objective measure of
        fast. Fortunately, the <a href="https://github.com/hugoduncan/criterium">Criterium library</a> provides a handy group of benchmarking utilities that
        measures the &nbsp;evaluation time of a Clojure expression. We could use Criterium to learn that <code>(foo 99)</code> requires 4.1&nbsp;nanoseconds to
        evaluate.
      </p>
      <p>
        Is…that good? Difficult to say. What we&apos;d really like to know is how &nbsp;4.1&nbsp;nanoseconds compares to some previous version. So if, for
        example, &nbsp;version&nbsp;1.12.1 of <code>foo</code> evaluated in 4.1&nbsp;nanoseconds, whereas version&nbsp;1.11.4 required
        &nbsp;5.4&nbsp;nanoseconds, we have good reason to believe the later implementation is &nbsp;faster.
      </p>
      <p>
        A performance report ought to be:
      </p>
      <ul>
        <li>
          <p>
            <strong>Objective</strong> Thanks to Criterium, we can objectively measure &nbsp;how long it takes to evaluate a function with a particular
            argument, &nbsp;independent of vagaries of the environment.
          </p>
        </li>
        <li>
          <p>
            <strong>Thorough</strong> Performance tests ought to fully exercise &nbsp;invocation patterns, e.g., all the relevant, performance-sensitive
            arities, and &nbsp;the arguments ought to span several orders of magnitude.
          </p>
        </li>
        <li>
          <p>
            <strong>Straighforward</strong> Once the benchmarks are written, it ought to &nbsp;be effortless to run the benchmarks for any subsequent version.
          </p>
        </li>
        <li>
          <p>
            <strong>Comprehensible</strong> A person ought to be able glance at the &nbsp;performance report and immediately grasp the improvements, with
            details &nbsp;available as needed.
          </p>
        </li>
      </ul>
      <h3>
        Objectively measure performance
      </h3>
      <p>
        Thorough data. No trust required. Can see how performance changes, under &nbsp;exactly what conditions. Also, what was *not* tested.
      </p>
      <h3>
        Communicate performance changes
      </h3>
      <p>
        Claiming a performance improvement requires objective measurement, wide &nbsp;range of inputs, invoking many arities/call patterns. I.e., `map` with
        &nbsp;different mapping functions applied to disparate types.
      </p>
      <p>
        Makes people less reluctant to use our software. Remove objections, etc. &nbsp;What exactly will happen when they switch from one version to another?
      </p>
      <p>
        Particularly if a version number is merely a label without semantics. &nbsp;what information is necessary to make an informed decision about if there
        is &nbsp;any benefit to change versions. the people using the software can decide &nbsp;whether it is worth switching.
      </p>
      <h3>
        A low threshold for breakage
      </h3>
      <p>
        Mindset: Performance regression is a <a href="">breaking change</a>.
      </p>
      <h3>
        Clear communication
      </h3>
      <p>
        A single source of performance information helps inform what will happen &nbsp;when switching versions. Must accurately communicate, and clearly and
        obvious.
      </p>
      <h3>
        <em>Et cetera</em>
      </h3>
      <ul>
        <li>
          <p>
            The performance document is accreting. Once version&nbsp;9 is benchmarked &nbsp;and released, that&apos;s it. Corrections are encouraged, and later
            additional tests &nbsp;to compare to some new feature are also okay. The data is versioned-controlled, &nbsp;and the <span class=
            "small-caps">html</span>/markdown documents that are generated &nbsp;from the data are also under version-control.
          </p>
        </li>
        <li>
          <p>
            The performance data is objective, but people may interpret it to &nbsp;suit their tastes. 4.1&nbsp;nanoseconds may be fast enough for one person,
            but not &nbsp;another. The accompanying commentary may express the library author&apos;s opinions. &nbsp;That&apos;s okay. The author is merely
            communicating that opinion to the person &nbsp;considering switching versions. The author may consider a particular version <em>fast</em>, but the
            person using the software may not.
          </p>
        </li>
      </ul>
    </section>
    <section id="usage">
      <h2>
        Usage
      </h2>
      <p>
        Prologue: We have previously profiled some path of our code and identified &nbsp;a bottleneck. We then changed the implementation and objectively
        verified that &nbsp;this new implementation provides shorter execution times than the previous &nbsp;version.
      </p>
      <p>
        After <a href="#setup">declaring and requiring</a> the dependency, there are four steps to using Fastester.
      </p>
      <ol>
        <li>
          <p>
            <a href="#set-options">Set</a> the options.
          </p>
        </li>
        <li>
          <p>
            <a href="#write-benchmarks">Write</a> benchmarks.
          </p>
        </li>
        <li>
          <p>
            <a href="#run-benchmarks">Run</a> the benchmarks.
          </p>
        </li>
        <li>
          <p>
            <a href="#generate">Generate</a> an <span class="small-caps">html</span> document that displays the performance data.
          </p>
        </li>
      </ol>
      <p>
        Steps&nbsp;1 and&nbsp;2 are done once, and only occasionally updated as needed. &nbsp;Steps&nbsp;3 and&nbsp;4 are done only when a function&apos;s
        implementation changes with &nbsp;affects on performance.
      </p>
      <h3 id="set-options">
        1. Set the options
      </h3>
      <p>
        We must first set the options that govern how Fastester behaves. Options &nbsp;live in the file <a href=
        "https://github.com/blosavio/fastester/blob/main/resources/fastester_options.edn"><code>resources/fastester_options.edn</code></a> as a Clojure map.
      </p>
      <p>
        The following options have <a href="https://blosavio.github.io/fastester.display.html#var-fastester-defaults">default values</a>.
      </p>
      <table>
        <tr>
          <th>
            key
          </th>
          <th>
            default
          </th>
          <th>
            usage
          </th>
        </tr>
        <tr>
          <td>
            <code>:benchmarks-directory</code>
          </td>
          <td>
            <code>&quot;test/fastester/performance/&quot;</code>
          </td>
          <td>
            <p>
              Directory from which to load benchmark tests.
            </p>
          </td>
        </tr>
        <tr>
          <td>
            <code>:benchmarks-filenames</code>
          </td>
          <td>
            <code>&quot;benchmarks.clj&quot;</code>
          </td>
          <td>
            <p>
              Filename from which to load benchmark tests.
            </p>
          </td>
        </tr>
        <tr>
          <td>
            <code>:html-directory</code>
          </td>
          <td>
            <code>&quot;doc/&quot;</code>
          </td>
          <td>
            <p>
              Directory to write html document.
            </p>
          </td>
        </tr>
        <tr>
          <td>
            <code>:html-filename</code>
          </td>
          <td>
            <code>&quot;performance.html&quot;</code>
          </td>
          <td>
            <p>
              Filename to write html document.
            </p>
          </td>
        </tr>
        <tr>
          <td>
            <code>:img-subdirectory</code>
          </td>
          <td>
            <code>&quot;img/&quot;</code>
          </td>
          <td>
            <p>
              Under <code>:html-directory</code>, directory to write svg image files.
            </p>
          </td>
        </tr>
        <tr>
          <td>
            <code>:markdown-directory</code>
          </td>
          <td>
            <code>&quot;doc/&quot;</code>
          </td>
          <td>
            <p>
              Directory to write markdown files.
            </p>
          </td>
        </tr>
        <tr>
          <td>
            <code>:markdown-filename</code>
          </td>
          <td>
            <code>&quot;performance.md&quot;</code>
          </td>
          <td>
            <p>
              Filename to write markdown document.
            </p>
          </td>
        </tr>
        <tr>
          <td>
            <code>:results-url</code>
          </td>
          <td>
            <code>&quot;https://example.com&quot;</code>
          </td>
          <td>
            <p>
              Base URL for where to find benchmark data. For local filesystem, use &nbsp;something like <code>&quot;../&quot;</code>.
            </p>
          </td>
        </tr>
        <tr>
          <td>
            <code>:results-directory</code>
          </td>
          <td>
            <code>&quot;resources/performance_entries/&quot;</code>
          </td>
          <td>
            <p>
              Directory to find benchmark data, appended to <code>:results-url</code>.
            </p>
          </td>
        </tr>
        <tr>
          <td>
            <code>:excludes</code>
          </td>
          <td>
            <code>#{}</code>
          </td>
          <td>
            <p>
              A set of benchmark groups (strings) to skip, i.e., <code>#{}</code> skips zero benchmark groups.
            </p>
          </td>
        </tr>
        <tr>
          <td>
            <code>:verbose?</code>
          </td>
          <td>
            <code>false</code>
          </td>
          <td>
            <p>
              A boolean that governs printing benchmark status.
            </p>
          </td>
        </tr>
        <tr>
          <td>
            <code>:testing-thoroughness</code>
          </td>
          <td>
            <code>:quick</code>
          </td>
          <td>
            <p>
              Assigns Criterium benchmark settings. One of <code>:default</code>, <code>:quick</code>, <code>:lightning</code>.
            </p>
          </td>
        </tr>
        <tr>
          <td>
            <code>:parallel?</code>
          </td>
          <td>
            <code>false</code>
          </td>
          <td>
            <div id="parallel?">
              <p>
                A boolean that governs running benchmarks on multiple threads in &nbsp;parallel.
              </p>
              <p>
                Warning: Running benchmarks in parallel results in undesirable, high &nbsp;variance. Associate to <code>true</code> only to quickly verify the
                value returned from evaluating an expression. &nbsp;Do not use for final benchmarking.
              </p>
            </div>
          </td>
        </tr>
        <tr>
          <td>
            <code>:n-threads</code>
          </td>
          <td>
            <code>1</code>
          </td>
          <td>
            <p>
              An integer that governs the number of threads to use when <code>:parallel</code> is <code>true</code>. Recommendation: Set to one fewer than
              machine core count. See warning &nbsp;for <a href="#parallel?"><code>:parallel?</code></a>.
            </p>
          </td>
        </tr>
        <tr>
          <td>
            <code>:save-benchmark-fn-results?</code>
          </td>
          <td>
            <code>true</code>
          </td>
          <td>
            <p>
              When assigned <code>true</code>, includes the value returned from evaluating the benchmark expression. &nbsp;Useful during development to check
              the correctness of the benchmark expression. &nbsp;However, file sizes grow unwieldy. Setting to <code>false</code> replaces the data with
              <code>:fastester/benchmark-fn-results-elided</code>
            </p>
          </td>
        </tr>
        <tr>
          <td>
            <code>:tidy-html?</code>
          </td>
          <td>
            <code>false</code>
          </td>
          <td>
            <p>
              Default setting causes html to be writen to file with no line breaks. &nbsp;If set to <code>true</code> line breaks are inserted for readability,
              and for better version control.
            </p>
          </td>
        </tr>
        <tr>
          <td>
            <code>:preamble</code>
          </td>
          <td>
            <code>[:div &quot;...&quot;]</code>
          </td>
          <td>
            <p>
              A hiccup/html block inserted at the top of the results document.
            </p>
          </td>
        </tr>
      </table>
      <p>
        The following options have no defaults.
      </p>
      <table>
        <tr>
          <th>
            key
          </th>
          <th>
            example
          </th>
          <th>
            usage
          </th>
        </tr>
        <tr>
          <td>
            <code>:responsible</code>
          </td>
          <td>
            <code>{:name &quot;Grace Hopper&quot;, :email &quot;RDML@univac.net&quot;}</code>
          </td>
          <td>
            <p>
              A hashmap with <code>:name</code> and <code>email</code> strings that report a person responsible for the report.
            </p>
          </td>
        </tr>
        <tr>
          <td>
            <code>:copyright-holder</code>
          </td>
          <td>
            <code>&quot;Abraham Lincoln&quot;</code>
          </td>
          <td>
            <p>
              Copyright holder listed in the footer of the document.
            </p>
          </td>
        </tr>
        <tr>
          <td>
            <code>:fastester-UUID</code>
          </td>
          <td>
            <code>de280faa-ebc5-4d75-978a-0a2b2dd8558b</code>
          </td>
          <td>
            <p>
              A version 4 Universally unique ID listed in the footer of the document. &nbsp;To generate a new UUID, evaluate <code>(random-uuid)</code>.
              Associate to <code>nil</code> to skip.
            </p>
          </td>
        </tr>
      </table>
      <h3 id="write-benchmarks">
        2. Write benchmarks
      </h3>
      <p>
        Writing benchmarks follows a similar pattern to writing unit tests. We &nbsp;make a file (defaulting to
        <code>test/fastester/performance/benchmarks.clj</code>) with a &nbsp;namespace declaration. See this <a href=
        "https://github.com/blosavio/fastester/blob/main/test/fastester/performance/benchmarks.clj">example</a>.
      </p>
      <p>
        Within the benchmarks file, we use <code>defbench</code> to <strong>def</strong>ine a <strong>bench</strong>mark. Here is its signature.
      </p>
      <pre><code>(defbench <em>name &quot;group&quot; fn-expression args</em>)</code></pre>
      <p>
        We supply <code>defbench</code> a name, an unquoted symbol. The benchmark name labels the three following &nbsp;arguments.
      </p>
      <p>
        After the name, we supply a <em>group</em>, a string that associates this benchmark with other &nbsp;conceptually-related benchmarks. For example, if
        we&apos;re benchmarking <code>map</code>, we will want to test its performance in different ways with different types &nbsp;of arguments. The ultimate
        html document will gather the benchmarks sharing a &nbsp;group.
      </p>
      <p>
        The real meat of defining a benchmark lies in the last two arguments. The &nbsp;function expression is a 1-arity function that demonstrates some
        performance &nbsp;aspect of the new version of the function. Let&apos;s pretend we updated <code>map</code> so that it processes elements faster. One
        way to test its performance is to &nbsp;increment sequences of integers with increasing lengths. That expression might &nbsp;look like this.
      </p>
      <pre><code>(fn [n] (map inc (range n)))</code></pre>
      <p>
        The <code>n</code> argument in the expression is supplied serially by the next part of the &nbsp;benchmark definition. Increasing the length by powers
        of ten explores the &nbsp;range of behaviors.
      </p>
      <pre><code>[10 100 1000 10000 100000]</code></pre>
      <p>
        Altogether, that benchmark definition looks like this.
      </p>
      <pre><code>(defbench map-inc &quot;faster map implementation&quot; (fn [n] (map inc (range n))) [10 100 1000 10000 100000])</code></pre>
      <p>
        However, there&apos;s an issue with this definition. This benchmark&apos;s function &nbsp;expression includes <code>range</code>. If we run this
        benchmark as is, the evaluation time would include <code>range</code>&apos;s processing time. We may want that in some cases, but in this example, it
        &nbsp;would be misleading. Let&apos;s move that part out.
      </p>
      <pre><code>(def range-of-length-n (reduce #(assoc %1 %2 (range %2)) {} [10 100 1000 10000 100000]))</code><br><br><code>(defbench map-inc &quot;faster map implementation&quot; (fn [n] (map inc (range-of-length-n))) [10 100 1000 10000 100000])</code></pre>
      <p>
        Perhaps you spotted a remaining problem. If we were to run these &nbsp;benchmarks, we&apos;d notice that the evaluation times were suspiciously
        consistent, &nbsp;no matter how many integers the sequence contained. <code>map</code> returns a lazy sequence. So we must force the return sequence to
        be &nbsp;realized so the benchmarking measures what we intend. <code>doall</code> is handy for that.
      </p>
      <pre><code>(defbench map-inc &quot;faster map implementation&quot; (fn [n] (doall (map inc (range-of-length-n)))) [10 100 1000 10000 100000])</code></pre>
      <h3 id="run-benchmarks">
        3. Run benchmarks
      </h3>
      <p>
        Bar!
      </p>
      <h3 id="generate">
        4. Generate the <span class="small-caps">html</span>
      </h3>
      <p>
        Baz!
      </p>
      <h3 id="gotchas"></h3>
      <p>
        We must be particularly careful to define our benchmarks to test what we &nbsp;intend to test. Writing a benchmark using some common Clojure idioms may
        mask &nbsp;the property we&apos;re interested in. For example, if we&apos;re interested in &nbsp;benchmarking mapping over a sequence, if we create the
        sequence in the map &nbsp;expression, Criterium will include that process in addition to the mapping. , such as defining sequences at the location,
        will measure
      </p>
      <p>
        We must be particularly careful to define our benchmarks to test what we &nbsp;intend to test. The first problematic pattern is a direct result of
        Clojure&apos;s &nbsp;inherent concision. It&apos;s idiomatic to compose a sequence right at the spot &nbsp;where we require it, like this.
      </p>
      <pre><code>(map inc (repeatedly 99 #(rand))</code></pre>
      <p>
        However, if we were to submit this expression to Criterium, intending to &nbsp;measure how long it takes <code>map</code> to increment the sequence,
        we&apos;d be <em>also</em> benchmarking creating the sequence, which may be a non-negligible portion of &nbsp;the evaluation time. Instead, we should
        hoist the sequence creation out of the &nbsp;expression.
      </p>
      <pre><code>;; *create* the sequence</code><br><code>(def ninety-nine-rands (repeatedly 99 #(rand))</code><br><br><code>;; *use* the pre-existing sequence</code><br><code>(map inc ninety-nine-rands)</code></pre>
      <p>
        The second expression now involves mostly the <code>map</code> action, and is more appropriate for benchmarking.
      </p>
      <p>
        But, there is another lurking problem! <code>map</code> (and friends) returns a lazy sequence, which is almost certainly not what we &nbsp;were
        intending to benchmark. We must remember to force the realization of the &nbsp;lazy sequence, conveniently done with <code>doall</code>.
      </p>
      <pre><code>(doall (map inc ninety-nine-rands))</code></pre>
      <p>
        One final <em>gotcha</em>, particular to Fastester itself, will be familiar to Clojurists programming at the <span class="small-caps">repl</span>.
        During development, it&apos;s typical to define and re-define benchmarks with <code>defbench</code>. It&apos;s not difficult for the namespace to hold
        stale benchmark definitions that are not apparent from visual inspection of the current state of the text in the file. Fastester provides a pair of
        tools to help with this. The <code>undefbench</code> utility undefines a benchmark by name. <code>clear-registry!</code> undefines all benchmarks, and
        a quick re-evaluation of the entire text buffer redefines only the benchmarks currently expressed in the file.
      </p>
    </section>
    <section id="limits">
      <h2>
        Limitations &amp; mitigations
      </h2>
      <p>
        Modern operating systems (OSes) and virtual machines (VMs) provide a perilous environment for accurate, reliable benchmarking. They both toss an
        uncountable number of non-deterministic confounders onto our laps. The OS may host other processes which contend for computing resources, interrupt for
        I/O or network events, etc. The Java VM may unpredicatably just-in-time (JIT) compile hot spots, making the code run faster (or slower!) after some
        unpredicatable delay, and the garbage collecter (GC) is quite skilled at messing with precise timing measurements.
      </p>
      <p>
        <strong>So we must exercise great care when running the benchmarks and be very conservative with our claims when reporting the benchmark
        results.</strong>
      </p>
      <p>
        Fastester delegates the benchmarking to Criterium, which fortunately goes to considerable effort to minimize this non-determinism. First, just before
        running the benchmark, Criterium forces the GC in order to minimize the chance of occuring during the benchmark itself. Furthermore, Criterium includes
        a warm-up period to give the JIT compiler an opportunity to optimize the benchmarked code so that the evaluation time are more consistent.
      </p>
      <p>
        To try to control for other non-determinism, we run each benchmark multiple times (default 60), and calculate statistics on those results, which helps
        suggest whether or not our benchmark data is consistent.
      </p>
      <p>
        Fastester, following Criterium&apos;s lead, focuses on the mean (average) evaluation time, not the minimum. This policy is intended to avoid
        over-emphasizing edge cases that coincidentally perform well and giving a more unbiased view.
      </p>
      <p>
        If our new implementation is only a few percent &apos;faster&apos; than the old version, we ought to consider very carefully whether it is worth
        changing the the implementation which may or may not be an improvement.
      </p>
    </section>
    <section id="alternatives">
      <section id="alternatives">
        <h2>
          Alternatives &amp; References
        </h2>
        <ul>
          <li>
            <p>
              <a href="https://github.com/clojure-goes-fast/clj-async-profiler">clj-async-profiler</a> A single-dependency, embedded high-precision performance
              profiler.
            </p>
          </li>
          <li>
            <p>
              <a href="https://github.com/jafingerhut/clojure-benchmarks/tree/master">clojure-benchmarks</a> Andy Fingerhut&apos;s project for benchmarking
              programs to compare amongst other languages.
            </p>
          </li>
          <li>
            <p>
              <a href="https://clojure-goes-fast.com/">Clojure Goes Fast</a> A hub for news, docs, and tools related to Clojure and high performance.
            </p>
          </li>
          <li>
            <p>
              <a href="https://github.com/hugoduncan/criterium/">Criterium</a> Measures the computation time of an expression, addressing some of the pitfalls
              of benchmarking. Criterium provides the vital benchmarking engine of the Fastester library.
            </p>
          </li>
          <li>
            <p>
              <a href="https://github.com/openjdk/jmh">Java Microbenchmark Harness</a> (JMH) For building, running, and analysing nano/micro/milli/macro
              benchmarks written in Java and other languages targeting the JVM.
            </p>
          </li>
          <li>
            <p>
              Laurence Tratt&apos;s benchmarking essays.<br>
              <a href="https://tratt.net/laurie/blog/2019/minimum_times_tend_to_mislead_when_benchmarking.html"><em>Minimum times tend to mislead when
              benchmarking</em></a><br>
              <a href="https://soft-dev.org/pubs/html/barrett_bolz-tereick_killick_mount_tratt__virtual_machine_warmup_blows_hot_and_cold_v6/"><em>Virtual
              machine warmup blows hot and cold</em></a><br>
              <em>Why aren’t more users more happy with our VMs?</em> <a href=
              "https://tratt.net/laurie/blog/2018/why_arent_more_users_more_happy_with_our_vms_part_1.html">Part 1</a> <a href=
              "https://tratt.net/laurie/blog/2018/why_arent_more_users_more_happy_with_our_vms_part_2.html">Part 2</a>
            </p>
          </li>
          <li>
            <p>
              <a href="https://clojure-goes-fast.com/kb/benchmarking/time-plus/"><code>time+</code></a> A paste-and-go macro that measures an expression&apos;s
              evaluation time, useful for interactive development.
            </p>
          </li>
        </ul>
      </section>
    </section>
    <section id="goals">
      <h2>
        Non-goals
      </h2>
      <p>
        Fastester does not aspire to be:
      </p>
      <ul>
        <li>
          <p>
            <strong>A diagnostic profiler.</strong> Fastester will not <a href="https://github.com/clojure-goes-fast/clj-async-profiler">locate
            bottlenecks</a>. It is only intended to communicate performance changes when a new version behaves differently than a previous version. I.e.,
            we&apos;ve already located the bottlenecks and made it quicker. Fastester is a release task, not a dev-time tool.
          </p>
        </li>
        <li>
          <p>
            <strong>A comparative profiler.</strong> Fastester doesn&apos;t address if <em>My Clojure function runs faster than that OCaml function</em>, and,
            in fact, isn&apos;t intended to demonstrate <em>My Clojure function runs faster than someone else&apos;s Clojure function.</em> Fastester focuses
            on comparing benchmark results of one particular function to a previous version of itself.
          </p>
        </li>
        <li>
          <p>
            <strong>A general-purpose charting facility.</strong> Apart from choosing whether a chart axis is linear or logarithmic, any other charting option
            like color, marker shape, etc., will not be adjustable.
          </p>
        </li>
      </ul>
    </section>
    <section id="examples">
      <h2>
        Examples
      </h2>
      <p>
        Here is an <a href="https://github.io/blosavio/fastester/performance.html">example</a> that simulates performance changes of a few
        <code>clojure.core</code> functions across several versions, and demonstrates many of Fastester&apos;s features.
      </p>
    </section>
    <section id="glossary">
      <h2>
        Glossary
      </h2>
      <dl>
        <dt id="benchmark">
          benchmark
        </dt>
        <dd>
          <p>
            (noun) def 1
          </p>
        </dd>
      </dl>
      <dl>
        <dt id="document">
          document
        </dt>
        <dd>
          <p>
            (noun) def 2
          </p>
        </dd>
      </dl>
      <dl>
        <dt id="group">
          group
        </dt>
        <dd>
          <p>
            (noun) def 3
          </p>
        </dd>
      </dl>
      <dl>
        <dt id="name">
          name
        </dt>
        <dd>
          <p>
            def 4
          </p>
        </dd>
      </dl>
      <dl>
        <dt id="version">
          version
        </dt>
        <dd>
          <p>
            A notable release of software, labeled by a version number.
          </p>
        </dd>
      </dl>
    </section><br>
    <h2>
      License
    </h2>
    <p></p>
    <p>
      This program and the accompanying materials are made available under the terms of the <a href="https://opensource.org/license/MIT">MIT License</a>.
    </p>
    <p></p>
    <p id="page-footer">
      Copyright © 2024–2025 Brad Losavio.<br>
      Compiled by <a href="https://github.com/blosavio/readmoi">ReadMoi</a> on 2025 August 12.<span id="uuid"><br>
      a19c373d-6b51-428e-a99f-a8e89a37b60c</span>
    </p>
  </body>
</html>
