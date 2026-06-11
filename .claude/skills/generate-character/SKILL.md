---
name: generate-character
description: Use when the user wants to create a new creature or NPC stat block for the Cosmere TTRPG system — whether inventing from scratch, adapting a book creature, or generating a variant of an existing one.
---

# Cosmere Generate Creature

## Overview

Interactive workflow for inventing a new creature stat block. Ask questions first, generate second. Every creature should feel mechanically distinct — copy the *format* of existing creatures but invent *unique* abilities.

## Step 1: Ask for the Name

Ask the user for the creature's name. Then immediately:

1. Search `creatures/` for creatures whose name, type, or theme might be similar:
   ```bash
   ls creatures/*.json | xargs -I{} python3 -c "import json,sys; d=json.load(open('{}'));  print(d['name'], d['type'], d['role'])"
   ```
2. Read the 1–2 closest matches as mechanical references.
3. Tell the user which existing creatures you're drawing on.

## Step 2: Ask Three Questions

Ask all three in one message:

1. **Tier** (1–5): How dangerous is this creature?
2. **Role**: minion / rival / boss
3. **Concept**: One sentence — what makes this creature interesting? What's its combat gimmick or ecological role?

## Step 3: Propose Stats

Use these ranges as your starting point, then adjust for the creature's concept:

| Tier + Role | STR | PHY-DEF | SPD | INT | COG-DEF | WIL | AWA | SPI-DEF | PRE | HP avg | Focus |
|---|---|---|---|---|---|---|---|---|---|---|---|
| T1 Minion | 0–2 | 11–14 | 1–2 | 0–1 | 10–13 | 0–1 | 1–3 | 10–13 | 0–1 | 9–14 | 0–3 |
| T1 Rival | 1–4 | 12–15 | 1–4 | 0–3 | 11–15 | 1–3 | 1–3 | 12–16 | 0–3 | 18–30 | 3–5 |
| T1 Boss | ~2 | ~15 | ~3 | ~5 | ~17 | ~2 | ~2 | ~16 | ~4 | ~50 | ~6 |
| T2 Rival | 1–5 | 13–19 | 2–6 | 0–6 | 12–19 | 2–6 | 1–5 | 14–19 | 0–6 | 30–50 | 4–8 |
| T2 Boss | ~5 | ~18 | ~3 | ~2 | ~15 | ~3 | ~4 | ~16 | ~2 | ~144 | ~5 |
| T3 Rival | 1–5 | 14–19 | 3–6 | 1–4 | 15–20 | 3–6 | 3–6 | 15–19 | 1–4 | 47–60 | 5–8 |
| T3 Boss | 2–9 | 16–24 | 1–7 | 2–7 | 16–20 | 3–7 | 2–6 | 16–18 | 2–4 | 185–215 | 7–11 |
| T4 Rival | 2–5 | 16–17 | 5–6 | 2–5 | 15–19 | 3–4 | 4–5 | 16–18 | 2–3 | 42–65 | 5–6 |
| T4 Boss | 7–9 | 19–23 | 0–6 | 3–4 | 18–20 | 5–6 | 4–5 | 17–19 | 2–5 | 190–240 | 10–11 |

**Stat formulas (auto-calculated by the editor, but set manually for imported creatures):**
- `physical-defense = 10 + STR + SPD` (+ deflect for armor)
- `cognitive-defense = 10 + INT + WIL`
- `spiritual-defense = 10 + AWA + PRE`
- `health-min = round(0.75 × health-avg)`
- `health-max = round(1.20 × health-avg)`
- `focus = 2 + WIL`
- Movement: SPD 0→20 ft, 1–2→25, 3–4→30, 5–6→40, 7→60, 8+→80
- Sense range: AWA 0→5, 1–2→10, 3–4→20, 5–6→50, 7–8→100

**Deflect** represents armor (leather=1, chain=2, breastplate=2, plate=3–5).

Present the proposed stats in the stat block table format and ask if anything should change before continuing.

## Step 4: Invent Abilities

**Every creature needs at least one ability that no other creature has.**

### Trait count by tier/role
| Role | Traits | Actions | Strikes |
|---|---|---|---|
| Minion | 1–3 | 1–2 | 0–2 |
| Rival | 1–3 | 1–3 (up to 6 at T3+) | 1–2 |
| Boss | 2–5 | 2–6 | 1–3 |

Bosses always get the **Boss** trait. Minions always get the **Minion** trait.

### Traits (passive)
Traits represent innate qualities, immunities, or passive combat rules. Keep them short and specific. Good patterns:
- Conditional advantage on a test type
- Free reaction triggered by a specific event
- Double effect on a specific condition
- Extra action for a cost (focus/investiture)
- Immunity to a condition

### Actions
- **Single** (▶): most abilities, 1 focus if it has a meaningful effect
- **Double** (▶▶): powerful area effects, charge attacks, major repositioning
- **Free** (◇): minor bonuses, healing, passive triggers
- **Reaction** (⟳): triggered by a specific event (being hit, enemy moves near, ally uses ability)

Unique action ideas: an ability that punishes a specific player behavior, a battlefield control ability, a resource-generating ability, a combo-setup action.

### Strikes
- Name from the body part or weapon: Claw, Bite, Tail Slap, Greataxe, Javelin
- Damage: use `creature_constants.cljs` unarmed table — STR 0–2→1, 3–4→1d4, 5–6→1d8, 7–8→2d6, 9+→2d10
- Weapons add 1d4–1d12 depending on type
- `on-hit`: one brief additional effect (Prone, Afflicted, condition) — only if the strike has something special
- Skill: `athletics` for natural attacks; `heavy-weapons` for axes/maces; `light-weapons` for swords/bows
- Attack bonus ≈ relevant attribute + tier

### Skill bonuses
`bonus = relevant_attribute + tier + proficiency_offset` (where offset varies ±1 based on specialisation)

## Step 5: Present Draft and Iterate

Present the full creature as a prose summary (not the raw JSON) so the user can react quickly:

> **Stonewarden Crab** — Tier 2 Rival, Large Animal
> STR 4 / PHY-DEF 18 / SPD 1 | INT 0 / COG-DEF 12 / WIL 2 | AWA 2 / SPI-DEF 13 / PRE 0
> Health: 38 (28–45), Focus: 4, Movement: 25 ft., Senses: 20 ft. (smell)
> **Traits:** Calcified Shell (deflect 3 carapace), Stone Sense
> **Actions:** Shell Lock (reaction — when hit in melee, grapple attacker), Burrow (double)
> **Strikes:** Pincer ×2 (1d8 impact, reach 5, on-hit: Restrained)

Ask: "Does this feel right? Anything to change before I write the file?"

## Step 6: Write the JSON File

Once approved, write to `creatures/<kebab-case-id>.json`. Use this schema:

```json
{
  "id": "kebab-case-name",
  "name": "Display Name",
  "tier": 1,
  "role": "minion|rival|boss",
  "size": "small|medium|large|huge|gargantuan",
  "type": "animal|humanoid|entity|swarm",
  "strength": 0, "physical-defense": 10, "speed": 0,
  "intellect": 0, "cognitive-defense": 10, "willpower": 0,
  "awareness": 0, "spiritual-defense": 10, "presence": 0,
  "health-min": 0, "health-max": 0, "health-avg": 0,
  "focus": 0, "investiture": 0,
  "movement": 30,
  "deflect": 0, "deflect-explanation": null,
  "sense-range": 10, "sense-primary": "sight",
  "skills": {
    "physical": {},
    "cognitive": {},
    "spiritual": {}
  },
  "languages": "none",
  "traits": [{"name": "...", "description": "..."}],
  "actions": [{"action-cost": "single|double|free|reaction", "name": "...", "description": "..."}],
  "strikes": [{
    "action-cost": "single",
    "name": "...",
    "skill": "athletics|heavy-weapons|light-weapons",
    "reach": 5,
    "damage-base": "1d6",
    "damage-type": "keen|impact|energy|spirit|vital",
    "on-hit": null,
    "description": "Attack +N, reach N ft., one target. Graze: X damage. Hit: Y damage."
  }]
}
```

Confirm the file path after writing.

## Uniqueness Checklist

Before finalising, make sure the creature has:
- [ ] At least one trait no other creature in `creatures/` shares
- [ ] At least one action that creates an interesting decision for the players
- [ ] Stats that reflect its concept (fast creature has SPD 4+, tough creature has high STR and deflect, etc.)
- [ ] Health that makes sense for its size (large creatures trend higher)
- [ ] A reaction that makes the creature dangerous to engage carelessly
