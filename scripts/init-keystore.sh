#!/usr/bin/env bash
ALIAS=${1:-token-encryption}
BASE64_KEY=${2}
PASSPHRASE=${3}
FORCE=${4:-false}

if [ -z "$BASE64_KEY" ] || [ -z "$PASSPHRASE" ]; then
  echo "Usage: $0 <base64-key> <passphrase> [force:true|false]"
  exit 2
fi

JAR=target/cockpit-core-0.0.1-SNAPSHOT.jar
if [ ! -f "$JAR" ]; then
  echo "Jar not found at $JAR. Build first: mvn -pl cockpit-core package"
  exit 2
fi

java -jar "$JAR" --init-keystore=true --alias="$ALIAS" --key="$BASE64_KEY" --passphrase="$PASSPHRASE" --force=$FORCE
