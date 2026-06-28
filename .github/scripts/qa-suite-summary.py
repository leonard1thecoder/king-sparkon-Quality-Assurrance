#!/usr/bin/env python3
"""Build a GitHub Actions Markdown summary for King Sparkon QA results.

The script reads:
- Maven Surefire/JUnit XML files for build-level test suite totals.
- King Sparkon QA HTML reports for scenario-level passed/failed/skipped totals.
- JMeter JTL files in XML or CSV format for performance sample totals.

It writes a readable table to stdout and appends the same content to
GITHUB_STEP_SUMMARY when running inside GitHub Actions.
"""

from __future__ import annotations

import argparse
import csv
import glob
import html
import os
import re
import sys
import xml.etree.ElementTree as ET
from dataclasses import dataclass
from pathlib import Path
from typing import Iterable


@dataclass
class SuiteResult:
    source_type: str
    suite: str
    file: str
    total: int
    passed: int
    failed: int
    skipped: int
    duration: str = ""

    @property
    def pass_rate(self) -> str:
        executable = self.passed + self.failed
        if executable == 0:
            return "0.00%"
        return f"{(self.passed / executable) * 100:.2f}%"


def find_files(patterns: Iterable[str]) -> list[Path]:
    files: list[Path] = []
    for pattern in patterns:
        matches = [Path(path) for path in glob.glob(pattern, recursive=True)]
        files.extend(matches)
    return sorted({path for path in files if path.is_file()})


def local_name(tag: str) -> str:
    return tag.rsplit("}", 1)[-1]


def parse_junit_xml(path: Path) -> list[SuiteResult]:
    try:
        root = ET.parse(path).getroot()
    except ET.ParseError:
        return []

    suites = []
    if local_name(root.tag) == "testsuite":
        suites = [root]
    else:
        suites = [node for node in root.iter() if local_name(node.tag) == "testsuite"]

    results: list[SuiteResult] = []
    for suite in suites:
        total = int(float(suite.attrib.get("tests", "0") or 0))
        failures = int(float(suite.attrib.get("failures", "0") or 0))
        errors = int(float(suite.attrib.get("errors", "0") or 0))
        skipped = int(float(suite.attrib.get("skipped", "0") or 0))
        failed = failures + errors
        passed = max(total - failed - skipped, 0)
        name = suite.attrib.get("name", path.stem)
        duration = suite.attrib.get("time", "")
        if total > 0 or failed > 0 or skipped > 0:
            results.append(SuiteResult("JUnit", name, str(path), total, passed, failed, skipped, duration))
    return results


def parse_qa_html(path: Path) -> list[SuiteResult]:
    text = path.read_text(encoding="utf-8", errors="ignore")
    statuses = re.findall(r'<span class="pill\s+(PASSED|FAILED|SKIPPED)">', text)
    if not statuses:
        return []

    title_match = re.search(r"<title>(.*?)</title>", text, re.IGNORECASE | re.DOTALL)
    suite = html.unescape(re.sub(r"\s+", " ", title_match.group(1)).strip()) if title_match else path.stem
    passed = statuses.count("PASSED")
    failed = statuses.count("FAILED")
    skipped = statuses.count("SKIPPED")
    total = passed + failed + skipped
    return [SuiteResult("QA HTML", suite, str(path), total, passed, failed, skipped)]


def parse_jmeter_xml(path: Path) -> list[SuiteResult]:
    try:
        root = ET.parse(path).getroot()
    except ET.ParseError:
        return []

    sample_tags = {"sample", "httpSample"}
    total = passed = failed = 0
    for node in root.iter():
        if local_name(node.tag) not in sample_tags:
            continue
        total += 1
        success = node.attrib.get("s", "").lower() == "true"
        if success:
            passed += 1
        else:
            failed += 1

    if total == 0:
        return []
    return [SuiteResult("JMeter", path.stem, str(path), total, passed, failed, 0)]


def parse_jmeter_csv(path: Path) -> list[SuiteResult]:
    try:
        with path.open(newline="", encoding="utf-8", errors="ignore") as handle:
            sample = handle.read(4096)
            handle.seek(0)
            dialect = csv.Sniffer().sniff(sample, delimiters=",;\t") if sample.strip() else csv.excel
            reader = csv.DictReader(handle, dialect=dialect)
            if not reader.fieldnames:
                return []
            success_field = next((name for name in reader.fieldnames if name and name.lower() == "success"), None)
            if not success_field:
                return []
            total = passed = failed = 0
            for row in reader:
                total += 1
                success = str(row.get(success_field, "")).strip().lower()
                if success in {"true", "yes", "1", "ok"}:
                    passed += 1
                else:
                    failed += 1
    except (csv.Error, OSError):
        return []

    if total == 0:
        return []
    return [SuiteResult("JMeter", path.stem, str(path), total, passed, failed, 0)]


def parse_jmeter(path: Path) -> list[SuiteResult]:
    if path.suffix.lower() == ".xml":
        return parse_jmeter_xml(path)
    if path.suffix.lower() in {".jtl", ".csv"}:
        xml_results = parse_jmeter_xml(path)
        if xml_results:
            return xml_results
        return parse_jmeter_csv(path)
    return []


def markdown_table(results: list[SuiteResult]) -> str:
    lines = [
        "| Type | Suite | Total | Passed | Failed | Skipped | Pass Rate | Source |",
        "| --- | --- | ---: | ---: | ---: | ---: | ---: | --- |",
    ]
    for result in results:
        source = result.file.replace("\\", "/")
        lines.append(
            f"| {result.source_type} | {escape_md(result.suite)} | {result.total} | {result.passed} | "
            f"{result.failed} | {result.skipped} | {result.pass_rate} | `{source}` |"
        )
    return "\n".join(lines)


def escape_md(value: str) -> str:
    return value.replace("|", "\\|").replace("\n", " ")


def build_summary(title: str, results: list[SuiteResult]) -> str:
    total = sum(result.total for result in results)
    passed = sum(result.passed for result in results)
    failed = sum(result.failed for result in results)
    skipped = sum(result.skipped for result in results)
    executable = passed + failed
    pass_rate = f"{(passed / executable) * 100:.2f}%" if executable else "0.00%"

    status_icon = "✅" if failed == 0 else "❌"
    lines = [
        f"## {status_icon} {title}",
        "",
        f"**Total:** {total}  ",
        f"**Passed:** {passed}  ",
        f"**Failed:** {failed}  ",
        f"**Skipped:** {skipped}  ",
        f"**Pass rate:** {pass_rate}",
        "",
    ]

    if results:
        lines.append(markdown_table(results))
    else:
        lines.append("No test result files were found. Check workflow paths and artifact upload settings.")

    lines.append("")
    return "\n".join(lines)


def main() -> int:
    parser = argparse.ArgumentParser(description="Summarize King Sparkon QA test suite results.")
    parser.add_argument("--title", default="King Sparkon QA Suite Summary")
    parser.add_argument("--junit", action="append", default=[])
    parser.add_argument("--qa-html", action="append", default=[])
    parser.add_argument("--jmeter", action="append", default=[])
    parser.add_argument("--fail-on-test-failures", action="store_true")
    args = parser.parse_args()

    junit_patterns = args.junit or ["**/target/surefire-reports/TEST-*.xml"]
    html_patterns = args.qa_html or ["**/target/qa-report/*.html"]
    jmeter_patterns = args.jmeter or ["**/target/**/*.jtl", "**/target/**/*.xml"]

    results: list[SuiteResult] = []
    for path in find_files(junit_patterns):
        results.extend(parse_junit_xml(path))
    for path in find_files(html_patterns):
        results.extend(parse_qa_html(path))
    for path in find_files(jmeter_patterns):
        # Avoid double-counting Surefire XML as JMeter XML.
        normalized = str(path).replace("\\", "/")
        if "/surefire-reports/" in normalized:
            continue
        results.extend(parse_jmeter(path))

    summary = build_summary(args.title, results)
    print(summary)

    step_summary = os.environ.get("GITHUB_STEP_SUMMARY")
    if step_summary:
        with open(step_summary, "a", encoding="utf-8") as handle:
            handle.write(summary)
            handle.write("\n")

    if args.fail_on_test_failures and any(result.failed > 0 for result in results):
        return 1
    return 0


if __name__ == "__main__":
    sys.exit(main())
