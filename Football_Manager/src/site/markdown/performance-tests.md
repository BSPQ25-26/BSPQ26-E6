# Performance Tests

This section documents the performance testing strategy used in the Football Manager project.

The project originally considered ContiPerf, but that library is deprecated. Because of that, the performance testing implementation was migrated to JUnitPerf.

## Performance testing tool

The project uses:

- JUnitPerf
- JUnit 4 compatibility test execution
- HTML performance report generation

## Measured aspects

The performance tests validate aspects such as:

- Execution duration
- Concurrent threads
- Throughput
- Latency
- Error threshold

## Main performance test class

```txt
src/test/java/com/example/football_manager/performance/TeamServicePerformanceTest.java
```

## JUnitPerf HTML report

The generated JUnitPerf report is copied into the Maven Site and can be opened here:

[Open JUnitPerf Report](junitperf/junitperf_report.html)
