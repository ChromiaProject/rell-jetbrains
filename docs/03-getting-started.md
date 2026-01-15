# Getting Started

[← Previous: Architecture](02-architecture.md) | [Next: Components →](04-components.md)

---

## Prerequisites

**Required:**
- **JDK 21** (the plugin is built with Java 21 toolchain)
- **Gradle 8.14+** (wrapper included)
- **Git** (for version control)

**Optional:**
- **IntelliJ IDEA** (Community or Ultimate) for development
- **Chromia CLI** (`chr` command) for test runner functionality

---

## Initial Setup

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

---

## Running the Plugin in Development Mode

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

---

## Running Tests

```bash
# Run all tests
./gradlew test

# Run with code coverage
./gradlew test koverHtmlReport
# Coverage report: build/reports/kover/html/index.html
```

**What Tests Exist:**
- **Parser Tests:** Validates generated parser against test cases from Rell language server

---

## Project Structure Quick Reference

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

[← Previous: Architecture](02-architecture.md) | [Next: Components →](04-components.md)