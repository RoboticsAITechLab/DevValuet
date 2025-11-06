#!/usr/bin/env bash
OLD_PASS=${1}
NEW_PASS=${2}
DRY_RUN=${3:-false}
NO_BACKUP=${4:-false}
BACKUP_PATH=${5}

if [ -z "$OLD_PASS" ] || [ -z "$NEW_PASS" ]; then
  echo "Usage: $0 <old-pass> <new-pass> [dry-run:true|false] [no-backup:true|false] [backup-path]"
  exit 2
fi

JAR=target/cockpit-core-0.0.1-SNAPSHOT.jar
if [ ! -f "$JAR" ]; then
  echo "Jar not found at $JAR. Build first: mvn -pl cockpit-core package"
  exit 2
fi

CMD=(java -jar "$JAR" --migrate-keystore=true --passphrase="$OLD_PASS" --new-passphrase="$NEW_PASS" --dry-run=$DRY_RUN --backup=$([ "$NO_BACKUP" = "true" ] && echo false || echo true))
if [ -n "$BACKUP_PATH" ]; then
  CMD+=(--backup-path="$BACKUP_PATH")
fi

echo "Running: ${CMD[*]}"
"${CMD[@]}"
