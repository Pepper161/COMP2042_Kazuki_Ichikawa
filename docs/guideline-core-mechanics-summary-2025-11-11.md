# Guideline Core Mechanics Summary (2025-11-11)

This document outlines the gameplay features that now ship by default with the `feature/guideline-core-mechanics` branch, plus the enhancements implemented over the course of the recent work. It is intended for teammates who are new to the project and need a single reference for the current mechanics set.

## Code Organization (2025-11 Structure)
- `com.comp2042.app` – JavaFX entry points and the start menu controller (`Main`, `StartMenuController`).
- `com.comp2042.game` – Gameplay controller, logic, score handling, and Guideline scoring primitives.
- `com.comp2042.game.events` – DTOs and interfaces that describe movement inputs and tick updates.
- `com.comp2042.board` – Matrix helpers, board implementation, previews, and the SRS rotator.
- `com.comp2042.logic.bricks` – Immutable tetromino definitions plus the 7-Bag generator.
- `com.comp2042.ui` – JavaFX panels (HUD, queue, overlays) and the `GuiController`.

The packages map directly to the Guideline mechanics described below—logic stays headless under `game/board`, while JavaFX bindings live under `ui`.

## Deterministic Seeds & Debugging
- Launch with `--seed=<long>` (e.g., `./mvnw javafx:run -Dexec.args="--seed=1234"`) to reproduce a specific 7-Bag sequence.
- When no seed is provided, a random seed is generated per run and logged once play starts plus in the Game Over overlay; reuse it via the same `--seed` flag to replay a session.
- Restarting the game from the menu or Game Over panel reinitialises the bag so deterministic runs always start from the first bag.

## Input Settings (DAS / ARR / SDF)
- The Start Menu now has a **Settings** button that opens a JavaFX dialog to tune DAS delay (ms), ARR interval (ms), and the Soft Drop multiplier.
- Key bindings for move/rotate/drop/new game can be remapped in the same dialog; values are saved under `~/.tetrisjfx/settings.properties` so they persist across launches.
- GUI movement respects these settings: DAS governs the delay before repeats, ARR controls repeat cadence, and the soft drop multiplier accelerates `DOWN` key auto-shift.

## Baseline Gameplay
- **Single-player JavaFX Tetris** targeting a 25×10 playfield with hidden spawn rows and a standard tick-driven gravity loop.
- **Matrix-based board representation**, allowing collision checks, merges, line clears, and score notifications through `GameLogic`/`SimpleBoard`.
- **HUD layer & Start/Game Over screens** handled by `GuiController`, with keyboard input (arrow keys / WASD) wired into the logic layer via `GameController`.

## Enhancements Delivered in this Branch

### 1. Guideline 7-Bag Randomizer & Next(5) Preview (Issue #2)
- Replaced the legacy randomizer with a true 7-Bag generator (`PieceGenerator.peekUpcoming`).
- Extended `SimpleBoard`/`ViewData` to publish the next five tetrominoes and rendered them via `NextQueuePanel`.
- Added reusable color palette helpers so preview tiles match the main board.

### 2. Full SRS Rotation Support (Issue #3)
- `BrickRotator` now exposes clockwise/counter-clockwise kicks with the official SRS tables (standard + I pieces).
- Input pipeline (`Board`, `GameLogic`, `GameController`, `GuiController`) was split so both rotation directions are available (Up/W/X/E for CW, Z/Q for CCW).
- Regression tests (`BrickRotatorTest`) verify both kick tables.

### 3. Lock Delay & Reset Cap (Issue #4)
- Implemented an 8→2 tick lock delay (current tuning: 2 ticks ≈ 0.8s) that starts when a piece touches down.
- Added a reset cap (currently 5) so horizontal moves/rotations can only refresh the timer a limited number of times before the piece locks.
- `GameLogic` centralises these timers, while `GuiController` simply reflects the board refresh/game over signals.

### 4. Guideline Scoring + HUD (Issue #5)
- Introduced `LineClearType`/`LineClearStats` and revamped `Score` so base scores match Guideline tables (Single 100 / Double 300 / Triple 500 / Tetris 800 / T-Spin tiers 800/1200/1600).
- Combo chaining adds +50 per additional clear; Back-to-Back bonuses add +50% to qualifying clears (Tetris/T-Spin).
- HUD (`HudPanel`) shows Score, Combo, and B2B streaks in real time; documentation updated in Step 4 report.
- Added T-Spin inference in `GameLogic` (three-corner rule + last action rotation).

### 5. Hard Drop & Ghost Piece (Issue #16)
- Space bar performs a hard drop: immediate descent, `2 × dropped cells` bonus, lock delay bypass.
- `Board.calculateDropDistance` feeds `ViewData.getGhostYPosition`, enabling a translucent ghost rendered beneath the active tetromino (only real tiles are shown, not the entire bounding box).

### 6. Line-Clear Gravity Fix (Issue #17)
- Rewrote `MatrixOperations.checkRemoving` so rows above a cleared line cascade downward properly (no “floating” blocks).
- Added `MatrixOperationsTest` to ensure the row compaction logic stays correct.

### 7. Unit Test Expansion (Issue #8 – in progress)
- Existing tests now cover: 7-Bag integrity (`PieceGeneratorTest`), SRS kicks (`BrickRotatorTest`), Guideline scoring & T-Spin base points (`ScoreTest`), matrix gravity (`MatrixOperationsTest`), plus smoke tests for `SimpleBoard`.
- Further Board/GameLogic scenarios are planned, but the current suite already exercises the core scoring and line-clear mechanics.

## Outstanding / Next Steps
- **Issue #8**: add more Board/GameLogic unit cases (e.g., lock delay behaviour, rotation edge cases, DAS/ARR once implemented).
- **Issue #17 follow-up**: confirm the new gravity logic in UI tests and close the ticket once verified.
- Remaining roadmap items (#6 DAS/ARR/SDF, #7 level progression, #11 docs, #12 BGM) are outside this branch’s scope and will likely happen in dedicated branches.

For a deeper chronological record, see `docs-local/feature/guideline-core-mechanics.md`, which contains per-step reports and verifications. This summary should give newcomers a quick overview of what the game currently supports by default.
