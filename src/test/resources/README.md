# Test Resources

`src/test/resources/` enthält kleine kuratierte Artefakte für Tests.

## Struktur

- `mappings/` synthetische Test-Mappings und CLI-Fixtures
- `transfers/` kleine kuratierte ITF/XTF-Testdateien
- `fixtures/dm01-dmav/` Topic-Fixtures mit kuratierten `*-minimal`- und extractor-owned `*-real-extract`-Transfers
- `dm01-dmav/` Snapshots für Report-Tests

## Abgrenzung

- Produktive DM01/DMAV-Profile liegen nicht hier, sondern unter `profiles/`.
- Vollständige Echtdatensätze und offizielle Modelle liegen unter `src/test/data/`.
