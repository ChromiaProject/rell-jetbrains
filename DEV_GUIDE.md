# Plugin Development Guide

This document provides essential resources and guidelines for developing Rell plugin for IntelliJ platform.

## 📌 Useful Links & Resources

- [IntelliJ Platform SDK](https://plugins.jetbrains.com/docs/intellij/welcome.html)
- [IntelliJ Platform LSP API](https://plugins.jetbrains.com/docs/intellij/language-server-protocol.html)
- [IntelliJ Platform Explorer](https://plugins.jetbrains.com/intellij-platform-explorer/extensions)
- [Gradle IntelliJ Plugin ](https://github.com/JetBrains/intellij-platform-gradle-plugin)
- [Grammar Kit](https://github.com/JetBrains/Grammar-Kit)

---

## 🔧 Development Setup

### Prerequisites

- **IntelliJ IDEA Ultimate or Community Edition** (latest stable version)
- **JDK 21+** (recommended for plugin development)

### Setting Up Development Environment
**📦 Build the Plugin**
```sh
  ./gradlew buildPlugin
```

**🚀 Run the Plugin**
```sh
  ./gradlew runIde
```
Or use the intellij UI with the run configuration `Run Plugin`

### Development with External Language Server
When developing features that require changes in both the server and client side, it can be beneficial to run the 
language server separately and connect to it via socket. This setup allows for faster development iterations on the server side.

To run the plugin with socket connection to an external language server:
```sh
  ./gradlew runIde -PuseSocket
```
Or use the intellij UI with the run configuration `Run Plugin with Socket LSP`
This will launch the IDE with the plugin configured to connect to a language server running on localhost:5008.

### Development against a Local Rell Clone
To run the sandbox IDE with a language server built from Rell source instead of the published release:
```sh
  work/local-lsp.sh              # clones Rell into rell-local/ on first run, then runIde
  work/local-lsp.sh --update     # fetch and fast-forward the clone first
```
`RELL_REF` selects a branch or tag (default `dev`), `RELL_REPO` a different remote.

The clone is included into this build as a composite build, so `rell-toolbox-language-server` and
`rell-toolbox-common` are built from source, and the editor grammar is read from the clone's
`Rell.g4`. Only the newest supported Rell version is substituted — compatibility mode still
downloads the published runtimes for older versions. Gradle drives both builds with one version and
Rell needs a newer one than this project's wrapper ships, so the script runs the build through the
clone's wrapper.

---

Happy coding!
