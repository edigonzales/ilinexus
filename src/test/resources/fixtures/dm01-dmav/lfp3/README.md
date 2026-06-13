# LFP3 Fixtures

## Dateien

- `dm01-minimal.itf`: kuratierter DM01-Minimalinput für LFP3-Roundtrip-Gates.
- `dmav-minimal.xtf`: kuratierter DMAV-Minimalinput für LFP3-Roundtrip-Gates.
- `dm01-real-extract.itf`: extractor-owned DM01-Fixture aus `src/test/data/DMAV_Version_1_1/DM01-AV-CH.itf`.
- `dmav-real-extract.xtf`: extractor-owned DMAV-Fixture aus `src/test/data/DMAV_Version_1_1/DMAVTYM_Alles_V1_1.xtf`.

## Ownership

- `Lfp3MinimalFixtureRoundtripTest` verwendet die `*-minimal`-Fixtures.
- `ExtractedDm01FixtureValidationTest`, `ExtractedDmavFixtureValidationTest`, `RealDm01ToDmavLfp3EndToEndTest`, `RealDmavToDm01Lfp3EndToEndTest` und `Lfp3RealExtractRoundtripTest` verwenden die `*-real-extract`-Fixtures.
