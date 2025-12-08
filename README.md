Here’s a cleaned-up, rubric-friendly `README.md` you can paste into your repo and tweak if needed:

---

# COMP2042 – Tetris Coursework (DMS-CW2025)

This repository contains my submission for the COMP2042 *Developing Maintainable Software* coursework.
The original Tetris implementation (provided by the module) has been extended and refactored with a strong focus on:

* **Maintainability** (SRP, DRY, dependency inversion, state pattern, MVC separation)
* **Gameplay extensions** (new modes, hard drop, hold, previews, ghost piece, adaptive difficulty)
* **Testability** (JUnit 5 test scaffold, decoupled model, deterministic helpers)

---

## 1.0 My Github

**Name:** Arianna binti Ainurizam

**Student ID:** 20619015
> **Repository:** [https://github.com/ariannanrzm/DMS-CW2025/tree/Addition](https://github.com/ariannanrzm/DMS-CW2025/tree/Addition)

All changes described in this document refer to the `master` branch.

---

## 2. Compilation & Run Instructions

### 2.1 Prerequisites

* **JDK:** 23
* **Build Tool:** Maven (3.8+ recommended)
* **JavaFX:** Managed via `javafx-maven-plugin` (no manual module-path setup required if using Maven).

### 2.2 Clone the Repository

```bash
git clone https://github.com/ariannanrzm/DMS-CW2025.git
cd DMS-CW2025
git checkout Addition
```

### 2.3 Run via Maven (Recommended)

From the project root (where `pom.xml` is located):

```bash
mvn clean javafx:run
```

This uses the configured `javafx-maven-plugin` with the main class:

* `com.comp2042.Main`

### 2.4 Running from IntelliJ IDEA

1. **Open project**

    * `File → Open...` → Select the `pom.xml` in the repo root.
    * IntelliJ will import the Maven project automatically.

2. **Project SDK**

    * Set the Project SDK to **JDK 23** (`File → Project Structure → Project`).

3. **Run configuration (Maven)**

    * Open the *Maven* tool window.
    * Under `CW2025 > Plugins > javafx`, run `javafx:run`.
    * Or create a run configuration:

        * `Run → Edit Configurations... → + → Maven`
        * Command: `javafx:run`
        * Working directory: project root.

4. **Tests** (optional)

    * Run all tests with:

      ```bash
      mvn test
      ```

---

## 3. Implemented and Working Properly

### 3.1 Core Gameplay Features

#### 3.1.1 Hard Drop (Spacebar)

* **What:** Instantly drops the active brick to the lowest valid position and locks it.
* **Where:**

    * `com.comp2042.game.controller.GameController`
    * `com.comp2042.game.board.Board` / `SimpleBoard` (`hardDropBrick`)
    * `com.comp2042.game.events.EventType` (added `HARD_DROP`)
    * `com.comp2042.ui.input.InputHandler` (maps `SPACE` → hard drop)
* **Design / Rationale:**

    * Uses the same “floor” calculation as the ghost piece to ensure **exact landing**.
    * Awards score based on **rows dropped**, delegated to `Score`/`GameConfig` to keep scoring rules in one place.
    * Integrated with the state pattern: handled in `PlayingState`, ignored in paused / game-over states.

#### 3.1.2 Ghost Brick (Shadow Piece)

* **What:** Semi-transparent “shadow” that shows where the current brick will land.
* **Where:**

    * `SimpleBoard` – `calculateGhostY()` and ghost data in `getViewData()`
    * `com.comp2042.game.board.ViewData` – added `ghostYPosition`
    * `com.comp2042.ui.GuiController` – secondary `ghostPanel` rendering
* **Design / Rationale:**

    * Ghost position is calculated purely in the **model** (board), then passed to the view via `ViewData` DTO.
    * UI only reads data and draws both current brick and ghost brick (MVC separation).
    * Layout logic updated to remove grid gaps and align ghost exactly with the board.

#### 3.1.3 Hold Piece (C Key)

* **What:** Allows the player to store one brick and swap it later.
* **Where:**

    * `Board` / `SimpleBoard` – `holdBrick()` logic & `heldBrick`/`canHold` fields
    * `ViewData` – `heldBrickData` field
    * `GuiController` – `refreshHeldBrick(...)`, `holdBrickContainer`
    * `InputHandler` – maps `C` → hold event
    * `GameState` / `PlayingState` – `handleHoldEvent(...)`
* **Rules:**

    * First hold stores the current piece and spawns a new one.
    * After a swap, `canHold` is set to `false` until the next brick spawns (prevents infinite swapping within one turn).

#### 3.1.4 Next Bricks Preview (3-Item Queue)

* **What:** Shows the next **three** upcoming bricks.
* **Where:**

    * `BrickGenerator` – `getNextBricks(int count)`
    * `RandomBrickGenerator` – internal `Deque` buffer to support peeking
    * `SimpleBoard` – `NEXT_BRICK_COUNT` constant & population of `ViewData.nextBricks`
    * `ViewData` – replaced single `nextBrickData` with `List<int[][]> nextBricks`
    * `GuiController` – `refreshNextBricks(List<int[][]>)`
* **Design / Rationale:**

    * Implements a standard Tetris-style preview queue.
    * Uses **dependency inversion** (board depends on `BrickGenerator` interface, not concrete randomness) for testability.

---

### 3.2 Difficulty, Modes & Progression

#### 3.2.1 Adventure Mode (Levels 1–6)

* **Where:**

    * `com.comp2042.game.logic.LevelManager`
    * `GameController` (wires LevelManager, updates drop speeds)
    * `GuiController` (binds level label, resets Timeline speed)
* **Behaviour:**

| Level | Speed (drop interval) | Special Mechanics                  |
| ----- | --------------------- | ---------------------------------- |
| 1–2   | Normal                | Standard Tetris behaviour.         |
| 3     | Faster                | Garbage rows every ~15 seconds.    |
| 4     | Faster                | Garbage rows every ~10 seconds.    |
| 5     | Turbo                 | **Chaos Mode** (see below).        |
| 6     | Extreme               | Max speed; victory if you survive. |

* **Garbage Rows:**

    * `Board.addGarbageRow()` in `SimpleBoard`
    * Bottom row filled with random blocks (1 empty gap), board above shifts up.
    * Triggered by LevelManager based on level and lines cleared / elapsed time.

#### 3.2.2 Chaos Mode (Level 5)

* **What:**

    * Strong speed increase.
    * Garbage interval shortened further.
    * **Reversible controls**: Left/Right can randomly invert.
* **Where:**

    * `LevelManager` – logic to detect Level 5.
    * `GameController` – `controlsReversed` flag.
    * `InputHandler` – checks Chaos status before mapping key → MoveEvent.
    * `GuiController` – `showChaosNotification()` using center overlay.
* **Rationale:**

    * Encapsulates difficulty rules in a dedicated component (LevelManager) and keeps input mapping separate from game logic.

#### 3.2.3 Zen Mode

* **What:** A calmer mode without aggressive difficulty spikes; ideal for practice.
* **Where:**

    * `GameMode` enum (e.g. `ZEN`, `ADVENTURE`)
    * `MainMenuController` – menu selection
    * `GameController` – mode-dependent configuration (speed, level rules, BGM choice)

---

### 3.3 Architecture & Maintainability

#### 3.3.1 MVC Separation & State Pattern

* **GameController (Model/Controller layer)**

    * No longer handles JavaFX events directly.
    * Delegates UI updates via `GuiController` methods:
      `refreshBrick`, `refreshGameBackground`, `bindScore`, `bindLines`, `bindLevel`, `updateTimer`, etc.
    * Input is delivered via `InputHandler` as semantic `MoveEvent`s.

* **GuiController (View layer)**

    * Focuses purely on drawing: bricks, background, ghost, hold, next queue, notifications, overlays.
    * Exposes **public** refresh/binding methods so the GameController can request redraws without touching JavaFX internals.

* **Game States (State Design Pattern)**

    * `com.comp2042.game.states.GameState` (interface)
    * `PlayingState`, `PausedState`, `GameOverState`
    * GameController holds `currentState` and delegates:

        * `handleDownEvent`, `handleLeftEvent`, `handleRightEvent`, `handleRotateEvent`, `handleHardDropEvent`, `handleHoldEvent`
    * Ensures:

        * Paused state ignores movement but keeps display.
        * Game-over state truly freezes the board and input (no drops after game over).
        * New game resets back to PlayingState.

#### 3.3.2 Configuration & Constants (GameConfig)

* **Where:** `com.comp2042.game.config.GameConfig` (Singleton)
* **Responsibilities:**

    * Central source of truth for:

        * Board width/height.
        * Brick size, arc radius, top offset.
        * Drop interval, speed curve.
        * Scoring constants (single/double/triple/tetris, soft drop, hard drop).
    * Used by:

        * `SimpleBoard` (dimensions, spawn position).
        * `GuiController` (layout and animation timings).
        * `Score` (scoring values).
* **Rationale:** No duplicated “magic numbers” in controller/view; changing one configuration propagates everywhere.

#### 3.3.3 Score & Lines Tracking

* **Where:** `com.comp2042.game.board.Score`
* **Key Changes:**

    * Encapsulated all scoring rules:

        * `addSoftDrop()`, `addHardDrop(int rows)`, `addLinesCleared(int linesRemoved)`.
    * Standardised scoring:

        * Single: 100, Double: 300, Triple: 500, Tetris: 800.
    * Added **combo system**:

        * `comboCount` increments on consecutive clears and gives bonus `50 * comboCount`.
        * `resetCombo()` when a move clears zero lines.
    * Added **lines property** (`IntegerProperty`) and `linesProperty()` for UI binding.
    * `reset()` resets both `score` and `lines`.

#### 3.3.4 Board & Matrix Refactor

* **SimpleBoard**

    * Extracted common movement logic into `tryMove(dx, dy)` and `isMoveValid(...)`.
    * Introduced `GamePoint` record for storing offsets (replacing `java.awt.Point`).
    * Delegated all scoring to `Score` (no more embedded score formulas).
    * Uses injected `BrickGenerator` for dependency inversion.

* **MatrixOperations / ArrayOperations**

    * `MatrixOperations` now focused on **Tetris-specific** logic:

        * Collision detection, merging, row clearing.
        * Corrected row/column indexing (`matrix[row][col]`).
        * Rewrote boundary checks for clarity and correctness.
        * Implemented a standard Tetris row-clearing algorithm (collect non-full rows, add empty rows on top).
    * `ArrayOperations`:

        * Extracted generic array operations (copying, deep copying lists).
        * Used by bricks and board to avoid duplicating low-level array code.

---

### 3.4 UI / UX & Feedback

* **Notifications & Overlays**

    * `NotificationPanel` refactored to use constants and a simpler fade animation.
    * Added `showNotificationIfRowsCleared(ClearRow)`:

        * Shows “SINGLE / DOUBLE / TRIPLE / TETRIS”.
        * Uses combo count to show combo messages.
        * Separate **side** and **center** overlays (e.g. “CHAOS MODE”, “LEVEL UP”).

* **Game Over & Victory Screens**

    * `GameOverController` + `GameOver.fxml`:

        * Full-screen end state with Retry / Main Menu / Quit.
        * Re-used for both **game over** and **victory** (different header styling).
    * `GuiController.gameOver()` and `gameWon()`:

        * Stop timers, update high score or best time, switch scenes appropriately.

* **Timer & High Score**

    * Stopwatch Timeline for elapsed time.
    * `HighScoreManager` using `java.util.prefs.Preferences` to store:

        * Highest score.
        * Best time (lowest number of seconds).
    * `GuiController.updateTimer(String)` updates a timer label.

* **Performance & Visual Polish**

    * Switched from `Rectangle` shapes to `StackPane` cells with backgrounds + borders.
    * Introduced paint caching to avoid constantly creating new Color/Gradient objects.
    * Fixed layout offsets (grid gaps, off-by-one pixels) so bricks and ghost align perfectly.
    * Hard drop “thump” effect rendered when a brick is slammed down.

* **Audio**

    * `SoundManager` (SFX) and `MusicManager` (BGM) as Singletons.
    * Main menu starts looping menu track.
    * In-game uses different background music for Zen vs Adventure.
    * Sound playback centralised to avoid multiple MediaPlayers and leaks.

---

### 3.5 Testing

* **JUnit 5 Setup**

    * Added JUnit 5 dependencies in `pom.xml`.
    * Tests run with `mvn test`.

* **New Test Classes (examples)**

    * `GameControllerTest`

        * Verifies score increment on soft drop.
        * Checks brick spawning and game-over conditions.
    * `ArrayOperationsTest`

        * Ensures deep copy logic works (modifying copy does not affect original).
    * `MatrixOperationsTest`

        * Confirms row-clearing and collision logic after refactors.
    * `StatePatternTest`

        * Checks state transitions: Playing ↔ Paused, Playing → GameOver, New Game reset.
    * **MockGuiController**

        * Test-only stand-in for `GuiController` that avoids initializing JavaFX.
        * Ensures tests can run headless without FXML or a Stage.

All of the above have been **implemented and manually tested** as part of the coursework work flow.

---

## 4. Implemented but Not Working Properly

At the time of submission, all major features listed in **Section 3** are functioning as expected during manual testing.

If the marker encounters any issues, they are most likely to be in:

* **Edge timing conditions** (e.g. pressing movement keys exactly as a level changes or a game state switches).
* **Complex combinations** of overlays (multiple notifications, chaos mode, garbage rows, and ghost piece all active simultaneously).

These are not known reproducible bugs, but they are the most sensitive parts of the system due to animation and timing dependencies.

---

## 5. Features Not Implemented

The focus of this coursework is **maintenance and extension** of the provided Tetris game, not creating a completely new engine.
The following features were **intentionally not implemented** due to scope and time constraints:

* Customisable key bindings.
* Multiple visual themes / skins.
* Online leaderboards or cloud-synced scores.
* AI / autoplay or hint system.
* Replay / save-and-load of games.

I prioritised:

* Refactoring the existing code to be testable and maintainable.
* Implementing a robust Adventure mode, Zen mode, and modern Tetris quality-of-life features (hold, ghost, previews, hard drop).

---

## 6. New Java Classes (Summary)

Below is a non-exhaustive list of **new classes** introduced in this coursework, with their purpose and location.

### Core Logic & Architecture

* **`GameConfig`** – `com.comp2042.game.config`

    * Singleton configuration for board size, visual constants, timings, and scoring.

* **`LevelManager`** – `com.comp2042.game.logic`

    * Encapsulates level progression, speed curve, and garbage timing logic.

* **`GameState` (interface)** – `com.comp2042.game.states`

    * State pattern interface for handling input in different game states.

* **`PlayingState` / `PausedState` / `GameOverState`** – `com.comp2042.game.states`

    * Concrete states implementing behaviour for active gameplay, pause, and terminal game over.

* **`GamePoint` (record)** – model layer

    * Small value object to store (x, y) offsets purely in the game domain, replacing `java.awt.Point`.

### Bricks & Generators

* **`AbstractBrick`** – `com.comp2042.game.bricks`

    * Superclass holding `brickMatrix` and `getShapeMatrix()` to remove duplication from I/J/L/O/S/T/Z bricks.

* **`BrickGenerator` (interface)** – `com.comp2042.game.bricks`

    * Abstraction for generating bricks and peeking at upcoming pieces.

* **`RandomBrickGenerator`** – `com.comp2042.game.bricks`

    * Default pseudo-random generator with a buffer to support 3-item preview.

### UI & Controllers

* **`MainMenuController`** – `com.comp2042.ui`

    * Handles the main menu, mode selection (Zen vs Adventure), and starting games.

* **`GameOverController`** – `com.comp2042.ui`

    * Controls the full-screen game over / victory view; handles Retry/Main Menu/Quit buttons.

* **`InputHandler`** – `com.comp2042.ui.input`

    * Maps raw keyboard input (`KeyEvent`s) to semantic `MoveEvent`s (including hold and hard drop) and forwards them to `GameController`.

### Utilities & Persistence

* **`ArrayOperations`** – `com.comp2042.util`

    * Generic array/deep-copy utilities extracted from `MatrixOperations`.

* **`SoundManager`** – `com.comp2042.util`

    * Singleton for short SFX; caches audio resources and prevents duplicates.

* **`MusicManager`** – `com.comp2042.util`

    * Singleton for background music; ensures only one track plays at a time.

* **`HighScoreManager`** – `com.comp2042.util`

    * Stores and retrieves high scores and best times using `Preferences`.

### Testing Support

* **`MockGuiController`** – test source (`src/test/java`)

    * Lightweight replacement for `GuiController` in unit tests; avoids JavaFX initialisation.

* **JUnit Test Classes** – test source (`src/test/java`)

    * `GameControllerTest`, `ArrayOperationsTest`, `MatrixOperationsTest`, `StatePatternTest`
    * Provide regression safety net for movement, scoring, state transitions, and matrix operations.

---

## 7. Modified Java Classes (Key Changes)

Below is a high-level summary of the **most important modified classes**. Many other files were touched (e.g. package restructuring), but these represent the main maintenance work.

### `GameController.java` – `com.comp2042.game.controller`

* Refactored to:

    * Act as the **context** for the State pattern.
    * Delegate all input handling to `currentState`.
    * Delegate view updates to `GuiController` (no direct UI code outside these calls).
* Removed:

    * Boolean flags `isPause`, `isGameOver`; replaced with states.
* Added:

    * `togglePause()`, `setState()`.
    * Hard drop and hold handling methods (`onHardDropEvent`, `onHoldEvent`).
    * LevelManager integration (speed changes, notifications).
    * Timer management and high score/best-time checks.
* Rationale:

    * Eliminate long methods and mixed responsibilities.
    * Align with **SRP**, **Open/Closed**, and **MVC**.

### `SimpleBoard.java` & `Board.java` – `com.comp2042.game.board`

* Movement and collision:

    * Extracted `tryMove(dx, dy)` and `isMoveValid(...)`.
    * Centralised collision checks for move and rotation.
* Hard drop:

    * Implemented `hardDropBrick()` returning `DownData` (includes lines cleared, game over state).
* Hold & garbage:

    * Added `holdBrick()` logic and `addGarbageRow()`.
* Scoring:

    * Delegated to `Score` (no embedded patterns like `50 * lines * lines`).
* Dependency inversion:

    * Constructor now accepts `BrickGenerator` instead of instantiating `RandomBrickGenerator` directly.

### `Score.java` – `com.comp2042.game.board`

* Encapsulated:

    * Scoring methods, combo logic, and line counting.
* Linked to `GameConfig` for constants.
* Provides JavaFX properties for UI binding (`scoreProperty`, `linesProperty`).

### `MatrixOperations.java` – `com.comp2042.util`

* Corrected:

    * Row/column indexing and boundary checks.
* Simplified:

    * Row clearing algorithm to standard Tetris behaviour.
* Separated:

    * Removed scoring logic (now handled by `SimpleBoard`/`Score`).
    * Generic copy logic moved to `ArrayOperations`.

### `GuiController.java` – `com.comp2042.ui`

* Removed:

    * On-key-press logic (moved to `InputHandler`).
    * Hard-coded constants (now read from `GameConfig`).
* Added:

    * Public refresh methods for brick, board, ghost, held piece, next preview, score, lines, level, timer.
    * Notification handling (`showNotificationIfRowsCleared`, `showChaosNotification`, level-up popups).
    * Pause and game-over handling with overlays and scene transitions.
    * Geometry fixes for alignment (grid gaps, offsets).
    * Hard-drop thump animation and score popups.

### `BrickRotator.java` – `com.comp2042.game.bricks`

* Renamed methods for clarity (`setCurrentShape` → `applyRotation`).
* Added:

    * `tryRotate(...)` method using offset kicks `[0, 1, -1, 2, -2]`.
* Simplified:

    * Rotation index calculations via modular arithmetic.
* Rationale:

    * Clean separation of rotation logic and brick storage.

### `NotificationPanel.java` – `com.comp2042.ui.components`

* Replaced magic numbers with constants.
* Simplified animation to a single fade.
* Applied consistent style classes for sidebar notifications.

*(Several other classes were modified to reflect new packages, imports, and DTO fields, but the above list captures the core design and maintainability work.)*

---

## 8. Unexpected Problems & How They Were Addressed

### 8.1 Matrix Indexing & Spawn Bugs

* **Problem:** After refactoring `MatrixOperations` to row-major indexing, bricks began spawning at incorrect Y positions or clipping into the board.
* **Fix:**

    * Systematically corrected all uses of `brick[i][j]` vs `brick[j][i]`.
    * Updated the `SPAWN_Y` constant in `SimpleBoard` so pieces appear at the top of the board again.

### 8.2 Ghost Brick & Landing Artifacts

* **Problem:** After a line clear, the most recent brick (and its ghost) would visually “linger” for a frame at the bottom before disappearing.
* **Fix:**

    * Ensured the board merge and UI refresh order is correct:

        * Merge to background → clear rows → request GUI refresh for board + brick.
    * Removed duplicate calls to `mergeBrickToBackground()` and duplicate soft drop score increments.
    * Added guards in the states to avoid refreshing bricks when the game is already over.

### 8.3 Double Drop (Gravity vs Manual Down)

* **Problem:** Pressing manual down just before a gravity tick sometimes caused bricks to “skip” rows.
* **Fix:**

    * Added `resetTimeline()` in `GuiController` and called it after manual down events.
    * This restarts the gravity timer, ensuring a consistent delay before the next automatic drop.

### 8.4 JavaFX & Testing (MockGuiController)

* **Problem:** Initial unit tests threw `NullPointerException` or JavaFX initialisation errors because no actual UI was loaded.
* **Fix:**

    * Introduced `MockGuiController` to satisfy the controller’s dependencies without touching FXML.
    * Overrode methods like `setGameController()` to skip input wiring during tests.

### 8.5 GitHub / Maven “Untrustable Server” Warnings

* **Problem:** At one point Maven/GitHub reported “untrusted server” issues when downloading dependencies.
* **Fix:**

    * Verified `pom.xml` repositories were standard Maven Central / OpenJFX.
    * Resolved by clearing local Maven cache and re-running the build; the issue was environmental, not project-related.

---

If you’d like, I can now help you trim or expand specific sections (for example, shorten the architecture part if your marker’s attention span is limited, or emphasise particular design patterns you want to show off).
