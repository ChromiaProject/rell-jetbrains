# Rell version compatibility

## The version a project is analysed at

The plugin bundles one language server. It reads `compile.rellVersion` from your project's settings
file and analyses that project at the declared version, so diagnostics, completion and navigation
follow the Rell release you target rather than the one the server was built from.

| Plugin version | Bundled Rell |
|----------------|--------------|
| 0.4.8          | 0.16.6       |
| 0.4.7          | 0.16.6       |
| 0.4.5          | 0.16.5       |
| 0.4.4          | 0.16.4       |
| 0.4.3          | 0.16.3       |
| 0.4.2          | 0.16.2       |
| 0.4.1          | 0.16.1       |

A declared version never costs a file its language server: every `.rell` file in the project is
served, whatever its settings file says. A version the server cannot honour — not a version at all,
older than the oldest compatibility mode it offers, or newer than the server itself — is clamped
into range, and code using something the declared release lacks is reported as an ordinary
diagnostic on the offending line, like any other compile error.

## Where settings files go

A settings file is `chromia.yml`, or any `*.yml` with a top-level `blockchains` section — what `chr`
reads by default or via `-s/--settings`.

It governs the sources under its **source root**: `compile.source` if declared, otherwise the first
of `rell/src`, `rell`, or `src` that exists, otherwise its own directory.

```
contracts/
  chromia.yml          governs contracts/src/**
  src/main/main.rell
```

Nesting works — the deepest settings file whose source root contains a file wins. A file no source
root covers falls back to the nearest directory holding settings files.

When one directory holds several settings files (typically one per deployment network), the active
one governs. The status bar names it and switches it in one click, and that choice is passed as
`--settings` to Chromia tool-window commands. Without an explicit choice `chromia.yml` wins, else
the file declaring the newest version.
