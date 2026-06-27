#!/usr/bin/env bash
set -euo pipefail

TARGET_PROTOCOL="${TARGET_PROTOCOL:-http}"
TARGET_HOST="${TARGET_HOST:-localhost}"
TARGET_PORT="${TARGET_PORT:-8080}"
THREADS="${THREADS:-10}"
RAMP_UP_SECONDS="${RAMP_UP_SECONDS:-30}"
DURATION_SECONDS="${DURATION_SECONDS:-60}"
MAX_RESPONSE_MS="${MAX_RESPONSE_MS:-1500}"
AUTH_TOKEN="${AUTH_TOKEN:-}"

mvn -pl jmeter-performance-tests verify \
  -DtargetProtocol="${TARGET_PROTOCOL}" \
  -DtargetHost="${TARGET_HOST}" \
  -DtargetPort="${TARGET_PORT}" \
  -Dthreads="${THREADS}" \
  -DrampUpSeconds="${RAMP_UP_SECONDS}" \
  -DdurationSeconds="${DURATION_SECONDS}" \
  -DmaxResponseMs="${MAX_RESPONSE_MS}" \
  -DauthToken="${AUTH_TOKEN}"
