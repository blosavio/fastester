[:section#limits
 [:h2 "Limitations & mitigations"]

 [:p "Modern operating systems (OSes) and virtual machines (VMs) provide a
 perilous environment for accurate, reliable benchmarking. They both toss an
 uncountable number of non-deterministic confounders onto our laps. The OS may
 host other processes which contend for computing resources, interrupt for I/O
 or network events, etc. The Java VM may unpredicatably just-in-time (JIT)
 compile hot spots, making the code run faster (or slower!) after some
 unpredicatable delay, and the garbage collecter (GC) is quite skilled at
 messing with precise timing measurements."]

 [:p [:strong "So we must exercise great care when running the benchmarks and be
 very conservative with our claims when reporting the benchmark results."]]

 [:p "Fastester delegates the benchmarking to Criterium, which fortunately goes
 to considerable effort to minimize this non-determinism. First, just before
 running the benchmark, Criterium forces the GC in order to minimize the chance
 of occuring during the benchmark itself. Furthermore, Criterium includes a
 warm-up period to give the JIT compiler an opportunity to optimize the
 benchmarked code so that the evaluation time are more consistent."]

 [:p "To try to control for other non-determinism, we run each benchmark
 multiple times (default 60), and calculate statistics on those results, which
 helps suggest whether or not our benchmark data is consistent."]

 [:p "Fastester, following Criterium's lead, focuses on the mean (average)
 evaluation time, not the minimum. This policy is intended to avoid
 over-emphasizing edge cases that coincidentally perform well and giving a more
 unbiased view."]

 [:p "If our new implementation is only a few percent 'faster' than the old
 version, we ought to consider very carefully whether it is worth changing the
 the implementation which may or may not be an improvement."]]

