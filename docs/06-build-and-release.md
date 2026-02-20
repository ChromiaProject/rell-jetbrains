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
- **Rell GitLab Registry:** `https://gitlab.com/api/v4/projects/32802097`
- **Postchain GitLab Registry:** `https://gitlab.com/api/v4/projects/32294340`
- **Chromia GitLab Registry:** `https://gitlab.com/api/v4/projects/50818999`

**Why GitLab Maven?** Rell and related libraries are hosted on private GitLab repositories (not Maven Central).


### Jenkins pipeline

Internal Jenkins pipeline automates publishing plugin to JetBrains Marketplace.
Ask Chromia DevOps team for access.

Release steps:
- After merging new features to `main`, Manually trigger Jenkins pipeline build. (pipeline: rell-jetbrains)
- It will build, sign, and publish the plugin to JetBrains Marketplace.
- Check [JetBrains Marketplace](https://plugins.jetbrains.com/) for new version.
- Ask DevTools team credentials to log in to Marketplace and check submission status.
- Release isn't automatically available. review takes time (usually few business days).
- Jetbrains is periodically scanning the plugin for compatibility and security issues and reports back (through email)


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

[← Previous: Testing Strategy](05-testing.md)