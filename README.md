# Rell JetBrains

[![Version](https://img.shields.io/jetbrains/plugin/v/net.postchain.rellide.jetbrains.svg)](https://plugins.jetbrains.com/plugin/22585-rell)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/net.postchain.rellide.jetbrains.svg)](https://plugins.jetbrains.com/plugin/22585-rell)

![./docs/screens/rell-intellij.PNG](./docs/screens/rell-intellij.PNG)

<!-- Plugin description -->

# Rell Language Plugin for IntelliJ

The official JetBrains plugin for the **Rell programming language**, designed for the [Chromia blockchain platform](https://chromia.com).
Rell enables developers to build decentralized applications (dapps) in a safe, concise, and intuitive way, leveraging relational blockchain technology.

This plugin integrates Rell into IntelliJ-based IDEs providing a complete development experience with full language support.

---

## ✨ Features

The plugin supports following features:

* **Syntax & semantic highlighting**
* **Diagnostics & warnings** (syntax, compilation, linter feedback)
* **Go to Definition / Find usages**
* **Symbol renaming** (excluding module renaming)
* **Code formatting**
* **Workspace index caching**
* **Inlay hints** for improved code readability
* **Multi-project workspace support**
* **Test runner for Rell** integrated with [Chromia CLI](https://docs.chromia.com/intro/getting-started/installation/cli-installation)
* **Chromia sidebar tool window** for streamlined development

---

## 🔀 Rell Version Compatibility

Each project gets the language server matching `compile.rellVersion` in its settings file for every Rell release from 0.16.1 up.

[docs/COMPATIBILITY.md](https://gitlab.com/chromaway/rell-jetbrains/-/blob/main/docs/COMPATIBILITY.md)
lists the supported versions and where settings files go.

---

## 🧪 Rell Test Runner

Run and debug Rell tests directly from your JetBrains IDE:

* Powered by the [Chromia CLI](https://docs.chromia.com/intro/getting-started/installation/cli-installation).
* Supports both local CLI execution and Docker-based execution.
* Provides detailed runtime error reporting and status feedback.
* Allows running individual tests, test suites, or all tests within a Rell module.

---

## ⚡ Workspace & Index Caching

* Index caching improves performance when navigating large projects.
* Can be enabled/disabled in [**Settings | Tools | Rell**](jetbrains://idea/settings?name=Tools--Rell).
* Includes an **"Rell: Invalidate Cache"** action for refreshing caches.

---


## 🔍 Linter

Every rule is on by default; `.rell_lint` in the project root turns them off or opts into the two that are off:

```ini
[*.rell]
# Off by default: enforce a quote style (double | single)
rule_quote_format=double

# Off by default: report formatter violations as you type (true | false)
rule_formatter=true

# On by default: set to false to switch a rule off
rule_naming_convention=false
```

---

## 🖊 Code Formatter

Configured by `.rell_format` in the project root (deprecated `.rellformat` also works):

```ini
[*.rell]
# Maximum character count for each line.
max_line_width=120

# Whether to use spaces for indentation instead of tabs (true | false)
insert_spaces=true

# Number of spaces to be used for each level of indentation.
tab_size=4
```

---

## 📦 Installation

1. Install your preferred JetBrains IDE (IntelliJ IDEA, PyCharm, WebStorm, etc.).
2. Open [**Settings | Plugins**](jetbrains://idea/settings?name=Plugins) and switch to **Marketplace**.
3. Search for **Rell** and click **Install**.
4. Restart your IDE.
5. (Optional) Install the [Chromia CLI](https://docs.chromia.com/intro/getting-started/installation/cli-installation) to enable the test runner.

---

## 🔒 Privacy

The plugin collects no analytics or telemetry. Crash reports are sent to ChromaWay's error tracker
(Sentry, hosted in the EU) only when you explicitly submit one from the IDE's error dialog. The
[PRIVACY_POLICY.md](https://gitlab.com/chromaway/rell-jetbrains/-/blob/main/PRIVACY_POLICY.md) file in the plugin repository describes exactly what a report contains.

---

## 📖 Learn More

* [Rell Documentation](https://docs.chromia.com/rell/rell-intro)
* [Chromia Platform](https://docs.chromia.com)
* [Chromia CLI Installation Guide](https://docs.chromia.com/intro/getting-started/installation/cli-installation)

---

<!-- Plugin description end -->
