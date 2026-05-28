# Updating the Rell Grammar

Since Rell 0.16.0 the editor parser is built from Rell's own **ANTLR** grammar (`Rell.g4`),
consumed via [antlr4-intellij-adaptor](https://github.com/antlr/antlr4-intellij-adaptor). The grammar
shipped in `rell-base` is the single source of truth — there is no hand-maintained `Rell.bnf` or
JFlex lexer anymore, and no Grammar-Kit / JFlex IDE steps.

The grammar is **not vendored**: it is extracted at build time from the published
`net.postchain.rell:frontend:<rell>:sources` jar and fed to ANTLR codegen, so it always matches the
`rell` version. Most upgrades therefore need only a version bump.

## Step 1: Bump the Rell version

Update the `rell` version in `gradle/libs.versions.toml`, then refresh the bundled language server:

```bash
./get-lsp.sh   # downloads language-server/rell-toolbox-language-server-<version>.jar
```

That's it for the grammar — `extractRellGrammar` pulls `Rell.g4` from the matching `frontend` sources
jar into `build/rell-grammar/`, and `generateGrammarSource` (wired before `compileKotlin`) emits
`RellParser`/`RellLexer` into `build/generated-src/antlr/main/...` (generated, not committed). The
ANTLR tool/runtime version is pinned together in `libs.versions.toml` (`antlr`) so the serialized ATN
always matches the runtime.

```bash
./gradlew generateGrammarSource   # optional: regenerate on demand
```

## Step 2: Reconcile token/rule references (only if the grammar changed shape)

Consumers reference the grammar through generated constants and the
`RellPsiElementTypes` bridge, **not** hard-coded IDs — so most token renumbering needs no changes.
You only need to touch code if rule/token *names* were added, removed, or renamed. Check:

- `RellPsiElementTypes` — token sets (comments/whitespace/strings) and punctuation lookups by literal text.
- `RellSyntaxHighlighter` — keyword/operator/bracket coloring (classifies literal tokens automatically).
- `RellFoldingBuilder`, `RellAdvancedSyntaxHighlightingAnnotator`, `RellTestLineMarkerProvider` —
  navigate by `RellParser.RULE_*` rule indices and `RellLexer.RULE_*` token types.

Remember ANTLR **labeled alternatives** (e.g. `# nameTypeAttrHeader`) are *not* separate rule nodes —
they are alternatives of their parent rule, so distinguish them by inspecting tokens, not rule index.

## Step 3: Verify

```bash
./gradlew compileKotlin
./gradlew test --tests "RellAntlrGrammarTest"   # parser smoke test
./gradlew runIde                                # manually check highlighting/folding/braces/gutters
```

The grammar itself is validated upstream in `rell-base` (a differential gate compares the ANTLR parser
against the legacy parser across the full corpus), so the plugin keeps only a thin sanity test.
