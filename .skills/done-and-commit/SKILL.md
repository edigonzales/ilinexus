---
name: done-and-commit
description: Verify Definition of Done and create an informative commit only after tests pass
---

# Done and Commit Skill

## Rules

- Never commit before verification.
- Never claim tests passed unless the exact command was executed.
- Never include unrelated files in a commit.
- Never commit generated, invalid, or unverified INTERLIS artifacts.
- If tests fail, stop and report the failure.

## Workflow

1. Inspect working tree:

```bash
git status --short
git diff --stat
git diff
```

2. Identify change type.

3. Run verification.

For normal Java changes:

```bash
./gradlew test
```

For focused changes:

```bash
./gradlew test --tests "fully.qualified.TestClass"
./gradlew test
```

4. For INTERLIS artifacts:

- `.ili`: validate with `ili2c`
- `.itf`, `.xtf`, `.xml`: validate with `ilivalidator`

5. Stage only relevant files.

6. Commit with informative message.

## Commit message format

```text
<area>: <imperative summary>

Why:
- ...

What:
- ...

Verification:
- <exact command>: passed
```

## Stop conditions

- any required test fails
- test execution was skipped
- generated INTERLIS artifacts were not validated
- unrelated files are modified
