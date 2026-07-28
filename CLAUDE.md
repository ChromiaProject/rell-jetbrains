# rell-jetbrains

## Releases and CHANGELOG.md

Every published plugin version is tagged in git with the plain version number (e.g. `0.4.0`). Treat tagged versions as released and immutable:

- Never edit, rename, or delete a `## [x.y.z]` section in CHANGELOG.md whose version has a git tag — those are the shipped release notes.
- Check **remote** tags before modifying CHANGELOG.md: `git ls-remote --tags bitbucket` (the remote is named `bitbucket`, not `origin`). Local tags can be stale and miss recent releases — do not trust `git tag` alone.
- The section matching the current `pluginVersion` in gradle.properties is the in-progress release (unless that version is tagged on the remote): new entries go there, not into a new `[Unreleased]` section.
- The Gradle changelog plugin reads the section matching `pluginVersion`, so the new section header must match the new version exactly.
