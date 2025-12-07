Directory structure:
└── pepper161-comp2042_kazuki_ichikawa/
    ├── README.md
    ├── mvnw
    ├── mvnw.cmd
    ├── pom.xml
    ├── docs/
    │   ├── AI Usage Log file.docx
    │   ├── ARCHITECTURE_ja.md
    │   └── guideline-core-mechanics-summary-2025-11-11.md
    ├── src/
    │   ├── main/
    │   │   ├── java/
    │   │   │   └── com/
    │   │   │       └── comp2042/
    │   │   │           ├── app/
    │   │   │           │   ├── HelpDialogController.java
    │   │   │           │   ├── HelpDialogService.java
    │   │   │           │   ├── Main.java
    │   │   │           │   ├── ModeSelectController.java
    │   │   │           │   ├── SettingsController.java
    │   │   │           │   └── StartMenuController.java
    │   │   │           ├── audio/
    │   │   │           │   └── BackgroundMusicManager.java
    │   │   │           ├── board/
    │   │   │           │   ├── Board.java
    │   │   │           │   ├── BrickRotator.java
    │   │   │           │   ├── ClearRow.java
    │   │   │           │   ├── MatrixOperations.java
    │   │   │           │   ├── NextShapeInfo.java
    │   │   │           │   ├── SimpleBoard.java
    │   │   │           │   └── ViewData.java
    │   │   │           ├── config/
    │   │   │           │   ├── GameSettings.java
    │   │   │           │   ├── GameSettingsStore.java
    │   │   │           │   └── ResourceManager.java
    │   │   │           ├── game/
    │   │   │           │   ├── GameConfig.java
    │   │   │           │   ├── GameController.java
    │   │   │           │   ├── GameLogic.java
    │   │   │           │   ├── GameState.java
    │   │   │           │   ├── LevelProgression.java
    │   │   │           │   ├── LineClearStats.java
    │   │   │           │   ├── LineClearType.java
    │   │   │           │   ├── Score.java
    │   │   │           │   ├── events/
    │   │   │           │   │   ├── DownData.java
    │   │   │           │   │   ├── EventSource.java
    │   │   │           │   │   ├── EventType.java
    │   │   │           │   │   ├── InputEventListener.java
    │   │   │           │   │   └── MoveEvent.java
    │   │   │           │   └── stats/
    │   │   │           │       ├── HighScoreEntry.java
    │   │   │           │       └── HighScoreService.java
    │   │   │           ├── help/
    │   │   │           │   └── HelpContentProvider.java
    │   │   │           ├── logic/
    │   │   │           │   └── bricks/
    │   │   │           │       ├── Brick.java
    │   │   │           │       ├── BrickGenerator.java
    │   │   │           │       ├── IBrick.java
    │   │   │           │       ├── JBrick.java
    │   │   │           │       ├── LBrick.java
    │   │   │           │       ├── OBrick.java
    │   │   │           │       ├── PieceGenerator.java
    │   │   │           │       ├── SBrick.java
    │   │   │           │       ├── TBrick.java
    │   │   │           │       ├── TetrominoType.java
    │   │   │           │       └── ZBrick.java
    │   │   │           └── ui/
    │   │   │               ├── BrickColorPalette.java
    │   │   │               ├── GameOverPanel.java
    │   │   │               ├── GuiController.java
    │   │   │               ├── HudPanel.java
    │   │   │               ├── NextQueuePanel.java
    │   │   │               ├── NotificationPanel.java
    │   │   │               ├── anim/
    │   │   │               │   └── TetrisLogoView.java
    │   │   │               └── input/
    │   │   │                   └── AutoRepeatHandler.java
    │   │   └── resources/
    │   │       ├── digital.ttf
    │   │       ├── gameLayout.fxml
    │   │       ├── HelpDialog.fxml
    │   │       ├── ModeSelect.fxml
    │   │       ├── SettingsDialog.fxml
    │   │       ├── StartMenu.fxml
    │   │       ├── window_style.css
    │   │       └── help/
    │   │           └── help-content.md
    │   └── test/
    │       └── java/
    │           └── com/
    │               └── comp2042/
    │                   ├── MatrixOperationsTest.java
    │                   ├── ScoreTest.java
    │                   ├── board/
    │                   │   └── SimpleBoardTest.java
    │                   ├── config/
    │                   │   └── GameSettingsStoreTest.java
    │                   ├── game/
    │                   │   ├── GameLogicTest.java
    │                   │   ├── LevelProgressionTest.java
    │                   │   └── stats/
    │                   │       └── HighScoreServiceTest.java
    │                   └── logic/
    │                       ├── PieceGeneratorTest.java
    │                       └── bricks/
    │                           └── BrickRotatorTest.java
    ├── .github/
    │   └── workflows/
    │       └── maven-ci.yml
    └── .mvn/
        └── wrapper/
            └── maven-wrapper.properties
