# Creature Tags & Filtering Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add a tag system to the creature library — tags stored in JSON, filter bar above the creature grid (AND logic), tags displayed on cards alongside a renamed Server/Local source label, and tag inference in the generate-character skill.

**Architecture:** Tags are a `["string"]` array on each creature JSON. Two virtual tags (`Server`/`Local`) are derived at runtime from `static-creature?` and never stored. The home page holds filter state in a local Reagent atom and filters the creatures seq before rendering. The `decorated-creature-card` renders tag pills alongside the updated `code.source-label`.

**Tech Stack:** ClojureScript, Reagent, Shadow CLJS, Less. No test infrastructure exists — verify all changes by loading `http://localhost:8084` with `npm run dev` running. There are 59 creature JSON files total.

---

## File Map

| File | Change |
|---|---|
| `creatures/*.json` (58 files) | Add `"tags": [...]` field |
| `src/cljs/cosmere_tools/pages/home.cljs` | Filter atom, tag bar, filtered render |
| `src/less/main.less` | Import new tag styles file |
| `src/less/tags.less` | New file — filter bar + card pill styles |
| `src/cljs/cosmere_tools/components/decorated_creature_card.cljs` | Tag pills + rename source-label content |
| `.claude/skills/generate-character/skill.md` | Add tag inference + confirmation step |

---

## Tag Reference

Every creature gets exactly one of `Book`/`Homebrew` plus zero or more category tags.

| Creature | Tags |
|---|---|
| Altered One | Book, Fused |
| Angerspren | Book, Spren |
| Archer | Book, Human, Soldier |
| Assassin | Book, Human, Criminal |
| Axehound | Book, Animal |
| Bandit | Book, Human, Criminal |
| Brightlord | Book, Human, Soldier |
| Chasmfiend | Book, Animal |
| Chull | Book, Animal |
| Commoner | Book, Human |
| Cremling Swarm | Book, Animal |
| Crime Boss | Book, Human, Criminal |
| Deepest One | Book, Fused |
| Devastating One | Book, Fused |
| Direform Regal | Book, Singer |
| Duelist Shardbearer | Book, Human, Soldier |
| Dustbringer of the Fourth Ideal | Book, Human, Radiant |
| Dustbringer of the Second Ideal | Book, Human, Radiant |
| Dustbringer of the Third Ideal | Book, Human, Radiant |
| Elite Shardbearer | Book, Human, Soldier |
| Expert | Book, Human |
| Flowing One | Book, Fused |
| Focused One | Book, Fused |
| Ghostblood Enforcer | Book, Human, Criminal |
| Ghostblood Spy | Book, Human, Criminal |
| Greater Larkin | Book, Animal |
| Guard | Book, Human, Soldier |
| Hangar the Traitor | Homebrew, Human, Named |
| Heavenly One | Book, Fused |
| Herdaz Resistance Fighter | Homebrew, Human, Soldier |
| Herdaz Resistance Lieutenant | Homebrew, Human, Soldier |
| Husked One | Book, Fused |
| Khornak | Book, Animal |
| Larkin | Book, Animal |
| Lobster Kremling | Homebrew, Human, Criminal |
| Magnified One | Book, Fused |
| Masked One | Book, Fused |
| Midnight Essence | Book, Spren |
| Nightform Regal | Book, Singer |
| Nimbleform Singer | Book, Singer |
| Painspren | Book, Spren |
| Ryshadium | Book, Animal |
| Servant of Yelig-Nar | Book, Human |
| Shellmite | Book, Animal |
| Skybreaker of the Fourth Ideal | Book, Human, Radiant |
| Skybreaker of the Second Ideal | Book, Human, Radiant |
| Sleepless | Book |
| Socialite | Book, Human |
| Soulcaster Savant | Book, Human |
| Spear Infantry | Book, Human, Soldier |
| Stormform Regal | Book, Singer |
| Swordmaster Ardent | Book, Human, Soldier |
| Thief | Book, Human, Criminal |
| Thrill Berserker | Book, Human, Soldier |
| Thunderclast | Book |
| Warform Singer | Book, Singer |
| Whitespine | Book, Animal |
| Windrunner Squire | Book, Human, Radiant |
| Yu-Nerig | Book, Animal |

---

## Task 1: Tag all creature JSON files

**Files:**
- Modify: all 58 `creatures/*.json` files

- [ ] **Step 1: Add tags to Book creatures (A–G)**

For each file below, add `"tags": [...]` after the `"languages"` field:

```
creatures/altered-one.json        → "tags": ["Book", "Fused"]
creatures/angerspren.json         → "tags": ["Book", "Spren"]
creatures/archer.json             → "tags": ["Book", "Human", "Soldier"]
creatures/assassin.json           → "tags": ["Book", "Human", "Criminal"]
creatures/axehound.json           → "tags": ["Book", "Animal"]
creatures/bandit.json             → "tags": ["Book", "Human", "Criminal"]
creatures/brightlord.json         → "tags": ["Book", "Human", "Soldier"]
creatures/chasmfiend.json         → "tags": ["Book", "Animal"]
creatures/chull.json              → "tags": ["Book", "Animal"]
creatures/commoner.json           → "tags": ["Book", "Human"]
creatures/cremling-swarm.json     → "tags": ["Book", "Animal"]
creatures/crime-boss.json         → "tags": ["Book", "Human", "Criminal"]
creatures/deepest-one.json        → "tags": ["Book", "Fused"]
creatures/devastating-one.json    → "tags": ["Book", "Fused"]
creatures/direform-regal.json     → "tags": ["Book", "Singer"]
creatures/duelist-shardbearer.json → "tags": ["Book", "Human", "Soldier"]
creatures/dustbringer-of-the-fourth-ideal.json → "tags": ["Book", "Human", "Radiant"]
creatures/dustbringer-of-the-second-ideal.json → "tags": ["Book", "Human", "Radiant"]
creatures/dustbringer-of-the-third-ideal.json  → "tags": ["Book", "Human", "Radiant"]
creatures/elite-shardbearer.json  → "tags": ["Book", "Human", "Soldier"]
creatures/expert.json             → "tags": ["Book", "Human"]
creatures/flowing-one.json        → "tags": ["Book", "Fused"]
creatures/focused-one.json        → "tags": ["Book", "Fused"]
creatures/ghostblood-enforcer.json → "tags": ["Book", "Human", "Criminal"]
creatures/ghostblood-spy.json     → "tags": ["Book", "Human", "Criminal"]
creatures/greater-larkin.json     → "tags": ["Book", "Animal"]
creatures/guard.json              → "tags": ["Book", "Human", "Soldier"]
```

- [ ] **Step 2: Add tags to Book creatures (H–Z) + Homebrew creatures**

```
creatures/heavenly-one.json       → "tags": ["Book", "Fused"]
creatures/husked-one.json         → "tags": ["Book", "Fused"]
creatures/khornak.json            → "tags": ["Book", "Animal"]
creatures/larkin.json             → "tags": ["Book", "Animal"]
creatures/magnified-one.json      → "tags": ["Book", "Fused"]
creatures/masked-one.json         → "tags": ["Book", "Fused"]
creatures/midnight-essence.json   → "tags": ["Book", "Spren"]
creatures/nightform-regal.json    → "tags": ["Book", "Singer"]
creatures/nimbleform-singer.json  → "tags": ["Book", "Singer"]
creatures/painspren.json          → "tags": ["Book", "Spren"]
creatures/ryshadium.json          → "tags": ["Book", "Animal"]
creatures/servant-of-yelig-nar.json → "tags": ["Book", "Human"]
creatures/shellmite.json          → "tags": ["Book", "Animal"]
creatures/skybreaker-of-the-fourth-ideal.json → "tags": ["Book", "Human", "Radiant"]
creatures/skybreaker-of-the-second-ideal.json → "tags": ["Book", "Human", "Radiant"]
creatures/sleepless.json          → "tags": ["Book"]
creatures/socialite.json          → "tags": ["Book", "Human"]
creatures/soulcaster-savant.json  → "tags": ["Book", "Human"]
creatures/spear-infantry.json     → "tags": ["Book", "Human", "Soldier"]
creatures/stormform-regal.json    → "tags": ["Book", "Singer"]
creatures/swordmaster-ardent.json → "tags": ["Book", "Human", "Soldier"]
creatures/thief.json              → "tags": ["Book", "Human", "Criminal"]
creatures/thrill-berserker.json   → "tags": ["Book", "Human", "Soldier"]
creatures/thunderclast.json       → "tags": ["Book"]
creatures/warform-singer.json     → "tags": ["Book", "Singer"]
creatures/whitespine.json         → "tags": ["Book", "Animal"]
creatures/windrunner-squire.json  → "tags": ["Book", "Human", "Radiant"]
creatures/yu-nerig.json           → "tags": ["Book", "Animal"]

creatures/hangar-the-traitor.json         → "tags": ["Homebrew", "Human", "Named"]
creatures/herdaz-resistance-fighter.json  → "tags": ["Homebrew", "Human", "Soldier"]
creatures/herdaz-resistance-lieutenant.json → "tags": ["Homebrew", "Human", "Soldier"]
creatures/lobster-kremling.json           → "tags": ["Homebrew", "Human", "Criminal"]
```

- [ ] **Step 3: Verify tag field placement**

Open any 3 files and confirm `"tags"` appears after `"languages"` and is a valid JSON array:

```bash
python3 -c "
import json, glob
for f in glob.glob('creatures/*.json'):
    d = json.load(open(f))
    assert 'tags' in d, f'MISSING tags: {f}'
    assert isinstance(d['tags'], list), f'BAD tags: {f}'
    assert len(d['tags']) >= 1, f'EMPTY tags: {f}'
print('All', len(glob.glob('creatures/*.json')), 'files have valid tags')
"
```

Expected output ends with: `files have valid tags` (count will be 59)

- [ ] **Step 4: Commit**

```bash
git add creatures/
git commit -m "Add tags to all 59 creature JSON files"
```

---

## Task 2: Filter bar — home.cljs

**Files:**
- Modify: `src/cljs/cosmere_tools/pages/home.cljs`

- [ ] **Step 1: Rewrite home.cljs with filter state and tag bar**

Replace the entire file with:

```clojure
(ns cosmere-tools.pages.home
  (:require [cosmere-tools.creature-library :refer [creatures static-creature?]]
            [cosmere-tools.components.decorated-creature-card :refer [decorated-creature-card]]
            [reagent.core :as r]))

(defn all-tags []
  (concat
   (sort (distinct (mapcat :tags creatures)))
   ["Server" "Local"]))

(defn creature-matches-tags? [creature active-tags]
  (if (empty? active-tags)
    true
    (every? (fn [tag]
              (case tag
                "Server" (static-creature? creature)
                "Local"  (not (static-creature? creature))
                (contains? (set (:tags creature)) tag)))
            active-tags)))

(defn tag-filter-bar [active-tags]
  [:div.tag-filter-bar
   (for [tag (all-tags)]
     ^{:key tag}
     [:button.tag-pill
      {:class    (when (contains? @active-tags tag) "active")
       :on-click #(swap! active-tags
                         (if (contains? @active-tags tag) disj conj)
                         tag)}
      tag])
   (when (seq @active-tags)
     [:button.tag-pill.clear-pill
      {:on-click #(reset! active-tags #{})}
      "✕ Clear"])])

(defn home-page []
  (let [active-tags (r/atom #{})]
    (fn []
      (let [filtered (filter #(creature-matches-tags? % @active-tags) creatures)]
        [:div.page.home-page
         [tag-filter-bar active-tags]
         [:div.creatures-grid
          (doall
           (for [creature filtered]
             ^{:key (:id creature)}
             [decorated-creature-card creature]))]]))))
```

- [ ] **Step 2: Verify the page loads**

Open `http://localhost:8084`. The creature grid should render. Check the browser console for errors.

- [ ] **Step 3: Commit**

```bash
git add src/cljs/cosmere_tools/pages/home.cljs
git commit -m "Add tag filter bar to home page"
```

---

## Task 3: Filter bar — Less styles

**Files:**
- Create: `src/less/tags.less`
- Modify: `src/less/main.less`

- [ ] **Step 1: Create src/less/tags.less**

```less
// Tag filter bar above creature grid
.tag-filter-bar {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  padding: 12px 20px;
  max-width: 1400px;
  margin: 0 auto;
}

.tag-pill {
  padding: 3px 10px;
  border-radius: 12px;
  border: 1px solid @accent-color;
  background: transparent;
  color: @text-color;
  font-size: 0.75rem;
  cursor: pointer;
  transition: background 0.15s ease, color 0.15s ease;

  &:hover {
    background: fade(@accent-color, 20%);
  }

  &.active {
    background: @accent-color;
    color: #fff;
  }

  &.clear-pill {
    border-color: @danger-color;
    color: @danger-color;

    &:hover {
      background: @danger-color;
      color: #fff;
    }
  }
}

// Tag pills on creature cards
.creature-tags {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-left: auto;
  flex-wrap: wrap;
  justify-content: flex-end;
}

.creature-tag {
  padding: 1px 7px;
  border-radius: 10px;
  border: 1px solid fade(@accent-color, 40%);
  background: fade(@accent-color, 8%);
  color: @text-color;
  font-size: 0.65rem;
}
```

- [ ] **Step 2: Import tags.less in main.less**

Add at the bottom of `src/less/main.less`, after the last `@import`:

```less
@import url("./tags.less");
```

- [ ] **Step 3: Verify styles load**

Open `http://localhost:8084`. The filter bar should appear with pill-shaped buttons above the creature grid. Click a pill — it should highlight. Click Clear — pills should deactivate.

- [ ] **Step 4: Commit**

```bash
git add src/less/tags.less src/less/main.less
git commit -m "Add Less styles for tag filter bar and card tag pills"
```

---

## Task 4: Card tag display — decorated-creature-card.cljs

**Files:**
- Modify: `src/cljs/cosmere_tools/components/decorated_creature_card.cljs`

- [ ] **Step 1: Update decorated-creature-card.cljs**

Replace the entire file with:

```clojure
(ns cosmere-tools.components.decorated-creature-card
  (:require
   [cosmere-tools.components.creature-card :refer [creature-card download-json]]
   [cosmere-tools.creature-library :as creatures]
   [cosmere-tools.router :as router]
   [cosmere-tools.utils :refer [prevent-default]]))

(defn creature-tag-pills [creature]
  [:div.creature-tags
   (for [tag (:tags creature)]
     ^{:key tag}
     [:span.creature-tag tag])
   [:code.source-label
    (if (creatures/static-creature? creature)
      "Server"
      "Local")]])

(defn decorated-creature-card [creature]
  [:div.decorated-creature-card
   [:div.creature-controls
    [:button.edit-button
     {:on-click (prevent-default #(router/navigate! :edit {:id (:id creature)}))}
     "Edit"]
    [:button.download-button
     {:on-click (prevent-default #(download-json creature))}
     "Download JSON"]
    (when-not (creatures/static-creature? creature)
      [:button.delete-button
       {:on-click (prevent-default #(when (js/confirm "Are you sure you want to delete this creature?")
                                      (creatures/delete-creature! creature)))}
       "Delete"])
    [creature-tag-pills creature]]
   [creature-card creature]])
```

- [ ] **Step 2: Verify card display**

Open `http://localhost:8084`. Each card's controls bar should show tag pills on the right side (e.g. "Animal", "Book") followed by the "Server" or "Local" source label. Hangar the Traitor should show "Homebrew", "Human", "Named" + "Server".

- [ ] **Step 3: Commit**

```bash
git add src/cljs/cosmere_tools/components/decorated_creature_card.cljs
git commit -m "Show tag pills and rename source label to Server/Local on creature cards"
```

---

## Task 5: End-to-end verification

- [ ] **Step 1: Test filter AND logic**

In the browser at `http://localhost:8084`:
1. Click `Animal` — only animals should appear (Axehound, Chasmfiend, etc.)
2. Also click `Book` — list should not change (all animals are Book)
3. Also click `Homebrew` — list should be empty (no Homebrew animals)
4. Click `✕ Clear` — all creatures return

- [ ] **Step 2: Test virtual tags**

1. Click `Local` — only Hangar, Herdaz Resistance Fighter, Herdaz Resistance Lieutenant, Lobster Kremling should appear
2. Click `Server` — list should be empty (AND: nothing is both Local and Server)
3. Clear, then click `Server` alone — all static creatures appear

- [ ] **Step 3: Commit and push**

```bash
git add -A
git status  # confirm only expected files
git commit -m "Creature tags: end-to-end verification complete"
git push
```

---

## Task 6: Update generate-character skill

**Files:**
- Modify: `.claude/skills/generate-character/skill.md`

The skill currently goes: Step 5 (Present Draft) → Step 6 (Write JSON). Add a tag inference step between them.

- [ ] **Step 1: Add Step 5.5 to the skill after "Step 5: Present Draft and Iterate"**

After the Step 5 section (ending with `Ask: "Does this feel right? Anything to change before I write the file?"`), insert:

````markdown
## Step 5.5: Propose Tags

After the user approves the draft, infer tags from the creature's concept and propose them for confirmation.

**Inference rules (apply all that match):**
- Always assign exactly one of: `Book` (appears in Stormlight canon) or `Homebrew` (invented/custom). Creatures created with this skill are `Homebrew` by default unless the user says otherwise.
- `Human` — humanoid type, not Singer/Fused
- `Singer` — Singer humanoids (Warform, Nimbleform, Regals)
- `Fused` — Odium's Fused (the "One" suffix creatures)
- `Radiant` — Surgebinders / Knights Radiant
- `Animal` — non-humanoid fauna
- `Spren` — spren entities
- `Soldier` — military role (infantry, guard, mercenary)
- `Criminal` — thief, bandit, crime boss, assassin
- `Named` — a specific one-off named character (not a generic archetype)

Present inferred tags to the user before writing the file:

> **Proposed tags:** `Homebrew`, `Human`, `Named`
> Anything to add or remove?

Wait for confirmation or adjustment, then include the agreed tags in the JSON.
````

- [ ] **Step 2: Update Step 6 JSON schema to include tags**

In the Step 6 JSON schema block, add `"tags"` after `"languages"`:

```json
  "languages": "none",
  "tags": ["Homebrew"],
```

- [ ] **Step 3: Verify the skill reads correctly**

```bash
head -5 .claude/skills/generate-character/skill.md
grep -n "Step 5.5\|tags" .claude/skills/generate-character/skill.md | head -20
```

Expected: Step 5.5 heading appears, `tags` appears in the JSON schema.

- [ ] **Step 4: Commit**

```bash
git add .claude/skills/generate-character/skill.md
git commit -m "Add tag inference step to generate-character skill"
```

---

## Task 7: Push all changes

- [ ] **Step 1: Final push**

```bash
git push
```
