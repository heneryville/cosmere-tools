# Cosmere Tools

A web app for creating and managing NPC stat blocks for Cosmere TTRPG campaigns. Build creatures with auto-calculated stats, traits, actions, and strikes, then export them as JSON.

## Setup

```bash
npm install
npm run dev
```

Open `http://localhost:8084`.

## Scripts

| Command | Description |
|---------|-------------|
| `npm run dev` | Start ClojureScript + Less watchers in parallel |
| `npm run build` | Production build |
| `npm run release` | Build and deploy to S3 |
| `npm run watch:less` | Less watcher only |
| `npm run build:less` | One-off Less compile |
| `npm run clean` | Remove compiled output |

## Stack

- **ClojureScript + Reagent** — React components written in Hiccup syntax
- **Shadow CLJS** — build tooling and hot reload
- **Less** — styles compiled from `src/less/`
- **bide** — client-side HTML5 pushState routing
- No backend — creature data stored in `localStorage`

## Creature Data

Static/built-in creatures are defined in `src/cljs/cosmere_tools/static_creatures.cljc`. User-created creatures are persisted to `localStorage` under keys like `creature-{id}`.

The editor auto-calculates derived stats (defenses, health, movement, senses) whenever their source attributes change, but respects manual overrides.
