# ADR 0002: YAML DSL Design

**Status:** Accepted
**Date:** 2026-06-07
**Phase:** 1

## Context

Die Mapping-DSL muss deklarativ, versioniert und typisiert sein. Das alte Format hatte Inkonsistenzen zwischen YAML und Java-Modell (`clazz` vs `class`, `input` als String vs Liste).

## Decision

1. **`@JsonProperty("class")` + `@JsonAlias("clazz")`** auf `SourceSpec.clazz`. YAML verwendet `class:`, Java-Feld heisst `clazz` (da `class` ein reserviertes Wort ist).

2. **Nested `target`** als Primärformat: `target: { output: ..., class: ... }`. Flat `targetClass`/`output` bleiben via `@JsonAlias` und Helper-Methoden (`getEffectiveTargetClass()`) backward-kompatibel.

3. **`inputs` als `List<String>`** als Primärformat. Single-String `input` bleibt backward-kompatibel via `@JsonAlias` und `getInputIds()`.

4. **`assign` als `Map<String, String>`** als Primärformat. Die alte `attributes`-Liste bleibt backward-kompatibel. `getAllAttributes()` merged beide.

5. **`normalize()`** in `MappingLoader` konvertiert backward-compat Felder nach dem Laden, sodass Consumer nur das neue Format sehen.

6. **Diagnostic-basierte Validierung** statt Exceptions im `MappingCompiler`. `compile()` returned `CompileResult` mit `DiagnosticCollector`. `validate()` bleibt als Convenience-Methode.

## Consequences

- YAML-Dateien können nun `class:` statt `clazz:` verwenden.
- Alte YAML-Dateien mit `clazz:` oder `input:` (String) funktionieren weiterhin.
- Der Compiler wirft keine Exceptions mehr, sondern sammelt alle Fehler.
- Die Runtime (Engine) verwendet Helper-Methoden, die beide Formate unterstützen.
