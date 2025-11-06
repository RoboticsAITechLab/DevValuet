param(
    [string]$Alias = "token-encryption",
    [string]$Base64Key,
    [string]$Passphrase,
    [switch]$Force
)

if (-not $Base64Key) {
    Write-Host "Please set Base64 key via --Base64Key or set environment variable DEVVALUET_KEY_VALUE"
    exit 2
}
if (-not $Passphrase) {
    Write-Host "Please provide a passphrase via --Passphrase or set DEVVALUET_KEYSTORE_PASSPHRASE"
    exit 2
}

$jar = "target/cockpit-core-0.0.1-SNAPSHOT.jar"
if (-not (Test-Path $jar)) {
    Write-Host "Jar not found at $jar. Build first: mvn -pl cockpit-core package"
    exit 2
}

$cmd = "java -jar $jar --init-keystore=true --alias=$Alias --key=$Base64Key --passphrase=$Passphrase --force=$($Force.IsPresent)"
Write-Host "Running: $cmd"
iex $cmd
