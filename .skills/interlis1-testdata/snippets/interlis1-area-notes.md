# INTERLIS 1 AREA notes for agents

AREA in INTERLIS 1 is not just "a polygon attribute".

For ITF, AREA geometry is represented through a geometry helper/line table. This is the point that LLMs often get wrong.

## Mandatory facts

When working with raw INTERLIS 1 ITF containing AREA:

1. The geometry helper table comes before the main table.
2. The main table references/uses the AREA topology built from the helper table.
3. Shared edges between adjacent AREA objects must occur only once in the helper table.
4. Do not duplicate a common boundary once for each polygon.
5. Do not treat AREA like independent polygons if the test is meant to exercise ITF AREA topology.
6. Always validate with ilivalidator.
7. Prefer generating ITF with `ItfWriter`/`ItfWriter2` or an existing validated fixture instead of hand-writing raw ITF.

## Correct mental model

Two adjacent areas:

```text
+---------+---------+
|    A    |    B    |
|         |         |
+---------+---------+
```

The vertical boundary between A and B is a shared edge.

For AREA topology, that edge must be represented once, not once in A and once in B.

## If raw ITF must be edited

Add a large comment in the fixture or test explaining:

```text
This fixture intentionally tests INTERLIS 1 AREA ITF ordering.
The geometry helper table must appear before the main table.
Shared area boundaries must appear only once.
Do not reorder records unless ilivalidator still passes.
```

Never commit a raw AREA ITF fixture unless `ilivalidator` passes.
