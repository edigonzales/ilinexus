# Known Issues / Watch List

## Engine / Compiler

- TypeSystem-Zuordnung muss über Input-/Output-ID zuverlässig funktionieren.
- Deterministic UUID darf ohne Identity Keys nicht kollidieren.
- Rule-level `where` muss entweder korrekt funktionieren oder als unsupported abgewiesen werden.
- `validate-mapping` soll modellbewusste Validierung ausführen.
- `--validate` muss tatsächlich ilivalidator ausführen.

## INTERLIS

- INTERLIS-1-Testdaten sind fehleranfällig und müssen validiert werden.
- Roh geschriebene ITF-Fixtures sind zu vermeiden.
- INTERLIS-1-AREA-Fixtures sind besonders kritisch:
  - Hilfstabelle vor Haupttabelle,
  - gemeinsame Kanten nur einmal,
  - immer ilivalidator.

## DM01/DMAV

- Synthetische Testmodelle beweisen keine echte DM01↔DMAV-Unterstützung.
- Vollständige Datensätze unter `src/test/data/DMAV_Version_1_1` als Regression-Gate nutzen.
