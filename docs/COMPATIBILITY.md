# Rell version compatibility

Each plugin release supports **every Rell release from 0.16.0 up to the plugin's newest supported
version** with a version-exact grammar and a version-exact language server per project. Versions
below 0.16.0 are unsupported: no language server starts, and the editor recommends upgrading.

Compatibility is tracked per exact version because even patch releases change the language — for
example, lambda expressions entered the grammar in Rell 0.16.1, so a project declaring 0.16.0 must
see them flagged as errors. The Rell compiler itself cannot do this: its `compatibility` option
gates library members and behavior switches, never syntax, so only the matching toolchain produces
correct diagnostics.

## Version matrix

| Plugin version | Supported Rell versions | Bundled (newest) |
|----------------|-------------------------|------------------|
| 0.4.2          | 0.16.0, 0.16.1, 0.16.2  | 0.16.2           |
| 0.4.1          | 0.16.0, 0.16.1          | 0.16.1           |

## How the version is resolved

For every `.rell` file the plugin finds the nearest enclosing `chromia.yml` (walking up from the
file, never above the project content roots — mirroring how the language server anchors an index
root at each `chromia.yml` directory) and reads `compile.rellVersion` with the same parser the
Rell toolchain uses (`rell-toolbox-common`). Consequences of sharing the toolchain parser:

- only `chromia.yml` counts — `chromia.yaml` is ignored, exactly as `chr` ignores it;
- a blank `rellVersion:` value counts as absent;
- an unreadable or malformed YAML file counts as absent (with a log warning) — including
  comment-only files, which the toolchain also fails to parse.

The resolved version maps to behavior as follows:

| `compile.rellVersion`          | Grammar diagnostics      | Language server          | Banner                         |
|--------------------------------|--------------------------|--------------------------|--------------------------------|
| newest supported (e.g. 0.16.2) | newest (editor PSI)      | bundled newest           | —                              |
| older supported (e.g. 0.16.1)  | version-exact annotator  | downloaded version-exact | —                              |
| absent / no `chromia.yml`      | newest (editor PSI)      | bundled newest           | —                              |
| newer than the plugin knows    | newest (editor PSI)      | bundled newest           | "update the plugin"            |
| below 0.16.0                   | newest (editor PSI) only | **none**                 | "upgrade Rell" + one-click fix |

Editing `chromia.yml` re-resolves immediately: caches drop, highlighting re-runs, banners update,
and the Rell language servers restart so files re-route to the right toolchain.

Note: fresh `chr create-rell-dapp` projects currently pin `rellVersion: 0.14.5`, which lands in
the hard-cease row. The banner's quick-fix ("Set rellVersion to …") makes recovery one click.

## Version-exact diagnostics

The editor PSI (highlighting, completion, navigation) always uses the newest supported grammar — a
superset, so every file parses. On top of that:

- for an **older supported** version, `RellVersionSyntaxAnnotator` runs that version's own ANTLR
  parser (generated at build time from that release's `Rell.g4`) and reports its syntax errors as
  "Not valid in Rell X.Y.Z", and the version-matched language server reports the same at compiler
  level;
- for the **newest** version, PSI syntax errors and the bundled server already are version-exact;
- **below the floor**, only the newest grammar's own highlighting and syntax errors remain — code
  using legacy syntax the newest grammar dropped may show parse errors there.

## Language-server runtimes

The newest supported server ships inside the plugin. Older supported servers are downloaded on
first use into `<IDE system dir>/rell-lsp/<version>/` — exactly the artifacts pinned by the
build-generated lockfile (`rell/lsp-lockfiles/<version>.lock`), SHA-256-verified, reused across
IDE sessions. Until the download completes (or offline), affected projects simply have no language
server; a wrong-version server is never substituted. A failure shows a notification with Retry.

## Release checklist for a new Rell version

1. Bump `rell` in `gradle/libs.versions.toml`.
2. Append the new version to `supportedRellVersions` in `build.gradle.kts` (the build fails if the
   two drift).
3. Add the previous newest version to `VersionedRellParsers` (its grammar now generates
   automatically; `VersionedRellParsersTest` fails until the entry exists).
4. Add a `<server>` + `<languageMapping>` + `<semanticTokensColorsProvider>` triple for the
   previous newest version in `plugin.xml`, with `Rell<v>DocumentMatcher` /
   `Rell<v>LanguageServerFactory` subclasses (`RellLspRoutingTest` fails until they exist).
5. Extend the version matrix in this document and CHANGELOG.md.
