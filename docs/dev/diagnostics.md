# Diagnostic Codes

## Compiler-Validierung (ILITRF-MAP-*)

| Code | Severity | Bedeutung |
|---|---|---|
| `ILITRF-MAP-VERSION` | ERROR | `version`-Feld fehlt oder < 1 |
| `ILITRF-MAP-MISSING-ID` | ERROR | Rule hat kein `id`-Feld |
| `ILITRF-MAP-DUPLICATE-ID` | ERROR | Rule-`id` ist nicht eindeutig |
| `ILITRF-MAP-MISSING-TARGET-CLASS` | ERROR | Zielklasse (`target.class`) fehlt |
| `ILITRF-MAP-UNKNOWN-OUTPUT` | ERROR | Output-ID existiert nicht in `job.outputs` |
| `ILITRF-MAP-MISSING-SOURCE-CLASS` | ERROR | Source hat kein `class`-Feld |
| `ILITRF-MAP-MISSING-ALIAS` | ERROR | Source hat kein `alias`-Feld |
| `ILITRF-MAP-DUPLICATE-ALIAS` | ERROR | Source-`alias` ist nicht eindeutig pro Rule |
| `ILITRF-MAP-UNKNOWN-INPUT` | ERROR | Input-ID existiert nicht in `job.inputs` |
| `ILITRF-MAP-MISSING-INPUT` | ERROR | Source hat kein `input`/`inputs`-Feld |

## Runtime (ILITRF-RUN-*)

| Code | Severity | Bedeutung |
|---|---|---|
| `ILITRF-RUN-REF-UNRESOLVED` | WARNING | Referenz konnte nicht aufgelöst werden |
| `ILITRF-RUN-REF-AMBIGUOUS` | ERROR | Referenz ist mehrdeutig (mehrere Ziele) |

## Geplant (spätere Phasen)

| Code | Phase | Bedeutung |
|---|---|---|
| `ILITRF-MAP-UNKNOWN-CLASS` | 3 | Ziel-/Quellklasse existiert nicht im Modell |
| `ILITRF-MAP-UNKNOWN-ATTRIBUTE` | 3 | Attribut existiert nicht |
| `ILITRF-MAP-TYPE-MISMATCH` | 3 | Expression-Typ passt nicht zum Zieltyp |
| `ILITRF-MAP-MANDATORY-MISSING` | 3 | Pflichtattribut wird nicht gesetzt |
| `ILITRF-MAP-ENUM-INCOMPLETE` | 3 | Enum-Mapping unvollständig |
| `ILITRF-MAP-TODO` | 3 | TODO im Mapping |
| `ILITRF-RUN-CARDINALITY` | 5 | Kardinalität verletzt |
| `ILITRF-RUN-OID-COLLISION` | 6 | OID-Kollision |
| `ILITRF-RUN-BASKET` | 6 | Basket-Zuordnung fehlerhaft |
| `ILITRF-DMAV-CORRELATION-PARSE` | 8 | XLSX-Hint nicht interpretierbar |
