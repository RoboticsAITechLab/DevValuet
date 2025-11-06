param(
    [string]$OldPass,
    [string]$NewPass,
    [switch]$DryRun,
    [switch]$NoBackup,
    [string]$BackupPath
)

$jar = "target/cockpit-core-0.0.1-SNAPSHOT.jar"
if (-not (Test-Path $jar)) {
    Write-Host "Jar not found at $jar. Build first: mvn -pl cockpit-core package"
    exit 2
}

$cmd = "java -jar $jar --migrate-keystore=true --passphrase=$OldPass --new-passphrase=$NewPass --dry-run=$($DryRun.IsPresent) --backup=$(!$NoBackup.IsPresent)"
if ($BackupPath) { $cmd += " --backup-path=$BackupPath" }
Write-Host "Running: $cmd"
iex $cmd
