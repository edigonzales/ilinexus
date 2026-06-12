---
name: interlis-validation
description: Validate INTERLIS models and transfer data using ili2c and ilivalidator
---

# INTERLIS Validation Skill

Use this skill whenever creating or modifying:

- `.ili`
- `.itf`
- `.xtf`
- `.xml`
- mapping profiles that generate transfer files
- tests involving INTERLIS model or transfer data

## Rules

- Generated `.ili` files must be checked with `ili2c`.
- Generated transfer files must be checked with `ilivalidator`.
- INTERLIS 2.3 vs 2.4 XTF syntax differences are handled by reader/writer libraries. Do not special-case transfer syntax unless a library limitation is proven.
- If local tools are needed, use:
  - `/Users/stefan/apps/ili2c-5.6.8/ili2c.jar`
  - `/Users/stefan/apps/ilivalidator-1.15.0/ilivalidator-1.15.0.jar`

## Example commands

Compile model:

```bash
java -jar /Users/stefan/apps/ili2c-5.6.8/ili2c.jar   --modeldir "src/test/data/models;https://models.interlis.ch"   MyModel
```

Validate transfer:

```bash
java -jar /Users/stefan/apps/ilivalidator-1.15.0/ilivalidator-1.15.0.jar   --modeldir "src/test/data/models;https://models.interlis.ch"   --models MyModel   path/to/file.xtf
```

## Stop conditions

- model cannot be compiled
- transfer data does not validate
- modeldir is unclear
