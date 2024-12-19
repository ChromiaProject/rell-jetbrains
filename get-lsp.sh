#!/bin/bash
set -eu

GROUP_ID=net/postchain/rell/toolbox
ARTIFACT_ID=rell-language-server
VERSION=0.4.16
FILE_NAME=rell-language-server-$VERSION-all.jar
LSP_URL=https://gitlab.com/api/v4/projects/51303085/packages/maven/$GROUP_ID/$ARTIFACT_ID/$VERSION/$FILE_NAME
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
