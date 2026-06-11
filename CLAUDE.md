# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Development (runs both ClojureScript compiler and Less watcher in parallel)
npm run dev

# ClojureScript only
npx shadow-cljs watch app

# Less only
npm run watch:less

# Production build + deploy to S3
npm run release

# One-off Less compile
npm run build:less
```

The dev server runs at `http://localhost:8084` (configured in `shadow-cljs.edn`, not 3000).

## Architecture

This is a purely client-side ClojureScript + Reagent (React) SPA for creating and managing NPC stat blocks for the Cosmere TTRPG system.

**Stack:**
- ClojureScript + Reagent (Hiccup-style React components)
- Shadow CLJS for build tooling
- Less for styles (`src/less/`, compiled to `public/css/`)
- bide for client-side routing (HTML5 pushState)
- No backend — all persistence via `localStorage`

**Routing (`router.cljs`):** Routes are defined as a bide router. `current-route` is a Reagent atom; components read `@router/current-route` directly to get the active handler and URL params. The edit page uses the same `:edit` handler for both `/create` (new creature) and `/edit/:id` (existing creature).

**Creature data flow:**
- Static/bundled creatures live in `static-creatures.cljc` and are read-only
- User-created creatures are stored in `localStorage` under keys like `creature-{id}`
- `creature-library.cljs` merges both sources into `creatures` and `creatures-by-id` defs (these are mutable vars, re-set via `set!` after saves/deletes)
- The edit page owns a local Reagent atom for the in-progress creature and passes it down via `on-change` callbacks

**Reactive calculations (`creature-editor.cljs`):** The `write` function is the central update path. After any field change it: (1) consolidates traits to remove duplicates, (2) runs all matching calculations from `creature-constants/calculations` to recompute derived stats (defenses, health, movement, etc.) — but only if the user hasn't manually overridden the target field.

**Libraries (`.cljc` files):** `trait-library.cljc`, `strike-library.cljc`, and `action-library.cljc` derive their content by scraping existing creatures, making them available as autocomplete/template sources in the editors.

**Styling:** `src/less/main.less` is the entry point and imports all other Less files. Variables are in `src/less/variables.less`. Component-specific styles live in their own files (`creature-editor.less`, `creature.less`, etc.).
