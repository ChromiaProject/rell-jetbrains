# rell-jetbrains

## Adding support for a new Rell version

1. Bump `rell` in `gradle/libs.versions.toml`.
2. Append the new version to `supportedRellVersions` in `build.gradle.kts` (the build fails if the
   two drift).
3. Add the previous newest version to `VersionedRellParsers` (its grammar now generates
   automatically; `VersionedRellParsersTest` fails until the entry exists).
4. Bump the newest-version literals in the tests that assert against it by string:
   `RellLspRoutingTest`, `RellVersionResolverTest`, `RellVersionSyntaxAnnotatorTest`,
   `RellVersionEditorNotificationProviderTest`. `plugin.xml` needs nothing — the single
   `platform.lsp.integrationProvider` (`RellLspIntegrationProvider`) routes each file to
   `RellLspClientDescriptor.versioned`, built per version at runtime.
5. Extend the version matrix in `docs/COMPATIBILITY.md` and CHANGELOG.md.

Older language servers are downloaded on first use into `<IDE system dir>/rell-lsp/<version>/`,
pinned and SHA-256-verified by the build-generated lockfile `rell/lsp-lockfiles/<version>.lock`; a
wrong-version server is never substituted.

## Releases and CHANGELOG.md

Every published plugin version is tagged in git with the plain version number (e.g. `0.4.0`). Treat tagged versions as released and immutable:

- Never edit, rename, or delete a `## [x.y.z]` section in CHANGELOG.md whose version has a git tag — those are the shipped release notes.
- Check **remote** tags before modifying CHANGELOG.md: `git ls-remote --tags bitbucket` (the remote is named `bitbucket`, not `origin`). Local tags can be stale and miss recent releases — do not trust `git tag` alone.
- The section matching the current `pluginVersion` in gradle.properties is the in-progress release (unless that version is tagged on the remote): new entries go there, not into a new `[Unreleased]` section.
- The Gradle changelog plugin reads the section matching `pluginVersion`, so the new section header must match the new version exactly.
