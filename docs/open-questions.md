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

## Phase 2 (INTERLIS Model Service und Inventory)

### Resolved
- **OID-Typen-Erkennung**: Über `Topic.getOid()`/`Topic.getBasketOid()` → Domain → Type-Klassenname.
- **Mandatory-Detektion**: Über `AttributeDef.getCardinality().getMinimum() > 0`.
- **Rollen-Extraktion**: Über `Table.getTargetForRoles()` (Roles aus Associations).
- **INTERLIS-interne Modelle filtern**: `INTERLIS`, `GeometryCHLV95_V2`, `CoordSystem` werden ignoriert.
- **Enum-Typ-Darstellung**: `getDomain()` gibt bei Domain-Referenzen den Domain-Namen zurück (`DOMAIN Model.Topic.DomainName`). Enum-Werte-Auflösung via `EnumerationType.getEnumeration().getElement(i).getName()`.

### Open
- Sollen Structure-Definitionen als eigene `ClassInventory`-Einträge erscheinen (aktuell nicht)?
- Soll der `inspect-model` Output auch View-Klassen auflisten?
- Soll die Enum-Werte-Liste direkt im Typ-String erscheinen oder als separates Feld?
- Wie soll das `modeldir`-Handling bei `--modeldir`-Option mit Semikolon-Trennung mit ili2c-Settings interagieren?

## Phase 3 (Typed Mapping Compiler)

### Resolved
- **TypedPlan-Struktur**: `TransformPlan` → `RulePlan` → `SourcePlan`, `AssignmentPlan`, `RefPlan`. Runtime akzeptiert nur noch `TransformPlan`.
- **Typkompatibilität**: Einfache Heuristik (Literal-Klassifikation, SOURCE_PATH-Auflösung, STRUCTURE→STRUCTURE, ENUM↔TEXT). Funktionsaufrufe als UNKNOWN (→ Phase 4).
- **Mandatory-Coverage**: `AttributeDef.getCardinality().getMinimum() > 0` → Warning wenn nicht im assign.
- **Compiler-Report**: JSON + Markdown via `CompilerReport` Klasse.
- **DiagnosticCode-Präfix**: `ILITRF-MAP-*` für Compiler-Diagnostics.
- **Backward-Kompatibilität**: Engine behält `run(JobConfig)`-Methode, neue `runTyped(TransformPlan)` parallel.

### Open
- ~~Soll die Typ-Inferenz bereits in Phase 4 Funktionsrückgabetypen kennen? (z.B. `truncate()` → TEXT, `toXmlDateTime()` → XML_DATE_TIME)~~ **Erledigt in Phase 4**: `MappingCompiler` nutzt jetzt `FunctionRegistry` zur Typ-Inferenz für `FUNCTION_CALL`.
- Soll `isAbstract()`-Prüfung Error oder Warning sein? (aktuell Error)
- Sollen OID und Basket-Strategien bereits im `TransformPlan` repräsentiert werden?
- Wie granular sollen Enum-Mapping-Coverage-Checks sein? (aktuell keine)

## Phase 4 (Expression Engine und Function Registry)

### Resolved
- **Expression Parser**: Eigener rekursiver Abstiegs-Parser (kein JEXL). Unterstützt Literale (String, Number, Boolean, null, Enum), Pfadreferenzen (`${alias.attr}`), Funktionsaufrufe, `if()`-Konditionale, `!= null`/`== null`-Vergleiche.
- **Value Layer**: `sealed interface Value` mit `TextValue`, `NumberValue`, `BooleanValue`, `DateValue`, `XmlDateTimeValue`, `EnumValue`, `CoordValue`, `ReferenceValue`, `NullValue`.
- **Function Registry**: `FunctionRegistry` mit Builtins: `coalesce`, `defined`, `notDefined`, `isNull`, `default`, `null` (Basic); `concat`, `substring`, `trim`, `upper`, `lower`, `replace`, `truncate` (String); `toXmlDateTime`, `now` (Date, `now` ist @NonDeterministic); `enumMap` (Stub, Phase 10), `enumDefault`, `enumName`; `refOid`, `refEquals` (Reference).
- **Compiler-Integration**: `MappingCompiler` inferiert Funktionsrückgabetypen über `FunctionRegistry` (z.B. `truncate()` → TEXT, `toXmlDateTime()` → XML_DATE_TIME).
- **TransformationEngine**: Nutzt neue `ExpressionEngine` mit `EvalContext` und typisierten `Value`-Rückgaben.
- **`enumMap()`**: Als Stub implementiert (Pass-through mit Diagnostic-Warnung). Vollständige Enum-Mapping-Logik in Phase 10.

### Open
- Soll der Parser auch arithmetische Ausdrücke (`+`, `-`, `*`, `/`) unterstützen? Aktuell nicht.
- Sollen Vergleichsoperatoren (`>`, `<`, `>=`, `<=`) unterstützt werden? Aktuell nur `!= null`/`== null`.
- Sollen `lookupOne`/`lookupMany` (StateStore-Lookups) bereits in Phase 4 implementiert werden aber Zugriff auf StateStore erst in Phase 5?
