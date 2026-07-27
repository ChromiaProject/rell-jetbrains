# Core Components

[← Previous: Getting Started](03-getting-started.md) | [Next: Testing Strategy →](05-testing.md)

---

This document provides a detailed breakdown of the plugin's core components, explaining what each does, why it exists, and how it works.

---

## 1. Language Server Integration (`lsp4ij/`)

LSP4IJ is a LSP and DAP client for JetBrains IDEs.

LSP4IJ Repository: https://github.com/redhat-developer/lsp4ij

**Purpose:** Bridge between IDE and Rell LSP server.

### RellLanguageServerFactory.kt

**Role:** Creates the connection provider for the bundled (newest supported) Rell server.

**Logic:**
```kotlin
override fun createConnectionProvider(project: Project): StreamConnectionProvider {
    val useSocket = System.getProperty("rell.lsp.useSocket", "false").toBoolean()

    return if (useSocket) {
        RellSocketLanguageServer(project)
    } else {
        RellLanguageServer(project)
    }
}
```

It also supplies the language client (`RellLanguageClient`), the custom server interface
(`RellServerApi`), and the client features (`RellLspClientFeatures`).

**Why Two Modes?**
- **Subprocess mode (`RellLanguageServer`):** Production use. Plugin controls server lifecycle.
- **Socket mode (`RellSocketLanguageServer`):** Development. External server runs on port 5008, plugin connects to it. Useful for debugging server with IDE. Enabled by `-Drell.lsp.useSocket=true`, which `./gradlew runIde -PuseSocket` passes.

Older supported Rell versions get their own factories — see
[Version compatibility](#3-version-compatibility-chromia).

### RellLanguageServer.kt

**Role:** Launches an LSP server runtime as a subprocess, with `<lib dir>/*` on the classpath and
`net.postchain.rell.toolbox.lsp.StdioMainKt` as the main class. The lib dir is a constructor
parameter: it defaults to the bundled one, and the versioned factories pass a downloaded one.

**Design Decision - JVM Heap Size:**
- Uses `JvmHeapSizeManager.determineMaxHeapSizeMB()` to calculate heap, defaulting to 2048 MB
- **Why:** Large Rell projects can have many files. Generous heap prevents OutOfMemoryError during indexing.

**Other launch flags:** `-Dlog4j2.configurationFile` pointing at the bundled
`log4j2-override.properties` — the log4j2 config inside the server jar wires a Sentry appender into
the root logger, which would upload every logged error without asking. On JDK 23+,
`--sun-misc-unsafe-memory-access=allow` silences a per-launch deprecation warning.

**Initialization options:** the `indexCaching` setting and the IDE's inlay-hint settings.

**Where Is the LSP Runtime?**
- The bundled one is resolved by Gradle from the `rell` version in `gradle/libs.versions.toml` and
  copied into `<plugin dir>/language-server/` by `prepareSandbox`. No manual download step.
- Runtimes for older supported versions live in `<IDE system dir>/rell-lsp/<version>/`, downloaded on
  demand (see `RellLspRuntimeManager`).

### RellServerApi.kt

**Role:** Defines custom LSP extensions beyond standard LSP protocol.

**Custom Methods:**

| Method                  | Purpose                                        | Returns              |
|-------------------------|------------------------------------------------|----------------------|
| `rell/invalidateCaches` | Clears server's workspace index cache          | `Boolean`            |
| `rell/listTestFiles`    | Returns all Rell test files in workspace       | `List<RellTestFile>` |
| `rell/getTestFile`      | Returns the test file at a URI, if it is one   | `RellTestFile?`      |
| `rell/listTestCases`    | Returns test cases in a specific test file     | `List<RellTestCase>` |
| `rell/addToProject`     | Adds Rell feature template to existing project | `Void`               |

**Why Custom Methods?**
Standard LSP doesn't cover IDE-specific features like test discovery or project templates. These extensions allow the plugin to provide richer functionality.

### RellLanguageClient.kt

**Role:** Extends `IndexAwareLanguageClient` so LSP work participates in the IDE's indexing/dumb-mode
machinery rather than running while the index is unavailable.

### RellLspClientFeatures.kt

**Role:** Tunes LSP4IJ's per-feature behavior. It keeps the server alive when no Rell file is open,
disables the document-color feature (the Rell server never provides colors, and LSP4IJ would
otherwise queue a `textDocument/documentColor` request on every highlighting pass), and resolves file
URIs from disk so a rename reports the new URI.

### RellSemanticTokensColorProvider.kt

**Role:** Maps LSP semantic token types — and Rell's custom token modifiers — to IDE color attributes.

**Example Mappings:**
```kotlin
SemanticTokenTypes.Namespace -> RellColor.NAMESPACE_NAME
SemanticTokenTypes.Struct    -> RellColor.STRUCT_NAME
SemanticTokenTypes.Class     -> RellColor.ENTITY_NAME  // with the "rell-entity" modifier
SemanticTokenTypes.Function  -> RellColor.QUERY_NAME   // with the "rell-query" modifier
```

The `RellTokenModifier` enum lists the server's Rell-specific modifiers (`rell-entity`,
`rell-object`, `rell-query`, `rell-operation`, `rell-local_val`, …), which is how one LSP token type
splits into several IDE colors. Unmapped types fall through to LSP4IJ's default provider.

**Why Needed:** LSP semantic tokens are standardized strings.
IDE needs to map them to actual TextAttributesKey objects for rendering.
[More details](https://microsoft.github.io/language-server-protocol/specifications/lsp/3.17/specification/#textDocument_semanticTokens)

---

## 2. Language Definition

**Purpose:** Defines Rell as a language to JetBrains platform.

### RellLanguage.kt

```kotlin
object RellLanguage : Language("Rell") {
    override fun isCaseSensitive() = true
}
```

**Why `object`?** Singleton pattern. Only one Language instance should exist per language.

### RellFileType.kt

**Role:** Associates `.rell` file extension with Rell language.

```kotlin
override fun getDefaultExtension() = "rell"
override fun getIcon() = RellIcons.FILE
```

### RellParserDefinition.kt

**Role:** Provides parser implementation to IDE.

**Key Methods:**

| Method              | Returns              | Purpose                                            |
|---------------------|----------------------|----------------------------------------------------|
| `createLexer()`     | `RellLexerAdapter`   | `ANTLRLexerAdaptor` wrapping the ANTLR `RellLexer` |
| `createParser()`    | `ANTLRParserAdaptor` | Drives the ANTLR `RellParser` (root rule `file`)   |
| `getFileNodeType()` | `IFileElementType`   | Root PSI node type                                 |
| `createElement()`   | `ANTLRPsiNode`       | Wraps each parser-rule node as a PSI element       |

**Generated Code:** `RellParser`/`RellLexer` are generated by the ANTLR Gradle plugin into `build/generated-src/antlr/main/` (not in Git) from `Rell.g4`, which is extracted at build time from the `net.postchain.rell:frontend:<rell>:sources` jar (`extractRellGrammar` task) so it always matches the `rell` version. Token and parser-rule `IElementType`s are bridged to the IDE by `RellPsiElementTypes` (via antlr4-intellij-adaptor's `PSIElementTypeFactory`); `RellPsiNavigation` provides rule/token helpers for consumers (folding, annotator, test markers).

The adaptor's `lexer`/`parser` packages (tag 0.2.0) are **vendored** under `src/main/java/org/antlr/intellij/adaptor/` — 0.2.0 was never published to Maven, and only those two self-contained packages are needed (PSI nodes use the platform's `ASTWrapperPsiElement`). They depend only on `antlr4-runtime`, pinned to the same version as the generator.

### Editor behavior built on the PSI

`RellSyntaxHighlighter` (lexer-level coloring), `RellAdvancedSyntaxHighlightingAnnotator`,
`RellFoldingBuilder`, `RellPairedBraceMatcher`, `RellCommenter`, `RellQuoteTokenHandler` and
`RellSpellcheckingStrategy` all navigate the generated rule/token indices rather than hard-coded IDs.

### Grammar Update Process

**Why Updates?** Rell language evolves (new keywords, syntax changes). The plugin must stay synchronized.

**Process (from [update_grammar.md](update_grammar.md)):** bump the `rell` version — the build extracts the matching `Rell.g4` and regenerates the parser/lexer automatically (no vendored grammar, no manual BNF/JFlex steps). Verify with `RellAntlrGrammarTest` and a `runIde` smoke test.

### Lexer/Parser Numeric Validation

**Why in the grammar?** Rell supports arbitrary-precision numbers (BigInteger, BigDecimal). `Rell.g4`'s `@parser::members` block validates numeric literals (range/precision) and reports out-of-range values via `notifyErrorListeners`, so the behavior comes straight from the upstream grammar rather than a hand-maintained lexer.

---

## 3. Version Compatibility (`chromia/`)

**Purpose:** Run the Rell toolchain that matches each project's declared `compile.rellVersion`
instead of assuming the newest. The user-facing rules live in [COMPATIBILITY.md](COMPATIBILITY.md);
this section maps them to classes.

### RellVersionRegistry.kt

**Role:** The versions this plugin build supports, read from the build-generated resource
`rell/supported-versions.txt` (written by the `generateRellVersionRegistry` task from
`supportedRellVersions` in `build.gradle.kts`). Exposes `floor` (the oldest, below which nothing
runs) and `max` (the newest, which is bundled and is the default when nothing is declared).

### RellVersionResolver.kt

**Role:** Project service resolving a file's Rell version from the nearest enclosing `chromia.yml`.

The walk stops at the project content roots, mirroring how the language server anchors an index root
at each `chromia.yml` directory. Parsing goes through `ChromiaModelProvider` from
`rell-toolbox-common` — the toolchain's own parser — so `.yml`-only, blank-is-absent and
swallowed parse failures behave exactly as they do in `chr`. Results are cached per config path and
dropped when the file changes.

`RellVersionResolution` is the outcome: `Supported` (declared, or defaulted with an `Origin`
explaining why), `Clamped` (declared newer than this build knows), or `Unsupported` (below the
floor — no toolchain at all).

### RellLspRouting.kt

**Role:** Document matchers that decide which LSP4IJ `<server>` claims a file.
`RellNewestVersionDocumentMatcher` takes everything resolving to the newest version;
`RellVersionedDocumentMatcher` subclasses (e.g. `Rell0160DocumentMatcher`) take one specific older
version, triggering the runtime download and declining until it is ready. Files below the floor match
nothing. `RellLspServers` derives the server ids that `plugin.xml` must declare — `RellLspRoutingTest`
fails if the two drift.

### RellVersionedLanguageServerFactory.kt

**Role:** Same factory as `RellLanguageServerFactory` but pointed at a downloaded runtime directory.
One concrete subclass per older supported version (`Rell0160LanguageServerFactory`).

### RellLspRuntimeManager.kt / RellLspLockfile.kt

**Role:** Downloads and validates the older runtimes. The build writes
`rell/lsp-lockfiles/<version>.lock` (GAV, file name, SHA-256) for each older version; the manager
fetches exactly those artifacts into `<IDE system dir>/rell-lsp/<version>/`, verifies checksums, and
writes a `.complete` marker containing the lockfile contents — so a plugin upgrade that re-pins the
same Rell version to different artifacts invalidates the cache. Failures show a notification with a
Retry action; a wrong-version server is never substituted.

### VersionedRellParsers.kt / RellVersionSyntaxAnnotator.kt

**Role:** Version-true syntax errors. The build generates an ANTLR parser from each older version's
own `Rell.g4` into a version-suffixed package; `VersionedRellParsers` holds the entry points, and the
external annotator runs the right one and reports "Not valid in Rell X.Y.Z (declared in chromia.yml)".
The editor PSI always uses the newest grammar (a superset), so this is the only client-side place
where "valid in the newest Rell but not in this project's Rell" surfaces.

### RellVersionEditorNotificationProvider.kt

**Role:** The two banners — an error banner below the floor, with a one-click "Set rellVersion to …"
fix, and a warning banner when the declared version is newer than the plugin knows.

### ChromiaConfigChangeListener.kt / ChromiaConfigReloadNotificationProvider.kt

**Role:** Keeping resolution live. The VFS listener drops resolver caches on any `chromia.yml`
change, restarts highlighting, refreshes banners and restarts the Rell language servers. The
notification provider adds a save-and-reload bar on a `chromia.yml` whose edited `rellVersion` is
still unsaved.

---

## 4. Test Runner

**Purpose:** Integrate Rell tests with IDE test runner UI.

### RellTestConfigurationType.kt

**Role:** Registers "Rell Test" as a run configuration type.

**ID:** `RellTestConfigurationType`
**Icon:** Rell file icon
**Display Name:** "Rell Test"

### RellTestRunConfiguration.kt

**Role:** Stores test run parameters.

**Options (via `RellTestRunConfigurationOptions`):**

| Field                 | Type        | Purpose                                                       |
|-----------------------|-------------|---------------------------------------------------------------|
| `testScope`           | `TestScope` | What to run: MODULE, BLOCKCHAIN, TEST_PATTERN, ALL_IN_PROJECT |
| `testModule`          | `String?`   | Module name (for MODULE scope)                                |
| `testBlockchain`      | `String?`   | Blockchain name (for BLOCKCHAIN scope)                        |
| `testPattern`         | `String?`   | Test name pattern (for TEST_PATTERN scope)                    |
| `chrExecutable`       | `String?`   | Per-run override of the Chromia CLI command                   |
| `workingDirectory`    | `String?`   | Directory to run tests from                                   |
| `additionalArguments` | `String?`   | Extra arguments appended to the `chr test` command            |

**Validation:** `checkConfiguration()` ensures the field the selected scope needs is filled in.

### RellTestRunProfileState.kt

**Role:** Builds the command, runs it, and wires the output into the test runner.

The command is `chr test` plus the scope flag (`--modules`, `--blockchain` or `--tests`; nothing for
ALL_IN_PROJECT), the configured extra arguments, and `--hide-lib-warnings` unless already present. It
is executed through the system shell by `RellPluginSettingsState.buildChromiaCliCommandLine`, so a
`docker run …` command works as well as a plain path.

**Results:** the IDE's SM test runner consumes TeamCity service messages from the process output.
`RellTestResultsListener` adds a node for the run itself, so a non-zero exit surfaces as a failure
even when the CLI emitted no per-test messages.

**Service Messages Example:**
```
##teamcity[testStarted name='testFunctionName']
##teamcity[testFinished name='testFunctionName' duration='42']
##teamcity[testFailed name='testFunctionName' message='assertion failed']
```

### RellTestLineMarkerProvider.kt

**Role:** Shows green "Run Test" icons in editor gutter.

**Logic:**
1. Ask `RellProjectService` whether the containing file is a test file (an `rell/getTestFile` result)
2. If the element is a test function, return `LineMarkerInfo` with run icon
3. Clicking the icon creates a `RellTestRunConfiguration` and executes it

`RellTestLocator` maps test names reported by the CLI back to source elements, so clicking a result
in the test tree navigates to the test. `RellTestFinder` backs the platform's "Go to Test" action.

---

## 5. Chromia Tool Window

**Purpose:** Sidebar panel showing Rell project structure (similar to Maven/Gradle tool windows).

### ChromiaToolWindowFactory.kt

**Role:** Creates tool window on IDE startup.

```xml
<toolWindow id="Chromia"
            anchor="right"
            factoryClass="...ChromiaToolWindowFactory"
            icon="/icons/chromia-big.png"/>
```

`isApplicableAsync` hides the tool window entirely in projects where no Chromia project was found.

**`DumbAware` Interface:** Allows tool window to function during IDE indexing (when "dumb mode" is active).

### ChromiaProjectDiscovery.kt

**Role:** Finds Chromia projects in the workspace by walking the project base path (up to 10 levels
deep, skipping `build`, `node_modules`, dot-directories and similar) and recording every directory
that contains a `chromia.yml`.

Only `chromia.yml` counts — the Rell toolchain never reads `chromia.yaml`. Discovery records the
file's location; it does not parse the YAML (version resolution does that, through the toolchain
parser — see `RellVersionResolver`).

### ChromiaTreeModel.kt

**Role:** Tree data model for displaying project hierarchy, with `ChromiaTreeCellRenderer`,
`ChromiaTreeMouseListener` and `ChromiaTreePopupMenu` providing presentation and interaction.

### ChromiaCommandExecutor.kt

**Role:** Executes Chromia CLI commands from tool window UI (`chr build`, `chr test`, …).

Each command runs as its own process in a Run tool window tab with stop and rerun actions, using the
CLI command configured in settings.

---

## 6. Code Formatting

**Purpose:** Make "Reformat Code" work on `.rell` files.

Formatting itself is served by LSP4IJ: its `formattingService` extension turns the action into a
`textDocument/formatting` (or `rangeFormatting`) request against whichever Rell server owns the file.
The plugin contributes no formatting model of its own.

What the plugin does contribute is the Rell page under Settings → Editor → Code Style
(`RellCodeStyleSettingsProvider`, `RellLanguageCodeStyleSettingsProvider`): indent options and a Rell
code sample. `RellEnterHandler` handles newline behavior inside blocks.

> `RellFormattingModelBuilder` / `RellFormattingBlock` and `KeywordCompletionContributor` exist in
> the source tree but are not registered in `plugin.xml`, so they are not part of any code path.

---

## 7. Settings

### RellPluginSettingsState.kt

**Role:** Persistent storage for plugin settings, plus the logic that turns them into a command line.

**Stored Settings:**

| Field                   | Type      | Default | Purpose                                                  |
|-------------------------|-----------|---------|----------------------------------------------------------|
| `indexCaching`          | `Boolean` | `true`  | Enable workspace index caching (sent to the LSP server)   |
| `chromiaCliCommand`     | `String`  | `""`    | Chromia CLI path *or* full shell command; blank = auto    |
| `chromiaCliExecutable`  | `String`  | `""`    | Legacy field, migrated into `chromiaCliCommand` on load   |

**Storage Location:** `<IDE_CONFIG>/options/RellPluginSettings.xml`

The command is always run through the system shell (`sh -c` / `cmd /c`), which is what makes a
`docker run … chr` command usable in place of a path. When blank, well-known install locations are
probed, then `chr` on `PATH`.

### RellPluginSettingsConfigurable.kt / RellPluginSettingsComponent.kt

**Role:** UI page in Settings → Tools → Rell.

**UI Elements:**
- Checkbox: "Enable/disable caching of Rell project index. (Restart required)"
- Text field: "Chromia CLI" (path or shell command) with a file chooser and a **Test** button that
  runs `<command> --version` and reports the result
- A "Use Docker image for CLI" link, shown only when Docker is on `PATH`

---

## 8. Actions

### RellInvalidateCacheAction.kt

**Role:** Sends `rell/invalidateCaches` to the running server and reports the outcome as a
notification.

**Keyboard Shortcut:** `Ctrl+Alt+I`

### RellAddToProjectAction.kt

**Role:** Sends `rell/addToProject`, adding Rell feature scaffolding to the current project.

**Keyboard Shortcut:** `Ctrl+Shift+F11`

### RunRellTestAction.kt

**Role:** "Run Rell Test" in the project view and editor popup menus.

**Keyboard Shortcut:** `Ctrl+Shift+F10`

### RellCreateFileAction.kt

**Role:** Creates new Rell file from template, offering the kinds below in the New… dialog.

**Templates (in `src/main/resources/fileTemplates/internal/`):**
- `Rell File.rell` - Empty file
- `Rell Entity.rell` - Entity template
- `Rell Struct.rell` - Struct template
- `Rell Enum.rell` - Enum template
- `Rell Object.rell` - Object template

---

## 9. Services

### RellProjectService.kt

**Role:** Project-scoped service wrapping `rell/getTestFile` with a short-lived cache
(`TimedCache`, 1 second TTL, keyed by file URI).

**Why 1 Second TTL?**
- Balance between responsiveness and LSP call reduction
- Allows rapid consecutive queries (e.g., updating test line markers) without spamming LSP
- Short enough that changes are reflected quickly

---

[← Previous: Getting Started](03-getting-started.md) | [Next: Testing Strategy →](05-testing.md)
