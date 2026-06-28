$ErrorActionPreference = "Stop"

$TargetUrl = if ($env:SECURITY_TARGET_URL) { $env:SECURITY_TARGET_URL } else { "http://localhost:3000" }
$ZapMinutes = if ($env:ZAP_MINUTES) { $env:ZAP_MINUTES } else { "1" }
$ModuleRoot = Resolve-Path "$PSScriptRoot/../../.."
$ReportDir = Join-Path $ModuleRoot "target/zap"
$ConfigSource = Join-Path $ModuleRoot "src/test/resources/zap/zap-baseline.conf"

New-Item -ItemType Directory -Force -Path $ReportDir | Out-Null
Copy-Item $ConfigSource (Join-Path $ReportDir "zap-baseline.conf") -Force

docker run --rm `
  -v "${ReportDir}:/zap/wrk:rw" `
  ghcr.io/zaproxy/zaproxy:stable `
  zap-baseline.py `
  -t "$TargetUrl" `
  -m "$ZapMinutes" `
  -c zap-baseline.conf `
  -r zap-baseline-report.html `
  -w zap-baseline-report.md `
  -J zap-baseline-report.json
