# Codex Usage

Codex sollte über `AGENTS.md` und die relevanten Skill-Dateien gesteuert werden. Referenziere die benötigten Skills im Task explizit.

## INTERLIS 1 AREA fixture task

```text
Lies zuerst AGENTS.md.

Verwende:
- .skills/interlis-validation/SKILL.md
- .skills/interlis1-testdata/SKILL.md
- .skills/done-and-commit/SKILL.md

Aufgabe:
Erzeuge oder repariere ein minimales INTERLIS-1-AREA-Testfixture.

Pflicht:
- Starte vom Snippet .skills/interlis1-testdata/snippets/minimal-area-model.ili.
- Wenn du ITF erzeugst, bevorzuge Java + ItfWriter/ItfWriter2.
- Wenn du Roh-ITF anfassen musst: Geometrie-Hilfstabelle vor Haupttabelle, gemeinsame AREA-Kanten nur einmal.
- Validiere .ili mit ili2c.
- Validiere .itf mit ilivalidator.
- Committe nur bei grünem Gate.
```
