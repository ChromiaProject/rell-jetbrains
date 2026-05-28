# Testing Strategy

[← Previous: Core Components](04-components.md) | [Next: Build & Release →](06-build-and-release.md)

---

This document describes the plugin's testing approach, available test types, and how to run them.

---

## Parser Sanity Test (`RellAntlrGrammarTest.kt`)

**Purpose:** Confirm the ANTLR-generated lexer/parser instantiate (ATN deserialization succeeds against the bundled `antlr4-runtime`) and parse representative Rell.

### Approach

- Run `RellLexer` + `RellParser` directly on Rell snippets via the ANTLR runtime.
- Assert a representative module parses with **no** syntax errors, and that malformed input **does** produce errors.

This is a thin smoke test. The grammar itself is the upstream source of truth and is exhaustively validated in `rell-base` (the 0.16.0 build runs a differential gate comparing the ANTLR parser against the legacy parser across the whole corpus), so the plugin does not re-host that corpus.

> The old `RellParsingTest.kt` consumed `net.postchain.rell:rell-api-gtx:<ver>:rell-test-cases.zip`. That artifact is no longer published (0.16.0 made it an internal Gradle configuration), and it validated the removed Grammar-Kit parser — so it was dropped.

### Test Execution

```bash
./gradlew test --tests "RellAntlrGrammarTest"
```

---

## Running All Tests

```bash
# Run all tests
./gradlew test

# Run with code coverage
./gradlew test koverHtmlReport
# Coverage report: build/reports/kover/html/index.html
```

---

[← Previous: Core Components](04-components.md) | [Next: Build & Release →](06-build-and-release.md)