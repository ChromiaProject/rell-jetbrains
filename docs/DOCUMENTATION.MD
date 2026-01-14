# Rell JetBrains Plugin 

---

## Project Overview

### What Is This?

An official JetBrains IDE plugin providing language support for **Rell**, 
a programming language for building decentralized applications on the Chromia blockchain platform.

- **Plugin ID:** `net.postchain.rellide.jetbrains`
- **Distribution:** JetBrains Marketplace
- **Target IDEs:** All JetBrains IDEs (IntelliJ IDEA, PyCharm, WebStorm, etc.)
- **Language:** Kotlin

### What Does It Do?

Provides IDE integration for `.rell` source files:
- Syntax and semantic highlighting
- Compilation errors and warnings
- Go to Definition / Find Usages
- Symbol renaming (excluding modules)
- Code formatting
- Inlay hints (type annotations)
- Test runner integration with Chromia CLI
- File templates (entity, struct, enum, etc.)

### Why Does This Exist?

Rell is a domain-specific language for blockchain applications. 
This plugin makes Rell development practical in professional IDEs by providing the same quality-of-life features developers expect from mainstream languages (Java, Python, etc.).

**Business Context:**
- Part of Chromia platform tooling
- Integrates with Chromia CLI (`chr` command) 
- Uses Sentry for error tracking (organization: `chromaway-ab-za`)

---

## Why This Architecture

### Core Architectural Decision: Language Server Protocol (LSP)

**What:** The plugin delegates nearly all language intelligence (syntax checking, completion, navigation) to a separate Rell Language Server(LSP) process.

**Why:**

1. **Code Reuse:** The same Rell language server can be used across multiple editor integrations (VS Code, Vim, Emacs, etc.). Building language intelligence once and reusing it is more maintainable than reimplementing it per IDE.

2. **Separation of Concerns:** Language semantics (parsing, type checking, analysis) are separate from IDE integration (UI, keybindings, platform APIs). This allows language experts to work on the server and IDE integration experts to work on the plugin.

3. **Performance Isolation:** The LSP server runs in a separate JVM process with dedicated heap (default max 2GB). Heavy language analysis doesn't block the IDE's UI thread or compete for IDE memory.

4. **Independent Updates:** Language features can be updated by releasing new LSP server versions without requiring plugin updates (as long as LSP protocol remains compatible).


### Why Grammar-Kit Parser Despite LSP?

**What:** The plugin generates a local parser from BNF grammar using JetBrains Grammar-Kit.

**Why:**

Even though the LSP server handles compilation, the IDE still needs a local AST (Abstract Syntax Tree) for:

1. **Fallback Highlighting:** Basic syntax highlighting works immediately while waiting for LSP server to start or when LSP is unavailable.

2. **PSI Tree Navigation:** JetBrains IDEs use PSI (Program Structure Interface) trees for local navigation features. Some IDE features expect a PSI tree to exist.

3. **Offline Capabilities:** Users can browse code structure without waiting for server responses.

**Trade-off:** The local parser must stay synchronized with the language server's grammar.

### Why Test Runner Integration?

**What:** Custom "Rell Test" run configuration that launches Chromia CLI (`chr test`) and displays results in IDE test runner UI.

**Why:**

1. **Developer Workflow:** Running tests from the IDE (with gutter icons and keyboard shortcuts) is standard practice. Forcing developers to switch to terminal interrupts flow.

2. **Result Visualization:** IDE test runners show pass/fail status, execution time, and failure messages in a familiar UI. Terminal output is harder to parse visually.

3. **Debugging Integration:** Future enhancement potential to attach debuggers to test processes.

**Alternative Considered (Not Chosen):** Could have used external terminal integration, but this provides worse UX and no structured result reporting.

---

## Getting Started

### Prerequisites

**Required:**
- **JDK 21** (the plugin is built with Java 21 toolchain)
- **Gradle 8.14+** (wrapper included)
- **Git** (for version control)

**Optional:**
- **IntelliJ IDEA** (Community or Ultimate) for development
- **Chromia CLI** (`chr` command) for test runner functionality

### Initial Setup

```bash
# Clone the repository
git clone https://bitbucket.org/chromawallet/rell-jetbrains.git
cd rell-jetbrains

# Build the plugin
./gradlew buildPlugin

# The built plugin will be at:
# build/distributions/rell-jetbrains-<VERSION>.zip
```

**Windows:** Use `gradlew.bat` instead of `./gradlew`

### Running the Plugin in Development Mode

```bash
# Launch IntelliJ IDEA with plugin loaded
./gradlew runIde
```

This opens a "sandbox" IDE instance with the plugin installed. You can create test projects and verify plugin behavior.

**Development Tip:** To connect to an external language server running on port 5008 (useful for debugging the LSP server):

```bash
./gradlew runIde -PuseSocket
```

This uses `RellSocketLanguageServer` instead of `RellLanguageServer`, connecting to `localhost:5008` instead of launching the embedded JAR.

### Running Tests

```bash
# Run all tests
./gradlew test

# Run with code coverage
./gradlew test koverHtmlReport
# Coverage report: build/reports/kover/html/index.html
```

**What Tests Exist:**
- **Parser Tests:** Validates generated parser against test cases from Rell language server
- **Plugin Tests:** Currently disabled (`@Ignore` annotation) - placeholder for integration tests

### Project Structure Quick Reference

```
rell-jetbrains/
├── src/main/kotlin/net/postchain/rellide/jetbrains/
│   ├── lsp4ij/          # Language Server Protocol integration (505 lines)
│   ├── language/        # Parser, lexer, file type definitions
│   ├── testing/         # Test runner integration (767 lines)
│   ├── toolwindow/      # "Chromia" sidebar tool window
│   ├── formatting/      # Code formatting integration
│   ├── colors/          # Syntax highlighting colors
│   ├── settings/        # Plugin settings UI
│   ├── actions/         # Custom IDE actions
│   └── services/        # Project-scoped services
├── src/main/gen/        # Generated parser code (not in Git)
├── src/main/resources/
│   ├── META-INF/plugin.xml  # Plugin manifest
│   ├── icons/           # UI icons
│   └── fileTemplates/   # New file templates
├── language-server/     # Embedded Rell LSP JAR (~56MB)
├── bnf-grammar-generator/ # Separate tool to generate BNF from Rell grammar
└── docs/                # Documentation
```

---

## Architecture Deep Dive

### Component Diagram

```
┌─────────────────────────────────────────────────────────┐
│                   JetBrains IDE                         │
│  ┌───────────────────────────────────────────────────┐  │
│  │         Rell Plugin (This Codebase)               │  │
│  │                                                   │  │
│  │  ┌─────────────┐      ┌──────────────────────┐    │  │
│  │  │  Language   │◄────►│  LSP4IJ Integration  │    │  │
│  │  │  Definition │      │  (RellLanguageServer)│    │  │
│  │  │  (Grammar)  │      └──────────┬───────────┘    │  │
│  │  └─────────────┘                 │                │  │
│  │                                  │ LSP Protocol   │  │
│  │  ┌─────────────┐                 │                │  │
│  │  │    Test     │                 ▼                │  │
│  │  │   Runner    │      ┌──────────────────────┐    │  │
│  │  └──────┬──────┘      │   Rell Language      │    │  │
│  │         │             │   Server Process     │    │  │
│  │         │             │  (Separate JVM)      │    │  │
│  │         │             └──────────────────────┘    │  │
│  │         │ Launches                                │  │
│  │         ▼                                         │  │
│  │  ┌─────────────┐                                  │  │
│  │  │  Chromia    │  (External)                      │  │
│  │  │    CLI      │                                  │  │
│  │  │  (`chr`)    │                                  │  │
│  │  └─────────────┘                                  │  │
│  └───────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────┘
```

### Lifecycle: Plugin Initialization to User Action

**1. Plugin Loads (IDE Startup)**
- IDE reads `META-INF/plugin.xml`
- Registers language (`RellLanguage`), file type (`.rell`)
- Registers LSP factory (`RellLanguageServerFactory`)
- Registers test configuration type
- Registers tool window factory

**2. User Opens Rell File**
- IDE creates PSI file using `RellParserDefinition`
- Local parser generates PSI tree from source
- Basic syntax highlighting applied via `RellSyntaxHighlighter`
- LSP4IJ detects Rell file and calls `RellLanguageServerFactory.createConnectionProvider()`
- Factory returns `RellLanguageServer` (subprocess mode) or `RellSocketLanguageServer` (socket mode)
- LSP server starts on first `.rell` file opening (embedded JAR launched as subprocess or connection made to port 5008)
- Initialization options sent to server:
  ```json
  {
    "indexCaching": true/false,
    "inlayHints": { ... }
  }
  ```

**3. Language Intelligence (User Types Code)**
- IDE sends `textDocument/didChange` notification to LSP
- LSP analyzes document, publishes diagnostics (errors/warnings)
- IDE displays red squiggles for errors
- Semantic tokens received from LSP
- `RellSemanticTokensColorProvider` maps tokens to IDE colors
- Syntax highlighting updates

**4. User Invokes Action (e.g., Go to Definition)**
- IDE sends `textDocument/definition` request to LSP
- LSP responds with file URI and position
- IDE navigates to target location

**5. User Runs Test**
- Click gutter icon (provided by `RellTestLineMarkerProvider`)
- IDE creates `RellTestRunConfiguration`
- `RellTestRunProfileState.execute()` called
- Builds command: `chr test <scope> --output-format json`
- Launches process via `GeneralCommandLine`
- `RellTestResultsListener` parses output (Service Messages protocol)
- Results displayed in test runner UI

**6. Plugin Unload (IDE Shutdown)**
- LSP server process terminated
- Caches cleared

### Data Flow: How Code Gets Analyzed

```
User Types in Editor
       ↓
IDE Editor Buffer Updates
       ↓
textDocument/didChange → LSP Server
       ↓
LSP Server: Parse → Type Check → Lint
       ↓
textDocument/publishDiagnostics ← LSP Server
       ↓
IDE Displays Errors/Warnings
       ↓
(In Parallel)
       ↓
textDocument/semanticTokens/full → LSP Server
       ↓
Semantic Tokens ← LSP Server
       ↓
RellSemanticTokensColorProvider Maps to Colors
       ↓
IDE Updates Syntax Highlighting
```

---

## Core Components

### 1. Language Server Integration (`lsp4ij/` - 505 lines)

**Purpose:** Bridge between IDE and Rell LSP server.

#### RellLanguageServerFactory.kt

**Role:** Creates LSP connection providers.

**Logic:**
```kotlin
override fun createConnectionProvider(project: Project): LanguageServerConnectionProvider {
    return if (System.getProperty("rell.lsp.useSocket") != null) {
        RellSocketLanguageServer(project)
    } else {
        RellLanguageServer(project)
    }
}
```

**Why Two Modes?**
- **Subprocess mode (`RellLanguageServer`):** Production use. Plugin controls server lifecycle.
- **Socket mode (`RellSocketLanguageServer`):** Development. External server runs on port 5008, plugin connects to it. Useful for debugging server with IDE.

#### RellLanguageServer.kt

**Role:** Launches embedded LSP server JAR as subprocess.

**Key Code:**
```kotlin
val javaCmd = ProcessHandle.current()?.info()?.command()?.orElse("java") ?: "java"
val maxHeapSize = JVMHeapSizeManager.determineMaxHeapSize()

ProcessBuilder()
    .command(javaCmd, "-Xmx${maxHeapSize}M", "-jar", jarPath, "--stdio")
    .start()
```

**Design Decision - JVM Heap Size:**
- Uses `JVMHeapSizeManager.determineMaxHeapSize()` to calculate heap
- Default: `min(availableMemory * 0.25, 2048MB)`
- **Why:** Large Rell projects can have many files. Generous heap prevents OutOfMemoryError during indexing.

**Where Is the LSP JAR?**
- JAR file gets downloaded by running `./get-lsp.sh` script and placed in `language-server/rell-language-server-<LSP_SERVER_VERSION>.jar`

#### RellServerApi.kt

**Role:** Defines custom LSP extensions beyond standard LSP protocol.

**Custom Methods:**

| Method | Purpose | Returns |
|--------|---------|---------|
| `rell/invalidateCaches` | Clears server's workspace index cache | `Boolean` |
| `rell/listTestFiles` | Returns all Rell test files in workspace | `List<RellTestFile>` |
| `rell/listTestCases` | Returns test cases in a specific test file | `List<RellTestCase>` |
| `rell/addToProject` | Adds Rell feature template to existing project | `Void` |

**Why Custom Methods?**
Standard LSP doesn't cover IDE-specific features like test discovery or project templates. These extensions allow the plugin to provide richer functionality.


#### RellLanguageClient.kt

**Role:** Extends `IndexAwareLanguageClient` to handle server-to-client notifications.

**Why `IndexAwareLanguageClient`?**
- Base class provides workspace indexing support
- Allows LSP to query plugin about workspace files
- Needed for multi-root workspace support

#### RellSemanticTokensColorProvider.kt

**Role:** Maps LSP semantic token types to IDE color attributes.

**Example Mappings:**
```kotlin
"namespace" → RellColors.NAMESPACE
"class" → RellColors.ENTITY_TYPE
"function" → RellColors.FUNCTION_CALL
"keyword" → DefaultLanguageHighlighterColors.KEYWORD
```

**Why Needed:** LSP semantic tokens are standardized strings. IDE needs to map them to actual TextAttributesKey objects for rendering.

**How It Works:**
1. LSP sends semantic tokens: `[(line, col, length, tokenType, modifiers), ...]`
2. Provider converts token type to `TextAttributesKey`
3. IDE applies color scheme to those attributes

---

### 2. Language Definition

**Purpose:** Defines Rell as a language to JetBrains platform.

#### RellLanguage.kt

```kotlin
object RellLanguage : Language("Rell") {
    override fun isCaseSensitive() = true
}
```

**Why `object`?** Singleton pattern. Only one Language instance should exist per language.

#### RellFileType.kt

**Role:** Associates `.rell` file extension with Rell language.

```kotlin
override fun getDefaultExtension() = "rell"
override fun getIcon() = RellIcons.FILE
```

#### RellParserDefinition.kt

**Role:** Provides parser implementation to IDE.

**Key Methods:**

| Method | Returns | Purpose |
|--------|---------|---------|
| `createLexer()` | `RellLexerAdapter` | Tokenizes source code |
| `createParser()` | `RellParser` | Parses tokens into AST |
| `getFileNodeType()` | `IFileElementType` | Root PSI node type |
| `createElement()` | `ASTNode` | Creates PSI elements from AST |

**Generated Code:** `RellParser` is auto-generated by Grammar-Kit from `Rell.bnf`.

#### Grammar Update Process

**Why Manual Updates?** Rell language evolves (new keywords, syntax changes). Plugin must stay synchronized.

**Process (from `docs/update_grammar.md`):**

1. Update Rell version in `bnf-grammar-generator/gradle.properties` and `gradle.properties`
2. Run `bnf-grammar-generator/src/main/kotlin/.../BnfGrammar.kt` main function
3. This introspects Rell compiler's grammar and generates BNF to stdout
4. Manually review and update `src/main/kotlin/.../Rell.bnf`
5. Right-click `Rell.bnf` → "Generate Parser Code" (creates parser in `src/main/gen/`)
6. If new tokens added, right-click `Rell.bnf` → "Generate JFlex Lexer"
7. Manually merge changes into `_RellLexer.flex`
8. Right-click `_RellLexer.flex` → "Run JFlex Generator"
9. Run parser tests to verify

**Why Not Fully Automated?** Grammar generation is a starting point. Human review catches edge cases and ensures compatibility with IDE PSI requirements.

#### Lexer Numeric Validation

**Why in Lexer?** Rell supports arbitrary-precision numbers (BigInteger, BigDecimal). The lexer validates numeric literals to prevent overflow/precision loss.

**Example from `_RellLexer.flex`:**
```java
// Validates integer is within -2^63 to 2^63-1
private boolean validateInteger(String text, boolean negative) {
    if (text.length() > 19) return false;
    // ... boundary checking logic ...
}

// Validates BigInteger doesn't exceed 131,072 digits
private boolean validateBigInteger(String text) {
    return text.length() <= 131_072;
}

// Validates BigDecimal precision (20 fractional digits, 131,072 total)
private boolean validateDecimal(String text) {
    // ... precision checking logic ...
}
```

---

### 3. Test Runner

**Purpose:** Integrate Rell tests with IDE test runner UI.

#### RellTestConfigurationType.kt

**Role:** Registers "Rell Test" as a run configuration type.

**ID:** `RellTestConfigurationType`
**Icon:** Rell file icon
**Display Name:** "Rell Test"

#### RellTestRunConfiguration.kt

**Role:** Stores test run parameters.

**Options (via `RellTestRunConfigurationOptions`):**

| Field | Type | Purpose |
|-------|------|---------|
| `testScope` | `TestScope` | What to run: MODULE, BLOCKCHAIN, TEST_PATTERN, ALL_IN_PROJECT |
| `moduleName` | `String?` | Module name (for MODULE scope) |
| `testPattern` | `String?` | Regex pattern (for TEST_PATTERN scope) |
| `executablePath` | `String?` | Path to `chr` CLI executable |
| `workingDirectory` | `String?` | Directory to run tests from |

**Validation:** `checkConfiguration()` ensures required fields are set for selected scope.

#### RellTestRunProfileState.kt

**Role:** Executes test command and captures results.

**Execution Flow:**

1. **Build Command:**
   ```kotlin
   val command = mutableListOf(chrPath, "test")
   when (testScope) {
       MODULE -> command.add(moduleName)
       BLOCKCHAIN -> command.add("--blockchain")
       TEST_PATTERN -> command.addAll(listOf("--pattern", testPattern))
       ALL_IN_PROJECT -> { /* no additional args */ }
   }
   command.addAll(listOf("--output-format", "json", "--no-color"))
   ```

2. **Launch Process:**
   ```kotlin
   val commandLine = GeneralCommandLine(command)
       .withWorkDirectory(workingDirectory)
       .withCharset(Charsets.UTF_8)

   val handler = ColoredProcessHandler(commandLine)
   ```

3. **Attach Result Listener:**
   ```kotlin
   val listener = RellTestResultsListener(...)
   handler.addProcessListener(listener)
   ```

4. **Display in Test Runner:**
   ```kotlin
   val console = SMTRunnerConsoleView(...)
   return DefaultExecutionResult(console, handler)
   ```

**Output Format:** Chromia CLI outputs JSON with test results. Listener parses this and converts to Service Messages protocol (TeamCity format) that IDE understands.

**Service Messages Example:**
```
##teamcity[testStarted name='testFunctionName']
##teamcity[testFinished name='testFunctionName' duration='42']
##teamcity[testFailed name='testFunctionName' message='assertion failed']
```

#### RellTestLineMarkerProvider.kt

**Role:** Shows green "Run Test" icons in editor gutter.

**Logic:**
1. Analyze PSI tree for test functions (functions in test modules)
2. If element is a test function, return `LineMarkerInfo` with run icon
3. Clicking icon creates `RellTestRunConfiguration` and executes it

**Performance:** Only analyzes visible code (gutter icons update as you scroll).

---

### 4. Chromia Tool Window

**Purpose:** Sidebar panel showing Rell project structure (similar to Maven/Gradle tool windows).

#### ChromiaToolWindowFactory.kt

**Role:** Creates tool window on IDE startup.

**Configuration:**
```xml
<toolWindow id="Chromia"
            factoryClass="...ChromiaToolWindowFactory"
            icon="ChromiaIcons.LOGO_13"
            anchor="right" />
```

**`DumbAware` Interface:** Allows tool window to function during IDE indexing (when "dumb mode" is active).

#### ChromiaProjectDiscovery.kt

**Role:** Finds Rell projects in workspace.

**Detection Logic:**
- Searches for `chromia.yml` files (Chromia project configuration)
- Each `chromia.yml` represents a Rell project
- Parses YAML to extract project metadata

**Why YAML Parsing:** Chromia projects use `chromia.yml` to define modules, dependencies, and blockchain configuration. Tool window displays this structure.

#### ChromiaTreeModel.kt

**Role:** Tree data model for displaying project hierarchy.


#### ChromiaCommandExecutor.kt

**Role:** Executes Chromia CLI commands from tool window UI.

**Example Commands:**
- `chr build` - Build project
- `chr test` - Run tests

**Execution:** Uses `GeneralCommandLine` similar to test runner.

---

### 5. Code Formatting

**Purpose:** Integrate LSP-based formatting with IDE's "Reformat Code" action.

#### RellFormattingModelBuilder.kt

**Role:** Creates formatting model by building block structure.

**Why Needed:** IDE's formatter expects a block-based model (nested blocks with indentation rules). LSP returns formatted text. This bridges the two approaches.

**Approach:**
1. When user invokes "Reformat Code", IDE calls `createModel()`
2. Builder sends `textDocument/formatting` request to LSP
3. LSP returns formatted text
4. Builder creates `RellFormattingBlock` tree representing document structure
5. IDE applies formatting

**Configuration:**
- `.rellformat` file in project root (parsed by LSP server, not plugin)
- Settings: `max_line_width`, `insert_spaces`, `tab_size`

**Known Gap:** How `.rellformat` is discovered and parsed by LSP is not evident from plugin code. Assumed LSP handles this.

---

### 6. Settings

#### RellPluginSettingsState.kt

**Role:** Persistent storage for plugin settings.

**Stored Settings:**

| Field | Type | Default | Purpose |
|-------|------|---------|---------|
| `indexCachingEnabled` | `Boolean` | `true` | Enable workspace index caching |
| `chrExecutablePath` | `String?` | `null` | Path to Chromia CLI executable |

**Storage Location:** `<IDE_CONFIG>/options/RellPluginSettings.xml`

**Why Persistent:** Settings survive IDE restart.

#### RellPluginSettingsConfigurable.kt

**Role:** UI page in Settings → Tools → Rell.

**UI Elements:**
- Checkbox: "Enable index caching"
- Text field: "Chromia CLI executable path" (with file chooser button)

**Validation:** Validates `chrExecutablePath` points to valid executable.

---

### 7. Actions

#### RellInvalidateCacheAction.kt

**Role:** Clears LSP server's workspace index cache.

**Keyboard Shortcut:** `Ctrl+Alt+I` (configurable)

#### RellCreateFileAction.kt

**Role:** Creates new Rell file from template.

**Templates (in `src/main/resources/fileTemplates/internal/`):**
- `Rell File.rell` - Empty file
- `Rell Entity.rell` - Entity template
- `Rell Struct.rell` - Struct template
- `Rell Enum.rell` - Enum template
- `Rell Object.rell` - Object template


---

### 8. Services (`services/` - ~100 lines)

#### RellProjectService.kt

**Role:** Project-scoped service providing cached LSP data.

**Cached Data:**

| Cache | TTL | Purpose |
|-------|-----|---------|
| `testFilesCache` | 1 second | Caches result of `rell/listTestFiles` |
| `testCasesCache` | 1 second | Caches result of `rell/listTestCases` |

**Why 1 Second TTL?**
- Balance between responsiveness and LSP call reduction
- Allows rapid consecutive queries (e.g., updating test line markers) without spamming LSP
- Short enough that changes are reflected quickly

---

## Testing Strategy

### Parser Tests (`RellParsingTest.kt`)

**Purpose:** Ensure generated parser produces correct PSI trees.

**Approach:**

1. **Test Data Source:** JSON files from Rell LSP package
   - Downloaded during build from `net.postchain.rell:rell-api-gtx:0.15.0` (classifier: `rell-test-cases`)
   - Extracted to `build/rell-test-cases/test-cases/*.json`

2. **Test Case Format:**
   ```json
   {
     "files": {
       "test.rell": "entity user { name: text; }"
     },
     "parsing": {
       "test.rell": [
         // Expected errors (if any)
       ]
     }
   }
   ```

3. **Validation:**
   - Parse each `.rell` source from JSON
   - Generate PSI tree
   - Verify no error elements exist (for valid code)
   - Verify error elements exist at correct positions (for invalid code)

**Why This Approach:**
- **Single Source of Truth:** Test cases come from Rell compiler team
- **Automatic Updates:** Upgrading Rell version pulls new test cases
- **Comprehensive:** Rell compiler has extensive test suite

**Test Execution:**
```bash
./gradlew test --tests "RellParsingTest"
```

### Plugin Tests (`RellPluginTest.kt`)

**Status:** Currently disabled (`@Ignore` annotation)

**Why Disabled:** Not documented. Possibly work-in-progress or deprecated.

**What They Would Test:**
- Full plugin integration (file editing, LSP communication, etc.)
- Uses `BasePlatformTestCase` framework

**How to Enable:**
1. Remove `@Ignore` annotation
2. Implement test methods
3. Run `./gradlew test`

---

## Build & Release Pipeline

### Build Tasks

```bash
# Build plugin ZIP
./gradlew buildPlugin
# Output: build/distributions/rell-jetbrains-<version>.zip

# Run plugin in sandbox IDE
./gradlew runIde

# Run tests
./gradlew test

# Verify plugin compatibility
./gradlew verifyPlugin
# Checks compatibility with IntelliJ versions 242-253.*

# Sign plugin (requires certificates)
./gradlew signPlugin

# Publish to JetBrains Marketplace
./gradlew publishPlugin
# Requires PUBLISH_TOKEN environment variable
```

### Dependencies

**Managed by Gradle IntelliJ Platform Plugin:**

```kotlin
intellijPlatform {
    create("IC", "2025.2.4")  // IntelliJ Community 2025.2.4
    bundledPlugins("com.intellij")
    plugins(
        "com.redhat.devtools.lsp4ij:0.19.0",
        "org.jetbrains.plugins.terminal:251.26094.87"
    )
}
```

**External Maven Repositories:**
- **Rell GitLab Registry:** `https://gitlab.com/api/v4/projects/32802097/packages/maven`
- **Postchain GitLab Registry:** `https://gitlab.com/api/v4/projects/32294340/packages/maven`
- **Chromia GitLab Registry:** `https://gitlab.com/api/v4/projects/50818999/packages/maven`

**Why GitLab Maven?** Rell and related libraries are hosted on private GitLab repositories (not Maven Central).

### Sentry Integration

**Purpose:** Automatic error reporting for plugin crashes.

**Configuration (`build.gradle.kts`):**
```kotlin
sentry {
    includeSourceContext = true
    org = "chromaway-ab-za"
    projectName = "rell-jetbrains"
    authToken = System.getenv("SENTRY_AUTH_TOKEN")
}
```

**Error Reporting (`SentryReportSubmitter.kt`):**
- Catches uncaught exceptions in plugin code
- Sends stack traces to Sentry
- Includes IDE version, plugin version, OS info

**Privacy:** Users can opt-in/out via IDE settings.

---

