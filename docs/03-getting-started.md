# Getting Started

[← Previous: Architecture](02-architecture.md) | [Next: Components →](04-components.md)

---

## Prerequisites

- **JDK 21**
- **Chromia CLI** (`chr`) — only needed to exercise the test runner and the Chromia tool window.

---

## Initial Setup

```bash
git clone https://bitbucket.org/chromawallet/rell-jetbrains.git
cd rell-jetbrains
./gradlew buildPlugin
# Output: build/distributions/rell-jetbrains-<VERSION>.zip
```

The Rell artifacts come from the Chromia GitLab Maven registries listed in `build.gradle.kts`, not
from Maven Central.

---

## Running the Plugin in Development Mode

```bash
./gradlew runIde
```

This opens a sandbox IDE instance with the plugin installed.

To connect to an external language server on port 5008 instead of launching the bundled one (useful
when debugging the server):

```bash
./gradlew runIde -PuseSocket
```

This uses `RellSocketLanguageServer` instead of `RellLanguageServer`.

The `.run/` directory ships equivalent IDE run configurations: `Run Plugin`,
`Run Plugin with Socket LSP`, `Run Tests`, `Run Verifications`, `Run Qodana`, `Run IDE for UI Tests`.

To run the sandbox against a language server built from a local Rell clone, see
[DEV_GUIDE.md](../DEV_GUIDE.md) (`work/local-lsp.sh`).

---

## Running Tests

```bash
./gradlew test

# With code coverage
./gradlew test koverHtmlReport
# Coverage report: build/reports/kover/html/index.html
```

Most tests run against a headless IDE fixture, so `test` starts a platform instance. See
[Testing Strategy](05-testing.md) for what the suites cover.

---

[← Previous: Architecture](02-architecture.md) | [Next: Components →](04-components.md)
