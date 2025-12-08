 # **COMP2042 – Tetris Coursework (DMS-CW2025)**

A fully refactored and extended Tetris implementation focusing on **maintainability**, **testability**, and **modern gameplay enhancements**, built with Java 23, JavaFX 21, and Maven.

---
#  **1.0 My GitHub**

**Name:** Arianna binti Ainurizam

**Student ID:** 20619015

**Coursework Repository:**

https://github.com/ariannanrzm/DMS-CW2025

All documentation below refers to the **master** branch.


---

#  **2.0 Setup and Compilation Instructions**

This section is intentionally detailed because installation issues are common in JavaFX/Maven projects.

---

 ### **2.1 Required Software**

| Dependency | Version | Purpose |
| --- | --- | --- |
| **Java Development Kit (JDK)** | **23** | Required to compile and run the project |
| **Maven** | 3.8+ | Build, dependency management, JavaFX plugin execution |
| **JavaFX** | 21+ | GUI framework used by the Tetris game |
| **IntelliJ IDEA (Recommended)** | 2023+ | IDE with built-in Maven + JavaFX support |

---

 ### **2.2 Install Java 23**

Check if Java is installed:

```bash
java -version

```

If version is *not* 23, install from:

- [https://adoptium.net](https://adoptium.net/)

  or

- Oracle JDK: https://www.oracle.com/java/technologies/downloads/

Verify installation:

```bash
javac -version

```

---

 ### **2.3 Install Maven**

Check Maven:

```bash
mvn -v

```

If not found (e.g. `zsh: command not found: mvn`), install via Homebrew:

```bash
brew install maven

```

Verify:

```bash
mvn -v

```

---

### **2.4 Clone the Repository**

```bash
git clone https://github.com/ariannanrzm/DMS-CW2025.git
cd DMS-CW2025

```

Ensure you are on the correct branch:

```bash
git checkout master

```

---

### **2.5 Running with Maven (Most Reliable Method)**

The `pom.xml` includes the `javafx-maven-plugin`, so you **do not** need to configure module paths manually.

Run the game:

```bash
mvn clean javafx:run

```

This automatically:

1. Downloads JavaFX dependencies
2. Builds the project
3. Launches the application using `com.comp2042.Main`

---

 ### **2.6 Running with IntelliJ IDEA**

### **Step 1 — Open the Project**

- `File → Open…` → select the `pom.xml`
- IntelliJ will import Maven automatically.

### **Step 2 — Configure Project SDK**

- `File → Project Structure → Project`
- Set **SDK = JDK 23**

### **Step 3 — Use Maven Run Configuration**

Open **Maven Tool Window** → expand:

```
CW2025 → Plugins → javafx → javafx:run

```

Or create manual configuration:

```
Run → Edit Configurations → + → Maven
Command: javafx:run
Working Directory: <project root>

```

### **Step 4 — Run Tests (Optional)**

```bash
mvn test

```

---

 ### **2.7 Common Maven Build Commands**

| Command | Purpose |
| --- | --- |
| `mvn clean` | Remove previous build files |
| `mvn compile` | Compile Java source |
| `mvn test` | Run JUnit 5 tests |
| `mvn package` | Create runnable JAR (if configured) |
| `mvn javafx:run` | Run JavaFX application |
| `mvn javadoc:javadoc` | Generate Javadoc into `/target/site/apidocs` |

---

# **3.0 Features**

 ### **3.1 Implemented and Working Properly**

These features are **fully functional**, manually tested, and integrated into Controller + Board + UI layers.

### 3.1.1 Hard Drop (Spacebar)

- **What:** Instantly drops the active brick to the lowest valid position and locks it.
- **Where:**
    - `com.comp2042.game.controller.GameController`
    - `com.comp2042.game.board.Board` / `SimpleBoard` (`hardDropBrick`)
    - `com.comp2042.game.events.EventType` (added `HARD_DROP`)
    - `com.comp2042.ui.input.InputHandler` (maps `SPACE` → hard drop)
- **Design / Rationale:**
    - Uses the same “floor” calculation as the ghost piece to ensure **exact landing**.
    - Awards score based on **rows dropped**, delegated to `Score`/`GameConfig` to keep scoring rules in one place.
    - Integrated with the state pattern: handled in `PlayingState`, ignored in paused / game-over states.

### 3.1.2 Ghost Brick (Shadow Piece)

- **What:** Semi-transparent “shadow” that shows where the current brick will land.
- **Where:**
    - `SimpleBoard` – `calculateGhostY()` and ghost data in `getViewData()`
    - `com.comp2042.game.board.ViewData` – added `ghostYPosition`
    - `com.comp2042.ui.GuiController` – secondary `ghostPanel` rendering
- **Design / Rationale:**
    - Ghost position is calculated purely in the **model** (board), then passed to the view via `ViewData` DTO.
    - UI only reads data and draws both current brick and ghost brick (MVC separation).
    - Layout logic updated to remove grid gaps and align ghost exactly with the board.

### 3.1.3 Hold Piece (C Key)

- **What:** Allows the player to store one brick and swap it later.
- **Where:**
    - `Board` / `SimpleBoard` – `holdBrick()` logic & `heldBrick`/`canHold` fields
    - `ViewData` – `heldBrickData` field
    - `GuiController` – `refreshHeldBrick(...)`, `holdBrickContainer`
    - `InputHandler` – maps `C` → hold event
    - `GameState` / `PlayingState` – `handleHoldEvent(...)`
- **Rules:**
    - First hold stores the current piece and spawns a new one.
    - After a swap, `canHold` is set to `false` until the next brick spawns (prevents infinite swapping within one turn).

### 3.1.4 Next Bricks Preview (3-Item Queue)

- **What:** Shows the next **three** upcoming bricks.
- **Where:**
    - `BrickGenerator` – `getNextBricks(int count)`
    - `RandomBrickGenerator` – internal `Deque` buffer to support peeking
    - `SimpleBoard` – `NEXT_BRICK_COUNT` constant & population of `ViewData.nextBricks`
    - `ViewData` – replaced single `nextBrickData` with `List<int[][]> nextBricks`
    - `GuiController` – `refreshNextBricks(List<int[][]>)`
- **Design / Rationale:**
    - Implements a standard Tetris-style preview queue.
    - Uses **dependency inversion** (board depends on `BrickGenerator` interface, not concrete randomness) for testability.

### 3.1.5 Adventure Mode (Levels 1–6)

- **What:** The primary, progression-based game mode. It introduces escalating difficulty through increasing speed, garbage mechanics, and special chaos rules. The main objective is to clear levels until the victory condition (reaching above Level 6) is met.
- **Where:**
    - `com.comp2042.game.config.GameMode` (defines `ADVENTURE`).
    - `com.comp2042.game.controller.GameController` (manages level progression and win condition via `handleLevelChange`).
    - `com.comp2042.game.logic.LevelManager` (calculates level ups and determines game speed).
- **Design / Rationale:**
    - Difficulty is controlled by `LevelManager`, where clearing 5 lines triggers a level increment.
    - Game speed gradually increases per level, referenced from the `SPEED_CURVE` array in `LevelManager`.
    - The Garbage Generation mechanic (adding garbage rows) is exclusive to this mode and is timed to activate from Level 3 onwards.

### 3.1.6 Chaos Mode (Level 5)

- **What:** A dynamic mechanic that is introduced on Level 5 and persists on Level 6 of **Adventure Mode**. It involves periodically reversing the player's lateral controls (left/right) to increase difficulty.
- **Where:**
    - `com.comp2042.game.controller.GameController` (`handleChaosMode`, `applyLevelMechanics`, `isControlsReversed`).
- **Design / Rationale:**
    - A `chaosTimer` within the main game stopwatch loop handles the periodic reversal.
    - The state is toggled on a 13-second cycle: controls are reversed for the first 3 seconds, then normal for the remaining 10.
    - A separate logic branch in `applyLevelMechanics` ensures controls are immediately reversed when entering Levels 5 and 6, regardless of the timer, and displays a "RANDOM CONTROLS" notification.


### 3.1.7 Zen Mode

- **What:** A casual game mode focused purely on clearing lines without the increasing difficulty or adversarial elements of Adventure Mode.
- **Where:**
    - `com.comp2042.game.config.GameMode` (defines `ZEN`).
    - `com.comp2042.game.controller.GameController` (`notifyLinesCleared`, `setupStopwatch`).
- **Design / Rationale:**
    - Level progression is disabled: the `GameController` checks for `GameMode.ZEN` and returns immediately from `notifyLinesCleared` if true.
    - Garbage generation is skipped by the `GameController`'s main time loop if the `gameMode` is not `ADVENTURE`.
    - The user plays at the base speed without any mechanical interruptions.


### 3.1.8 Notification System (Combo, Tetris, Level Up)

- **What:** Provides visible feedback for key game events, such as achieving a new level or introducing level-specific mechanics.
- **Where:**
    - `com.comp2042.ui.GuiController` (interface for displaying messages - inferred from use).
    - `com.comp2042.game.controller.GameController` (`handleLevelChange`, `applyLevelMechanics`).
- **Design / Rationale:**
    - The `GuiController` methods, such as `showLevelUpNotification(int level)` and `showChaosNotification(String message)`, are called directly from the `GameController`.
    - Notifications are triggered when the `LevelManager` increases the level.
    - Custom messages are used to warn the player when garbage mechanics are about to begin (Levels 3 and 4) and when entering Chaos Mode (Levels 5 and 6).


### 3.1.9 Adaptive Scoring System + Combo Streaks

- **What:** A comprehensive scoring model that awards base points for soft/hard drops and large bonuses for simultaneous line clears (Singles, Doubles, Triples, Tetris) and consecutive line clears (Combos).
- **Where:**
    - `com.comp2042.game.config.GameConfig` (central storage for all score constants).
    - `com.comp2042.game.controller.GameController` (binds the score property to the UI).
- **Design / Rationale:**
    - Score values are set as constants in `GameConfig`: 100 for Single, 800 for Tetris, and a 50 point `SCORE_COMBO_BONUS`.
    - Bonus points are awarded for piece drops: 1 for `SOFT_DROP_SCORE` and 2 for `HARD_DROP_SCORE`.
    - The `GameController` dynamically links the game board's score (`board.getScore().scoreProperty()`) and line count (`linesProperty()`) to the UI, ensuring the display is always up-to-date.


### 3.1.10 Stopwatch Timer + High Score / Best Time Saving

- **What:** A real-time timer tracks the duration of the current game session. On an Adventure Mode victory, the total time is saved as the "Best Time".
- **Where:**
    - `com.comp2042.game.controller.GameController` (`setupStopwatch`, `updateTimeLabel`, `handleVictory`).
    - `com.comp2042.game.logic.HighScoreManager` (used for persistence of high score and fastest time).
- **Design / Rationale:**
    - Implemented using a JavaFX `Timeline` running a `KeyFrame` that executes every second to increment `secondsElapsed`.
    - The `updateTimeLabel` method handles formatting the raw seconds into the `MM:SS` display format.
    - The timer automatically pauses when the game state is toggled to `PausedState` and stops permanently upon entering `GameOverState`.
    - The `handleVictory` method is responsible for calling `HighScoreManager.tryUpdateFastestTime(secondsElapsed)` when the player wins.


## **3.2 Implemented but Not Working Properly**

Currently, no consistently reproducible bugs remain. However, the following areas may show **inconsistencies depending on timing**:

- Timing-sensitive interactions between:
    - gravity tick
    - notifications
    - chaos-mode reversing
    - garbage row insertion
    - manual fast inputs

These are *edge-case* behaviours and not core functional issues.

## 3.3 Features Not Implemented

The following features were intentionally excluded from the final scope of this project. The priority was placed on establishing a robust, modular core game loop and state management (`GameController`, `PlayingState`, etc.) to maximize **maintainability** and **testability** over advanced user-facing features.

---

### 3.3.1 Expanded Game Modes

- **Feature:** **Classic Marathon Mode** and **Powerups/Special Bricks**.
- **Rationale:** The game currently implements **Adventure Mode** (progression-based, escalating difficulty with forced garbage) and **Zen Mode** (non-competitive, no progression). A "Classic" mode, typically defined by a specific speed-curve/line goal without forced garbage or chaos mechanics, was excluded to maintain focus on the implemented modes. Similarly, powerups were excluded to simplify the core game logic and avoid extensive refactoring of the existing brick and board architecture.

---

### 3.3.2 User Settings and Customization

- **Feature:** **Key-binding Customization** and **User-Adjustable Audio Settings** (e.g., mute music/SFX volume sliders).
- **Rationale:** To maintain scope, controls are hard-coded via the `InputHandler` (inferred). While sound management components exist (e.g., `MusicManager`, `SoundManager` are in the file list), user-exposed controls for muting or setting music and sound effect volumes are not implemented. This allowed developers to focus on the core game loop and logic validation.

---

### 3.3.3 Advanced Scoring and Persistence

- **Feature:** **Online Leaderboard** or a comprehensive High Score UI.
- **Rationale:** While the system includes a `HighScoreManager` capable of tracking a local best time and high score, a fully-featured, visual leaderboard interface or an *online* component was left out. This decision streamlined the data layer, prioritizing simple persistence over complex networking or data presentation features.

---

### 3.3.4 Visuals and Mechanics

- **Feature:** **Skin/Theme Switching**, **Ghost Animations/Particle Effects**, **AI/Hint Systems**, and "Infinite Bag" Randomization.
- **Rationale:**
    - **Visuals:** Advanced visual flair (skins, particle effects) were deemed low priority compared to functional requirements.
    - **Mechanics:** The game uses a simple, pseudo-random brick generator (inferred from `RandomBrickGenerator` usage in `GameController`). Implementing a statistically "fairer" system like the 7-bag ("Infinite Bag") randomization model, or complex AI/hint systems, would require significant additions to the `BrickGenerator` interface and increase complexity, conflicting with the goal of **maintainability** and **testability**.

# 3.4 Gameplay Modes and Progression

The game offers two distinct modes: **Adventure Mode** and **Zen Mode** designed to cater to players seeking escalating challenges and those preferring a calm, practice-focused experience. Game progression, speed, and difficulty are centrally managed by the `GameController` and `LevelManager`.

---

### 3.4.1 Adventure Mode (Levels 1–6)

This is the core competitive experience, where the difficulty progresses across six levels, requiring the player to achieve a score goal and survive escalating speeds and aggressive mechanics. The ultimate goal is to reach Level 7 for a victory condition.

- **Mechanics & Flow:**
    - Progression is measured by clearing **5 lines per level**, which triggers a level up (`LINES_PER_LEVEL` in `com.comp2042.game.logic.LevelManager`).
    - The **drop speed** (brick drop interval) is dynamically pulled from the `SPEED_CURVE` array in `LevelManager`, increasing dramatically at each level boundary.
    - The `GameController` is responsible for applying the new drop speed by restarting the `Timeline` via `GuiController.updateGameSpeed()`.

| **Level** | **Speed (Drop Interval)** | **Special Mechanics** |
| --- | --- | --- |
| **1–2** | **Normal** (`900ms` → `800ms`) | Standard Tetris gameplay. |
| **3** | **Faster** (`700ms`) | **Garbage Rows** activated (`15s` interval). |
| **4** | **Faster** (`600ms`) | **Garbage Rows** accelerate (`10s` interval). |
| **5** | **Turbo** (`500ms`) | **Chaos Mode** activated (random controls), **Garbage** accelerates (`7s` interval). |
| **6** | **Extreme** (`400ms`) | **Max speed** and most intense challenge; successful clearance triggers the win state. |
- **Garbage Rows Implementation:**
    - Garbage insertion is managed by a dedicated timer loop (`garbageTimer`) in the `GameController`.
    - The interval for generation is level-dependent, accelerating from 15 seconds (Level 3) to 7 seconds (Levels 5 & 6).
    - The actual addition of the row is performed by calling `Board.addGarbageRow()` on the `SimpleBoard`, which shifts all existing pieces up and generates a new, bottom row with one random empty cell (`1 empty gap`).

---

### 3.4.2 Chaos Mode (Level 5 & 6)

Chaos Mode introduces unpredictable mechanics designed to test advanced player control and adaptation.

- **Core Mechanic:** **Reversible Controls**. The player's Left and Right movement inputs are periodically inverted.
- **Implementation:**
    - A `chaosTimer` in the `GameController` manages the period of reversal.
    - Controls are toggled based on a 13-second cycle, with a shorter window of reversal.
    - The `InputHandler` (inferred from the changes) checks the `controlsReversed` flag on the `GameController` immediately before mapping a key press to a `MoveEvent`, ensuring the game logic is separate from the input inversion.
    - Visual feedback is provided via `GuiController.showChaosNotification()` to alert the player when the mode is active.

---

### 3.4.3 Zen Mode

Zen Mode offers a relaxed environment focused purely on scoring and line clearing, making it ideal for practice or casual play.

- **Purpose:** A calmer mode without the aggressive difficulty spikes of Adventure Mode.
- **Differentiation:**
    - **No Progression:** The `GameController` explicitly ignores line clear notifications (`notifyLinesCleared`) if `GameMode.ZEN` is active, preventing level ups and speed changes.
    - **No Adversity:** Garbage row generation is disabled and automatically skipped by the game's time loop.
    - **BGM:** The `GuiController` plays a distinct BGM track for Zen Mode, further differentiating the experience.

# **4.0 Refactoring Process**

This section maps directly to the marking rubric: location → what changed → why it changed.

---

### **4.1 New Java Classes**

| Class | Location | Description |
| --- | --- | --- |
| **GameConfig** | `com.comp2042.game.config` | Singleton storing all constants (dimensions, scoring, speed, visuals). Removes magic numbers. |
| **LevelManager** | `com.comp2042.game.logic` | Handles levels, garbage timing, drop speed curve. |
| **GameState** | `com.comp2042.game.states` | Interface for State Pattern. |
| **PlayingState** | `com.comp2042.game.states` | Main gameplay logic. |
| **PausedState** | `com.comp2042.game.states` | Freezes gameplay input. |
| **GameOverState** | `com.comp2042.game.states` | Locks inputs post-game. |
| **GamePoint** | `com.comp2042.model` | Replaces `java.awt.Point`. Pure domain layer. |
| **AbstractBrick** | `com.comp2042.game.bricks` | Parent class for all 7 brick shapes → removes duplication. |
| **RandomBrickGenerator** | `com.comp2042.game.bricks` | Supports 3-preview queue with Deque buffer. |
| **InputHandler** | `com.comp2042.ui.input` | Converts KeyEvents → MoveEvents (SRP, cleaner UI). |
| **MainMenuController** | `com.comp2042.ui` | Handles mode selection and entry point. |
| **GameOverController** | `com.comp2042.ui` | Handles full-screen end menu. |
| **SoundManager** | `com.comp2042.util` | Singleton, caches SFX. |
| **MusicManager** | `com.comp2042.util` | Singleton, manages background music safely. |
| **ArrayOperations** | `com.comp2042.util` | Extracted from MatrixOperations. |
| **MockGuiController** | `src/test/java` | Test double, supports unit tests without JavaFX. |

---

## **4.2** Modified **Java Classes**

The project underwent significant refactoring to enforce the **Model-View-Controller (MVC)** architectural pattern, the **State Design Pattern**, and core SOLID principles like the **Single Responsibility Principle (SRP)** and **Dependency Inversion Principle (DIP)**.

---

### Package Structure Refactoring

The entire codebase was reorganized to group classes by their functional responsibility, significantly improving modularity, separation of concerns, and maintainability.

| **Old Package (Inferred)** | **New Package Structure (com.comp2042)** | **Purpose** |
| --- | --- | --- |
| `com.comp2042` | `com.comp2042.board/` | Core game logic and model data transfer objects. |
| `com.comp2042` | `com.comp2042.bricks/` | Brick shape definitions, generation, and rotation logic. |
| `com.comp2042` | `com.comp2042.controller/` | Central game loop and state context (`GameController`). |
| `com.comp2042` | `com.comp2042.ui/` | JavaFX view components, rendering, and input handling. |
| `com.comp2042` | `com.comp2042.events/` | Custom input and movement event data structures. |
| `com.comp2042` | `com.comp2042.util/` | General-purpose utility classes (`MatrixOperations`, `ArrayOperations`). |
| (New) | `com.comp2042.game.states/` | State implementations (`PlayingState`, `PausedState`, etc.). |
| (New) | `com.comp2042.game.logic/` | Gameplay management (`LevelManager`, `HighScoreManager`). |
| (New) | `com.comp2042.game.scoring/` | Scoring rules and state (`Score`). |

---

### 4.2.1 `Main.java`

- **Layout Expansion**: The JavaFX **Scene dimensions** were increased (e.g., to 600x600) to provide a larger game window.
- **Menu Integration**: Replaced the previous direct loading of the game view with a new **Main Menu** to enable the user to select a game mode (`ZEN` or `ADVENTURE`) before starting.

---

### 4.2.2 `GameController.java`

The `GameController` was entirely refactored from a tightly-coupled monolithic class into the central **Context** for the State Design Pattern and the primary coordinator of the MVC architecture.

| **Category** | **Changes and Rationale** |
| --- | --- |
| **Architectural** | **State Pattern Implementation:** Removed procedural boolean flags (`isPause`, `isGameOver`). Introduced `currentState` to delegate all game logic (via methods like `onDownEvent`) to dedicated `GameState` objects (`PlayingState`, `PausedState`, `GameOverState`). Added `setState()` and `togglePause()` for explicit state transitions. |
| **Decoupling (MVC)** | **View Decoupling (Input):** Removed all internal GUI initialization and **removed the class's role as a UI key listener**. It now receives abstracted game events via the `InputEventListener` interface from the `GuiController`’s input layer. |
| **Dependency Injection** | **Decoupled Brick Generation:** Modified the constructor to accept a `BrickGenerator` interface instead of directly instantiating a `RandomBrickGenerator`. This adheres to the **Dependency Inversion Principle (DIP)** and allows for non-deterministic brick generation for testing. |
| **Configuration** | **Integrated `GameConfig`:** Adopted the **Singleton pattern** by replacing hard-coded magic numbers (e.g., `BOARD_HEIGHT`, `BOARD_WIDTH`) with dynamic calls to `GameConfig.get()` for consistent dimensions. |
| **Logic Refactoring (SRP)** | **Procedural Decomposition:** Refactored the monolithic `onDownEvent()` method into five dedicated, highly cohesive helper methods (`handleBrickMovement()`, `handleLanding()`, `handleRowClear()`, `trySpawnNewBrick()`, `incrementSoftDropScore()`) to address **Long Method** and **SRP** issues. |
| **New Features** | **Hard Drop & Hold Events:** Implemented `onHardDropEvent()` and `onHoldEvent()` to delegate these new actions to the current game state. |
| **New Features** | **Level/Mode Management:** Integrated the `LevelManager` and `GameMode` field to manage difficulty progression, speed updates, and trigger mode-specific mechanics (`notifyLinesCleared()`). |
| **New Features** | **Time/Difficulty Loop:** Added a JavaFX `Timeline` to manage the **Stopwatch Timer**, linking its start/stop/pause behavior to state changes. Implemented Adventure Mode difficulty triggers: **Garbage Triggers** (Level 3+) and **Chaos Mode** (Level 5+) with control reversal logic. |
| **Game Over/Victory** | Added logic to automatically update `HighScoreManager` for both score and **Best Time** upon game over or victory (Level 6 clearance). Added logic guards to all movement handlers to block input when `isGameOver()` is true, ensuring game stability. |
| **Testing/Clean-up** | Added `public Board getBoard()` getter. Refactored scoring calls to use semantic methods (`board.getScore().addSoftDrop()`) instead of numeric constants. |

---

### 4.2.3 `GuiController.java`

The `GuiController` was refined to be a purely rendering layer, strictly decoupled from game logic. Its responsibilities are now limited to displaying data received from the `GameController` and relaying user input.

| **Category** | **Changes and Rationale** |
| --- | --- |
| **Decoupling (MVC)** | **Pure View Role:** Removed the circular dependency with `GameController`. It now uses `setGameController()` and initializes its `InputHandler` only after the controller is ready, delegating all input processing. |
| **Configuration** | **Integrated `GameConfig`:** Removed all duplicated visual constants (e.g., `BRICK_SIZE`, `DROP_INTERVAL_MS`). All visual dimensions and timings are fetched dynamically from `GameConfig.get()`. |
| **Responsiveness/Refresh** | **Immediate Refresh:** Made core rendering methods (`refreshBrick()`, `refreshGameBackground()`) **public**. This allows the `GameController` to **push screen updates immediately** after any logical state change (e.g., movement), fixing input lag and visual glitches. |
| **Movement/Gravity Fix** | **"Double Drop" Fix:** Implemented `resetTimeline()` to stop and restart the gravity timer on manual descent, synchronizing the game loop with user input. |
| **Visual Performance** | **Performance Optimizations:** Switched from `Rectangle` to `StackPane` for bricks to use hardware-accelerated rendering, which **eliminated rendering lag**. Implemented **Color Caching** to reduce garbage collection overhead and memory use. |
| **Visual Features** | **Ghost Piece Rendering:** Added a secondary `ghostPanel` and logic in `refreshBrick()` to calculate and draw the semi-transparent **ghost piece**. |
| **Visual Features** | **Hold/Preview:** Added `refreshHeldBrick()` and `refreshNextBricks()` to interpret the new DTO data and render the preview pieces (up to `NEXT_BRICK_COUNT`) and the held piece. |
| **Data Binding** | Implemented three key binding methods (`bindScore()`, `bindLines()`, `bindLevel()`) that use JavaFX properties to ensure **real-time UI updates** without manual refresh calls. |
| **Notifications** | Enhanced the `showNotificationIfRowsCleared()` logic to display specific labels ("SINGLE", "TETRIS") and include the **Combo Streak** bonus text. Added `showChaosNotification()` to display center-screen alerts. |
| **Effects/UI** | Implemented `playHardDropBounce()` for a visual feedback animation. Added `updateTimer()` to reflect the stopwatch state. Added explicit calls to display the pause message and update the high score display. |
| **Alignment Fixes** | Fixed subtle visual misalignment between the falling brick and the grid by adjusting layout calculations, including setting **GridPane gaps to zero** on the ghost layer and eliminating the `RIGHT_EDGE_VISUAL_TWEAK`. |

---

### 4.2.4 `SimpleBoard.java`

`SimpleBoard` now fully encapsulates the Model's logic, accepting dependency injections and exposing key methods for game features.

| **Category** | **Changes and Rationale** |
| --- | --- |
| **Dependency Injection** | **BrickGenerator Acceptance:** Modified the constructor to accept a `BrickGenerator` instance, adhering to **DIP** and decoupling the board's behavior from a specific random implementation. |
| **New Core Logic** | **Hard Drop:** Implemented the `hardDropBrick()` method, which contains the full logic sequence: calculating drop distance, applying hard drop score, merging, line clearing, and spawning a new brick. |
| **New Core Logic** | **Hold Mechanic:** Added `heldBrick` and `canHold` state variables. Implemented `holdBrick()` to manage the swap-and-lockout functionality. |
| **New Core Logic** | **Garbage Rows:** Implemented `addGarbageRow()` to shift the board upwards, delete the top row, and inject a new, incomplete row at the bottom with a single randomly placed gap. |
| **State Output** | **View Data Enrichment:** Implemented `calculateGhostY()` and updated `getViewData()` to include the **ghost piece position**, **held brick data**, and the **list of next bricks** (`NEXT_BRICK_COUNT`). |
| **Refactoring (SRP/DRY)** | **Movement Helper:** Extracted the core movement logic into the reusable private method `tryMove(dx, dy)` to eliminate repetition. |
| **Refactoring (Cleanup)** | Replaced hard-coded spawn coordinates and movement offsets with descriptive named constants (`SPAWN_X`, `SPAWN_Y`, etc.). Updated matrix validation to use the dedicated `isMoveValid()` helper. |
| **Decoupling (MVC)** | Replaced the deprecated `java.awt.Point` dependency with the custom, lightweight **`GamePoint`** record for coordinate tracking. |
| **Decoupling (Scoring)** | Removed `calculateScoreBonus()` method. Score calculation logic is now fully delegated to the `Score` object, ensuring the board only handles piece movement and placement. |

---

### 4.2.5 `Score.java`

The `Score` class was redesigned to fully encapsulate all scoring rules, directly referencing constants from `GameConfig.java`. This strictly enforces the **Single Responsibility Principle (SRP)** for scoring.

| **Category** | **Changes and Rationale** |
| --- | --- |
| **Encapsulation (SRP)** | **Scoring Rules:** All score calculation logic and constants (previously spread across `SimpleBoard` and `GameController`) were moved here. Updated `add(int i)` calls were replaced with semantic methods: `addSoftDrop()`, `addHardDrop(int rows)`, and `addLinesCleared(int linesRemoved)`. |
| **Scoring Logic** | **Standardized Line Clear:** Replaced the previous generic quadratic formula (`50 * count * count`) with a standardized **switch-case** logic for fixed points: **Single (100), Double (300), Triple (500), Tetris (800)**. |
| **New Feature** | **Combo System:** Added `comboCount` logic that increments on consecutive line clears and applies a `SCORE_COMBO_BONUS` multiplier to the score. Added `resetCombo()` to break the streak. |
| **Data Binding** | **Line Counter:** Added the private `lines` property and `linesProperty()` getter to allow the UI to bind to the total number of lines cleared. Updated `reset()` to clear the lines count. |
| **Testing/Readability** | Added the convenience getter `getScore()` to simplify access to the raw score integer value for external classes and unit testing. |

---

### 4.2.6 Data Transfer Objects (DTOs) and Interfaces

These non-logic files were modified to support the flow of information between the decoupled Model and View layers.

| **File** | **Changes and Rationale** |
| --- | --- |
| **`ViewData.java`** | **Data Transport Update:** Added three new fields: `ghostYPosition`, a `List<int[][]> nextBricks` (for previews), and `int[][] heldBrickData` to carry all necessary visual state from the logic board to the UI. |
| **`ClearRow.java`** | **Data Transport Update:** Added the `scoreBonus` field to explicitly pass the points awarded for a line clear from the Model layer to the Controller/View, enabling score popup notifications. |
| **`GamePoint.java`** | **Decoupling:** New record/class created to replace the dependency on `java.awt.Point`, ensuring the Model is fully decoupled from external UI/graphics libraries. |
| **`EventType.java`** | **Input Expansion:** Added two new enum constants: **`HARD_DROP`** and **`HOLD`** to distinguish the new input events. |
| **`InputEventListener.java`** | **Contract Expansion:** Added method signatures for **`onHardDropEvent(MoveEvent event)`** and **`onHoldEvent(MoveEvent event)`**, defining the contract for handling the new input actions. |
| **`Board.java`** | **Contract Expansion:** Added abstract method signatures for the new core mechanics: `DownData hardDropBrick()` and `void holdBrick()`. |
| **`DownData.java`** | **New DTO:** Introduced to encapsulate the results of a downward movement (e.g., collision status, success/fail) in a single object. |

---

### 4.2.7 Utility and Brick Classes

Utility classes were heavily refactored for structural correctness and to enforce SRP.

| **File** | **Changes and Rationale** |
| --- | --- |
| **`MatrixOperations.java`** | **Structural Fixes:** **Corrected the i/j indexing bug** by changing `brick[j][i]` to `brick[i][j]` across all collision/merge methods, restoring standard matrix access (row-major). **Rewritten Boundary Checking** for improved robustness. |
| **`MatrixOperations.java`** | **Algorithm Change:** The row-clearing logic was completely **rewritten** to adopt the conventional Tetris algorithm (collect non-full rows and rebuild from the top) for clarity and maintainability. |
| **`MatrixOperations.java`** | **Decoupling (SRP):** Extracted generic array helpers to `ArrayOperations.java`. **Removed all score calculation logic** (`computeScoreBonus()`, `SCORE_MULTIPLIER`) to maintain SRP, focusing the class purely on matrix manipulation. |
| **`ArrayOperations.java`** | **New Class (SRP):** Created to house generic array/matrix copy and manipulation methods, decoupling them from the game-specific logic in `MatrixOperations`. |
| **`BrickRotator.java`** | **New Logic:** Implemented `tryRotate(int[][] boardMatrix, GamePoint currentOffset)` to centralize the **Super Rotation System (SRS) kick-testing** and collision checks, essential for valid rotations near walls. |
| **`IBrick.java`, `JBrick.java`, `LBrick.java`, `OBrick.java`, `SBrick.java`, `TBrick.java`, `ZBrick.java`** | **DRY Principle:** All seven brick classes were modified to extend a common **`AbstractBrick`** superclass, removing the duplicate code for matrix storage and retrieval. |
| **`NotificationPanel.java`** | **Refactoring:** Replaced all hard-coded dimensions and durations with named constants. Simplified event handling with a lambda expression and adjusted styling to fit the new side panel notification format. |

---

### 4.2.8 Configuration and Game Management

| **File** | **Changes and Rationale** |
| --- | --- |
| **`GameConfig.java`** | **Centralized Constants:** Became the single source of truth for all game constants, including Board dimensions and **all Scoring values** (`SCORE_SINGLE`, `SCORE_TETRIS`, `DROP_INTERVAL_MS`, etc.), supporting the overall Singleton and Config pattern. |
| **`LevelManager.java`** | **Game Progression:** Initializes the level and speed state. Added the `levelProperty` for data binding, and `onLinesCleared()` logic to manage when the game levels up and speeds increase. |
| **`HighScoreManager.java`** | **Persistence Integration:** Used to retrieve the existing high score/best time at game start and to update these records when the game enters the `GameOverState` or on a Level 6 victory. |

---

## **4.3** Summary of Architectural Refactoring and Project Modernization

The project was fundamentally restructured to prioritize maintainability and testability through the rigorous application of architectural patterns and SOLID principles.

- **Architectural Decoupling (MVC/State):** The codebase was strictly separated into Model (`SimpleBoard`), View (`GuiController`), and Controller (`GameController`). The `GameController` was refactored as the **Context for the State Design Pattern**, delegating all input and logic to dedicated `GameState` objects (`PlayingState`, `PausedState`). All core logic was fully decoupled from JavaFX dependencies for enhanced **testability**.
- **Principle Enforcement (SOLID):**
    - **SRP:** Scoring logic was entirely moved to `Score.java` to enforce the Single Responsibility Principle. Game constants were centralized in the `GameConfig` Singleton, eliminating magic numbers.
    - **DIP/DRY:** Dependency Injection was used for core components (like `BrickGenerator`). The logic for all seven brick shapes was consolidated into an `AbstractBrick` superclass.
- **Core Mechanics and Fixes:**
    - Implemented full **Hard Drop** and **Hold** functionality, including **Ghost Piece** rendering and a new score-tracking **Combo System**.
    - Corrected a critical **Matrix Indexing Bug** and rewrote the **Row-Clearing Algorithm** to restore expected Tetris behaviour.
    - Improved **UI performance** by switching to hardware-accelerated JavaFX components (`StackPane`) and implementing **Color Caching**.
- **Game Modes and Difficulty:**
    - Implemented the **Adventure Mode** progression loop via `LevelManager`, featuring escalating speed and the introduction of timed **Garbage Rows** (Level 3+).
    - Added **Chaos Mode** (Level 5+), which periodically reverses player controls.
    - Introduced **Zen Mode** as a non-competitive, static-speed alternative.
    - Integrated a **Stopwatch Timer** and updated `HighScoreManager` to record **Best Time** upon game victory.

---

## **5.0 Unexpected Problems**

| **Problem Area** | **Unexpected Problem** | **Resolution** |
| --- | --- | --- |
| **Matrix Indexing & Spawning** | The core matrix logic broke after refactoring to row-major indexing, causing bricks to spawn at incorrect `Y` positions (mid-screen) or clip into the board. | **Structural Correction:** Systematically corrected all instances of inverted matrix access (`[j][i]` to `[i][j]`) within `MatrixOperations` for collision and merging. The `SPAWN_Y` constant in `SimpleBoard` was updated to restore correct spawning at the top of the grid. |
| **Ghosting & Visual Artifacts** | After a brick landed and rows were cleared, the previous brick (and its ghost) would visually "linger" for a frame at the bottom of the screen. | **UI Synchronization & Cleanup:** Enforced a correct merge-clear-refresh sequence. Crucially, **duplicate calls** to `board.mergeBrickToBackground()` and redundant soft drop score increments were removed. State guards were added to movement handlers to prevent UI refresh when the game state is **Game Over**. |
| **Double Drop (Gravity Timing)** | A race condition occurred where a manual `Move Down` action was occasionally followed instantly by an automatic gravity tick, causing pieces to visually skip rows or jitter. | **Timeline Synchronization:** Implemented `GuiController.resetTimeline()` and ensured it is called after every manual downward movement. This restarts the gravity timer, guaranteeing a consistent delay before the next automatic drop. |
| **Testability & JavaFX Dependency** | Unit tests failed with `NullPointerException` because core logic components (like `GameController`) require a `GuiController` instance, and tests could not initialize the JavaFX environment. | **Decoupling for Testing:** A non-rendering **`MockGuiController`** was introduced. This mock object satisfies the `GameController`'s dependency contract, allowing the game logic to be tested in complete isolation without launching the full JavaFX framework. |
| **Build & Environment Errors** | Transient build issues were encountered, specifically Maven reporting "untrustable server" warnings when fetching dependencies. | **Environmental Fix:** Resolved by verifying the `pom.xml` integrity and clearing the local Maven cache, confirming the issue was environmental rather than a structural flaw in the project code. |