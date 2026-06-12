# INTERLIS 1 fixture area

Suggested layout:

```text
src/test/data/interlis1/
  models/
    MinimalPoint_V1.ili
    MinimalSurface_V1.ili
    MinimalArea_V1.ili
  transfers/
    minimal-point.itf
    minimal-surface.itf
    minimal-area.itf
```

Rules:

- Every `.ili` must compile with `ili2c`.
- Every `.itf` must validate with `ilivalidator`.
- AREA `.itf` fixtures are special:
  - geometry helper table before main table,
  - shared boundaries only once,
  - no unvalidated manual edits.
