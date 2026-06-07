# Mapping DSL

Die Mapping-Datei ist eine YAML-Konfiguration, die die Transformation von INTERLIS-Transferdaten steuert. Sie wird durch den `MappingCompiler` in einen typisierten Ausführungsplan übersetzt.

## Version

```yaml
version: 1
```

Pflichtfeld. Muss bei Breaking Changes der DSL erhöht werden. Aktuell: `1`.

## Minimale Mapping-Datei

```yaml
version: 1

job:
  inputs:
    - id: in1
      path: "input.xtf"
      model: "SourceModel"
  outputs:
    - id: out1
      path: "output.xtf"
      model: "TargetModel"

mapping:
  rules:
    - id: my-rule
      target:
        output: out1
        class: "TargetModel.Topic.Class"
      sources:
        - alias: src
          input: in1
          class: "SourceModel.Topic.Class"
      assign:
        AttributeName: "${src.SourceAttribute}"
```

## Job-Sektion

| Feld | Typ | Pflicht | Beschreibung |
|---|---|---|---|
| `version` | `int` | Ja | DSL-Version (mind. 1) |
| `job.name` | `string` | Nein | Name des Jobs |
| `job.description` | `string` | Nein | Beschreibung |
| `job.direction` | `string` | Nein | Transformationsrichtung (z.B. `dm01-to-dmav`) |
| `job.failPolicy` | `string` | Nein | Fehlerpolitik: `strict` (default), `lenient`, `reportOnly` |
| `job.modeldir` | `list[string]` | Nein | Modellverzeichnisse (URLs oder Pfade) |
| `job.inputs` | `list[InputSpec]` | Ja | Eingabedateien |
| `job.outputs` | `list[OutputSpec]` | Ja | Ausgabedateien |

### InputSpec

```yaml
- id: in1          # Pflicht: eindeutige ID
  path: "in.xtf"   # Pflicht: Pfad zur Eingabedatei
  model: "Model"   # Pflicht: INTERLIS-Modellname
  format: "xtf"    # Optional: "itf" oder "xtf" (wird aus Dateiendung erkannt)
```

### OutputSpec

```yaml
- id: out1         # Pflicht: eindeutige ID
  path: "out.xtf"  # Pflicht: Pfad zur Ausgabedatei
  model: "Model"   # Pflicht: INTERLIS-Modellname
  format: "xtf"    # Optional: "itf" oder "xtf"
```

## Mapping-Sektion

| Feld | Typ | Pflicht | Beschreibung |
|---|---|---|---|
| `mapping.oidStrategy` | `OidStrategySpec` | Nein | OID-Strategie |
| `mapping.basketStrategy` | `BasketStrategySpec` | Nein | Basket-Strategie |
| `mapping.enums` | `map[string, map[string, string]]` | Nein | Enum-Mapping-Tabellen |
| `mapping.defaults` | `map[string, string]` | Nein | Default-Werte |
| `mapping.compileMode` | `string` | Nein | `strict` (default) oder `allowTodos` |
| `mapping.rules` | `list[RuleSpec]` | Ja | Transformationsregeln |

### OidStrategySpec

```yaml
oidStrategy:
  default: deterministicUuid   # preserve | integer | uuid | deterministicUuid | external
  namespace: "my-namespace"    # für deterministicUuid
```

### BasketStrategySpec

```yaml
basketStrategy:
  default: preserve   # preserve | generateUuid | preserveOrGenerateUuid | byTopic | expression
```

## RuleSpec

Jede Rule erzeugt Zielobjekte aus Quellobjekten.

| Feld | Typ | Pflicht | Beschreibung |
|---|---|---|---|
| `id` | `string` | Ja | Eindeutige Rule-ID (innerhalb der Mapping-Datei) |
| `target` | `TargetSpec` | Ja | Zielklasse und Output |
| `sources` | `list[SourceSpec]` | Ja | Quellklassen (mindestens eine) |
| `where` | `string` | Nein | Filter-Expression für Quellobjekte |
| `identity` | `IdentitySpec` | Nein | Schlüsselfelder für OID-Bestimmung |
| `assign` | `map[string, string]` | Nein | Attributzuweisungen (Zielattribut → Expression) |
| `refs` | `list[RefMapping]` | Nein | Referenzen / Associations |
| `bags` | `map[string, BagSpec]` | Nein | BAG OF STRUCTURE |
| `create` | `list[CreateSpec]` | Nein | Zu erstellende Objekte |
| `joins` | `list[JoinSpec]` | Nein | Join-Definitionen |
| `metadata` | `MetadataSpec` | Nein | Metadaten (Direction, Roundtrip, Lossiness) |
| `defaults` | `map[string, string]` | Nein | Default-Werte für Zielattribute |

### TargetSpec

```yaml
target:
  output: out1     # Pflicht: Output-ID (muss in job.outputs existieren)
  class: "M.T.C"   # Pflicht: qualifizierte INTERLIS-Klasse
```

**Backward-Compat:** Die flachen Felder `targetClass` und `output` werden weiterhin unterstützt.

### SourceSpec

```yaml
sources:
  - alias: src               # Pflicht: Alias für Expressions
    class: "M.T.SourceClass" # Pflicht: qualifizierte INTERLIS-Quellklasse
    inputs: [in1, in2]       # Pflicht: Input-IDs (Liste)
    where: "src.Status == 'aktiv'" # Optional: Filter
```

**Backward-Compat:** Das flache Feld `input` (einzelner String) wird weiterhin unterstützt.

### IdentitySpec

```yaml
identity:
  sourceKey: ["src.NBIdent", "src.Nummer"]  # Felder für deterministische OID
```

### AttributeMapping (assign)

```yaml
assign:
  ZielAttribut: "Expression"     # Einfache Zuweisung
  Name: "${src.Name}"             # Quellattribut kopieren
  Status: "#aktiv"                # Literalwert
  Text: "truncate(src.Text, 60)"  # Funktion
```

### RefMapping

```yaml
refs:
  - association: "Entstehung_LFP3"    # Vollqualifizierte Association
    role: "Entstehung"                # Rollenname
    targetObject:
      rule: "regel-id"               # Rule-ID des Zielobjekts
      sourceRef: "src.Entstehung"    # Quellreferenz
```

### BagSpec

```yaml
bags:
  Textposition:
    from:
      input: dm01
      class: "M.T.PosTable"
      alias: pos
      where: "refEquals(pos.Ref, src)"
    structure: "M.Grafik.Textposition"
    assign:
      Position: "pos.Pos"
```

### CreateSpec

```yaml
create:
  - class: "M.T.NewClass"
    assign:
      attr1: "${src.val}"
```

### JoinSpec

```yaml
joins:
  - left: src1
    right: src2
    on: "src1.Ref == src2.OID"
    type: inner    # inner | left
```

### MetadataSpec

```yaml
metadata:
  direction: dm01-to-dmav
  roundtrip: notGuaranteed
  lossiness: minor   # none | minor | significant | unknown
```

## Enum-Mappings

```yaml
mapping:
  enums:
    Zuverlaessigkeit:
      ja: true
      nein: false
```

## Kompilierung

Der `MappingCompiler` validiert die Mapping-Datei und erzeugt einen `CompileResult` mit normalisiertem `JobConfig` und `DiagnosticCollector`. Fehlerhafte Mappings werden diagnostiziert:

```bash
ili-transformer validate-mapping --mapping my-mapping.yaml
```

## Nicht unterstützte Konstrukte (Phase 5)

Folgende DSL-Felder sind im Datenmodell vorbereitet, werden aber in der Runtime noch nicht ausgewertet:
- `bags` (erst Phase 12)
- `create` (noch nicht implementiert)
- `joins` (noch nicht implementiert)
- `metadata.lossiness` (rein dokumentativ)

Ab Phase 5 unterstützt:
- `where` auf Source-Ebene: Filter-Expression, die Quellobjekte vor der Transformation filtert. Unterstützt `!= null`, `== null`, `defined()`, `notDefined()`, Funktionsaufrufe und `${alias.attr}`-Pfadreferenzen.
