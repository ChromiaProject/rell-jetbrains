# Testing Strategy

[← Previous: Core Components](04-components.md) | [Next: Build & Release →](06-build-and-release.md)

---

This document describes the plugin's testing approach, available test types, and how to run them.

---

## Parser Tests (`RellParsingTest.kt`)

**Purpose:** Ensure generated parser produces correct PSI trees.

### Approach

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

### Why This Approach

- **Single Source of Truth:** Test cases come from Rell compiler team
- **Automatic Updates:** Upgrading Rell version pulls new test cases
- **Comprehensive:** Rell compiler has extensive test suite

### Test Execution

```bash
./gradlew test --tests "RellParsingTest"
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