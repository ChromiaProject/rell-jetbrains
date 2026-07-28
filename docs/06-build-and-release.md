# Build & Release Pipeline

[← Previous: Testing Strategy](05-testing.md)

---

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
# Runs the JetBrains Plugin Verifier against the recommended IDEs in the
# 253.33813-262.* build range declared in gradle.properties

# Sign plugin (requires CERTIFICATE_CHAIN / PRIVATE_KEY / PRIVATE_KEY_PASSWORD)
./gradlew signPlugin

# Publish to JetBrains Marketplace
./gradlew publishPlugin
# Requires PUBLISH_TOKEN environment variable
```

`verifyPlugin` only fails on `COMPATIBILITY_PROBLEMS`, `INVALID_PLUGIN` and `MISSING_DEPENDENCIES`.
Deprecated, experimental, internal and override-only API usages still land in
`build/reports/pluginVerifier/`, but the platform accumulates them faster than they can be migrated
away, so they do not block a release.

### Build-generated inputs

Three generation steps run before compilation, all driven by `supportedRellVersions` and the `rell`
version in `gradle/libs.versions.toml`:

- `extractRellGrammar` + `generateGrammarSource` — `Rell.g4` for the newest supported version, from
  the `net.postchain.rell:frontend:<rell>:sources` jar, generating the editor parser/lexer. Each
  older version gets its own extract/generate pair into a version-suffixed package.
- `generateRellVersionRegistry` — `rell/supported-versions.txt`, read by `RellVersionRegistry`. It
  fails the build if `supportedRellVersions` does not end with the `rell` version, so the two cannot
  drift.
- `generateRellLspLockfiles` — `rell/lsp-lockfiles/<version>.lock` (GAV + SHA-256) for the
  downloadable language-server runtimes of older versions.

The bundled language server is resolved from Maven as a detached configuration and copied into the
plugin by `prepareSandbox`, together with `lsp-config/log4j2-override.properties`. There is no manual
download step.

### Dependencies

**Managed by the IntelliJ Platform Gradle Plugin** (configured from `gradle.properties`):

```properties
platformType=IU
platformVersion=2026.1.4
platformBundledPlugins=com.intellij
```

The platform's own LSP client modules (`intellij.platform.lsp`, `intellij.platform.lsp.impl`) are
added as bundled modules in `build.gradle.kts`.

**External Maven Repositories:**
- **Rell GitLab Registry:** `https://gitlab.com/api/v4/projects/32802097`
- **Postchain GitLab Registry:** `https://gitlab.com/api/v4/projects/32294340`
- **Chromia GitLab Registry:** `https://gitlab.com/api/v4/projects/50818999`
- **Chromia CLI tools GitLab Registry:** `https://gitlab.com/api/v4/projects/64941451`
- **etherjar:** `https://maven.emrld.io`

**Why GitLab Maven?** Rell and related libraries are hosted on GitLab package registries (not Maven Central).

`RellLspRuntimeManager` mirrors this list at runtime when downloading older language-server runtimes.

### CI: Bitbucket Pipelines

`bitbucket-pipelines.yml` defines three steps:

- **Pull requests:** `buildPlugin test`.
- **`main`:** the release build — `buildPlugin test verifyPlugin` with `SENTRY_AUTH_TOKEN` set, so
  source context is uploaded. This gates the publish step, which Bitbucket only offers once the
  build is green.
- **Publish (manual trigger, Production deployment):** refuses to run if a git tag for the current
  `pluginVersion` already exists, checks that `SENTRY_AUTH_TOKEN` is present and accepted by
  Sentry, then runs `publishPlugin` and tags the commit with the plain version number.

Released versions are therefore always tagged — which is why a `## [x.y.z]` section in CHANGELOG.md
with a matching tag must never be edited.

After publishing: the Marketplace review takes a few business days, and JetBrains periodically
rescans published plugins for compatibility and security issues, reporting by email.

### Sentry Integration

**Purpose:** Crash reporting for plugin errors, submitted by the user from the IDE's error dialog.

**Configuration (`build.gradle.kts`):**
```kotlin
sentry {
    autoInstallation { enabled = false }
    includeSourceContext = sentryAuthToken != null
    org = "chromaway-ab-za"
    projectName = "rell-jetbrains"
    authToken = System.getenv("SENTRY_AUTH_TOKEN") ?: ""
}
```

Auto-installation is off deliberately: it grafts extra Sentry modules onto every resolved
configuration, including the language server's runtime classpath, and the Sentry SDK refuses to start
on mixed versions — which kills the server on launch.

**Error Reporting (`SentryReportSubmitter.kt`):**
- Registered as the plugin's `errorHandler`, so it appears in the IDE's error dialog
- Nothing is sent unless the user presses the report button; the dialog shows a privacy notice first
- Events whose stack traces do not involve the plugin or the platform LSP integration are dropped, the machine hostname is
  not attached, and home directories are scrubbed from paths

See [PRIVACY_POLICY.md](../PRIVACY_POLICY.md) for the full statement.

[← Previous: Testing Strategy](05-testing.md)
