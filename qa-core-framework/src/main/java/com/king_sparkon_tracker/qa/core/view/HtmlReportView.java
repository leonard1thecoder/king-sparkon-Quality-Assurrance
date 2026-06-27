package com.king_sparkon_tracker.qa.core.view;

import com.king_sparkon_tracker.qa.core.model.TestExecutionResponse;
import com.king_sparkon_tracker.qa.core.model.TestStatus;
import com.king_sparkon_tracker.qa.core.model.TestSuiteReport;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class HtmlReportView {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            .withZone(ZoneId.systemDefault());

    public String buildHtml(TestSuiteReport report) {
        return """
                <!doctype html>
                <html lang=\"en\">
                <head>
                    <meta charset=\"UTF-8\" />
                    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />
                    <title>%s</title>
                    <style>
                        :root { --bg:#f8fafc; --card:#ffffff; --text:#0f172a; --muted:#64748b; --line:#e2e8f0; --pass:#16a34a; --fail:#dc2626; --skip:#ca8a04; --accent:#f97316; }
                        * { box-sizing:border-box; }
                        body { margin:0; font-family:Inter, ui-sans-serif, system-ui, -apple-system, BlinkMacSystemFont, Segoe UI, sans-serif; background:var(--bg); color:var(--text); }
                        .shell { width:min(1180px, calc(100%% - 32px)); margin:32px auto 56px; }
                        .hero { background:linear-gradient(135deg,#ffffff 0%%,#fff7ed 50%%,#ffffff 100%%); border:1px solid var(--line); border-radius:28px; padding:28px; box-shadow:0 24px 80px rgba(15,23,42,.08); }
                        .eyebrow { display:inline-flex; gap:8px; align-items:center; padding:8px 12px; border:1px solid #fed7aa; border-radius:999px; color:#9a3412; background:#fff7ed; font-weight:700; font-size:13px; }
                        h1 { margin:18px 0 8px; font-size:clamp(30px,4vw,52px); letter-spacing:-.04em; }
                        .meta { color:var(--muted); margin:0; }
                        .grid { display:grid; grid-template-columns:repeat(5,minmax(0,1fr)); gap:14px; margin:22px 0; }
                        .card { background:var(--card); border:1px solid var(--line); border-radius:22px; padding:18px; box-shadow:0 16px 42px rgba(15,23,42,.06); }
                        .label { color:var(--muted); font-size:12px; font-weight:800; text-transform:uppercase; letter-spacing:.08em; }
                        .value { margin-top:8px; font-size:28px; font-weight:900; letter-spacing:-.03em; }
                        .layout { display:grid; grid-template-columns:360px 1fr; gap:18px; align-items:start; }
                        .chart-wrap { position:relative; display:grid; place-items:center; min-height:310px; }
                        canvas { max-width:280px; max-height:280px; }
                        .tooltip { position:absolute; pointer-events:none; transform:translate(-50%%,-120%%); display:none; padding:8px 10px; border-radius:12px; background:#0f172a; color:#fff; font-size:12px; font-weight:800; box-shadow:0 12px 30px rgba(15,23,42,.22); }
                        .legend { display:flex; justify-content:center; gap:14px; flex-wrap:wrap; margin-top:12px; }
                        .legend span { display:inline-flex; align-items:center; gap:7px; color:var(--muted); font-size:13px; font-weight:700; }
                        .dot { width:10px; height:10px; border-radius:50%%; display:inline-block; }
                        .table-card { overflow:hidden; }
                        table { width:100%%; border-collapse:collapse; font-size:14px; }
                        th, td { padding:14px 16px; text-align:left; border-bottom:1px solid var(--line); vertical-align:top; }
                        th { background:#f8fafc; color:#334155; font-size:12px; text-transform:uppercase; letter-spacing:.08em; }
                        tr:hover td { background:#fff7ed; }
                        .pill { display:inline-flex; align-items:center; justify-content:center; min-width:78px; border-radius:999px; padding:6px 10px; font-size:12px; font-weight:900; }
                        .PASSED { color:#166534; background:#dcfce7; }
                        .FAILED { color:#991b1b; background:#fee2e2; }
                        .SKIPPED { color:#92400e; background:#fef3c7; }
                        .error { color:#b91c1c; max-width:320px; }
                        .footer { margin-top:18px; color:var(--muted); font-size:13px; text-align:center; }
                        @media (max-width: 980px) { .grid { grid-template-columns:repeat(2,minmax(0,1fr)); } .layout { grid-template-columns:1fr; } }
                        @media (max-width: 640px) { .grid { grid-template-columns:1fr; } table { min-width:860px; } .table-scroll { overflow:auto; } }
                    </style>
                </head>
                <body>
                    <main class=\"shell\">
                        <section class=\"hero\">
                            <span class=\"eyebrow\">👑 King Sparkon QA Report</span>
                            <h1>%s</h1>
                            <p class=\"meta\">Started: %s · Completed: %s</p>
                            <section class=\"grid\">
                                %s
                            </section>
                        </section>
                        <section class=\"layout\" style=\"margin-top:18px;\">
                            <article class=\"card\">
                                <div class=\"label\">Passed vs Failed</div>
                                <div class=\"chart-wrap\">
                                    <canvas id=\"resultChart\" width=\"280\" height=\"280\"></canvas>
                                    <div id=\"chartTooltip\" class=\"tooltip\"></div>
                                </div>
                                <div class=\"legend\">
                                    <span><i class=\"dot\" style=\"background:var(--pass)\"></i> Passed %s</span>
                                    <span><i class=\"dot\" style=\"background:var(--fail)\"></i> Failed %s</span>
                                </div>
                            </article>
                            <article class=\"card table-card\">
                                <div class=\"label\" style=\"margin-bottom:12px;\">Test Case Result Details</div>
                                <div class=\"table-scroll\">
                                    <table>
                                        <thead>
                                            <tr>
                                                <th>Test Case ID</th>
                                                <th>Scenario</th>
                                                <th>Test Case Name</th>
                                                <th>Description</th>
                                                <th>Status</th>
                                                <th>Duration</th>
                                                <th>Error</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            %s
                                        </tbody>
                                    </table>
                                </div>
                            </article>
                        </section>
                        <p class=\"footer\">Generated by King Sparkon QA Core Framework.</p>
                    </main>
                    <script>
                        const reportData = { passed:%d, failed:%d, skipped:%d, total:%d };
                        const canvas = document.getElementById('resultChart');
                        const tooltip = document.getElementById('chartTooltip');
                        const ctx = canvas.getContext('2d');
                        const cx = 140, cy = 140, radius = 108;
                        const segments = [
                            { label:'Passed', value:reportData.passed, color:'#16a34a' },
                            { label:'Failed', value:reportData.failed, color:'#dc2626' }
                        ];
                        const visibleTotal = Math.max(1, segments.reduce((sum, item) => sum + item.value, 0));
                        let start = -Math.PI / 2;
                        segments.forEach(segment => {
                            const angle = (segment.value / visibleTotal) * Math.PI * 2;
                            segment.start = start;
                            segment.end = start + angle;
                            start += angle;
                        });
                        function drawChart(activeLabel) {
                            ctx.clearRect(0,0,280,280);
                            segments.forEach(segment => {
                                ctx.beginPath();
                                ctx.moveTo(cx, cy);
                                ctx.arc(cx, cy, activeLabel === segment.label ? radius + 7 : radius, segment.start, segment.end);
                                ctx.closePath();
                                ctx.fillStyle = segment.color;
                                ctx.fill();
                            });
                            ctx.beginPath();
                            ctx.arc(cx, cy, 62, 0, Math.PI * 2);
                            ctx.fillStyle = '#ffffff';
                            ctx.fill();
                            ctx.fillStyle = '#0f172a';
                            ctx.font = '900 26px Inter, sans-serif';
                            ctx.textAlign = 'center';
                            ctx.fillText('%s', cx, cy - 2);
                            ctx.fillStyle = '#64748b';
                            ctx.font = '800 11px Inter, sans-serif';
                            ctx.fillText('PASS RATE', cx, cy + 22);
                        }
                        function activeSegment(event) {
                            const rect = canvas.getBoundingClientRect();
                            const x = event.clientX - rect.left - cx;
                            const y = event.clientY - rect.top - cy;
                            const distance = Math.sqrt(x*x + y*y);
                            if (distance > radius + 12 || distance < 52) return null;
                            let angle = Math.atan2(y, x);
                            if (angle < -Math.PI / 2) angle += Math.PI * 2;
                            return segments.find(segment => angle >= segment.start && angle <= segment.end);
                        }
                        canvas.addEventListener('mousemove', event => {
                            const segment = activeSegment(event);
                            if (!segment) { tooltip.style.display = 'none'; drawChart(); return; }
                            const percentage = ((segment.value / Math.max(1, reportData.total)) * 100).toFixed(2);
                            tooltip.textContent = `${segment.label}: ${segment.value} (${percentage}%%)`;
                            tooltip.style.left = event.offsetX + 'px';
                            tooltip.style.top = event.offsetY + 'px';
                            tooltip.style.display = 'block';
                            drawChart(segment.label);
                        });
                        canvas.addEventListener('mouseleave', () => { tooltip.style.display = 'none'; drawChart(); });
                        drawChart();
                    </script>
                </body>
                </html>
                """.formatted(
                escape(report.suiteName()),
                escape(report.suiteName()),
                DATE_FORMATTER.format(report.startedAt()),
                DATE_FORMATTER.format(report.completedAt()),
                summaryCards(report),
                report.passPercentageLabel(),
                report.failPercentageLabel(),
                resultRows(report),
                report.passedTests(),
                report.failedTests(),
                report.skippedTests(),
                report.totalTests(),
                report.passPercentageLabel()
        );
    }

    private String summaryCards(TestSuiteReport report) {
        return """
                <article class=\"card\"><div class=\"label\">Total Duration</div><div class=\"value\">%d ms</div></article>
                <article class=\"card\"><div class=\"label\">Test Cases</div><div class=\"value\">%d</div></article>
                <article class=\"card\"><div class=\"label\">Passed Tests</div><div class=\"value\" style=\"color:var(--pass)\">%d</div></article>
                <article class=\"card\"><div class=\"label\">Failed Tests</div><div class=\"value\" style=\"color:var(--fail)\">%d</div></article>
                <article class=\"card\"><div class=\"label\">Overall Coverage</div><div class=\"value\">%s</div></article>
                """.formatted(
                report.totalDurationMs(),
                report.totalTests(),
                report.passedTests(),
                report.failedTests(),
                report.passPercentageLabel()
        );
    }

    private String resultRows(TestSuiteReport report) {
        if (report.responses().isEmpty()) {
            return "<tr><td colspan=\"7\">No test results were supplied.</td></tr>";
        }
        StringBuilder rows = new StringBuilder();
        for (TestExecutionResponse response : report.responses()) {
            rows.append("""
                    <tr>
                        <td>%s</td>
                        <td>%s</td>
                        <td>%s</td>
                        <td>%s</td>
                        <td><span class=\"pill %s\">%s</span></td>
                        <td>%d ms</td>
                        <td class=\"error\">%s</td>
                    </tr>
                    """.formatted(
                    escape(response.testCaseId()),
                    escape(response.scenario()),
                    escape(response.testCaseName()),
                    escape(response.description()),
                    response.status(),
                    statusLabel(response.status()),
                    response.durationMs(),
                    escape(response.errorMessage())
            ));
        }
        return rows.toString();
    }

    private String statusLabel(TestStatus status) {
        return switch (status) {
            case PASSED -> "PASSED";
            case FAILED -> "FAILED";
            case SKIPPED -> "SKIPPED";
        };
    }

    private String escape(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
