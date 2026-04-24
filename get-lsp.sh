#!/bin/bash
set -eu

GROUP_ID=net/postchain/rell
ARTIFACT_ID=rell-toolbox-language-server
VERSION=$(grep '^rell = ' gradle/libs.versions.toml | sed -E 's/.*"([^"]+)".*/\1/')
FILE_NAME=$ARTIFACT_ID-$VERSION-all.jar
LSP_URL=https://gitlab.com/api/v4/projects/32802097/packages/maven/$GROUP_ID/$ARTIFACT_ID/$VERSION/$FILE_NAME
JAR_FILE=./language-server/$ARTIFACT_ID-$VERSION.jar
mkdir -p ./language-server

HTTP_STATUS=$(curl -s -o $JAR_FILE -w "%{http_code}" "$LSP_URL")

if [[ "$HTTP_STATUS" -ge 200 && "$HTTP_STATUS" -lt 300 ]]; then
    echo "Language server downloaded successfully with HTTP status: $HTTP_STATUS"
else
    echo "Language server download failed with HTTP status: $HTTP_STATUS"
    rm $JAR_FILE
    exit 1
fi
