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

[← Previous: Architecture](02-architecture.md) | [Next: Components →](04-components.md)