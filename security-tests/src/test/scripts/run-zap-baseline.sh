#!/usr/bin/env bash
set -euo pipefail

TARGET_URL="${SECURITY_TARGET_URL:-http://localhost:3000}"
ZAP_MINUTES="${ZAP_MINUTES:-1}"
REPORT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../../.." && pwd)/target/zap"
CONFIG_SOURCE="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)/resources/zap/zap-baseline.conf"

mkdir -p "${REPORT_DIR}"
cp "${CONFIG_SOURCE}" "${REPORT_DIR}/zap-baseline.conf"

docker run --rm \
  -v "${REPORT_DIR}:/zap/wrk:rw" \
  ghcr.io/zaproxy/zaproxy:stable \
  zap-baseline.py \
  -t "${TARGET_URL}" \
  -m "${ZAP_MINUTES}" \
  -c zap-baseline.conf \
  -r zap-baseline-report.html \
  -w zap-baseline-report.md \
  -J zap-baseline-report.json
