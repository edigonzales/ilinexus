---
name: java-test-gap
description: Identify the smallest useful tests before implementing a Java change
---

# Java Test Gap Skill

Use this skill before changing production Java code.

## Workflow

1. Restate the requested behavior.
2. Find existing tests for the affected behavior.
3. Identify the smallest missing tests:
   - one happy path,
   - one failure path,
   - one boundary case if relevant.
4. Prefer tests that fail before implementation.
5. Do not mock across boundaries that production code owns.
6. Follow existing test style.
7. Stop after proposing or adding tests unless implementation was requested.

## Output

Report:

- affected behavior
- existing coverage
- missing tests
- test files to add/change
- expected initial failures
