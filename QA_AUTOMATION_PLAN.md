# King Sparkon QA Automation Plan

Added coverage for backend API, web UI, product catalogue, barcode scanner, tips, Android readiness, security baseline, and performance.

## New assets

- `test-assets/test-cases/king-sparkon-test-cases.csv` is the full cross-project test inventory.
- `test-assets/scenarios/king-sparkon-test-steps.scv` contains the project-level test steps.
- `testProduct` is the new MVC module for product test definitions.
- `jmeter-performance-tests/src/test/jmeter/king-sparkon-web-pages-load.jmx` covers frontend pages.
- `jmeter-performance-tests/src/test/jmeter/king-sparkon-whole-project-performance.jmx` covers backend and frontend together.

## Run

Product QA suite:

`mvn -am -pl testProduct test`

Performance suite:

`mvn -am -pl jmeter-performance-tests verify -DtargetHost=localhost -DtargetPort=8080 -DwebHost=localhost -DwebPort=3000`

## Gate

P0 tests must have a scenario, expected result, and step definition. Performance tests should keep p95 inside the SLA set in the JMeter properties.
