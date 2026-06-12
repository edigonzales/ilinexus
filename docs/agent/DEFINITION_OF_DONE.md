# Definition of Done

Eine Aufgabe ist erst erledigt, wenn alle zutreffenden Punkte erfüllt sind.

## Allgemein

- Die Änderung ist auf den Auftrag begrenzt.
- Neue oder geänderte Funktionalität ist getestet.
- Fehlerfälle sind getestet, wenn sie relevant sind.
- Dokumentation ist angepasst, wenn Verhalten, CLI, DSL, Testdaten oder bekannte Einschränkungen betroffen sind.
- Unsupported-Verhalten ist explizit diagnostiziert.

## Java/Gradle

- Relevante Unit-Tests laufen.
- Bei Produktionscodeänderungen läuft mindestens `./gradlew test`.
- Bei Integrationstests läuft der passende Integrationstest-Task, falls vorhanden.
- Keine neuen Warnungen oder TODOs ohne Begründung.

## INTERLIS

- Neue/geänderte `.ili`-Dateien wurden mit `ili2c` geprüft.
- Neue/geänderte `.itf`, `.xtf` oder `.xml`-Transferdaten wurden mit `ilivalidator` geprüft.
- INTERLIS-1-Testdaten verwenden keine INTERLIS-2-Syntax.
- INTERLIS-1-AREA-Testdaten beachten:
  - Geometrie-Hilfstabelle vor Haupttabelle,
  - gemeinsame AREA-Kanten nur einmal,
  - keine unvalidierten Roh-ITF-Fixtures.
- Transferdaten werden nach Möglichkeit mit `Iom_jObject` und `ItfWriter`/`XtfWriter` erzeugt statt roh von Hand.

## Mapping/DSL

- Parser-/Loader-Tests vorhanden.
- Compiler-Diagnostics-Tests vorhanden.
- Runtime-Test vorhanden, falls Semantik geändert wurde.
- Unsupported-Felder werden abgewiesen oder dokumentiert.

## DM01/DMAV

- Nicht auf synthetische Mini-Modelle als alleinigen Nachweis verlassen.
- Wenn reale Daten betroffen sind, Datensatz unter `src/test/data/DMAV_Version_1_1` berücksichtigen.
- Keine Produktionsreife behaupten ohne real-transfer-tested und validator-proven Status.
