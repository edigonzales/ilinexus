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
| `ILITRF-MAP-UNKNOWN-TARGET-CLASS` | ERROR | Zielklasse existiert nicht im Modell |
| `ILITRF-MAP-UNKNOWN-SOURCE-CLASS` | ERROR | Quellklasse existiert nicht im Modell |
| `ILITRF-MAP-ABSTRACT-TARGET-CLASS` | ERROR | Zielklasse ist abstrakt |
| `ILITRF-MAP-UNKNOWN-TARGET-ATTRIBUTE` | ERROR | Zielattribut existiert nicht in der Zielklasse |
| `ILITRF-MAP-UNKNOWN-SOURCE-ATTRIBUTE` | ERROR | Quellattribut existiert nicht |
| `ILITRF-MAP-UNKNOWN-ROLE` | WARNING | Rolle existiert nicht in der Zielklasse |
| `ILITRF-MAP-TYPE-MISMATCH` | WARNING | Expression-Typ passt nicht zum Zieltyp |
| `ILITRF-MAP-MANDATORY-MISSING` | WARNING | Pflichtattribut wird nicht gesetzt |
| `ILITRF-MAP-DUPLICATE-TARGET-ASSIGN` | ERROR | Target-Attribut mehrfach zugewiesen |
| `ILITRF-MAP-CYCLIC-DEPENDENCY` | ERROR | Zyklische Rule-Referenz |
| `ILITRF-MAP-NON-TRANSFERABLE-TARGET` | WARNING | Zielklasse ist nicht transferierbar (View) |

## Model (ILITRF-MODEL-*)

| Code | Severity | Bedeutung |
|---|---|---|
| `ILITRF-MODEL-COMPILE-FAILED` | ERROR | Modell-Kompilierung fehlgeschlagen |

## Runtime (ILITRF-RUN-*)

| Code | Severity | Bedeutung |
|---|---|---|
| `ILITRF-RUN-REF-UNRESOLVED` | WARNING | Referenz konnte nicht aufgelöst werden |
| `ILITRF-RUN-REF-AMBIGUOUS` | ERROR | Referenz ist mehrdeutig (mehrere Ziele) |

## Expression (ILITRF-EXPR-*) – Phase 4

| Code | Severity | Bedeutung |
|---|---|---|
| `ILITRF-EXPR-SYNTAX` | ERROR | Ausdruck konnte nicht geparst werden |
| `ILITRF-EXPR-UNKNOWN-FUNC` | ERROR | Unbekannte Funktion aufgerufen |
| `ILITRF-EXPR-TYPE` | ERROR | Typ-Fehler bei Expression-Auswertung |
| `ILITRF-EXPR-NON-DETERMINISTIC` | WARNING | Nicht-deterministische Funktion verwendet |
| `ILITRF-EXPR-UNSUPPORTED` | WARNING | Funktion/Feature noch nicht vollständig unterstützt |

## Geplant (spätere Phasen)

| Code | Phase | Bedeutung |
|---|---|---|
| `ILITRF-MAP-ENUM-INCOMPLETE` | 3/10 | Enum-Mapping unvollständig |
| `ILITRF-MAP-TODO` | 9 | TODO im Mapping |
| `ILITRF-RUN-CARDINALITY` | 5 | Kardinalität verletzt |
| `ILITRF-RUN-OID-COLLISION` | 6 | OID-Kollision |
| `ILITRF-RUN-BASKET` | 6 | Basket-Zuordnung fehlerhaft |
| `ILITRF-DMAV-CORRELATION-PARSE` | 8 | XLSX-Hint nicht interpretierbar |
