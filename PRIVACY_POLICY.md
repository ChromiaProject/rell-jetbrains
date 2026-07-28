# Privacy Policy

_Last updated: 2026-07-25_

This policy describes what data the **Rell** plugin for JetBrains IDEs (`net.postchain.rellide.jetbrains`)
sends off your machine, when it is sent, and who receives it. The plugin is published by
ChromaWay AB.

## Summary

The plugin does not collect analytics, usage statistics, or telemetry. It transmits data in
exactly one situation: when you explicitly submit a crash report through the IDE's error dialog
by pressing **"Report Error to Rell Plugin Maintainers"**. Nothing is sent in the background, and
nothing is sent if you never press that button.

## Crash reporting via Sentry

Crash reports are processed by [Sentry](https://sentry.io/), operated by Functional Software, Inc.
Reports are sent to ChromaWay's Sentry organization (`chromaway-ab-za`) on Sentry's **EU**
infrastructure (`ingest.de.sentry.io`), where they are stored and processed.

### When a report is sent

The IDE shows its standard "IDE Internal Error" dialog when an unhandled exception occurs. The
plugin adds a submit action to that dialog, along with a notice describing what will be sent. A
report leaves your machine only after you activate that action. The plugin additionally filters
submissions: an event is transmitted only if its stack trace originates in the Rell plugin
(`net.postchain.rellide.jetbrains`) or in the LSP integration layer it depends on
(`com.intellij.platform.lsp`). Errors from the IDE or from unrelated plugins are discarded
locally and never sent.

### What a report contains

- The exception: its type, message, and full stack trace, including class names, method names,
  source file names, and line numbers.
- The log message associated with the IDE error event.
- The plugin version and the IDE build number.
- Operating system name, version, and CPU architecture.
- Java version, Java runtime version, and JVM vendor.
- Any free-text description you typed into the error dialog before submitting.
- Technical metadata added by the Sentry SDK, such as an event identifier, timestamp, and SDK
  version.
- Your IP address, which Sentry records at its ingest endpoint and from which it derives an
  approximate location (country and city). Reports are not otherwise linked to your identity, and
  the plugin does not send your name, email, IDE account, or license information.

The plugin does not send your machine's hostname. Absolute file paths are rewritten to remove your
home directory before transmission, so `/Users/jsmith/project` is sent as `~/project`.

Plugin source code is uploaded to Sentry at build time so that stack frames can be displayed with
the surrounding plugin source. This is our own source code, published under the plugin's license —
it is not read from your machine.

### What is not collected

- No usage analytics, feature tracking, session recording, or performance monitoring.
- No contents of your Rell projects, source files, or build output.
- No private keys, mnemonics, node credentials, blockchain RIDs, or database credentials.
- No automatic or background transmission of any kind.

### Data that may be included incidentally

Exception messages and stack traces are produced by code, not curated by us. Even after home
directory paths are removed, they can incidentally contain project and file names, module names,
or fragments of Rell code or compiler diagnostics involved in the failure. If a particular report
would expose something you consider sensitive, do not submit it — the plugin works normally whether
or not you report the error. The same applies to the free-text field: it is sent verbatim, so avoid
pasting credentials or customer data into it.

## Bundled language server

The plugin bundles the Rell language server (`rell-toolbox-language-server`), which runs as a local
process on your machine and communicates with the IDE over a local connection only. All Rell
compilation and analysis happens locally, and the plugin configures the language server so that it
does not report errors anywhere.

Note for users of plugin versions **0.4.0 and earlier**: those versions shipped a language server
that automatically uploaded its own error logs, including absolute file paths, to ChromaWay's
Sentry organization without asking. This was not intended, was not previously disclosed, and is
fixed from version 0.4.1 onward. If you are affected and want the historical reports from your
machine deleted, contact us at the address below.

## Network connections

Apart from crash reports you submit, the plugin does not contact ChromaWay or any other service.
Update checks and plugin downloads are performed by the JetBrains IDE itself against the JetBrains
Marketplace and are governed by
[JetBrains' privacy policy](https://www.jetbrains.com/legal/docs/privacy/privacy/).

## Retention and access

Submitted reports are retained according to the retention settings of ChromaWay's Sentry
organization and are accessible to ChromaWay developers maintaining the plugin. Reports are used
solely to diagnose and fix defects. They are not sold, and they are not shared with third parties
other than Sentry acting as our processor.

Sentry's own handling of the data it processes is described in its
[privacy policy](https://sentry.io/privacy/).

## Disabling crash reporting

There is no separate setting to disable it, because reporting is not automatic: simply do not use
the "Report Error to Rell Plugin Maintainers" action. You can also disable the plugin's error
handler by disabling the plugin itself in **Settings → Plugins**.

## Contact

Questions, or requests to delete a report you submitted:
[devex@chromaway.com](mailto:devex@chromaway.com).

Include the approximate submission time, the plugin version, and the error text so we can locate
the report; reports carry no account identifier, so we cannot look them up by user.

## Changes to this policy

Material changes will be noted in [CHANGELOG.md](CHANGELOG.md) alongside the release that
introduces them, and the "Last updated" date above will be revised.
