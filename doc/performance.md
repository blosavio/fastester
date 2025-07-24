
  <body>
    <h1>
      Fastester library performance log
    </h1>
    <div>
      <a href="#group-0">plus, vary number of digits in args</a><br>
      <a href="#group-1">plus, vary number of operands</a><br>
      <a href="#group-2">custom `conj`</a><br>
      <a href="#group-3">mapping stuff</a>
    </div>
    <div>
      <p>
        Perflog preamble comments...
      </p>
      <pre><code>(+ 1 2) ;; =&gt; 3</code></pre>
      <p>
        Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam,
        quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse
        cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.
      </p>
    </div>
    <section>
      <h3 id="group-0">
        plus, vary number of digits in args
      </h3>
      <div>
        <h4 id="group-0-fexpr-0">
          (fn [n] (delayed-+ n n n))
        </h4>
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
                3
              </td>
              <td>
                <a href="../resources/performance_entries/version 3/test-0.edn">8.3e-02±1.0e-02</a>
              </td>
              <td>
                <a href="../resources/performance_entries/version 3/test-1.edn">8.5e-02±1.2e-02</a>
              </td>
              <td>
                <a href="../resources/performance_entries/version 3/test-2.edn">9.3e-02±1.5e-02</a>
              </td>
              <td>
                <a href="../resources/performance_entries/version 3/test-3.edn">1.1e-01±2.1e-02</a>
              </td>
              <td>
                <a href="../resources/performance_entries/version 3/test-4.edn">8.5e-02±1.0e-02</a>
              </td>
            </tr>
            <tr>
              <td>
                4
              </td>
              <td>
                <a href="../resources/performance_entries/version 4/test-7.edn">7.2e-02±2.3e-02</a>
              </td>
              <td>
                <a href="../resources/performance_entries/version 4/test-8.edn">6.9e-02±1.7e-02</a>
              </td>
              <td>
                <a href="../resources/performance_entries/version 4/test-9.edn">5.5e-02±8.5e-03</a>
              </td>
              <td>
                <a href="../resources/performance_entries/version 4/test-10.edn">5.1e-02±1.9e-03</a>
              </td>
              <td>
                <a href="../resources/performance_entries/version 4/test-11.edn">5.8e-02±1.8e-02</a>
              </td>
            </tr>
            <tr>
              <td>
                5
              </td>
              <td>
                <a href="../resources/performance_entries/version 5/test-15.edn">4.2e-02±2.1e-02</a>
              </td>
              <td>
                <a href="../resources/performance_entries/version 5/test-16.edn">3.8e-02±1.2e-02</a>
              </td>
              <td>
                <a href="../resources/performance_entries/version 5/test-17.edn">3.9e-02±1.9e-02</a>
              </td>
              <td>
                <a href="../resources/performance_entries/version 5/test-18.edn">4.2e-02±1.5e-02</a>
              </td>
              <td>
                <a href="../resources/performance_entries/version 5/test-19.edn">4.0e-02±9.4e-03</a>
              </td>
            </tr>
            <tr>
              <td>
                6
              </td>
              <td>
                <a href="../resources/performance_entries/version 6/test-17.edn">2.3e-06±1.7e-06</a>
              </td>
              <td>
                <a href="../resources/performance_entries/version 6/test-18.edn">2.9e-06±2.9e-06</a>
              </td>
              <td>
                <a href="../resources/performance_entries/version 6/test-19.edn">6.4e-06±2.5e-06</a>
              </td>
              <td>
                <a href="../resources/performance_entries/version 6/test-20.edn">9.1e-07±7.4e-08</a>
              </td>
              <td>
                <a href="../resources/performance_entries/version 6/test-21.edn">3.1e-06±6.5e-07</a>
              </td>
            </tr>
          </table>
        </div>
        <h4 id="group-0-fexpr-1">
          (fn [n] (delayed-+ n n))
        </h4>
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
                3
              </td>
              <td>
                <a href="../resources/performance_entries/version 3/test-10.edn">8.3e-02±9.6e-03</a>
              </td>
              <td>
                <a href="../resources/performance_entries/version 3/test-11.edn">8.1e-02±9.7e-03</a>
              </td>
              <td>
                <a href="../resources/performance_entries/version 3/test-12.edn">8.2e-02±5.2e-03</a>
              </td>
              <td>
                <a href="../resources/performance_entries/version 3/test-13.edn">8.2e-02±8.0e-03</a>
              </td>
              <td>
                <a href="../resources/performance_entries/version 3/test-14.edn">8.1e-02±7.7e-03</a>
              </td>
            </tr>
            <tr>
              <td>
                4
              </td>
              <td>
                <a href="../resources/performance_entries/version 4/test-12.edn">6.9e-02±2.3e-02</a>
              </td>
              <td>
                <a href="../resources/performance_entries/version 4/test-13.edn">7.7e-02±2.8e-02</a>
              </td>
              <td>
                <a href="../resources/performance_entries/version 4/test-14.edn">6.9e-02±1.8e-02</a>
              </td>
              <td>
                <a href="../resources/performance_entries/version 4/test-15.edn">5.0e-02±3.5e-04</a>
              </td>
              <td>
                <a href="../resources/performance_entries/version 4/test-16.edn">6.5e-02±1.8e-02</a>
              </td>
            </tr>
            <tr>
              <td>
                5
              </td>
              <td>
                <a href="../resources/performance_entries/version 5/test-5.edn">4.0e-02±1.7e-02</a>
              </td>
              <td>
                <a href="../resources/performance_entries/version 5/test-6.edn">4.2e-02±2.2e-02</a>
              </td>
              <td>
                <a href="../resources/performance_entries/version 5/test-7.edn">4.5e-02±2.2e-02</a>
              </td>
              <td>
                <a href="../resources/performance_entries/version 5/test-8.edn">3.1e-02±7.7e-03</a>
              </td>
              <td>
                <a href="../resources/performance_entries/version 5/test-9.edn">3.5e-02±1.7e-02</a>
              </td>
            </tr>
            <tr>
              <td>
                6
              </td>
              <td>
                <a href="../resources/performance_entries/version 6/test-12.edn">1.8e-06±1.3e-06</a>
              </td>
              <td>
                <a href="../resources/performance_entries/version 6/test-13.edn">3.3e-06±1.1e-06</a>
              </td>
              <td>
                <a href="../resources/performance_entries/version 6/test-14.edn">1.0e-05±1.0e-05</a>
              </td>
              <td>
                <a href="../resources/performance_entries/version 6/test-15.edn">2.4e-06±4.5e-08</a>
              </td>
              <td>
                <a href="../resources/performance_entries/version 6/test-16.edn">3.4e-06±1.4e-06</a>
              </td>
            </tr>
          </table>
        </div>
        <h4 id="group-0-fexpr-2">
          (fn [n] (delayed-+ n))
        </h4>
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
                3
              </td>
              <td>
                <a href="../resources/performance_entries/version 3/test-5.edn">9.0e-02±2.1e-02</a>
              </td>
              <td>
                <a href="../resources/performance_entries/version 3/test-6.edn">8.5e-02±9.9e-03</a>
              </td>
              <td>
                <a href="../resources/performance_entries/version 3/test-7.edn">8.5e-02±1.1e-02</a>
              </td>
              <td>
                <a href="../resources/performance_entries/version 3/test-8.edn">8.5e-02±1.2e-02</a>
              </td>
              <td>
                <a href="../resources/performance_entries/version 3/test-9.edn">9.7e-02±1.7e-02</a>
              </td>
            </tr>
            <tr>
              <td>
                4
              </td>
              <td>
                <a href="../resources/performance_entries/version 4/test-17.edn">7.2e-02±2.1e-02</a>
              </td>
              <td>
                <a href="../resources/performance_entries/version 4/test-18.edn">6.9e-02±1.9e-02</a>
              </td>
              <td>
                <a href="../resources/performance_entries/version 4/test-19.edn">5.6e-02±4.3e-03</a>
              </td>
              <td>
                <a href="../resources/performance_entries/version 4/test-20.edn">6.0e-02±1.2e-02</a>
              </td>
              <td>
                <a href="../resources/performance_entries/version 4/test-21.edn">5.7e-02±4.1e-03</a>
              </td>
            </tr>
            <tr>
              <td>
                5
              </td>
              <td>
                <a href="../resources/performance_entries/version 5/test-10.edn">3.8e-02±1.7e-02</a>
              </td>
              <td>
                <a href="../resources/performance_entries/version 5/test-11.edn">2.9e-02±6.8e-03</a>
              </td>
              <td>
                <a href="../resources/performance_entries/version 5/test-12.edn">4.0e-02±1.7e-02</a>
              </td>
              <td>
                <a href="../resources/performance_entries/version 5/test-13.edn">3.4e-02±1.8e-02</a>
              </td>
              <td>
                <a href="../resources/performance_entries/version 5/test-14.edn">3.1e-02±8.6e-03</a>
              </td>
            </tr>
            <tr>
              <td>
                6
              </td>
              <td>
                <a href="../resources/performance_entries/version 6/test-22.edn">4.2e-06±1.3e-06</a>
              </td>
              <td>
                <a href="../resources/performance_entries/version 6/test-23.edn">4.6e-06±2.6e-06</a>
              </td>
              <td>
                <a href="../resources/performance_entries/version 6/test-24.edn">4.8e-06±1.5e-06</a>
              </td>
              <td>
                <a href="../resources/performance_entries/version 6/test-25.edn">2.8e-06±1.8e-06</a>
              </td>
              <td>
                <a href="../resources/performance_entries/version 6/test-26.edn">2.2e-06±3.4e-07</a>
              </td>
            </tr>
          </table>
        </div>
      </div>
      <p>
        Plus vary args comments...
      </p>
      <h3 id="group-1">
        plus, vary number of operands
      </h3>
      <div>
        <h4 id="group-1-fexpr-0">
          (fn [n] (apply + (take n (repeat 64))))
        </h4>
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
                4
              </td>
              <td>
                <a href="../resources/performance_entries/version 4/test-22.edn">7.3e-07±5.5e-07</a>
              </td>
              <td>
                <a href="../resources/performance_entries/version 4/test-23.edn">7.9e-06±7.1e-06</a>
              </td>
              <td>
                <a href="../resources/performance_entries/version 4/test-24.edn">4.9e-05±6.1e-05</a>
              </td>
              <td>
                <a href="../resources/performance_entries/version 4/test-25.edn">6.7e-04±4.0e-04</a>
              </td>
              <td>
                <a href="../resources/performance_entries/version 4/test-26.edn">3.6e-03±2.3e-03</a>
              </td>
            </tr>
            <tr>
              <td>
                5
              </td>
              <td>
                <a href="../resources/performance_entries/version 5/test-20.edn">5.9e-07±2.7e-07</a>
              </td>
              <td>
                <a href="../resources/performance_entries/version 5/test-21.edn">6.0e-06±2.0e-06</a>
              </td>
              <td>
                <a href="../resources/performance_entries/version 5/test-22.edn">3.8e-05±3.5e-05</a>
              </td>
              <td>
                <a href="../resources/performance_entries/version 5/test-23.edn">3.7e-04±2.1e-04</a>
              </td>
              <td>
                <a href="../resources/performance_entries/version 5/test-24.edn">4.9e-03±2.5e-03</a>
              </td>
            </tr>
            <tr>
              <td>
                6
              </td>
              <td>
                <a href="../resources/performance_entries/version 6/test-27.edn">1.2e-06±7.9e-07</a>
              </td>
              <td>
                <a href="../resources/performance_entries/version 6/test-28.edn">3.9e-06±1.7e-06</a>
              </td>
              <td>
                <a href="../resources/performance_entries/version 6/test-29.edn">9.6e-05±5.2e-05</a>
              </td>
              <td>
                <a href="../resources/performance_entries/version 6/test-30.edn">2.4e-04±1.1e-05</a>
              </td>
              <td>
                <a href="../resources/performance_entries/version 6/test-31.edn">2.6e-03±2.7e-04</a>
              </td>
            </tr>
          </table>
        </div>
      </div>
      <p>
        Note: Added this test starting with version 4
      </p>
      <h3 id="group-2">
        custom `conj`
      </h3>
      <div>
        <h4 id="group-2-fexpr-0">
          (fn [n] (my-conj (vec (repeatedly n (fn* [] (rand-int 99)))) :tail-value))
        </h4>
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
                5
              </td>
              <td>
                <a href="../resources/performance_entries/version 5/test-0.edn">2.8e-06±1.7e-06</a>
              </td>
              <td>
                <a href="../resources/performance_entries/version 5/test-1.edn">1.1e-05±4.6e-06</a>
              </td>
              <td>
                <a href="../resources/performance_entries/version 5/test-2.edn">3.3e-05±1.5e-06</a>
              </td>
              <td>
                <a href="../resources/performance_entries/version 5/test-3.edn">1.2e-03±5.4e-04</a>
              </td>
              <td>
                <a href="../resources/performance_entries/version 5/test-4.edn">3.8e-03±2.4e-04</a>
              </td>
            </tr>
            <tr>
              <td>
                6
              </td>
              <td>
                <a href="../resources/performance_entries/version 6/test-0.edn">3.9e-06±3.2e-06</a>
              </td>
              <td>
                <a href="../resources/performance_entries/version 6/test-1.edn">7.2e-06±2.4e-06</a>
              </td>
              <td>
                <a href="../resources/performance_entries/version 6/test-2.edn">4.4e-05±1.8e-06</a>
              </td>
              <td>
                <a href="../resources/performance_entries/version 6/test-3.edn">8.6e-04±6.8e-04</a>
              </td>
              <td>
                <a href="../resources/performance_entries/version 6/test-4.edn">1.4e-02±6.6e-03</a>
              </td>
            </tr>
          </table>
        </div>
      </div>
      <p>
        Version 6 implemented `conj` with transients, so should be faster...
      </p>
      <h3 id="group-3">
        mapping stuff
      </h3>
      <div>
        <h4 id="group-3-fexpr-0">
          (fn [n] (map inc (range n)))
        </h4>
        <div class="collapsable">
          <table>
            <caption>
              times in seconds, <em>mean±std</em>
            </caption>
            <thead>
              <tr>
                <td></td>
                <th colspan="4">
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
              </tr>
            </thead>
            <tr>
              <td>
                4
              </td>
              <td>
                <a href="../resources/performance_entries/version 4/test-3.edn">5.5e-07±4.3e-07</a>
              </td>
              <td>
                <a href="../resources/performance_entries/version 4/test-4.edn">9.6e-07±6.5e-07</a>
              </td>
              <td>
                <a href="../resources/performance_entries/version 4/test-5.edn">1.2e-06±1.3e-06</a>
              </td>
              <td>
                <a href="../resources/performance_entries/version 4/test-6.edn">4.5e-07±3.6e-07</a>
              </td>
            </tr>
            <tr>
              <td>
                6
              </td>
              <td>
                <a href="../resources/performance_entries/version 6/test-5.edn">6.0e-07±8.1e-07</a>
              </td>
              <td>
                <a href="../resources/performance_entries/version 6/test-6.edn">2.7e-07±6.4e-08</a>
              </td>
              <td>
                <a href="../resources/performance_entries/version 6/test-7.edn">8.1e-07±9.0e-07</a>
              </td>
              <td>
                <a href="../resources/performance_entries/version 6/test-8.edn">6.2e-07±7.4e-07</a>
              </td>
            </tr>
          </table>
        </div>
        <h4 id="group-3-fexpr-1">
          (fn [n] (map str/upper-case (take n (cycle [&quot;a&quot; &quot;b&quot; &quot;c&quot;]))))
        </h4>
        <div class="collapsable">
          <table>
            <caption>
              times in seconds, <em>mean±std</em>
            </caption>
            <thead>
              <tr>
                <td></td>
                <th colspan="3">
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
              </tr>
            </thead>
            <tr>
              <td>
                4
              </td>
              <td>
                <a href="../resources/performance_entries/version 4/test-0.edn">1.1e-06±9.3e-07</a>
              </td>
              <td>
                <a href="../resources/performance_entries/version 4/test-1.edn">1.9e-07±1.4e-07</a>
              </td>
              <td>
                <a href="../resources/performance_entries/version 4/test-2.edn">4.8e-07±6.2e-07</a>
              </td>
            </tr>
            <tr>
              <td>
                6
              </td>
              <td>
                <a href="../resources/performance_entries/version 6/test-9.edn">1.5e-07±1.6e-07</a>
              </td>
              <td>
                <a href="../resources/performance_entries/version 6/test-10.edn">3.1e-07±2.2e-07</a>
              </td>
              <td>
                <a href="../resources/performance_entries/version 6/test-11.edn">5.8e-07±6.5e-07</a>
              </td>
            </tr>
          </table>
        </div>
      </div>
      <p>
        Note: Skipped version 5...
      </p>
    </section>
    <p id="page-footer">
      Copyright © 2024–2025 Brad Losavio.<br>
      Compiled by <a href="https://github.com/blosavio/Fastester">Fastester</a> on 2025 July 24.<span id="uuid"><br>
      50c7eada-f96d-41bf-aed0-47d386e61136</span>
    </p>
  </body>
</html>
