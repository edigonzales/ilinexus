---
name: mapping-dsl-change
description: Change mapping DSL, YAML schema, or .ilimap semantics safely
---

# Mapping DSL Change Skill

Use this skill when changing:

- YAML schema
- `.ilimap` DSL
- `JobConfig`
- `MappingLoader`
- `MappingCompiler`
- `TransformPlan`
- expression syntax
- function registry

## Workflow

1. Identify whether this is syntax, static semantics, runtime behavior, or docs.
2. Add parser/loader tests.
3. Add compiler diagnostics tests.
4. Add runtime tests if semantics changed.
5. Update docs.
6. Unsupported fields must produce compiler diagnostics.

## Stop conditions

- no test demonstrates old vs new behavior
- docs not updated
- unsupported fields are silently ignored
