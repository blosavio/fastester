
  <body>
    <h1>
      `zap` example
    </h1>
    <div>
      <a href="#group-0">faster `zap` implementation</a>
    </div>
    <div>
      <p>
        This page follows the <code>zap</code> function benchmark example from the <a href="https://github.com/blosavio/fastester">Fastester ReadMe</a>. The
        benchmarks are defined in <a href="https://github.com/blosavio/fastester/blob/main/test/zap/benchmarks.clj">this file</a>, while the options (including
        this very text you are now reading) are defined in <a href="https://github.com/blosavio/fastester/blob/main/resources/zap_options.edn">this file</a>.
        To generate this <span class="small-caps">html</span> page, evaluate Fastester&apos;s <a href=
        "https://blosavio.github.io/fastester/fastester.core.html#var--main"><code>main</code></a> function with the <code>:documents</code> argument. For
        example, with Leiningen, run this.
      </p>
      <pre><code>$ lein run -m fastester.core :documents zap_options.edn</code></pre>
      <p>
        The hyperlink immediately preceding this preamble (&apos;faster `zap` implementation&apos;) is a one-element table of contents. Clicking that link
        whisks us to the <span class="small-caps">html</span> section for that benchmark <em>group</em> name. In a performance document like this one with only
        one group, it&apos;s not terribly useful. Real life documents will have multiple groups with longer sections and that table of contents will provide
        some handy navigation. Try clicking the link; it will scroll us only to the immediate next section.
      </p>
    </div>
    <section>
      <h3 id="group-0">
        faster `zap` implementation
      </h3>
      <div>
        <p>
          This is the comments section for our single benchmark <em>group</em>, &apos;faster `zap` implementation&apos;. We designated two benchmarks to that
          group, <code>zap-inc</code> and <code>zap-uc</code>. We don&apos;t see those names here because they are merely bookkeeping and not relevant for
          communicating the performance changes between version&nbsp;11 and version&nbsp;12.
        </p>
        <p>
          What we <strong>can</strong> see are the actual benchmark function expressions that were measured by Criterium, i.e., the <code>doall/zap</code>
          expressions. We defined two benchmarks, and after running the benchmarks and generating the documents, we see two charts summarizing the results.
        </p>
        <p>
          We simulated a performance improvement from version&nbsp;11 to version&nbsp;12 by giving the older version more work to do at each step. In both
          charts, all of version&nbsp;12&apos;s data points are lower, i.e., shorter evaluation times, than version&nbsp;11&apos;s respective data points,
          across the full range of argument sequence lengths (which span one element to ten-thousand elements). Given this, in the release notes, we might say:
        </p>
        <blockquote>
          <em>The new implementation of <code>zap</code> offers more than 25% speedup across four orders of magnitude input length for processing both integers
          and strings.</em>
        </blockquote>
        <p>
          This page merely shows an example. Actual performance reports ought to involve a wider span of inputs, and additional benchmarks to cover the
          majority of common use cases.
        </p>
      </div>
      <div>
        <h4 id="group-0-fexpr-0">
          (fn [i] (doall (zap str/upper-case (abc-cycle-of-length-i i))))
        </h4><img alt=
        "Benchmark measurements for expression `(fn [i] (doall (zap str/upper-case (abc-cycle-of-length-i i))))`, time versus &apos;n&apos; arguments, comparing different versions."
        src="zap_img/group-0-fexpr-0.svg"><button class="collapser" type="button">Show details</button>
        <div class="collapsable">
          <table>
            <caption>
              times in seconds, <em>mean±std</em>
            </caption>
            <thead>
              <tr>
                <td></td>
                <th colspan="5">
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
              </tr>
            </thead>
            <tr>
              <td>
                12
              </td>
              <td>
                <a href="https://github.com/blosavio/fastester/blob/main/resources/zap_performance/version 12/test-5.edn">9.8e-05±1.1e-06</a>
              </td>
              <td>
                <a href="https://github.com/blosavio/fastester/blob/main/resources/zap_performance/version 12/test-6.edn">9.7e-04±1.1e-05</a>
              </td>
              <td>
                <a href="https://github.com/blosavio/fastester/blob/main/resources/zap_performance/version 12/test-7.edn">9.8e-03±1.4e-04</a>
              </td>
              <td>
                <a href="https://github.com/blosavio/fastester/blob/main/resources/zap_performance/version 12/test-8.edn">9.7e-02±1.6e-03</a>
              </td>
              <td>
                <a href="https://github.com/blosavio/fastester/blob/main/resources/zap_performance/version 12/test-9.edn">9.8e-01±1.6e-02</a>
              </td>
            </tr>
            <tr>
              <td>
                11
              </td>
              <td>
                <a href="https://github.com/blosavio/fastester/blob/main/resources/zap_performance/version 11/test-5.edn">1.5e-04±1.5e-06</a>
              </td>
              <td>
                <a href="https://github.com/blosavio/fastester/blob/main/resources/zap_performance/version 11/test-6.edn">1.5e-03±1.8e-05</a>
              </td>
              <td>
                <a href="https://github.com/blosavio/fastester/blob/main/resources/zap_performance/version 11/test-7.edn">1.5e-02±1.7e-04</a>
              </td>
              <td>
                <a href="https://github.com/blosavio/fastester/blob/main/resources/zap_performance/version 11/test-8.edn">1.5e-01±3.2e-03</a>
              </td>
              <td>
                <a href="https://github.com/blosavio/fastester/blob/main/resources/zap_performance/version 11/test-9.edn">1.5e+00±2.1e-02</a>
              </td>
            </tr>
          </table>
        </div>
        <h4 id="group-0-fexpr-1">
          (fn [n] (doall (zap inc (range-of-length-n n))))
        </h4><img alt=
        "Benchmark measurements for expression `(fn [n] (doall (zap inc (range-of-length-n n))))`, time versus &apos;n&apos; arguments, comparing different versions."
        src="zap_img/group-0-fexpr-1.svg"><button class="collapser" type="button">Show details</button>
        <div class="collapsable">
          <table>
            <caption>
              times in seconds, <em>mean±std</em>
            </caption>
            <thead>
              <tr>
                <td></td>
                <th colspan="5">
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
              </tr>
            </thead>
            <tr>
              <td>
                12
              </td>
              <td>
                <a href="https://github.com/blosavio/fastester/blob/main/resources/zap_performance/version 12/test-0.edn">9.9e-05±1.7e-06</a>
              </td>
              <td>
                <a href="https://github.com/blosavio/fastester/blob/main/resources/zap_performance/version 12/test-1.edn">9.6e-04±5.5e-06</a>
              </td>
              <td>
                <a href="https://github.com/blosavio/fastester/blob/main/resources/zap_performance/version 12/test-2.edn">9.6e-03±6.1e-05</a>
              </td>
              <td>
                <a href="https://github.com/blosavio/fastester/blob/main/resources/zap_performance/version 12/test-3.edn">9.7e-02±1.4e-03</a>
              </td>
              <td>
                <a href="https://github.com/blosavio/fastester/blob/main/resources/zap_performance/version 12/test-4.edn">9.7e-01±1.4e-02</a>
              </td>
            </tr>
            <tr>
              <td>
                11
              </td>
              <td>
                <a href="https://github.com/blosavio/fastester/blob/main/resources/zap_performance/version 11/test-0.edn">1.3e-04±6.6e-07</a>
              </td>
              <td>
                <a href="https://github.com/blosavio/fastester/blob/main/resources/zap_performance/version 11/test-1.edn">1.3e-03±1.0e-05</a>
              </td>
              <td>
                <a href="https://github.com/blosavio/fastester/blob/main/resources/zap_performance/version 11/test-2.edn">1.3e-02±6.0e-05</a>
              </td>
              <td>
                <a href="https://github.com/blosavio/fastester/blob/main/resources/zap_performance/version 11/test-3.edn">1.3e-01±7.4e-04</a>
              </td>
              <td>
                <a href="https://github.com/blosavio/fastester/blob/main/resources/zap_performance/version 11/test-4.edn">1.3e+00±9.1e-03</a>
              </td>
            </tr>
          </table>
        </div>
      </div>
      <hr>
    </section>
    <p id="page-footer">
      Copyright © 2024–2025 Brad Losavio.<br>
      Compiled by <a href="https://github.com/blosavio/Fastester">Fastester</a> on 2025 September 19.<span id="uuid"><br>
      3615ae1c-d196-4cb0-bb4c-b4f29a8e2501</span>
    </p>
  </body>
</html>
