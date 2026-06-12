# Commit Policy

## Grundsatz

Ein Agent darf nur committen, wenn der Auftrag dies erlaubt oder ausdrücklich verlangt und alle relevanten Checks bestanden sind.

## Vor dem Commit

Ausführen:

```bash
git status --short
git diff --stat
git diff
```

Dann prüfen:

- Sind nur relevante Dateien geändert?
- Sind generierte Dateien beabsichtigt?
- Sind Testdaten validiert?
- Gibt es fremde Änderungen?
- Wurde Dokumentation aktualisiert, falls nötig?

## Verifikationspflicht

Für normale Java-Änderungen:

```bash
./gradlew test
```

Für fokussierte Änderungen zuerst den kleinsten relevanten Test:

```bash
./gradlew test --tests "fully.qualified.TestClass"
```

Für INTERLIS:

- `.ili`: `ili2c`
- `.itf`/`.xtf`/`.xml`: `ilivalidator`

## Commit Message

Format:

```text
<area>: <imperative summary>

Why:
- ...

What:
- ...

Verification:
- <exact command>: passed
```

## Stop-Bedingungen

Nicht committen, wenn:

- Tests fehlschlagen,
- Tests nicht ausgeführt wurden,
- INTERLIS-Artefakte nicht validiert wurden,
- fremde Dateien im Diff sind,
- die Änderung grösser ist als der Auftrag.
