# Creature Tags & Filtering

**Date:** 2026-06-11  
**Status:** Approved

## Overview

Add a tag system to the creature library that lets users filter the grid by one or more tags. Tags are AND-filtered — all selected tags must match. Tags are displayed on each creature card alongside the existing source label.

## Tags

### Real tags (stored in JSON)

Every creature gets a `"tags"` array in its JSON. The full tag vocabulary:

| Tag | Applies to |
|---|---|
| `Book` | Creatures from Stormlight canon sources |
| `Homebrew` | Custom/invented creatures (mutually exclusive with Book) |
| `Human` | Human humanoids |
| `Singer` | Singer humanoids (Warform, Nimbleform, Regals, etc.) |
| `Fused` | Odium's Fused (Altered One, Deepest One, etc.) |
| `Radiant` | Surgebinders / Knights Radiant |
| `Animal` | Non-humanoid fauna |
| `Spren` | Spren entities |
| `Soldier` | Military humanoids |
| `Criminal` | Thieves, bandits, crime bosses |
| `Named` | One-off named characters |

Every creature gets exactly one of `Book` / `Homebrew`. Other tags are additive.

**Notable exception:** Lobster Kremling is `Homebrew` despite being a static creature.

### Virtual tags (derived at runtime, not stored)

| Tag | Condition |
|---|---|
| `Server` | `(static-creature? creature)` is true |
| `Local` | `(static-creature? creature)` is false |

Virtual tags are filterable but never written to JSON.

## Data migration

All 58 existing static creature JSON files get a `"tags"` field added. The three custom creatures (Hangar the Traitor, Herdaz Resistance Fighter, Herdaz Resistance Lieutenant) get `"Homebrew"` plus appropriate category tags.

## Filter bar

- Horizontal bar of tag pills above the creature grid on the home page
- Tag list is derived dynamically: `(distinct (sort (mapcat :tags creatures)))` plus `Server` and `Local` at the end
- Pills are toggleable; active pills are visually highlighted
- **AND logic:** only creatures matching all active tags are shown
- A **Clear** button appears when any filter is active
- Filter state is a local Reagent atom in `home.cljs` — no persistence, resets on navigation

## Card display

In `decorated-creature-card`:

- The existing `code.source-label` element is kept with its class name; its content changes from `"predefined"` / `"personal"` to `"Server"` / `"Local"`
- Real tag pills are rendered alongside `code.source-label` in the controls bar (right side)
- Pills are display-only on cards — not clickable

## Implementation parts

1. **Migrate creature JSON files** — add `tags` to all 58 static creatures
2. **Filter bar** — filter state atom + pill bar in `home.cljs`; filter creatures seq before render
3. **Card display** — tag pills in `decorated-creature-card`; Less styles for pills

## Out of scope

- Tag editing UI for homebrew creatures
- Tag persistence / saved filter presets
- Color-coding tags by category
