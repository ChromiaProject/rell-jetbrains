# Project Overview

[Next: Architecture →](02-architecture.md)

---

## What Is This?

An official JetBrains IDE plugin providing language support for **Rell**,
a programming language for building decentralized applications on the Chromia blockchain platform.

- **Plugin ID:** `net.postchain.rellide.jetbrains`
- **Distribution:** JetBrains Marketplace
- **Target IDEs:** All JetBrains IDEs (IntelliJ IDEA, PyCharm, WebStorm, etc.)
- **Language:** Kotlin

---

## What Does It Do?

Provides IDE integration for `.rell` source files:
- Syntax and semantic highlighting
- Compilation errors and warnings
- Go to Definition / Find Usages
- Symbol renaming (excluding modules)
- Code formatting
- Inlay hints (type annotations)
- Test runner integration with Chromia CLI
- File templates (entity, struct, enum, etc.)

---

## Why Does This Exist?

Rell is a domain-specific language for blockchain applications.
This plugin makes Rell development practical in professional IDEs by providing the same quality-of-life features developers expect from mainstream languages (Java, Python, etc.).

**Business Context:**
- Part of Chromia platform tooling
- Integrates with Chromia CLI (`chr` command)
- Uses Sentry for error tracking (organization: `chromaway-ab-za`)

---

[Next: Architecture →](02-architecture.md)