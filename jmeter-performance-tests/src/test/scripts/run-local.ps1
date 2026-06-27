$ErrorActionPreference = "Stop"

$TargetProtocol = if ($env:TARGET_PROTOCOL) { $env:TARGET_PROTOCOL } else { "http" }
$TargetHost = if ($env:TARGET_HOST) { $env:TARGET_HOST } else { "localhost" }
$TargetPort = if ($env:TARGET_PORT) { $env:TARGET_PORT } else { "8080" }
$Threads = if ($env:THREADS) { $env:THREADS } else { "10" }
$RampUpSeconds = if ($env:RAMP_UP_SECONDS) { $env:RAMP_UP_SECONDS } else { "30" }
$DurationSeconds = if ($env:DURATION_SECONDS) { $env:DURATION_SECONDS } else { "60" }
$MaxResponseMs = if ($env:MAX_RESPONSE_MS) { $env:MAX_RESPONSE_MS } else { "1500" }
$AuthToken = if ($env:AUTH_TOKEN) { $env:AUTH_TOKEN } else { "" }

mvn -pl jmeter-performance-tests verify `
  -DtargetProtocol="$TargetProtocol" `
  -DtargetHost="$TargetHost" `
  -DtargetPort="$TargetPort" `
  -Dthreads="$Threads" `
  -DrampUpSeconds="$RampUpSeconds" `
  -DdurationSeconds="$DurationSeconds" `
  -DmaxResponseMs="$MaxResponseMs" `
  -DauthToken="$AuthToken"
