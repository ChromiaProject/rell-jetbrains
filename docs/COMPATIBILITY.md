# Rell version compatibility

## Supported Rell versions

| Plugin version | Supported Rell versions | Bundled |
|----------------|-------------------------|---------|
| 0.4.5          | 0.16.1, 0.16.2, 0.16.3, 0.16.4, 0.16.5 | 0.16.5 |
| 0.4.4          | 0.16.1, 0.16.2, 0.16.3, 0.16.4 | 0.16.4 |
| 0.4.3          | 0.16.1, 0.16.2, 0.16.3  | 0.16.3  |
| 0.4.2          | 0.16.1, 0.16.2          | 0.16.2  |
| 0.4.1          | 0.16.0, 0.16.1          | 0.16.1  |

Your project's version comes from `compile.rellVersion` in its settings file:

- **a supported version** &mdash; you get that version's grammar and language server. The newest one is
  bundled; older ones download on first use, and until that finishes those files have no server.
- **newer than the plugin knows** &mdash; the newest supported version is used, and the editor suggests
  updating the plugin.
- **below the oldest supported** &mdash; no language server runs at all. The editor shows an error with
  a one-click fix that raises `rellVersion`. Fresh `chr create-rell-dapp` projects land here.
- **absent** &mdash; the newest supported version is used.

Support is tracked per exact version because patch releases change the language: lambdas arrived in
Rell 0.16.1, so a 0.16.0 project must see them flagged. Rell's own `compatibility` option cannot do
this &mdash; it gates library members, never syntax.

## Where settings files go

A settings file is `chromia.yml`, or any `*.yml` with a top-level `blockchains` section &mdash; what `chr`
reads by default or via `-s/--settings`.

It governs the sources under its **source root**: `compile.source` if declared, otherwise the first
of `rell/src`, `rell`, or `src` that exists, otherwise its own directory.

```
contracts/
  chromia.yml          governs contracts/src/**
  src/main/main.rell
```

Nesting works &mdash; the deepest settings file whose source root contains a file wins. A file no source
root covers falls back to the nearest directory holding settings files.

When one directory holds several settings files (typically one per deployment network), the active
one governs. The status bar names it and switches it in one click, and that choice is passed as
`--settings` to Chromia tool-window commands. Without an explicit choice `chromia.yml` wins, else
the newest supported version.
