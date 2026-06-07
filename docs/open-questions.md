# Open Questions

## Phase 1 (DSL-/Config-Modell stabilisieren)

### Resolved
- **YAML `class:` field**: Solved via `@JsonProperty("class")` + `@JsonAlias("clazz")` on `SourceSpec.clazz`.
- **Multi-input as list**: Solved via `inputs` List field + backward-compat `input` string.
- **Nested `target` vs flat `targetClass`**: Solved via helper `getEffectiveTargetClass()` + `normalize()`.

### Open (moved to Phase 2+)
- Soll der `MappingCompiler` in Phase 3 die flachen Felder (`targetClass`, `output`, `input`) als deprecated markieren und Warnungen ausgeben?
- Wie soll der Typed Plan (`TransformPlan`) genau strukturiert sein? (Phase 3)
- Soll `enumMap()` in Expressions bereits in Phase 4 implementiert werden oder erst in Phase 10?
- Welche Jackson-Konfiguration ist nötig für unbekannte Felder: ignorieren oder Fehler melden?
