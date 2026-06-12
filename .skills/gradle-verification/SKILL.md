---
name: gradle-verification
description: Run the correct Gradle verification commands for Java changes
---

# Gradle Verification Skill

## Workflow

1. Inspect available tasks:

```bash
./gradlew tasks --group verification
```

2. For a focused code change:

```bash
./gradlew test --tests "fully.qualified.TestClass"
```

3. For production code changes:

```bash
./gradlew test
```

4. For integration behavior, if available:

```bash
./gradlew integrationTest
```

5. Capture exact commands and results.

## Stop conditions

- Gradle wrapper missing
- tests fail
- command result is ambiguous
