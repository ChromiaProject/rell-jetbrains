# Changelog

## [0.4.7]
### Added
- Chromia settings files are checked against the Chromia schema. This now covers settings files whatever they are named.
- An unresolved module that looks like a declared-but-not-installed library dependency now gets a
  banner and an Alt+Enter action suggesting `chr install`, instead of just "Module not found"
### Fixed
- Rename (Shift+F6) was greyed out for Rell symbols. The platform's default LSP customization only
  runs rename for plain-text/TextMate files; Rell has no PSI-based rename of its own, so it needs
  the same opt-in the plugin already does for formatting

## [0.4.6]
### Changed
- Function calls are italic, so calling a declared function looks different from invoking a function value held in
  a variable
### Fixed
- Alt+Enter no longer lists the same language-server action twice: quick fixes stay on the quick-fix
  channel and everything else stays on the intention channel
- The preview shown next to a language-server action renders again instead of failing silently
- Syntax errors in injected Rell (a ```` ```rell ```` fence, say) read the way the Rell compiler
  words them &mdash; `Name expected, got '123'` rather than
  ANTLR's `extraneous input '123' expecting RULE_ID`. ANTLR names no longer leak into the editor

## [0.4.5]
### Added
- Add Rell version 0.16.5
- The Project view draws Rell source trees semantically: the directory a settings file compiles from carries
  a source-root icon, every directory under it carries a package icon and is labelled with the module namespace it
  stands for, so `src/main/core` reads as `main.core`
### Changed
- The plugin no longer declares an upper IDE version bound, so it stays installable on IDE releases
  published after it
### Fixed
- Rell code is semantically highlighted again: declaration names of functions, queries and operations share the
  Function declaration colour,
  annotations use the Metadata colour, module-level constants use the Static field colour. 
  The IDE had been asking the language server for  semantic tokens only in plain-text files,
  so none of the Rell colours in Settings | Editor | Color Scheme | Rell were ever applied
- Code | Reformat Code works on Rell files again. The action was disabled, and hidden in context
  menus, because the IDE only routed formatting to the language server when it registered
  the capability dynamically

## [0.4.4]
### Added
- Add Rell version 0.16.4
- The Chromia tool window warns when the active settings file declares a Rell version newer than
  the Chromia CLI supports. The CLI's `chr --version` output is probed in the background and
  remembered; the Test button in Settings | Tools | Rell refreshes it too
### Fixed
- Rell snippets injected into other files &mdash; a ```` ```rell ```` fence in Markdown, say &mdash;
  are no longer flagged as broken modules. A fence holding statements or a trailing expression now
  parses as such, while one holding a whole module still parses as a module and genuinely broken
  snippets are still reported
- A malformed `compile.rellVersion` no longer surfaces an IDE error from the language server when
  test run markers are computed &mdash; failed custom requests now degrade to no markers
- A syntax error is reported once, with the language server's human-readable message. The editor
  grammar's raw ANTLR messages (`missing RULE_ID at '{'`) no longer stack on top of it in the same
  popup; they remain only where no language server runs, such as injected snippets

## [0.4.3]
### Added
- Add Rell version 0.16.3
- Support for arbitrary Chromia settings files, mirroring `chr --settings`: any `*.yml` with a
  top-level `blockchains` section will be considered
- Status-bar widget naming the settings file that governs the current file and switching it in one
  click &mdash; also selectable from the tool window, and passed as `--settings` to Chromia commands
- Settings files declaring an unsupported Rell version are flagged on their own editor, with the
  one-click `rellVersion` fix
### Changed
- A file is now governed by the settings file whose source tree contains it, rather than the
  nearest enclosing `chromia.yml`
- Version banners no longer appear just because several settings files disagree on the version
  &mdash; the status-bar widget shows and changes which one governs. Banners remain for unsupported
  versions and for versions newer than the plugin knows
- Semantic coloring now comes solely from the language server; the "Collection type" and "Global
  function call" entries, which nothing could ever color, are gone from Settings | Editor |
  Color Scheme | Rell
### Fixed
- Refresh in the Chromia tool window re-runs project discovery, so added or removed Chromia
  projects appear and disappear
- Opening a project no longer builds the whole Chromia tool window off-screen just to decide
  whether to show it

## [0.4.2]
### Added
- Chromia project generator in File | New | Project &mdash; pick a `chr create-rell-dapp`
  template (minimal, plain, plain-multi, plain-library, asset-management), optionally with a
  dev container, and the wizard generates the dapp via the configured Chromia CLI
- Rell Language Server with Rell version 0.16.2; 0.16.1 joins the older supported versions with a
  version-exact grammar and an on-demand downloaded language server (see
  [docs/COMPATIBILITY.md](docs/COMPATIBILITY.md))
- `.rell_lint` and `.rell_format` open with EditorConfig syntax highlighting (they use EditorConfig
  syntax) when the bundled EditorConfig plugin is enabled
- Reload bar on edited `.rell_lint` and `.rell_format` editors, like the `chromia.yml` one &mdash;
  the linter reads these files from disk, so the bar offers a one-click save that applies the
  change immediately
### Changed
- The language server integration now runs on the IntelliJ Platform's built-in LSP client instead
  of the LSP4IJ plugin &mdash; installing LSP4IJ is no longer required, and signature help,
  structure view, semantic highlighting, and the language-services status-bar widget come from the
  platform. Batch inspection runs (Code | Inspect Code) report server diagnostics through the new
  Rell | Language server diagnostics inspection, replacing LSP4IJ's Language Servers | Diagnostics
- Minimum supported IDE version raised to 2026.1.4
### Fixed
- "Disable linter for this line" no longer appears twice in the quick-fix popup &mdash; the
  language server answers quickfix-only code-action requests with its `source` actions too, so the
  plugin now keeps them out of the diagnostic's quick-fix group and shows them only as intentions
### Removed
- Support for Rell 0.16.0 &mdash; its language server fails to index a workspace containing any
  syntax error, and this cannot be fixed retroactively. Projects declaring `rellVersion: 0.16.0`
  now get the upgrade banner with the one-click `rellVersion` fix
- Dependency on the bundled Terminal plugin &mdash; unused since Chromia CLI commands moved from
  the Terminal tool window to Run tool window tabs. The plugin now also loads in IDEs where the
  Terminal plugin is disabled

## [0.4.1]
### Added
- Rell Language Server with Rell version 0.16.1
- Rell version compatibility mode (see [docs/COMPATIBILITY.md](docs/COMPATIBILITY.md)): `compile.rellVersion` from the
  nearest `chromia.yml` selects a version-exact toolchain per project, supporting every Rell
  release from 0.16.0 up to the plugin's newest supported version &mdash; version-true syntax errors
  (e.g. lambdas are flagged in 0.16.0 projects) and a version-matched language server, downloaded
  on demand for older versions
- Editor banners for unsupported (below 0.16.0) Rell versions &mdash; with a one-click
  `rellVersion` upgrade fix &mdash; and for versions newer than the plugin knows
- Support for IntelliJ Platform 2026.2
- `PRIVACY_POLICY.md`, documenting exactly what the plugin sends, when it is sent, and who receives it
- A notice in the IDE error dialog describing what a crash report contains before you submit it
### Changed
- Raised the minimum supported IDE version to 2025.3.6 (build 253.33813)
- Bumped Kotlin to 2.4.0
- `chromia.yaml` (with the `.yaml` extension) is no longer treated as a project marker in the
  Chromia tool window &mdash; the Rell toolchain only reads `chromia.yml`
- Crash reports submitted from the error dialog no longer carry the machine hostname, and absolute
  paths in them are rewritten to drop your home directory &mdash; `/Users/jsmith/project` is sent as
  `~/project`
### Fixed
- The bundled Rell language server no longer reports errors on its own. Versions 0.4.0 and earlier
  shipped a language server that automatically uploaded every error it logged (including
  absolute file paths) to ChromaWay's error tracker, without asking and with no way to opt
  out. Crash reporting is now opt-in only, through the IDE's error dialog
- Suppressed the `sun.misc.Unsafe` deprecation warnings printed by the language server on JDK 23+

## [0.4.0]
### Added
- Rell Language Server with Rell version 0.16.0
- Support for IntelliJ Platform 2026.1 (built against the unified IntelliJ IDEA 2026.1.1 distribution)
### Changed
- Reworked the editor parser/lexer to consume Rell's ANTLR grammar via antlr4-intellij-adaptor, replacing the removed GrammarKit BNF generator
- Build against the unified IntelliJ IDEA distribution; Community is no longer published separately since 2025.3
- Resolve the bundled Terminal plugin via `platformBundledPlugins` instead of a pinned marketplace build, so it tracks the platform version
- Bumped IntelliJ Platform Gradle Plugin to 2.11.0

## [0.3.5]
### Added
- Rell Language Server with Rell version 0.15.4
- Better settings UI for Chromia CLI

## [0.3.4]
### Added
- Support for Jetbrains 261 releases

## [0.3.3]
### Fixed
- Improved error reporting reliability

## [0.3.2]
### Fixed
- Adds safeguard check to see if server is running before getting server item

## [0.3.1]

### Added
- Rell Language Server with Rell version 0.15.2
### Fixed
- Optimized test discovery to avoid plugin freeze

## [0.3.0]

### Added
- Rell Language Server with Rell version 0.14.16
- Intellij 2025.3 support
- LSP4IJ 0.19.0 support

## [0.2.9]

### Added
- Rell Language Server with Rell version 0.14.15

## [0.2.8]

### Fixed
- Test run configuration name
- Test runner reporting runtime errors

## [0.2.7]

### Added
- Run all tests in Rell module
- Settings to set global chromia cli path

### Fixed
- Freeing UI
- Report compilation error as failure when running tests

## [0.2.6]

### Fixed
- Test runner status reporting
- Diagnostics reporting

## [0.2.5]

### Fixed
- Rell language server:
  * Ensure LSP takes new dependencies into account after `chr install`
  * Inlay hints related bugs

## [0.2.4]

### Fixed
- Fix freezing UI while language server is indexing project

## [0.2.3]

### Fixed
- DevContainer related bug

## [0.2.2]

### Added
- Inlay hints implementation
- DevContainer template support
- Rell language server version 0.8.5

## [0.2.1]

### Fixed
- Test runner docker support 

## [0.2.0]

### Added
- Rell language server version 0.8.4
- Test runner for Rell language
- Chromia sidebar tool window

## [0.1.10]

### Added
- Rell language server version 0.8.3
- LSP4IJ version 0.13.0

## [0.1.9]

### Added
- Rell language server version 0.8.2

## [0.1.8]

### Added
- Rell language server version 0.8.1

## [0.1.7]

### Added
- Rell language server version 0.7.1
- Intellij platform 252 support

## [0.1.6]

### Added
- Rell language server version 0.7.0
  - Fixed NullPointerException when dirtyFiles are missing from disk.
  - Enhanced didChangeWatchFileEvents to use efficient batching for grouped events.
  - Improved diagnostic publisher with caching to avoid overloading the client.

## [0.1.5]

### Added
- Rell language server version 0.6.0
- LSP code formatting support
- Keep alive language server when last rell file is closed
- Invalidate index cache action
- Set language server JVM min/max heap size
- LSP4IJ version 0.12.0

## [0.1.4]

### Added

- Rell language server version 0.5.4

## [0.1.3]

### Added

- Enable/Disable Index cache checkbox to Rell Settings
- Rell language server version 0.5.2

## [0.1.2]

### Added

- Module import completion.
- Snippet completion.
- Granular semantic highlighting for Rell language.

## [0.1.1]

### Added

- Multi project workspace support

## [0.1.0]

### Added

- Integrated LSP server for Rell language using LSP4IJ

## [0.0.14]

### Update

- Intellij platform 2024.3

## [0.0.13]

### Update

- Sentry error reporting

## [0.0.12]

### Update

- Compatible with IntelliJ Platform 242.*

## [0.0.11]

### Added

- Rell 0.13.14 support

## [0.0.10]

### Added

- Trailing comma support https://gitlab.com/chromaway/rell/-/blob/version-0.13.12/doc/release-notes/0.14.0.txt?ref_type=heads#L516
