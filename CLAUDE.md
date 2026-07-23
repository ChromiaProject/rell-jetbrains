# rell-jetbrains

## Releases and CHANGELOG.md

Every published plugin version is tagged in git with the plain version number (e.g. `0.4.0`). Treat tagged versions as released and immutable:

- Never edit, rename, or delete a `## [x.y.z]` section in CHANGELOG.md whose version has a git tag — those are the shipped release notes.
- Check Git tags before modifying CHANGELOG.md.
- The Gradle changelog plugin reads the section matching `pluginVersion`, so the new section header must match the new version exactly.
