# Architecture

[← Previous: Overview](01-overview.md) | [Next: Getting Started →](03-getting-started.md)

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

**What:** The plugin generates a local parser from BNF grammar used by JetBrains IDE platforms, even though the LSP server also performs parsing.

**Why:**

Even though the LSP server handles compilation, the IDE still needs a local AST (Abstract Syntax Tree) for:

1. **Fallback Highlighting:** Basic syntax highlighting works immediately while waiting for LSP server to start or when LSP is unavailable.

2. **PSI Tree Navigation:** JetBrains IDEs use PSI (Program Structure Interface) trees for local navigation features. Some IDE features expect a PSI tree to exist.


### Why Test Runner Integration?

**What:** Custom "Rell Test" run configuration that launches Chromia CLI (`chr test`) and displays results in IDE test runner UI.

**Why:**

1. **Developer Workflow:** Running tests from the IDE (with gutter icons and keyboard shortcuts) is standard practice. Forcing developers to switch to terminal interrupts flow.

2. **Result Visualization:** IDE test runners show pass/fail status, execution time, and failure messages in a familiar UI. Terminal output is harder to parse visually.

3. **Debugging Integration:** Future enhancement potential to attach debuggers to test processes.

**Alternative Considered (Not Chosen):** Could have used external terminal integration, but this provides worse UX and no structured result reporting.

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
- IDE reads `src/main/resources/META-INF/plugin.xml`
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
- Initialization options sent to server

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

[← Previous: Overview](01-overview.md) | [Next: Getting Started →](03-getting-started.md)
