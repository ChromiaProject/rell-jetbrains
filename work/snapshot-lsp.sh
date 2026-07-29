#!/usr/bin/env bash
# Run the sandbox IDE with the published Rell snapshot language server instead of the release
# pinned in libs.versions.toml, so server-side changes on Rell's dev branch can be tested through
# the real plugin without building Rell locally. Only the language-server runtime is swapped —
# the editor grammar, the chromia.yml parser and the compatibility-mode lockfiles stay pinned.
#
# Rell versions its snapshot as the next minor over the latest release: 0.X.Y -> 0.(X+1).0-SNAPSHOT.
#
#   work/snapshot-lsp.sh                                 # runIde against the current snapshot
#   RELL_SNAPSHOT=0.18.0-SNAPSHOT work/snapshot-lsp.sh   # a different snapshot version
#
# Extra arguments are passed to Gradle.
set -euo pipefail

ROOT=$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)

RELEASE=$(sed -n 's/^rell = "\([^"]*\)".*/\1/p' "$ROOT/gradle/libs.versions.toml")
VERSION=${RELL_SNAPSHOT:-$(IFS=. read -r major minor _ <<< "$RELEASE"; echo "$major.$((minor + 1)).0-SNAPSHOT")}

echo "Language server: net.postchain.rell:rell-toolbox-language-server:$VERSION"
exec "$ROOT/gradlew" -p "$ROOT" -PrellLspVersion="$VERSION" runIde "$@"
