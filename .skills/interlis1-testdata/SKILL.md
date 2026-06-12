---
name: interlis1-testdata
description: Create, modify, and validate INTERLIS 1 models and ITF test data safely, including AREA topology fixtures
---

# INTERLIS 1 Test Data Skill

Use this skill whenever you create or modify:

- INTERLIS 1 `.ili` models
- INTERLIS 1 `.itf` transfer data
- Java tests that generate ITF data
- AREA/SURFACE geometry fixtures

## Core rule

Do not invent INTERLIS 1 syntax from memory.

Always start from one of:

1. an existing validated fixture,
2. an official INTERLIS 1 model,
3. a minimal snippet from `.skills/interlis1-testdata/snippets/`,
4. Java-generated `IomObject` data written with `ItfWriter`/`ItfWriter2`.

## Preferred strategy

For transfer data, prefer this order:

1. Generate objects in Java with `Iom_jObject`.
2. Write ITF using `ItfWriter` or project-approved writer.
3. Validate the generated `.itf` with `ilivalidator`.
4. Only if absolutely necessary, edit raw `.itf` manually.

## INTERLIS 1 syntax reminders

- Start with `TRANSFER INTERLIS1;`.
- Use `MODEL ModelName`, not `MODEL ModelName =`.
- End a topic with `END TopicName.` including a dot.
- End the model with `END ModelName.` including a dot.
- Use `TABLE`, not `CLASS`.
- References use `attr: -> TargetTable;`.
- Optional attributes use `OPTIONAL`.
- Text length is written as `TEXT*12`.
- Identifiers use `IDENT ...;` or `NO IDENT`.
- Do not use INTERLIS 2 constructs such as `STRUCTURE`, `ASSOCIATION`, `BAG OF`, `MANDATORY`, `OID AS`, or `INTERLIS.UUIDOID`.

## AREA-specific rules

AREA is topological. Do not treat it as merely independent polygons.

When working with raw INTERLIS 1 ITF containing AREA:

1. The geometry helper table must appear before the main table.
2. The main table records depend on topology from the helper table.
3. Shared boundaries between adjacent AREA objects must occur only once.
4. Never duplicate a shared edge once per neighboring polygon.
5. Never commit a hand-written AREA ITF unless `ilivalidator` passes.
6. Prefer `ItfWriter`/`ItfWriter2` or an existing validated fixture.
7. When testing AREA reading, assert behavior through `ItfReader2`, because it merges AREA helper tables into canonical geometry objects.

Use these snippets:

- `.skills/interlis1-testdata/snippets/minimal-area-model.ili`
- `.skills/interlis1-testdata/snippets/interlis1-area-notes.md`
- `.skills/interlis1-testdata/snippets/java-write-itf-area-strategy.md`

## Required tests

When adding INTERLIS 1 fixtures, add tests that prove:

1. the model compiles,
2. the transfer validates,
3. the transfer can be read with `ItfReader2`,
4. expected `IomObject` tags are present,
5. expected attributes are present,
6. geometry attributes are readable.

For AREA fixtures additionally prove:

7. helper table ordering is preserved if raw ITF is part of the test,
8. adjacent AREA shared edges are not duplicated,
9. `ItfReader2` returns expected canonical main objects/geometries.

## Stop conditions

Stop and report instead of committing if:

- the `.ili` file does not compile,
- the `.itf` file does not validate,
- generated test data only works with a mock reader,
- raw ITF was edited manually and not validated,
- INTERLIS 2 syntax was accidentally used,
- AREA helper tables were reordered without validation,
- shared AREA edges are duplicated in a raw fixture,
- the modeldir is unclear.
