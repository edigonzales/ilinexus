# Agent Decisions

## D001: Runtime ist IOM-basiert

Die Transformationsruntime arbeitet konzeptionell auf `IoxEvent` und `IomObject`.

## D002: INTERLIS-I/O bleibt abstrahiert

INTERLIS 2.3 vs 2.4 Transferformatdetails sollen nicht in der Engine special-cased werden. Reader/Writer-Libraries abstrahieren diese Unterschiede.

## D003: DM01/DMAV ist Referenz-Use-Case, nicht Engine-Sonderfall

DM01/DMAV-spezifische Regeln gehören in Profile, Tests, Reports oder DMAV-spezifische Hilfsklassen, nicht in generische Engine-Pfade.

## D004: Validierung ist Pflicht

Generierte `.ili`-Dateien müssen mit `ili2c` geprüft werden.
Generierte `.itf`/`.xtf`/`.xml`-Transferdaten müssen mit `ilivalidator` geprüft werden.

## D005: Status braucht Evidenzlevel

- implemented
- unit-tested
- synthetic-integration-tested
- real-model-tested
- real-transfer-tested
- validator-proven

## D006: INTERLIS-1-AREA ist Spezialfall

AREA-ITF-Fixtures sind topologisch. Roh-ITF darf nicht frei erfunden werden. Geometrie-Hilfstabellen müssen vor Haupttabellen stehen, und gemeinsame Kanten dürfen nur einmal vorkommen.
