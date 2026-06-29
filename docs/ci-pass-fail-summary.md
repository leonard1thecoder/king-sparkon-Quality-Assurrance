# CI Pass/Fail Suite Summaries

GitHub Actions must show clear test counts in the job summary, not only upload HTML artifacts.

## What the CI summary shows

The reusable script is:

```text
.github/scripts/qa-suite-summary.py
```

It reads test output from:

| Source | Files parsed | What it shows |
| --- | --- | --- |
| Maven Surefire / JUnit | `**/target/surefire-reports/TEST-*.xml` | JUnit suite total, passed, failed, skipped, pass rate. |
| King Sparkon QA HTML reports | `**/target/qa-report/*.html` | Scenario-level total, passed, failed, skipped, pass rate. |
| JMeter | `**/target/**/*.jtl` and result XML | Performance sample total, passed, failed, pass rate. |

## CI output format

Each workflow summary includes:

```text
Total:   number of tests or samples discovered
Passed:  number that passed
Failed:  number that failed
Skipped: number skipped when applicable
Pass rate: passed / (passed + failed)
```

Then it prints a table:

| Type | Suite | Total | Passed | Failed | Skipped | Pass Rate | Source |
| --- | --- | ---: | ---: | ---: | ---: | ---: | --- |
| JUnit | Example suite | 3 | 3 | 0 | 0 | 100.00% | `target/surefire-reports/...` |
| QA HTML | Example scenario report | 57 | 50 | 2 | 5 | 96.15% | `target/qa-report/...` |
| JMeter | Example load result | 1200 | 1185 | 15 | 0 | 98.75% | `target/jmeter/...` |

## Workflows updated

| Workflow | Summary title |
| --- | --- |
| `.github/workflows/rest-business-flow-ci.yml` | `REST Business Flow QA Results` |
| `.github/workflows/rest-business-performance-ci.yml` | `REST Business Performance Results` |

## Why this matters

The HTML report is still the rich evidence artifact, but the GitHub Actions summary gives a fast management view:

- How many test suites ran.
- How many scenarios passed.
- How many failed.
- How many were skipped due missing auth token or disabled runtime execution.
- Which result file produced the numbers.

## Local usage

After running tests locally:

```bash
python3 .github/scripts/qa-suite-summary.py \
  --title "Local QA Results" \
  --junit "**/target/surefire-reports/TEST-*.xml" \
  --qa-html "**/target/qa-report/*.html" \
  --jmeter "**/target/**/*.jtl"
```

The same output will print in the terminal. In GitHub Actions, it is also appended to `$GITHUB_STEP_SUMMARY`.
