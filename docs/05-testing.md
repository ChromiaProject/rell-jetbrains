# Testing Strategy

[← Previous: Core Components](04-components.md) | [Next: Build & Release →](06-build-and-release.md)

---

Tests split in two: plain JUnit tests over pure logic, and `BasePlatformTestCase` tests that need a
headless IDE (PSI, VFS, editor notifications, platform LSP extension points). `./gradlew test` runs both.

---

## Grammar and parsers

`RellAntlrGrammarTest` confirms the ANTLR-generated lexer/parser instantiate (ATN deserialization
succeeds against the bundled `antlr4-runtime`) and that a representative module parses with **no**
syntax errors while malformed input **does** produce errors.

It is thin by design. The grammar itself is the upstream source of truth and is exhaustively
validated in `rell-base` (the 0.16.0 build runs a differential gate comparing the ANTLR parser
against the legacy parser across the whole corpus), so the plugin does not re-host that corpus.

> The old `RellParsingTest.kt` consumed `net.postchain.rell:rell-api-gtx:<ver>:rell-test-cases.zip`. That artifact is no longer published (0.16.0 made it an internal Gradle configuration), and it validated the removed Grammar-Kit parser — so it was dropped.

---

## Version compatibility

Part of the suite covers the version handling described in [COMPATIBILITY.md](COMPATIBILITY.md):

| Test                                         | Covers                                                                              |
|----------------------------------------------|-------------------------------------------------------------------------------------|
| `RellVersionTest`                            | Parsing and ordering of `major.minor.patch`                                          |
| `RellVersionResolverTest`                    | `chromia.yml` lookup and `compile.rellVersion` semantics, on real temp directories    |
| `RellLspRoutingTest`                         | The provider starts the bundled server for every Rell file, and only for those        |
| `ChromiaSettingsChooserTest`                 | The settings-file popup, and how each file's declared version is described            |
| `ChromiaConfigReloadNotificationProviderTest`| The save-and-reload bar shown while an edited `rellVersion` is unsaved                |

`RellVersionResolverTest` deliberately uses real filesystem temp directories rather than the
in-memory fixture, because the toolbox parser reads `chromia.yml` from an on-disk path.

---

## Test runner

`RellTestLocatorTest` covers mapping test names reported by the Chromia CLI back to source elements.

---

## Running All Tests

```bash
./gradlew test

# A single suite
./gradlew test --tests "RellAntlrGrammarTest"

# With code coverage
./gradlew test koverHtmlReport
# Coverage report: build/reports/kover/html/index.html
```

---

[← Previous: Core Components](04-components.md) | [Next: Build & Release →](06-build-and-release.md)
