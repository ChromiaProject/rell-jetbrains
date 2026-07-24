#!/usr/bin/env bash
# Run the sandbox IDE with a language server built from a local Rell clone instead of the
# published release, so server-side changes can be tested through the real plugin.
#
# The clone lives in rell-local/ (gitignored) and is included into this build as a composite
# build; see the dependencySubstitution block in settings.gradle.kts. Gradle drives both builds
# with a single version, and Rell needs a newer one than this project's wrapper ships, so the
# clone's wrapper runs the build.
#
#   work/local-lsp.sh              # clone if missing, then runIde against it
#   work/local-lsp.sh --update     # fetch and fast-forward the clone first
#   RELL_REF=my-branch work/local-lsp.sh --update
#
# Extra arguments are passed to Gradle.
set -euo pipefail

ROOT=$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)
CLONE=$ROOT/rell-local
REPO=${RELL_REPO:-git@gitlab.com:chromaway/rell.git}
REF=${RELL_REF:-dev}

UPDATE=false
GRADLE_ARGS=()
for arg in "$@"; do
    if [[ $arg == --update ]]; then UPDATE=true; else GRADLE_ARGS+=("$arg"); fi
done

if [[ ! -d $CLONE/.git ]]; then
    echo "Cloning $REPO into $CLONE ($REF)"
    git clone --branch "$REF" "$REPO" "$CLONE"
elif [[ $UPDATE == true ]]; then
    echo "Updating $CLONE ($REF)"
    git -C "$CLONE" fetch --prune origin
    if git -C "$CLONE" rev-parse --verify --quiet "refs/remotes/origin/$REF" > /dev/null; then
        git -C "$CLONE" checkout "$REF"
        git -C "$CLONE" merge --ff-only "origin/$REF"
    else
        # A tag or commit: no branch to fast-forward, check it out detached.
        git -C "$CLONE" checkout --detach "$REF"
    fi
fi

echo "Rell clone at $(git -C "$CLONE" rev-parse --short HEAD) ($(git -C "$CLONE" rev-parse --abbrev-ref HEAD))"

# bash 3.2 (macOS) treats an empty array as unbound under `set -u`.
exec "$CLONE/gradlew" -p "$ROOT" -PrellLocal="$CLONE" runIde ${GRADLE_ARGS[@]+"${GRADLE_ARGS[@]}"}
