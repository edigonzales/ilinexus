# BB Fixtures

## Dateien

- `dm01-minimal.itf`: kuratierter DM01-Minimalinput für BB-Roundtrip-Gates.
- `dmav-minimal.xtf`: kuratierter DMAV-Minimalinput für BB-Roundtrip-Gates.
- `dm01-real-extract.itf`: extractor-owned DM01-Fixture aus `src/test/data/DMAV_Version_1_1/DM01-AV-CH.itf`.
- `dmav-real-extract.xtf`: extractor-owned DMAV-Fixture aus `src/test/data/DMAV_Version_1_1/DMAVTYM_Alles_V1_1.xtf`.

## Ownership

- `BbMinimalFixtureRoundtripTest` verwendet die `*-minimal`-Fixtures.
- `ExtractedBbDm01FixtureValidationTest` und `ExtractedBbDmavFixtureValidationTest` verwenden die `*-real-extract`-Fixtures.
- `BbFullDatasetForwardGateTest`, `BbFullDatasetRoundtripSmokeTest` und `BbItfAreaReadDiagnosticTest` bleiben bewusst auf dem vollständigen Datensatz.
