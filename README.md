
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
      <pre><code>[com.sagevisuals/fastester &quot;0-SNAPSHOT4&quot;]</code></pre>
      <h3>
        Clojure CLI/deps.edn
      </h3>
      <pre><code>com.sagevisuals/fastester {:mvn/version &quot;0-SNAPSHOT4&quot;}</code></pre>
      <h3>
        Require
      </h3>
      <pre><code>(require &apos;[fastester.define :refer [defbench]]
&nbsp;        &apos;[fastester.display :refer [generate-documents]]
&nbsp;        &apos;[fastester.measure :refer [run-benchmarks]])
</code></pre>
    </section>
    <section id="intro">
      <h2>
        Introduction
      </h2>
      <p>
        Imagine: We notice that the <code>zap</code> function of version&nbsp;11 of our library is sub-optimal. We improve the &nbsp;implementation so that
        <code>zap</code> executes faster.
      </p>
      <p>
        In the version&nbsp;12 changelog, we could mumble,
      </p>
      <blockquote>
        <em>Function <code>zap</code> is faster.</em>
      </blockquote>
      <p>
        Or instead, we could assert,
      </p>
      <blockquote>
        <em>Version&nbsp;12 of function <code>zap</code> is 20 to 30&nbsp;percent faster than version&nbsp;11 for integers spanning five &nbsp;orders of
        magnitude. This implementation change will improve performance for &nbsp;the vast majority of intended use cases.</em>
      </blockquote><img alt=
      "Chart of synthetic performance benchmark of function `zap`, &nbsp; comparing versions 11 and 12; version 12 demonstrates &nbsp; approximately 25% faster performance across a specific range of &nbsp; arguments."
      src="doc/zap_img/group-0-fexpr-0.svg">&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;
      &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;
      <table>
        <caption>
          &nbsp; times in seconds, <em>mean±std</em> &nbsp;
        </caption>
        <thead>
          <tr>
            <td></td>
            <th colspan="5">
              &nbsp; arg, n &nbsp;
            </th>
          </tr>
          <tr>
            <th>
              &nbsp; version &nbsp;
            </th>
            <th>
              &nbsp; 1 &nbsp;
            </th>
            <th>
              &nbsp; 10 &nbsp;
            </th>
            <th>
              &nbsp; 100 &nbsp;
            </th>
            <th>
              &nbsp; 1000 &nbsp;
            </th>
            <th>
              &nbsp; 10000 &nbsp;
            </th>
          </tr>
        </thead>
        <tr>
          <td>
            &nbsp; 12 &nbsp;
          </td>
          <td>
            &nbsp; <a href="https://github.com/blosavio/fastester/blob/main/resources/zap_performance/version 12/test-5.edn">1.8e-04±1.5e-06</a> &nbsp;
          </td>
          <td>
            &nbsp; <a href="https://github.com/blosavio/fastester/blob/main/resources/zap_performance/version 12/test-6.edn">1.8e-03±3.8e-05</a> &nbsp;
          </td>
          <td>
            &nbsp; <a href="https://github.com/blosavio/fastester/blob/main/resources/zap_performance/version 12/test-7.edn">1.9e-02±4.6e-04</a> &nbsp;
          </td>
          <td>
            &nbsp; <a href="https://github.com/blosavio/fastester/blob/main/resources/zap_performance/version 12/test-8.edn">1.8e-01±1.4e-03</a> &nbsp;
          </td>
          <td>
            &nbsp; <a href="https://github.com/blosavio/fastester/blob/main/resources/zap_performance/version 12/test-9.edn">1.8e+00±1.9e-02</a> &nbsp;
          </td>
        </tr>
        <tr>
          <td>
            &nbsp; 11 &nbsp;
          </td>
          <td>
            &nbsp; <a href="https://github.com/blosavio/fastester/blob/main/resources/zap_performance/version 11/test-5.edn">2.6e-04±5.9e-06</a> &nbsp;
          </td>
          <td>
            &nbsp; <a href="https://github.com/blosavio/fastester/blob/main/resources/zap_performance/version 11/test-6.edn">2.7e-03±1.6e-04</a> &nbsp;
          </td>
          <td>
            &nbsp; <a href="https://github.com/blosavio/fastester/blob/main/resources/zap_performance/version 11/test-7.edn">2.6e-02±9.1e-04</a> &nbsp;
          </td>
          <td>
            &nbsp; <a href="https://github.com/blosavio/fastester/blob/main/resources/zap_performance/version 11/test-8.edn">2.7e-01±7.0e-03</a> &nbsp;
          </td>
          <td>
            &nbsp; <a href="https://github.com/blosavio/fastester/blob/main/resources/zap_performance/version 11/test-9.edn">2.6e+00±2.2e-02</a> &nbsp;
          </td>
        </tr>
      </table>
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
        measures the &nbsp;evaluation time of a Clojure expression. We could use Criterium to learn that <code>(zap inc [1 2 3])</code> requires
        183±2&nbsp;microseconds to evaluate.
      </p>
      <p>
        Is…that good? Difficult to say. What we&apos;d really like to know is how &nbsp;183&nbsp;microseconds compares to some previous version. So if, for
        example, &nbsp;version&nbsp;12 of <code>zap</code> evaluates in 183&nbsp;microseconds, whereas version&nbsp;11 required &nbsp;264&nbsp;microseconds, we
        have good reason to believe the later implementation is &nbsp;faster.
      </p>
      <p>
        Another problem is that tossing out raw numbers like &quot;183&quot; and &quot;264&quot; &nbsp;requires people to perform mental arithmetic to figure
        out if version&nbsp;12 is &nbsp;better. <em>One-hundred, eighty-three divided by two-hundred, sixty-four is &nbsp;approximately eighteen divided by
        twenty-six, which is approximately…</em> Not ideal.
      </p>
      <p>
        To address these problems, a Fastester aspires to generate an objective, &nbsp;relative, and comprehensible performance report.
      </p>
      <h3 id="objective">
        Objective
      </h3>
      <p>
        Thanks to Criterium, we can measure, in concrete, real world time units, &nbsp;how long it takes to evaluate a function with a particular argument,
        (somewhat) &nbsp;independent of vagaries of the computing environment.
      </p>
      <h3 id="comparative">
        Relative
      </h3>
      <p>
        A single, isolated timing measurement doesn&apos;t mean much to a person, even &nbsp;if it is <a href="#objective">objective</a>. People simply
        don&apos;t have everyday intuition for an event that occurs in a &nbsp;few nanoseconds or microseconds. So when we discuss the concept of
        &apos;fast&apos;, &nbsp;we&apos;re often implicitly speaking in relative terms.
      </p>
      <p>
        Fastester focuses on comparing the speed of one function to a previous &nbsp;version of itself.
      </p>
      <h3 id="comprehensible">
        Comprehensible
      </h3>
      <p>
        Humans are visually-oriented, and a straightforward two-dimensional chart &nbsp;is an excellent tool to convey relative performance changes between
        versions. &nbsp;A person ought to be able to glance at the performance report and immediately &nbsp;grasp the improvements, with details available as
        needed.
      </p>
      <p>
        Fastester documents consist primarily of charts with accompanying text. &nbsp;A show/hide button reveals details as the reader desires.
      </p>
      <h3>
        <em>Et cetera</em>
      </h3>
      <ul>
        <li>
          <p>
            The performance document is accreting. Once version&nbsp;12 is &nbsp;benchmarked and released, it&apos;s there for good. Corrections are
            encouraged, and &nbsp;later additional tests to compare to some new feature are also okay. The data &nbsp;is versioned-controlled, and the
            <span class="small-caps">html</span>/markdown documents that are generated from the data are also under &nbsp;version-control.
          </p>
        </li>
        <li>
          <p>
            The performance data is objective, but people may interpret it to &nbsp;suit their tastes. 183&nbsp;microseconds may be fast enough for one person,
            but not &nbsp;another. The accompanying commentary may express the library author&apos;s opinions. &nbsp;That&apos;s okay. The author is merely
            communicating that opinion to the person &nbsp;considering switching versions. The author may consider a particular version <em>fast</em>, but the
            person using the software may not.
          </p>
        </li>
        <li>
          <p>
            We should probably consider a performance regression as a <em>breaking change</em>. Fastester can help estimate and communicate how much the
            performance &nbsp;regressed.
          </p>
        </li>
      </ul>
    </section>
    <section id="usage">
      <h2>
        Usage
      </h2>
      <p>
        Let&apos;s review our imaginary scenario. We have previously profiled some &nbsp;execution path of version&nbsp;11 of our library. We discovered a
        bottleneck in a &nbsp;function, <code>zap</code>, which just so happens to behave exactly like <code>clojure.core/map</code>: apply some function to
        every element of a collection.
      </p>
      <pre><code>(zap inc [1 2 3]) ;; =&gt; (2 3 4)</code></pre>
      <p>
        We then changed <code>zap</code>&apos;s implementation for better performance and objectively verified that this &nbsp;new implementation for
        version&nbsp;12 provides shorter execution times than the &nbsp;previous version.
      </p>
      <p>
        We&apos;re now ready to release version&nbsp;12 with the updated <code>zap</code>. When we&apos;re writing the changelog/release notes, we want to
        include a &nbsp;performance report that demonstrates the improvement. After <a href="#setup">declaring and requiring</a> the dependency, there are four
        steps to using Fastester.
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
        Keep in mind we don&apos;t need to do these steps for every function for every &nbsp;release, but only when a function&apos;s implementation changes
        with measurable &nbsp;affects on performance.
      </p>
      <p>
        Follow along with this <a href="https://github.com/blosavio/fastester/blob/main/resources/zap_options.edn">example options file</a> and this <a href=
        "https://github.com/blosavio/fastester/blob/main/test/zap/benchmarks.clj">example benchmark definition file</a>.
      </p>
      <h3 id="set-options">
        1. Set the options
      </h3>
      <p>
        We must first set the options that govern how Fastester behaves. Options &nbsp;live in a file (defaulting to
        <code>resources/fastester_options.edn</code>) as a Clojure map. One way to get up and running quickly is to copy-paste &nbsp;a <a href=
        "https://github.com/blosavio/fastester/blob/main/resources/zap_options.edn">sample options file</a> and edit as needed.
      </p>
      <p>
        The following options have <a href="https://blosavio.github.io/fastester.options.html#var-fastester-defaults">default values</a>.
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
            <code>:benchmarks</code>
          </td>
          <td>
            <code>{}</code>
          </td>
          <td>
            <div>
              <p>
                Hashmap arranging a hierarchy of namespaces and benchmark &nbsp;definitions. Keys (quoted symbols) represent namespaces. Values (sets of quoted
                &nbsp;symbols) represent benchmark names. See <a href="#hierarchy">discussion</a>.
              </p>
              <p>
                Note: This setting only affects running benchmarks. It does not affect &nbsp;which data sets are used to generate the <span class=
                "small-caps">html</span> documents.
              </p>
            </div>
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
              Under <code>:html-directory</code>, directory to write svg image files. If a project&apos;s benchmark definitions &nbsp;are split among multiple
              files/namespaces, be sure to give each file/namespace &nbsp;their own dedicated image subdirectory.
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
              Directory to find benchmark data, appended to <code>:results-url</code>. Note: Every <code>edn</code> file in this directory will be used to
              create a datum in the document. Errant data files will produce confusing entries in charts and tables.
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
              Assigns Criterium benchmark settings. One of <code><a href=
              "https://github.com/hugoduncan/criterium/blob/bb10582ded6de31b4b985dc31d501db604c0e461/src/criterium/core.clj#L83">:default</a></code>,
              <code><a href=
              "https://github.com/hugoduncan/criterium/blob/bb10582ded6de31b4b985dc31d501db604c0e461/src/criterium/core.clj#L92">:quick</a></code>,
              <code><a href=
              "https://github.com/blosavio/fastester/blob/d1fccf5e3acfb056ecd9d2e775d62d91b55b04c8/src/fastester/measure.clj#L61">:lightning</a></code>.
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
            <code>:save-benchmark-fn-results?</code>
          </td>
          <td>
            <code>true</code>
          </td>
          <td>
            <p>
              When assigned <code>true</code>, the results file will include the value returned from evaluating the &nbsp;benchmark expression. Useful during
              development to check the correctness of the &nbsp;benchmark expression. However, file sizes grow unwieldy. Setting to <code>false</code> replaces
              the data with <code>:fastester/benchmark-fn-results-elided</code>.
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
              Default setting causes <span class="small-caps">html</span> to be written to file with no line breaks. If set to <code>true</code>, line breaks
              are inserted for readability, and for smaller version &nbsp;control diffs.
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
              A hiccup/<span class="small-caps">html</span> block inserted at the top of the results document.
            </p>
          </td>
        </tr>
        <tr>
          <td>
            <code>:sort-comparator</code>
          </td>
          <td>
            <code>clojure.core/compare</code>
          </td>
          <td>
            <p>
              Comparator used for sorting versions in the performance document chart &nbsp;legends and table rows. Comparator must accept two strings
              representing &nbsp;version entries extracted from either a Leiningen &apos;project.clj&apos; or a &nbsp;&apos;pom.xml&apos;. <a href=
              "https://clojure.org/guides/comparators#_mistakes_to_avoid">Write custom comparators with caution</a>.
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
            <code>:title</code>
          </td>
          <td>
            <code>&quot;Taffy Yo-yo Library performance&quot;</code>
          </td>
          <td>
            <p>
              A string providing the title for the performance document.
            </p>
          </td>
        </tr>
        <tr>
          <td>
            <code>:responsible</code>
          </td>
          <td>
            <code>{:name &quot;Grace Hopper&quot;, :email &quot;univac@example.com&quot;}</code>
          </td>
          <td>
            <p>
              A hashmap with <code>:name</code> and <code>:email</code> strings that report a person responsible for the report.
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
              A version&nbsp;4 Universally Unique&nbsp;ID listed in the footer of the document. &nbsp;To generate a new UUID, evaluate
              <code>(random‑uuid)</code>. Associate to <code>nil</code> to skip.
            </p>
          </td>
        </tr>
        <tr>
          <td>
            <code>:preferred-version-info</code>
          </td>
          <td>
            <code>:pom-xml</code>
          </td>
          <td>
            <p>
              Declares preference for source of project version. If <code>:lein</code>, consults &apos;project.clj&apos;. If <code>:pom‑xml</code>, consults
              &apos;pom.xml&apos;. If both files exist, a preference must be declared. &nbsp;If only one file exists, <code>:preferred‑version‑info</code> may
              be omitted.
            </p>
          </td>
        </tr>
      </table>
      <h3 id="write-benchmarks">
        2. Write benchmarks
      </h3>
      <p>
        Before we start bashing the keyboard, let&apos;s think a little about how we &nbsp;want to test <code>zap</code>. We&apos;d like to demonstrate
        <code>zap</code>&apos;s improved performance for a variety of argument types, over a wide range of &nbsp;argument sizes. To do that, we&apos;ll write
        two benchmarks.
      </p>
      <ol>
        <li>
          <p>
            The first benchmark will measure the evaluation times of incrementing &nbsp;increasingly-lengthy sequences of integers.
          </p>
          <pre><code>(benchmark (zap inc [1]))</code><br><code>(benchmark (zap inc [1 2]))</code><br><code>(benchmark (zap inc [1 2 3]))</code><br><code>(benchmark (zap inc [1 2 3 ...]))</code></pre>
          <p>
            We&apos;ll label this series of benchmark runs with the name <code>zap-inc</code> .
          </p>
        </li>
        <li>
          <p>
            The second benchmark will measure the evaluation times of upper-casing &nbsp;ever-longer sequences of strings.
          </p>
          <pre><code>(benchmark (zap str/uppercase [&quot;a&quot;]))</code><br><code>(benchmark (zap str/uppercase [&quot;a&quot; &quot;b&quot;]))</code><br><code>(benchmark (zap str/uppercase [&quot;a&quot; &quot;b&quot; &quot;c&quot;]))</code><br><code>(benchmark (zap str/uppercase [&quot;a&quot; &quot;b&quot; &quot;c&quot; ...]))</code><br></pre>
          <p>
            We&apos;ll label this series of benchmark runs with the name <code>zap-uc</code>.
          </p>
        </li>
      </ol>
      <p>
        Writing benchmarks follows a similar pattern to writing unit tests. We &nbsp;create a file, perhaps named <a href=
        "https://github.com/blosavio/fastester/blob/main/test/zap/benchmarks.clj">benchmarks.clj</a>, topped with a namespace declaration. For organizing
        purposes, we may write &nbsp;more than one benchmarks file if, for example, we&apos;d like to write one benchmark &nbsp;file per source namespace.
      </p>
      <p>
        Within our benchmarks file, we use <code>defbench</code> to <strong>def</strong>ine a <strong>bench</strong>mark. Here is its signature.
      </p>
      <pre><code>(defbench <em>name &quot;group&quot; fn-expression args</em>)</code></pre>
      <p>
        For the first argument, we supply <code>defbench</code> with a <em>name</em>, an unquoted symbol. The name resolves the benchmark definition in a
        &nbsp;namespace. We&apos;ve chosen <code>zap-inc</code> and <code>zap-uc</code>. The names don&apos;t have any functional significance. We could have
        named the &nbsp;benchmarks <code>Romeo</code> and <code>Juliet</code> without affecting the measurements, but like any other Clojure symbol, it&apos;s
        &nbsp;nice if the names have some semantic meaning.
      </p>
      <p>
        So far, we have the following two incomplete benchmark definitions: <code>defbench</code> followed by a name.
      </p>
      <pre><code>(defbench zap-inc ...)</code><br><code>(defbench zap-uc ...)</code></pre>
      <p>
        When we evaluate a <code>defbench</code> expression, Fastester binds a hashmap to the <em>name</em> in the namespace where we evaluated the expression.
        If two expressions use &nbsp;the same name in the same namespace, the later-evaluated definition will &nbsp;overwrite the earlier. If we&apos;d like to
        give the same name to two different &nbsp;benchmarks, we could isolate the definitions into two different namespaces. For &nbsp;this demonstration
        benchmarking <code>zap</code>, we&apos;ve chosen two different names, so we won&apos;t worry about overwriting.
      </p>
      <p>
        After the name, we supply a <em>group</em>, a string that associates one benchmark with other &nbsp;conceptually-related benchmarks. Later, while
        generating the <span class="small-caps">html</span> results document, Fastester will aggregate benchmarks sharing a group. For <code>zap</code>, we
        have our two related benchmarks. Let&apos;s assign both of those benchmarks &nbsp;to the <code>&quot;faster zap implementation&quot;</code> group.
      </p>
      <p>
        Now, we have the following two incomplete benchmark definitions, with the &nbsp;addition of the group.
      </p>
      <pre><code>(defbench zap-inc &quot;faster zap implementation&quot; ...)</code><br><code>(defbench zap-uc &quot;faster zap implementation&quot; ...)</code></pre>
      <p>
        The final two arguments, <em>fn-expression</em> and <em>args</em>, do the heavy lifting. The next step of the workflow, <a href=
        "#run-benchmarks"><em>running the benchmarks</em></a>, involves serially supplying elements of <code>args</code> to the function expression.
      </p>
      <p>
        The function expression is a 1-arity function that demonstrates some &nbsp;performance aspect of the new version of the function. We updated
        <code>zap</code> so that it processes elements faster. One way to demonstrate its improved &nbsp;performance is to increment a sequence of integers
        with <code>inc</code>. That particular function expression looks like this.
      </p>
      <pre><code>(fn [n] (zap inc (range n)))</code></pre>
      <p>
        In addition to incrementing integers, we wanted to demonstrate &nbsp;upper-casing strings. Clojure&apos;s <code>clojure.string/upper-case</code>
        &nbsp;performs that operation on a single string.
      </p>
      <pre><code>(require &apos;[clojure.string :as str])</code></pre>
      <p>
        To create sequence of strings, we can use <code>cycle</code>, and <code>take</code> the number of elements we desire.
      </p>
      <pre><code>(take 1 (cycle [&quot;a&quot; &quot;b&quot; &quot;c&quot;])) ;; =&gt; (&quot;a&quot;)</code><br><code>(take 2 (cycle [&quot;a&quot; &quot;b&quot; &quot;c&quot;])) ;; =&gt; (&quot;a&quot; &quot;b&quot;)</code><br><code>(take 3 (cycle [&quot;a&quot; &quot;b&quot; &quot;c&quot;])) ;; =&gt; (&quot;a&quot; &quot;b&quot; &quot;c&quot;)</code></pre>
      <p>
        Our second function expression looks like this.
      </p>
      <pre><code>(fn [i] (zap str/upper-case (take i (cycle [&quot;a&quot; &quot;b&quot; &quot;c&quot;]))))</code></pre>
      <p>
        And with the addition of their respective function expressions, our two &nbsp;almost-complete benchmark definitions look like this.
      </p>
      <pre><code>(defbench zap-inc &quot;faster zap implementation&quot; (fn [n] (zap inc (range n))) ...)</code><br><br><code>(defbench zap-uc &quot;faster zap implementation&quot; (fn [i] (zap str/upper-case (take i (cycle [&quot;a&quot; &quot;b&quot; &quot;c&quot;])))) ...)</code></pre>
      <p>
        Note that there is nothing special about the function expression&apos;s &nbsp;parameter. <code>zap-inc</code> uses <code>n</code>, while
        <code>zap-uc</code> uses <code>i</code>.
      </p>
      <p>
        &apos;Running&apos; a benchmark with those function expressions means that arguments &nbsp;are serially passed to the expression, measuring the
        evaluation times for each. &nbsp;The arguments are supplied by the final component of the benchmark definition, &nbsp;a sequence. For
        <code>zap-inc</code>, let&apos;s explore <code>range</code>s from ten to one-hundred thousand.
      </p>
      <p>
        An <em>args</em> sequence of five integers like this…
      </p>
      <pre><code>[10 100 1000 10000 100000]</code></pre>
      <p>
        …declares a series of five maximum values, producing the following series &nbsp;of five sequences to feed to <code>zap</code> for benchmarking.
      </p>
      <pre><code>[0 ... 9]</code><br><code>[0 ... 99]</code><br><code>[0 ... 999]</code><br><code>[0 ... 9999]</code><br><code>[0 ... 99999]</code></pre>
      <p>
        Ratcheting <code>(range n)</code> by powers of ten stresses <code>zap&apos;s</code> performance. Roughly speaking, we&apos;ll be doing this.
      </p>
      <pre><code>(benchmark (zap inc (range 10)))</code><br><code>(benchmark (zap inc (range 100)))</code><br><code>(benchmark (zap inc (range 1000)))</code><br><code>(benchmark (zap inc (range 10000)))</code><br><code>(benchmark (zap inc (range 100000)))</code></pre>
      <p>
        Altogether, that benchmark definition looks like this.
      </p>
      <pre><code>(defbench zap-inc
&nbsp;         &quot;faster zap implementation&quot;
&nbsp;         (fn [n] (zap inc (range n)))
&nbsp;         [10 100 1000 10000 100000])</code></pre>
      <p>
        Similarly, we&apos;d like <code>zap-uc</code> to exercise a span of strings.
      </p>
      <pre><code>(benchmark (zap str/upper-case (take 10 (cycle &quot;a&quot; &quot;b&quot; &quot;c&quot;))))</code><br><code>(benchmark (zap str/upper-case (take 100 (cycle &quot;a&quot; &quot;b&quot; &quot;c&quot;))))</code><br><code>(benchmark (zap str/upper-case (take 1000 (cycle &quot;a&quot; &quot;b&quot; &quot;c&quot;))))</code><br><code>(benchmark (zap str/upper-case (take 10000 (cycle &quot;a&quot; &quot;b&quot; &quot;c&quot;))))</code><br><code>(benchmark (zap str/upper-case (take 100000 (cycle &quot;a&quot; &quot;b&quot; &quot;c&quot;))))</code><br></pre>
      <p>
        That completed benchmark definition looks like this.
      </p>
      <pre><code>(defbench zap-uc
&nbsp;         &quot;faster zap implementation&quot;
&nbsp;         (fn [i] (zap str/upper-case (take i (cycle [&quot;a&quot; &quot;b&quot; &quot;c&quot;]))))
&nbsp;         [10 100 1000 10000 100000])</code></pre>
      <p>
        However, there&apos;s a problem. The function expressions contain <code>range</code> and <code>cycle</code>. If we run these benchmarks as is, the
        evaluation times would include <code>range</code>&apos;s and <code>cycle</code>&apos;s processing times. We might want to do that in some other
        scenario, but in &nbsp;this case, it would be misleading. We want to focus solely on how fast <code>zap</code> can process its elements. Let&apos;s
        extract <code>range</code> to an external expression.
      </p>
      <pre><code>(def range-of-length-n (reduce #(assoc %1 %2 (range %2)) {} [10 100 1000 10000 100000]))</code><br><br><br><code>(defbench zap-inc
&nbsp;         &quot;faster zap implementation&quot;
&nbsp;         (fn [n] (zap inc (range-of-length-n n)))
&nbsp;         [10 100 1000 10000 100000])</code></pre>
      <p>
        <code>range-of-length-n</code> generates all the sequences ahead of time. With the sequences now created &nbsp;outside of the benchmark expression, the
        time measurement will mainly reflect &nbsp;the work done by <code>zap</code> itself.
      </p>
      <p>
        If you extrapolated that <code>zap</code> behaves like <code>map</code>, perhaps you anticipated a remaining problem. If we were to run the
        <code>zap-inc</code> benchmarks as defined above, we&apos;d notice that the evaluation times were &nbsp;suspiciously consistent, no matter how many
        integers the sequence contained. <code>zap</code>, like many core sequence functions, returns a lazy sequence. We must force &nbsp;the return sequence
        to be realized so that <code>zap-inc</code> measures <code>zap</code> actually doing work. <code>doall</code> is handy for that.
      </p>
      <pre><code>(defbench zap-inc
&nbsp;         &quot;faster zap implementation&quot;
&nbsp;         (fn [n] (doall (zap inc (range-of-length-n n))))
&nbsp;         [10 100 1000 10000 100000])</code></pre>
      <p>
        We handle <code>zap-uc</code> similarly. First, we&apos;ll pre-compute the test sequences so that running the benchmark doesn&apos;t measure
        <code>cycle</code>. Then we&apos;ll wrap the <code>zap</code> expression in a <code>doall</code>.
      </p>
      <pre><code>(def abc-cycle-of-length-n
&nbsp;    (reduce #(assoc %1 %2 (take %2 (cycle [&quot;a&quot; &quot;b&quot; &quot;c&quot;])))
&nbsp;            {}
&nbsp;            [10 100 1000 10000 100000]))</code><br><br><br><code>(defbench zap-uc
&nbsp;         &quot;faster zap implementation&quot;
&nbsp;         (fn [n] (doall (zap str/upper-case (abc-cycle-of-length-n n))))
&nbsp;         [10 100 1000 10000 10000])</code></pre>
      <p>
        So what happens when we evaluate a <code>defbench</code> expression? It binds the benchmark name to a hashmap of group, function &nbsp;expression,
        arguments, and some metadata. Let&apos;s evaluate the name <code>zap-inc</code>.
      </p>
      <pre><code>zap-inc ;; =&gt; {:fexpr (fn [n] (zap inc (range-of-length-n n)))
&nbsp;              :group &quot;faster zap implementation&quot;
&nbsp;              :ns &quot;zap.benchmarks&quot;
&nbsp;              :name &quot;zap-inc&quot;
&nbsp;              :n [10 100 1000 10000 100000]
&nbsp;              :f #function[fn--8882]}</code></pre>
      <p>
        Yup. We can see an everyday Clojure hashmap containing all of the &nbsp;arguments we supplied to <code>defbench</code> (some stringified), plus the
        namespace and the <span class="small-caps">repl</span>&apos;s rendering of the function object.
      </p>
      <p>
        Soon, in the <a href="#run-benchmarks">run benchmarks</a> step, Fastester will rip through the benchmark names declared in the options &nbsp;hashmap
        key <code>:benchmarks</code> and run a Criterium benchmark for every name.
      </p>
      <p>
        Once we evaluate the two <code>defbench</code> expressions, the namespace contains two benchmark definitions that will &nbsp;demonstrate
        <code>zap</code>&apos;s performance: one incrementing sequences of integers, named <code>zap-inc</code>, and one upper-casing sequences of strings,
        named <code>zap-uc</code>.
      </p>
      <h4>
        Helper utilities
      </h4>
      <p>
        Fastester provides a few helper utilities. If we want to see how a &nbsp;benchmark would work, we can invoke <code>run-one-defined-benchmark</code>.
      </p>
      <pre><code>(require &apos;[fastester.measure :refer [run-one-defined-benchmark]])</code><br><br><code>(run-one-defined-benchmark zap-inc :quick)</code><br><code>;; =&gt; ...output elided for brevity...</code></pre>
      <p>
        In the course of writing benchmarks, we often need a sequence of &nbsp;exponentially-growing integers. For that, Fastester offers
        <code>range‑pow‑10</code> and <code>range‑pow‑2</code>.
      </p>
      <pre><code>(require &apos;[fastester.measure :refer [range-pow-2 range-pow-10]])</code><br><br><code>(range-pow-10 5) ;; =&gt; (1 10 100 1000 10000 100000)</code><br><code>(range-pow-2 5) ;; =&gt; (1 2 4 8 16 32)</code></pre>
      <p>
        Sometimes, we&apos;ll want to remove a defined benchmark, which we can do &nbsp;with <code>clojure.core/ns-unmap</code>.
      </p>
      <pre><code>(ns-unmap *ns* &apos;zap-something-else)</code></pre>
      <h4>
        Final checks
      </h4>
      <p id="hierarchy">
        Before we go to the next step, running the benchmarks, let&apos;s &nbsp;double-check the options. We need Fastester to find our two benchmark
        &nbsp;definitions, so we must correctly set <code>:benchmarks</code>. This options key is associated to a hashmap.
      </p>
      <p>
        That nested hashmap&apos;s keys are symbols indicating the namespace. In our &nbsp;example, we have one namespace, and therefore one key,
        <code>&apos;zap.benchmarks</code>. Associated to that one key is a set of simple symbols indicating the &nbsp;benchmark names, in our example,
        <code>&apos;zap-inc</code> and <code>&apos;zap-uc</code>. Altogether, that section of the options looks like this.
      </p>
      <pre><code>:benchmarks {&apos;zap.benchmarks #{&apos;zap-inc
&nbsp;                              &apos;zap-uc}}</code></pre>
      <p>
        We should also be on guard: saving <code>zap</code>&apos;s results (e.g., one-hundred-thousand incremented integers) blows up the &nbsp;file sizes, so
        let&apos;s set <code>:save-benchmark-fn-results?</code> to <code>false</code>.
      </p>
      <h3 id="run-benchmarks">
        3. Run benchmarks
      </h3>
      <p>
        Now that we&apos;ve written <code>zap-inc</code> and <code>zap-uc</code>, we can run the benchmarks in two ways. If we&apos;ve got our editor open with
        an &nbsp;attached <span class="small-caps">repl</span>, we can invoke <code>(run-benchmarks)</code>. If we&apos;re at the command line, invoking
      </p>
      <pre><code>$ lein run -m fastester.core :benchmarks</code></pre>
      <p>
        has the same effect. <a href="#affinity">Later</a>, we&apos;ll discuss a modification of this invocation that attempts to address a &nbsp;possible
        issue with contemporary CPUs.
      </p>
      <p>
        We should expect each benchmark to take about a minute with the default &nbsp;benchmark settings. To minimize timing variance, we ought to use a
        multi-core &nbsp;machine with minimal competing processes, network activity, etc.
      </p>
      <p>
        We should see one <code>edn</code> file per function-expression/argument pairing. If not, double check the &nbsp;options hashmap to make sure all the
        namespace and name symbols within <code>:benchmarks</code> are complete and correct.
      </p>
      <h3 id="generate">
        4. Generate the <span class="small-caps">html</span>
      </h3>
      <p>
        When the benchmarks are finished running, we can generate the performance &nbsp;report. Sometimes it&apos;s useful to have an <span class=
        "small-caps">html</span> file to quickly view in the browser, and other times it&apos;s useful to have a &nbsp;markdown file (i.e., to show on GitHub),
        so Fastester generates one of each.
      </p>
      <p>
        To generate the documents, we can invoke <code>(generate‑documents)</code> at the <span class="small-caps">repl</span>, or
      </p>
      <pre><code>$ lein run -m fastester.core :documents</code></pre>
      <p>
        from the command line.
      </p>
      <p>
        Note: Fastester uses all data files in the directory set by the options <code>:results-directory</code>. The <code>:benchmarks</code> setting has no
        affect on generating the documents.
      </p>
      <p>
        When we look at the report, there&apos;s only version&nbsp;12! We wanted a <a href="#comparative">comparative</a> report which shows how the
        performance of version&nbsp;12&apos;s <code>zap</code> has improved <em>relative</em> to version&nbsp;11&apos;s <code>zap</code>. To fix this, we use
        our version control system to roll-back to the &nbsp;version&nbsp;11 tag, and then we run the benchmarks with version&nbsp;11. Once done, we
        &nbsp;roll-forward again to version&nbsp;12.
      </p>
      <p>
        After a followup <code>generate-documents</code> invocation, the charts and tables show the version&nbsp;12 benchmark measurements &nbsp;side-by-side
        with version&nbsp;11&apos;s, similar to the <a href="#intro">introduction example</a>. We can clearly see that the new <code>zap</code> implementation
        executes faster across a wide range of sequence lengths, both &nbsp;for incrementing integers and upper-casing strings.
      </p>
      <p>
        The charts and tables present strong evidence, but a morsel of explanatory &nbsp;text enhances our story. Fastester provides two opportunities to add
        text. Near &nbsp;the top, between the table of contents and the first group&apos;s section, we can &nbsp;insert some introductory text by associating a
        hiccup/<span class="small-caps">html</span> block to the <code>:preamble</code> key in the options hashmap.
      </p>
      <p>
        Also, we can insert text after each group&apos;s section heading by creating an &nbsp;entry in the <code>:comments</code> part of the options hashmap.
        The comments option is a nested hashmap whose &nbsp;keys are the group (a string) and the values are hiccup/<span class="small-caps">html</span>
        blocks.
      </p>
      <p>
        For example, what we read in the <a href="https://blosavio.github.io/fastester/zap_performance.html"><code>zap</code> performance document</a> derives
        from the <code>:preamble</code> and <code>:comments</code> options defined roughly like this.
      </p>
      <pre><code>:preamble [:div
&nbsp;           [:p &quot;This page follows the &quot;
&nbsp;             [:code &quot;zap&quot;]
&nbsp;             &quot; function benchmark example from the &quot;
&nbsp;             [:a {:href &quot;https://github.com/blosavio/fastester&quot;}
&nbsp;              &quot;Fastester ReadMe&quot;]
&nbsp;             ...]]

:comments {&quot;faster `zap` implementation&quot; [:div
&nbsp;                                          [:p &quot;This is the comments section... &quot;
&nbsp;                                            [:em &quot;group&quot;]
&nbsp;                                            &quot;, &apos;faster `zap` implementation&apos;...&quot;
&nbsp;                                            [:code &quot;zap-inc&quot;]
&nbsp;                                            &quot; and &quot;]
&nbsp;                                            ...]}</code></pre>
      <p>
        For both the preamble and group comments, we can insert more than one <span class="small-caps">html</span> element by wrapping them with a
        <code>[:div&nbsp;...]</code>.
      </p>
      <h3 id="gotchas">
        Gotchas
      </h3>
      <p>
        We must be particularly careful to define our benchmarks to test exactly &nbsp;and only what we intend to test. One danger is idiomatic Clojure
        patterns &nbsp;polluting our time measurements. It&apos;s typical to compose a sequence right at &nbsp;the spot where we require it, like this.
      </p>
      <pre><code>(map inc (repeatedly 99 #(rand))</code></pre>
      <p>
        However, if we were to submit this expression to Criterium, intending to &nbsp;measure how long it takes <code>map</code> to increment the sequence,
        we&apos;d be <em>also</em> benchmarking creating the sequence, which may be a non-negligible portion of &nbsp;the evaluation time. Instead, we should
        hoist the sequence creation out of the &nbsp;expression.
      </p>
      <pre><code>;; *create* the sequence</code><br><code>(def ninety-nine-rands (repeatedly 99 #(rand)))</code><br><br><code>;; *use* the pre-existing sequence</code><br><code>(map inc ninety-nine-rands)</code></pre>
      <p>
        The second expression now involves mostly the <code>map</code> action, and is more appropriate for benchmarking.
      </p>
      <p>
        Another danger is that while we may be accurately timing an expression, &nbsp;the expression isn&apos;t calculating what we&apos;d like to measure.
        <code>map</code> (and friends) returns a lazy sequence, which is almost certainly not what we &nbsp;were intending to benchmark. We must remember to
        force the realization of the &nbsp;lazy sequence, conveniently done with <code>doall</code>.
      </p>
      <pre><code>(doall (map inc ninety-nine-rands))</code></pre>
      <p>
        Regarding Fastester itself, three final <em>gotchas</em> will be familiar to Clojurists programming &nbsp;at the <span class="small-caps">repl</span>.
        During development, it&apos;s typical to define and re-define benchmarks with <code>defbench</code>. It&apos;s not difficult for the namespace to get
        out of sync with the visual &nbsp;appearance of the text represented in the file. Maybe we renamed a benchmark, &nbsp;and the old benchmark is still
        floating around invisibly. Such an orphaned &nbsp;definition won&apos;t hurt anything because Fastester will only run benchmarks &nbsp;explicitly
        listed in the option&apos;s <code>:benchmarks</code>. If we want to actively remove the benchmark, we can use <code>clojure.core/ns-unmap</code>.
      </p>
      <p>
        Perhaps more dangerous, maybe we edited a <code>defbench</code>&apos;s textual expression, but failed to re-evaluate it. What we see with our
        &nbsp;eyes won&apos;t accurately reflect the benchmark definition that Fastester actually &nbsp;runs. To fix this problem, a quick re-evaluation of the
        entire text buffer &nbsp;redefines all the benchmarks currently expressed in the namespace.
      </p>
      <p>
        Finally, we need to remember that when running from the command line, &nbsp;Fastester consults only the options and benchmark definitions from the file
        contents <strong>as they exist on disk</strong>. A <span class="small-caps">repl</span>-attached editor with unsaved options or definitions, even with
        a &nbsp;freshly-evaluated namespace, will not affect the results from a command line &nbsp;invocation. Saving the files to disk synchronizes what we
        see in the editor &nbsp;and what is consumed by command line-initiated actions.
      </p>
      <p>
        When displaying relative performance comparisons, it&apos;s crucial to &nbsp;hold the environment as consistent as possible. If a round of benchmarks
        are &nbsp;run when the CPU, RAM, operating system, Java version, or Clojure version are &nbsp;changed, we need to re-run <strong>all</strong> previous
        benchmarks. Or, maybe better, we ought to make a new options file &nbsp;and generate a completely different performance document, while keeping the old
        &nbsp;around.
      </p>
      <p id="affinity">
        <em>Unresolved:</em> Contemporary systems often use multiple, heterogeneous CPU cores, i.e., &nbsp;X&nbsp;efficiency cores running light tasks at low
        power and Y&nbsp;high-performance &nbsp;cores running intense tasks. Linux provides a utility, <code>taskset</code>, that explicitly sets CPU affinity.
        Invoking
      </p>
      <pre><code>$ taskset --cpu-list 3 lein run -m fastester.core :benchmarks</code></pre>
      <p>
        from the command line pins the benchmark process to the fourth CPU. Fastester does not provide a turn-key solution for setting CPU affinity for other
        operating systems such as Windows or MacOS.
      </p>
    </section>
    <section id="limits">
      <h2>
        Limitations &amp; mitigations
      </h2>
      <p>
        Modern operating systems (OSes) and virtual machines (VMs) provide a <a href="#tratt">perilous environment</a> for accurate, reliable benchmarking.
        They both toss an uncountable number of non-deterministic confounders onto our laps. The OS may host other processes which contend for computing
        resources, interrupt for I/O or network events, etc. The Java VM may nondeterministically just-in-time (JIT) compile hot spots, making the code run
        faster (or slower!) after some unpredictable delay, and the garbage collector (GC) is quite skilled at messing with precise timing measurements.
      </p>
      <p>
        <strong>So we must exercise great care when running the benchmarks and be very conservative with our claims when reporting the benchmark
        results.</strong>
      </p>
      <p>
        Fastester delegates the benchmarking to Criterium, which fortunately goes to considerable effort to minimize this non-determinism. First, just before
        running the benchmark, Criterium forces the GC in order to minimize the chance of it running during the benchmark itself. Furthermore, Criterium
        includes a warm-up period to give the JIT compiler an opportunity to optimize the benchmarked code so that the evaluation times are more consistent.
      </p>
      <p>
        To try to control for other sources of non-determinism, we should run each benchmark multiple times (default 60), and calculate statistics on those
        results, which helps suggest whether or not our benchmark data is consistent and significantly different.
      </p>
      <p>
        Fastester, following Criterium&apos;s lead, focuses on the mean (average) evaluation time, not the minimum. This policy is intended to avoid
        over-emphasizing edge cases that coincidentally perform well and giving a more unbiased view.
      </p>
      <p>
        If our new implementation is only a few percent &apos;faster&apos; than the old version, we ought to consider very carefully whether it is worth
        changing the the implementation which may or may not be an actual improvement.
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
              <a href="https://github.com/openjdk/jmh">Java Microbenchmark Harness</a> (JMH) For building, running, and analyzing nano/micro/milli/macro
              benchmarks written in Java and other languages targeting the JVM.
            </p>
          </li>
          <li id="tratt">
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
            we&apos;ve already located the bottlenecks and made it quicker. Fastester performs a release task, not a dev-time task.
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
        An artificial <a href="https://github.io/blosavio/fastester/performance.html">example</a> that simulates performance changes of a few
        <code>clojure.core</code> functions across several versions, and demonstrates many of Fastester&apos;s features.
      </p>
      <p>
        An example that <a href="https://github.io/blosavio/fastester/zap_performance.html">follows-along</a> benchmarking <code>zap</code>, the scenario
        presented <a href="#usage">above</a> in this <em>ReadMe</em>.
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
            An assembly of a 1-arity function expression, a sequence of arguments, a name, and a group. We <em>define</em> or <em>write</em> a benchmark.
            Criterium <em>runs</em> benchmarks.
          </p>
        </dd>
      </dl>
      <dl>
        <dt id="document">
          document
        </dt>
        <dd>
          <p>
            An <span class="small-caps">html</span> or markdown file that contains benchmarks results consisting of charts, text, and tables.
          </p>
        </dd>
      </dl>
      <dl>
        <dt id="group">
          group
        </dt>
        <dd>
          <p>
            One or more conceptually-related benchmarks, e.g., all benchmarks that demonstrate the performance of <code>map</code>.
          </p>
        </dd>
      </dl>
      <dl>
        <dt id="name">
          name
        </dt>
        <dd>
          <p>
            A Clojure symbol that refers to a benchmark definition. <code>defbench</code> binds a name to benchmark <a href="#benchmark">definition</a>.
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
      Compiled by <a href="https://github.com/blosavio/readmoi">ReadMoi</a> on 2025 September 15.<span id="uuid"><br>
      a19c373d-6b51-428e-a99f-a8e89a37b60c</span>
    </p>
  </body>
</html>
