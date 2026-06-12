---
name: dm01-dmav-real-data-gate
description: Use full DM01 and DMAV Version 1.1 datasets as regression gate
---

# DM01/DMAV Real Data Gate Skill

Use this skill for changes that affect:

- I/O
- geometry
- references
- OID handling
- baskets
- mapping compiler
- expression evaluation
- DM01/DMAV profiles
- ilivalidator integration

## Known test data

The repository may contain:

```text
./src/test/data/DMAV_Version_1_1
```

Expected content:

- one complete DM01 dataset as ITF
- one complete DMAV dataset as XTF

The exact filenames must be discovered by the agent.

## Workflow

1. Locate both datasets.
2. Identify exact model names and modeldirs.
3. Verify both original datasets can be read.
4. Validate original datasets with `ilivalidator` if not already covered by tests.
5. Run the smallest relevant transformation test.
6. Never declare DM01↔DMAV support based only on synthetic models.
