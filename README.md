# Rell Jetbrains


[![Version](https://img.shields.io/jetbrains/plugin/v/net.postchain.rellide.jetbrains.svg)](https://plugins.jetbrains.com/plugin/net.postchain.rellide.jetbrains)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/net.postchain.rellide.jetbrains.svg)](https://plugins.jetbrains.com/plugin/net.postchain.rellide.jetbrains)

![./docs/screens/rell-intellij.PNG](./docs/screens/rell-intellij.PNG)

<!-- Plugin description -->

# Rell Language Plugin for JetBrains IDEs

The official JetBrains plugin for the **Rell programming language**, designed for the [Chromia blockchain platform](https://chromia.com).
Rell enables developers to build decentralized applications (dapps) in a safe, concise, and intuitive way, leveraging relational blockchain technology.

This plugin integrates Rell into JetBrains IDEs providing a complete development experience with full language support.

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

## 🧪 Rell Test Runner

Run and debug Rell tests directly from your JetBrains IDE:

* Powered by the [Chromia CLI](https://docs.chromia.com/intro/getting-started/installation/cli-installation).
* Supports both **local CLI** execution and **Docker-based** execution.
* Provides detailed runtime error reporting and status feedback.
* Allows running individual tests, test suites, or all tests within a Rell module.

---

## ⚡ Workspace & Index Caching

* Index caching improves performance when navigating large projects.
* Can be enabled/disabled via the **Settings -> Tools -> Rell** in your IDE.
* Includes an **"Rell: Invalidate Cache"** action for refreshing caches.

---


## 🔍 Linter

A built-in linter enforces best practices and detects potential issues early.

Configuration file: `.rell_lint` (must be placed in the project root).
Example:

```ini
[*.rell]
# Check Rell naming convention (true | false)
rule_naming_convention=true

# Warns about imports from non module files (true | false)
rule_import_from_non_module=true

# Preferred quote format (double | single)
rule_quote_format=double

# Rell formatter integration. Detects violations as you type (true | false)
rule_formatter=true

# Detects variables that could be declared as constants (true | false)
rule_constant_detection=true

# Warns about declared but unused variables (true | false)
rule_unused_variable=true

# Warns about outer joins without join conditions, which result in a Cartesian product (true | false)
rule_outer_join_cartesian_product=true
```

---

## 🖊 Code Formatter

Ensures consistent styling across your workspace.

Configurable via `.rellformat` in the root directory. Example:

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
2. Open **Settings -> Plugins -> Marketplace**.
3. Search for **Rell** and click **Install**.
4. Restart your IDE.
5. (Optional) Install the [Chromia CLI](https://docs.chromia.com/intro/getting-started/installation/cli-installation) to enable the test runner.

---

## 📖 Learn More

* [Rell Documentation](https://docs.chromia.com/rell/rell-intro)
* [Chromia Platform](https://docs.chromia.com)
* [Chromia CLI Installation Guide](https://docs.chromia.com/intro/getting-started/installation/cli-installation)

---

<!-- Plugin description end -->
