# rell-jetbrains

## Repository

The canonical repository is `gitlab.com/chromaway/rell-jetbrains`; CI, releases and issues live
there. The `bitbucket` remote is a frozen mirror whose `main` carries nothing but a tombstone
README — never push to it, and never merge from it.

## Bumping the bundled Rell version

1. Bump `rell` in `gradle/libs.versions.toml`. That drives the editor grammar, the bundled language
   server, and the build-generated `rell/bundled-version.txt` that `BundledRellVersion` reads.
2. Extend the version matrix in `docs/COMPATIBILITY.md` and CHANGELOG.md.

The plugin bundles one language server for every project. A project's `compile.rellVersion` selects
the compiler's compatibility mode inside that server, so an older project gets that release's
diagnostics without a second toolchain — there is nothing to download and no version-exact grammar
to generate.

## Releases and CHANGELOG.md

Every published plugin version is tagged in git with the plain version number (e.g. `0.4.0`). Treat tagged versions as released and immutable:

- Never edit, rename, or delete a `## [x.y.z]` section in CHANGELOG.md whose version has a git tag — those are the shipped release notes.
- Check **remote** tags before modifying CHANGELOG.md: `git ls-remote --tags gitlab` (the remote is named `gitlab`, not `origin`). Local tags can be stale and miss recent releases — do not trust `git tag` alone.
- The section matching the current `pluginVersion` in gradle.properties is the in-progress release (unless that version is tagged on the remote): new entries go there, not into a new `[Unreleased]` section.
- The Gradle changelog plugin reads the section matching `pluginVersion`, so the new section header must match the new version exactly.
