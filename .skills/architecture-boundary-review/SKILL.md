---
name: architecture-boundary-review
description: Review Java package/module boundaries and propose tests
---

# Architecture Boundary Review Skill

## Intended boundaries

- Generic engine must not depend on DM01/DMAV-specific packages.
- Core must not depend on optional WKF/GeoTools modules.
- CLI may depend on all modules.
- Runtime engine should not know whether input came from ITF, XTF, Shapefile, GeoPackage, CSV, or JDBC.

## Workflow

1. Inspect package structure.
2. Identify intended dependency direction.
3. Check existing architecture tests.
4. Find confirmed boundary violations.
5. Propose ArchUnit rules only for stable boundaries.
