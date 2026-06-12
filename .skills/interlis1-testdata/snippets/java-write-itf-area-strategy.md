# Java strategy: INTERLIS 1 AREA fixtures

INTERLIS 1 AREA test data is fragile when written as raw ITF.

Prefer this strategy:

1. Compile the model with `IliModelService` or `ili2c`.
2. Build the main `Iom_jObject`.
3. Build the AREA geometry with an existing project geometry helper.
4. Write through `ItfWriter` or `ItfWriter2`.
5. Validate with `ilivalidator`.
6. Read back with `ItfReader2`.

Pseudo-code:

```java
TransferDescription td = new InterlisModelLoader()
        .compileModel("MinimalArea_V1", "src/test/data/interlis1/models");

Path out = Files.createTempFile("minimal-area-", ".itf");

IoxWriter writer = new ItfWriter(out.toFile(), td);
writer.write(new StartTransferEvent("test", null, null));
writer.write(new StartBasketEvent("MinimalArea_V1.Boden", "b1"));

Iom_jObject area = new Iom_jObject("MinimalArea_V1.Boden.Bodenbedeckung", "1");
area.setattrvalue("Art", "Acker");

// Do not fake AREA as text.
// Use an existing project helper that creates a valid IOM AREA/SURFACE geometry.
// Example names, adapt to the repository:
// IomObject geom = TestGeometries.area(...);
// area.addattrobj("Geometrie", geom);

writer.write(new ObjectEvent(area));
writer.write(new EndBasketEvent());
writer.write(new EndTransferEvent());
writer.flush();
writer.close();
```

Important:

- The snippet is a strategy, not a complete copy-paste fixture.
- The exact IOM geometry construction must follow existing project helpers or iox-ili conventions.
- The produced ITF is only acceptable after ilivalidator passes.
- If adjacent AREA objects are needed, validate that shared edges are not duplicated in the raw ITF helper table.
