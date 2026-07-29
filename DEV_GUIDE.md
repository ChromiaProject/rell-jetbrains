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

### Development against a Rell Snapshot
Rell publishes `-SNAPSHOT` builds of the language server to its Maven repository. To run the
sandbox IDE against a snapshot instead of the release pinned in `libs.versions.toml`:
```sh
  work/snapshot-lsp.sh                                 # current snapshot (next minor over the pinned release)
  RELL_SNAPSHOT=0.18.0-SNAPSHOT work/snapshot-lsp.sh   # a different snapshot version
```
Only the language-server runtime is swapped — the editor grammar, the chromia.yml parser and the
compatibility-mode lockfiles stay at the pinned release.

---

Happy coding!
