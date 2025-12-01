# COMP2042 Coursework: JavaFX Tetris

## GitHub
[INSERT YOUR GITHUB REPOSITORY LINK HERE]

## Compilation Instructions
To compile and run the application, ensure you have **Java JDK 23** (or later) installed.

1.  Open a terminal in the project root directory.
2.  Run the following command using the Maven Wrapper:

    **Windows:**
    ```powershell
    .\mvnw.cmd clean javafx:run
    ```

    **Mac/Linux:**
    ```bash
    ./mvnw clean javafx:run
    ```

This command will clean the target directory, compile the source code, and launch the JavaFX application.

## Implemented and Working Properly
*   **7-Bag Randomizer**: A fairness algorithm that deals all 7 tetrominoes in a random permutation before repeating, preventing long droughts of specific pieces.
*   **Super Rotation System (SRS)**: Authentic rotation rules including wall kicks, allowing pieces to rotate into tight spaces.
*   **Ghost Piece**: A visual guide showing exactly where the current piece will land, improving placement accuracy.
*   **Next Queue (5 Pieces)**: Displays the next 5 upcoming tetrominoes, allowing players to plan ahead.
*   **Hold Queue**: Allows the player to store the current piece for later use (default key: C).
*   **Guideline Scoring System**: Implements standard scoring for Singles, Doubles, Triples, Tetris, and T-Spins, including Back-to-Back bonuses and Combo multipliers.
*   **Level Progression**: Gravity speed increases every 10 lines cleared, following a standard difficulty curve.
*   **Settings Dialog**: A comprehensive configuration menu to adjust DAS (Delayed Auto Shift), ARR (Auto Repeat Rate), Soft Drop speed, and Audio volume.
*   **Key Remapping**: Fully customizable keyboard controls via the Settings dialog.
*   **High Score System**: Tracks and displays the top scores locally.
*   **Help & Controls Dialog**: An in-game guide explaining the rules and current control scheme.
*   **Audio System**: Background music and sound effects with toggle/volume controls.

## Implemented but Not Working Properly
*   **None**: All implemented features are currently functioning as expected.

## Features Not Implemented
*   **Online Multiplayer**: Due to time constraints and the focus on refining the single-player core mechanics, network play was not implemented.

## New Java Classes
*   **`com.comp2042.app.SettingsController`**: Manages the Settings dialog UI and logic, handling user inputs for game configuration.
*   **`com.comp2042.app.HelpDialogController`**: Controls the Help dialog, displaying game instructions.
*   **`com.comp2042.config.GameSettings`**: A data model class that holds all configurable game parameters (DAS, ARR, keybindings, volume).
*   **`com.comp2042.config.GameSettingsStore`**: Handles the persistence of game settings to a local properties file (`~/.tetrisjfx/settings.properties`).
*   **`com.comp2042.config.ResourceManager`**: Centralizes resource loading (FXML, images, CSS) to ensure robust path handling and error reporting.
*   **`com.comp2042.audio.BackgroundMusicManager`**: Manages the playback of background music and sound effects using JavaFX `MediaPlayer`.
*   **`com.comp2042.ui.NextQueuePanel`**: A custom UI component responsible for rendering the preview of the next 5 pieces.
*   **`com.comp2042.ui.HudPanel`**: Displays real-time game statistics (Score, Level, Lines, Combo, Back-to-Back status).
*   **`com.comp2042.ui.BrickColorPalette`**: Defines the standard color scheme for tetrominoes to ensure visual consistency across the board and UI panels.
*   **`com.comp2042.ui.input.AutoRepeatHandler`**: Implements the logic for DAS and ARR, handling key repeat behavior for smooth movement.
*   **`com.comp2042.logic.bricks.PieceGenerator`**: Implements the 7-Bag randomizer algorithm.
*   **`com.comp2042.game.stats.HighScoreEntry` / `HighScoreService`**: Manages the storage and retrieval of high score data.

## Modified Java Classes
*   **`com.comp2042.app.Main`**: Updated to initialize the `ResourceManager` and load the application with the new `StartMenuController`. Added command-line argument parsing for debug seeds.
*   **`com.comp2042.app.StartMenuController`**: Added "Settings" and "Help" buttons and their corresponding event handlers to open the new dialogs.
*   **`com.comp2042.game.GameController`**: Refactored to integrate the new `GameLogic`, `Score` system, and `InputEventListener`. Decoupled the game loop from the UI.
*   **`com.comp2042.game.GameLogic`**: Rewritten to implement standard Tetris rules, including line clearing, locking, and T-Spin detection.
*   **`com.comp2042.board.SimpleBoard`**: Extended to support the Ghost Piece calculation and provide data for the Next Queue.
*   **`com.comp2042.ui.GuiController`**: Major refactoring to handle the game loop (Timeline), input delegation to `AutoRepeatHandler`, and integration of new UI panels (`HudPanel`, `NextQueuePanel`).
*   **`com.comp2042.board.BrickRotator`**: Updated to implement the full Super Rotation System (SRS) kick tables for all piece types.

## Unexpected Problems
*   **Background Grid Visibility**: The default background grid pattern made the text in the Settings and Help dialogs difficult to read.
    *   **Resolution**: I modified the CSS (`window_style.css`) to explicitly remove the background image (`-fx-background-image: null`) and apply a solid dark background color (`rgb(8, 12, 24)`) for these specific dialogs, ensuring high contrast and readability.
*   **FXML Stylesheet Loading**: Initially, the new dialogs (Settings, Help) were not picking up the global styles.
    *   **Resolution**: I ensured that the `window_style.css` was correctly linked in the `<stylesheets>` section of the FXML files and used the `ResourceManager` to resolve the path reliably.
