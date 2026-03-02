# ilinexus

Java-first INTERLIS transformation engine scaffold.

## Tech baseline
- Package root: `guru.interlis.ilinexus`
- Build tool: Gradle
- Java version: 25 (toolchain)

## Current implementation status
The project now includes:
1. Two-pass execution (`pass1Index` + target build + deferred ref resolution + write phase).
2. Explicit mapping validation via `MappingCompiler`.
3. INTERLIS model compile/read/write adapters for ITF/XTF.
4. Basic expression support (`${alias.attr}` and `if(...)`).
5. Diagnostics for unresolved/ambiguous references.
6. Initial unit tests and imported INTERLIS test fixtures under `src/test/data`.

## Run
```bash
./gradlew test
./gradlew run --args="path/to/mapping.yaml --modeldir path/to/models"
```
