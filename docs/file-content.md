(Files content cropped to 300k characters, download full ingest to see more)
================================================
FILE: README.md
================================================
# COMP2042 Coursework: JavaFX Tetris

## GitHub
https://github.com/Pepper161/COMP2042_Kazuki_Ichikawa

## Compilation Instructions
To compile and run the application, ensure you have **Java JDK 23** (or later) installed.

1.  Open a terminal in the project root directory.
2.  Compile dependencies via:

    ```bash
    ./mvnw clean compile
    ```

3.  Launch the JavaFX client with:

    **Windows:**
    ```powershell
    .\mvnw.cmd clean javafx:run
    ```

    **Mac/Linux:**
    ```bash
    ./mvnw clean javafx:run
    ```

You can reproduce a specific bag order by passing `--seed=<signed-long>` to JavaFX (e.g. `./mvnw javafx:run -Dexec.args="--seed=12345"`). Invalid seeds fall back to random bags. Execute the automated tests with `./mvnw test`.

## Project Deliverables
In accordance with the coursework specification, this submission includes:
*   **`README.md`**: This file, documenting maintenance and extensions.
*   **`Design.pdf`**: A high-level class diagram of the final system structure.
*   **`Javadoc/`**: Generated HTML documentation for the source code.
*   **`Demo.mp4`**: A video demonstration of the refactoring and new features.


## Implemented and Working Properly
*   **7-Bag Randomizer**: A fairness algorithm that deals all 7 tetrominoes in a random permutation before repeating, preventing long droughts of specific pieces.
*   **Super Rotation System (SRS)**: Authentic rotation rules including wall kicks, allowing pieces to rotate into tight spaces.
*   **Ghost Piece**: A visual guide showing exactly where the current piece will land, improving placement accuracy.
*   **Next Queue (5 Pieces)**: Displays the next 5 upcoming tetrominoes, allowing players to plan ahead.
*   **HUD & Overlays**: `HudPanel` binds score, combo, back-to-back count, level, and mode badges; `NotificationPanel` shows transient combo popups; `GameOverPanel` surfaces run summaries with restart/main menu actions. The pause overlay (ESC) and seed banner keep session context visible.
*   **Guideline Scoring System**: Implements standard scoring for Singles, Doubles, Triples, Tetris, and T-Spins, including Back-to-Back bonuses and Combo multipliers.
*   **Level Progression & Modes**: Gravity speed increases every 10 lines cleared, following a standard difficulty curve driven by `LevelProgression`. Mode selection (Endless, Timed 180s, Fixed 40 Lines) in `ModeSelectController` adjusts objectives and HUD timers.
*   **Deterministic Seeds**: `Main` accepts `--seed=<long>` to replay/practice bags; `GuiController` surfaces the active seed in-game and on the Game Over overlay.
*   **Settings Dialog**: A comprehensive configuration menu to adjust DAS (Delayed Auto Shift), ARR (Auto Repeat Rate), Soft Drop speed, color assist, outlines, and audio volume/toggles.
*   **Key Remapping**: Fully customizable keyboard controls via the Settings dialog.
*   **High Score System**: Tracks the top scores per mode locally and feeds both the start menu leaderboards and the in-game Game Over overlay.
*   **Help & Controls Dialog**: An in-game guide explaining the rules and current control scheme.
*   **Audio System**: Background music and sound effects played through JavaFX `AudioClip`, with enable/disable and volume controls surfaced in Settings.

## Implemented but Not Working Properly
*   **None**: All implemented features are currently functioning as expected.

## Features Not Implemented
*   **Online Multiplayer**: Due to time constraints and the focus on refining the single-player core mechanics, network play was not implemented.
*   **Hold Piece Queue**: There is currently no hold/ swap mechanic; focus remained on core movement, scoring, and mode support.
*   **Replay Saving**: Aside from the `--seed` hint printed on Game Over, there is no run recording/replay export feature.

## New Java Classes
*   **`com.comp2042.app.SettingsController`**: Manages the Settings dialog UI and logic, handling user inputs for game configuration.
*   **`com.comp2042.app.HelpDialogController`**: Controls the Help dialog, displaying game instructions.
*   **`com.comp2042.config.GameSettings`**: An immutable data model that holds DAS/ARR values, soft-drop tuning, key bindings, color-assist/outline toggles, and BGM enable + volume sliders.
*   **`com.comp2042.config.GameSettingsStore`**: Handles the persistence of game settings to a local properties file (`~/.tetrisjfx/settings.properties` by default, overridable via `tetris.settings.dir`).
*   **`com.comp2042.config.ResourceManager`**: Centralizes resource loading (FXML, fonts, markdown, audio) to ensure robust path handling and error reporting.
*   **`com.comp2042.audio.BackgroundMusicManager`**: Manages the playback of looping themes and one-shot effects using JavaFX `AudioClip`, with master volume/enabled state mirroring the Settings dialog.
*   **`com.comp2042.ui.NextQueuePanel`**: A custom UI component responsible for rendering the preview of the next 5 pieces.
*   **`com.comp2042.ui.HudPanel`**: Displays real-time score, combo, back-to-back chains, the current level, and the active mode badge.
*   **`com.comp2042.app.ModeSelectController`**: Modal for picking Endless / Timed / Fixed Lines with card UI.
*   **`com.comp2042.app.HelpDialogService`**: Utility to show the F1/help dialog from any controller.
*   **`com.comp2042.game.GameConfig` / `GameState` / `LevelProgression` / `LineClearStats` / `LineClearType`**: Runtime configuration, mode tracking, gravity progression, and detailed scoring metadata.
*   **`com.comp2042.game.events.*`**: Strongly typed input events powering GUI -> logic communication.
*   **`com.comp2042.help.HelpContentProvider`**: Loads Markdown help content for the F1 dialog.
*   **`com.comp2042.ui.BrickColorPalette`**: Defines the standard color scheme for tetrominoes to ensure visual consistency across the board and UI panels.
*   **`com.comp2042.ui.anim.TetrisLogoView`**: Animated start-menu logo with glitch overlays.
*   **`com.comp2042.ui.input.AutoRepeatHandler`**: Implements the logic for DAS and ARR, handling key repeat behavior for smooth movement.
*   **`com.comp2042.logic.bricks.PieceGenerator`**: Implements the 7-Bag randomizer algorithm.
*   **`com.comp2042.game.stats.HighScoreEntry` / `HighScoreService`**: Persists mode-specific leaderboard rows (score, mode, duration, timestamp) under `~/.comp2042/highscores.dat` and exposes fetch/record helpers for UI panels.

## Modified Java Classes
*   **`com.comp2042.app.Main`**:
    *   **Changes**: Updated to initialize the `ResourceManager` and load the application with the new `StartMenuController`. Added command-line argument parsing for debug seeds.
    *   **Rationale**: Necessary to support the new FXML-based start menu and to allow deterministic testing via seed injection.
*   **`com.comp2042.app.StartMenuController`**:
    *   **Changes**: Added "Settings" and "Help" buttons and their corresponding event handlers to open the new dialogs.
    *   **Rationale**: Required to expose the new configuration and help features to the user from the entry point.
*   **`com.comp2042.game.GameController`**:
    *   **Changes**: Refactored to integrate the new `GameLogic`, `Score` system, deterministic seeds, and `InputEventListener`. Decoupled the game loop from the UI.
    *   **Rationale**: The original monolithic design made testing difficult. Separating the controller allows for a cleaner MVC architecture where game state is managed independently of the view.
*   **`com.comp2042.game.GameLogic`**:
    *   **Changes**: Rewritten to implement standard Tetris rules, including line clearing, locking, and T-Spin detection.
    *   **Rationale**: The original logic was incomplete and buggy. A dedicated logic class ensures adherence to standard Tetris guidelines and simplifies unit testing.
*   **`com.comp2042.board.SimpleBoard`** / **`Board`** / **`MatrixOperations`** / **`ViewData`** / **`ClearRow`**:
    *   **Changes**: Extended to support the ghost piece, drop distance, next queue capture, and safer collision handling.
    *   **Rationale**: "Lookahead" features like the Ghost Piece and Next Queue required non-destructive access to the board state, which the original `Board` class did not support.
*   **`com.comp2042.ui.GuiController`**:
    *   **Changes**: Major refactoring to handle the game loop (Timeline), input delegation to `AutoRepeatHandler`, pause overlay, deterministic seed display, and integration of new UI panels (`HudPanel`, `NextQueuePanel`).
    *   **Rationale**: To provide a smoother 60FPS experience and responsive controls (DAS/ARR), the rendering loop needed to be decoupled from the logic loop.
*   **`com.comp2042.board.BrickRotator`**:
    *   **Changes**: Updated to implement the full Super Rotation System (SRS) kick tables for all piece types.
    *   **Rationale**: The original rotation was basic and often caused pieces to get stuck. SRS is the standard for modern Tetris and improves gameplay flow.
*   **`com.comp2042.Score`**:
    *   **Changes**: Enhanced to track combos, back-to-back streaks, and expose JavaFX properties for HUD bindings.
    *   **Rationale**: To support the new HUD and scoring rules, the score object needed to be observable by the UI.
*   **`com.comp2042.NotificationPanel` / `GameOverPanel`**:
    *   **Changes**: Reworked animations and layouts to support combo popups and seeded leaderboard overlays.
    *   **Rationale**: Enhances player feedback and provides a polished end-of-game experience.
*   **`com.comp2042.board.MatrixOperations`**:
    *   **Changes**: Safe handling of rectangular piece matrices (prevents crashes on future pieces).
    *   **Rationale**: Prevents `ArrayIndexOutOfBoundsException` when dealing with non-square matrices, improving stability.

## Key Changes at a Glance

| Change Type | Location(s) | Reason / Impact |
| --- | --- | --- |
| Extension | `GameConfig`, `ModeSelectController`, `GuiController`, `HudPanel` | Adds Endless / Timed / Fixed Lines, timers, and seed banners to broaden gameplay modes. |
| Extension | `PieceGenerator`, `BrickRotator`, `SimpleBoard`, `NextQueuePanel` | Implements guideline 7-bag, SRS kicks, ghost/next queue to match Tetris standards. |
| Extension | `SettingsController`, `GameSettings*`, `AutoRepeatHandler`, `BackgroundMusicManager` | Exposes DAS/ARR/SDF, color assist, outlines, and audio toggles for accessibility. |
| Maintenance | `MatrixOperations`, `ClearRow`, `ViewData` | Hardened collision/merge logic to support rectangular matrices and avoid out-of-bounds errors. |
| Maintenance | `GameController`, `GameLogic`, `Score`, `NotificationPanel` | Refactors the gameplay loop for deterministic seeds plus combos/back-to-back/T-Spin detection. |
| Maintenance | `HighScoreService`, `StartMenuController`, `GameOverPanel` | Adds durable per-mode leaderboards shared between the start menu and game-over overlay. |

## Screenshots

### Start Menu & Leaderboard
![Start Menu with Leaderboard](docs/screenshots/start-menu-with-leaderboard.png)
The main entry point featuring the animated "TETRIS FX" logo (letters pulse and slide over time), mode selection, and a persistent leaderboard showing top scores for each mode.

### Mode Selection
![Mode Selection Console](docs/screenshots/mode-select-console.png)
A dedicated console for choosing between Endless, Timed (180s), and Fixed Lines (40 Lines) modes.

### Gameplay Interface
![Gameplay Interface](docs/screenshots/gameplay.png)
The core gameplay view showing the board, ghost piece, next queue (right), and HUD stats (left) including score, combo, and level.

### Settings Menu
![Settings Menu](docs/screenshots/setting.png)
Comprehensive configuration for gameplay tuning (DAS, ARR, Soft Drop), audio controls, and accessibility options (Color Assist, Outlines).

### Help & Controls
![Help & Controls](docs/screenshots/help-and-control.png)
In-game reference guide explaining controls, scoring rules, and special mechanics like T-Spins and Wall Kicks.

### Pause Overlay
![Pause Overlay](docs/screenshots/pause-overlay.png)
The pause menu overlay that halts the game loop, allowing players to resume, restart, or return to the main menu.

### Game Over & Results
![Game Over Screen](docs/screenshots/game-over-endless-leaderboard.png)
The summary screen displaying the final score, run statistics, and updated leaderboard rankings.

## Unexpected Problems
*   **Background Grid Visibility**: The default background grid pattern made the text in the Settings and Help dialogs difficult to read.
    *   **Resolution**: I modified the CSS (`window_style.css`) to explicitly remove the background image (`-fx-background-image: null`) and apply a solid dark background color (`rgb(8, 12, 24)`) for these specific dialogs, ensuring high contrast and readability.
*   **FXML Stylesheet Loading**: Initially, the new dialogs (Settings, Help) were not picking up the global styles.
    *   **Resolution**: I ensured that the `window_style.css` was correctly linked in the `<stylesheets>` section of the FXML files and used the `ResourceManager` to resolve the path reliably.
*   **Audio / Resource Fallbacks**: Missing audio assets now log warnings in `BackgroundMusicManager` instead of crashing, and invalid `--seed` parameters fall back to random bags with a console notice.

## Testing and Documentation
*   **Automated tests**: `./mvnw test` executes the JUnit 5 suite (9 test classes across `board`, `config`, `game`, `logic`, and `stats` packages) to verify matrix math, line clears, generators, and persistence helpers before each submission.
*   **Design diagram**: `Design.pdf` (exported from `docs/design/Design.puml`) summarizes the final class structure. Include it alongside this README when preparing the submission folder/zip.
*   **Javadoc**: Generate via `./mvnw javadoc:javadoc`; the HTML output is produced under `target/site/apidocs/` and must be copied into the top-level `Javadoc/` directory for the final package.
*   **Internal notes**: Architecture and persistence references live under `docs-local/`, including the class diagram spec at `docs-local/design/class-diagram-spec.puml` and storage rationale under `docs-local/persistence/`.



================================================
FILE: mvnw
================================================
#!/bin/sh
# ----------------------------------------------------------------------------
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#    https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
# ----------------------------------------------------------------------------

# ----------------------------------------------------------------------------
# Maven Start Up Batch script
#
# Required ENV vars:
# ------------------
#   JAVA_HOME - location of a JDK home dir
#
# Optional ENV vars
# -----------------
#   M2_HOME - location of maven2's installed home dir
#   MAVEN_OPTS - parameters passed to the Java VM when running Maven
#     e.g. to debug Maven itself, use
#       set MAVEN_OPTS=-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000
#   MAVEN_SKIP_RC - flag to disable loading of mavenrc files
# ----------------------------------------------------------------------------

if [ -z "$MAVEN_SKIP_RC" ] ; then

  if [ -f /usr/local/etc/mavenrc ] ; then
    . /usr/local/etc/mavenrc
  fi

  if [ -f /etc/mavenrc ] ; then
    . /etc/mavenrc
  fi

  if [ -f "$HOME/.mavenrc" ] ; then
    . "$HOME/.mavenrc"
  fi

fi

# OS specific support.  $var _must_ be set to either true or false.
cygwin=false;
darwin=false;
mingw=false
case "`uname`" in
  CYGWIN*) cygwin=true ;;
  MINGW*) mingw=true;;
  Darwin*) darwin=true
    # Use /usr/libexec/java_home if available, otherwise fall back to /Library/Java/Home
    # See https://developer.apple.com/library/mac/qa/qa1170/_index.html
    if [ -z "$JAVA_HOME" ]; then
      if [ -x "/usr/libexec/java_home" ]; then
        export JAVA_HOME="`/usr/libexec/java_home`"
      else
        export JAVA_HOME="/Library/Java/Home"
      fi
    fi
    ;;
esac

if [ -z "$JAVA_HOME" ] ; then
  if [ -r /etc/gentoo-release ] ; then
    JAVA_HOME=`java-config --jre-home`
  fi
fi

if [ -z "$M2_HOME" ] ; then
  ## resolve links - $0 may be a link to maven's home
  PRG="$0"

  # need this for relative symlinks
  while [ -h "$PRG" ] ; do
    ls=`ls -ld "$PRG"`
    link=`expr "$ls" : '.*-> \(.*\)$'`
    if expr "$link" : '/.*' > /dev/null; then
      PRG="$link"
    else
      PRG="`dirname "$PRG"`/$link"
    fi
  done

  saveddir=`pwd`

  M2_HOME=`dirname "$PRG"`/..

  # make it fully qualified
  M2_HOME=`cd "$M2_HOME" && pwd`

  cd "$saveddir"
  # echo Using m2 at $M2_HOME
fi

# For Cygwin, ensure paths are in UNIX format before anything is touched
if $cygwin ; then
  [ -n "$M2_HOME" ] &&
    M2_HOME=`cygpath --unix "$M2_HOME"`
  [ -n "$JAVA_HOME" ] &&
    JAVA_HOME=`cygpath --unix "$JAVA_HOME"`
  [ -n "$CLASSPATH" ] &&
    CLASSPATH=`cygpath --path --unix "$CLASSPATH"`
fi

# For Mingw, ensure paths are in UNIX format before anything is touched
if $mingw ; then
  [ -n "$M2_HOME" ] &&
    M2_HOME="`(cd "$M2_HOME"; pwd)`"
  [ -n "$JAVA_HOME" ] &&
    JAVA_HOME="`(cd "$JAVA_HOME"; pwd)`"
fi

if [ -z "$JAVA_HOME" ]; then
  javaExecutable="`which javac`"
  if [ -n "$javaExecutable" ] && ! [ "`expr \"$javaExecutable\" : '\([^ ]*\)'`" = "no" ]; then
    # readlink(1) is not available as standard on Solaris 10.
    readLink=`which readlink`
    if [ ! `expr "$readLink" : '\([^ ]*\)'` = "no" ]; then
      if $darwin ; then
        javaHome="`dirname \"$javaExecutable\"`"
        javaExecutable="`cd \"$javaHome\" && pwd -P`/javac"
      else
        javaExecutable="`readlink -f \"$javaExecutable\"`"
      fi
      javaHome="`dirname \"$javaExecutable\"`"
      javaHome=`expr "$javaHome" : '\(.*\)/bin'`
      JAVA_HOME="$javaHome"
      export JAVA_HOME
    fi
  fi
fi

if [ -z "$JAVACMD" ] ; then
  if [ -n "$JAVA_HOME"  ] ; then
    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
      # IBM's JDK on AIX uses strange locations for the executables
      JAVACMD="$JAVA_HOME/jre/sh/java"
    else
      JAVACMD="$JAVA_HOME/bin/java"
    fi
  else
    JAVACMD="`\\unset -f command; \\command -v java`"
  fi
fi

if [ ! -x "$JAVACMD" ] ; then
  echo "Error: JAVA_HOME is not defined correctly." >&2
  echo "  We cannot execute $JAVACMD" >&2
  exit 1
fi

if [ -z "$JAVA_HOME" ] ; then
  echo "Warning: JAVA_HOME environment variable is not set."
fi

CLASSWORLDS_LAUNCHER=org.codehaus.plexus.classworlds.launcher.Launcher

# traverses directory structure from process work directory to filesystem root
# first directory with .mvn subdirectory is considered project base directory
find_maven_basedir() {

  if [ -z "$1" ]
  then
    echo "Path not specified to find_maven_basedir"
    return 1
  fi

  basedir="$1"
  wdir="$1"
  while [ "$wdir" != '/' ] ; do
    if [ -d "$wdir"/.mvn ] ; then
      basedir=$wdir
      break
    fi
    # workaround for JBEAP-8937 (on Solaris 10/Sparc)
    if [ -d "${wdir}" ]; then
      wdir=`cd "$wdir/.."; pwd`
    fi
    # end of workaround
  done
  echo "${basedir}"
}

# concatenates all lines of a file
concat_lines() {
  if [ -f "$1" ]; then
    echo "$(tr -s '\n' ' ' < "$1")"
  fi
}

BASE_DIR=`find_maven_basedir "$(pwd)"`
if [ -z "$BASE_DIR" ]; then
  exit 1;
fi

##########################################################################################
# Extension to allow automatically downloading the maven-wrapper.jar from Maven-central
# This allows using the maven wrapper in projects that prohibit checking in binary data.
##########################################################################################
if [ -r "$BASE_DIR/.mvn/wrapper/maven-wrapper.jar" ]; then
    if [ "$MVNW_VERBOSE" = true ]; then
      echo "Found .mvn/wrapper/maven-wrapper.jar"
    fi
else
    if [ "$MVNW_VERBOSE" = true ]; then
      echo "Couldn't find .mvn/wrapper/maven-wrapper.jar, downloading it ..."
    fi
    if [ -n "$MVNW_REPOURL" ]; then
      jarUrl="$MVNW_REPOURL/org/apache/maven/wrapper/maven-wrapper/3.1.0/maven-wrapper-3.1.0.jar"
    else
      jarUrl="https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.1.0/maven-wrapper-3.1.0.jar"
    fi
    while IFS="=" read key value; do
      case "$key" in (wrapperUrl) jarUrl="$value"; break ;;
      esac
    done < "$BASE_DIR/.mvn/wrapper/maven-wrapper.properties"
    if [ "$MVNW_VERBOSE" = true ]; then
      echo "Downloading from: $jarUrl"
    fi
    wrapperJarPath="$BASE_DIR/.mvn/wrapper/maven-wrapper.jar"
    if $cygwin; then
      wrapperJarPath=`cygpath --path --windows "$wrapperJarPath"`
    fi

    if command -v wget > /dev/null; then
        if [ "$MVNW_VERBOSE" = true ]; then
          echo "Found wget ... using wget"
        fi
        if [ -z "$MVNW_USERNAME" ] || [ -z "$MVNW_PASSWORD" ]; then
            wget "$jarUrl" -O "$wrapperJarPath" || rm -f "$wrapperJarPath"
        else
            wget --http-user=$MVNW_USERNAME --http-password=$MVNW_PASSWORD "$jarUrl" -O "$wrapperJarPath" || rm -f "$wrapperJarPath"
        fi
    elif command -v curl > /dev/null; then
        if [ "$MVNW_VERBOSE" = true ]; then
          echo "Found curl ... using curl"
        fi
        if [ -z "$MVNW_USERNAME" ] || [ -z "$MVNW_PASSWORD" ]; then
            curl -o "$wrapperJarPath" "$jarUrl" -f
        else
            curl --user $MVNW_USERNAME:$MVNW_PASSWORD -o "$wrapperJarPath" "$jarUrl" -f
        fi

    else
        if [ "$MVNW_VERBOSE" = true ]; then
          echo "Falling back to using Java to download"
        fi
        javaClass="$BASE_DIR/.mvn/wrapper/MavenWrapperDownloader.java"
        # For Cygwin, switch paths to Windows format before running javac
        if $cygwin; then
          javaClass=`cygpath --path --windows "$javaClass"`
        fi
        if [ -e "$javaClass" ]; then
            if [ ! -e "$BASE_DIR/.mvn/wrapper/MavenWrapperDownloader.class" ]; then
                if [ "$MVNW_VERBOSE" = true ]; then
                  echo " - Compiling MavenWrapperDownloader.java ..."
                fi
                # Compiling the Java class
                ("$JAVA_HOME/bin/javac" "$javaClass")
            fi
            if [ -e "$BASE_DIR/.mvn/wrapper/MavenWrapperDownloader.class" ]; then
                # Running the downloader
                if [ "$MVNW_VERBOSE" = true ]; then
                  echo " - Running MavenWrapperDownloader.java ..."
                fi
                ("$JAVA_HOME/bin/java" -cp .mvn/wrapper MavenWrapperDownloader "$MAVEN_PROJECTBASEDIR")
            fi
        fi
    fi
fi
##########################################################################################
# End of extension
##########################################################################################

export MAVEN_PROJECTBASEDIR=${MAVEN_BASEDIR:-"$BASE_DIR"}
if [ "$MVNW_VERBOSE" = true ]; then
  echo $MAVEN_PROJECTBASEDIR
fi
MAVEN_OPTS="$(concat_lines "$MAVEN_PROJECTBASEDIR/.mvn/jvm.config") $MAVEN_OPTS"

# For Cygwin, switch paths to Windows format before running java
if $cygwin; then
  [ -n "$M2_HOME" ] &&
    M2_HOME=`cygpath --path --windows "$M2_HOME"`
  [ -n "$JAVA_HOME" ] &&
    JAVA_HOME=`cygpath --path --windows "$JAVA_HOME"`
  [ -n "$CLASSPATH" ] &&
    CLASSPATH=`cygpath --path --windows "$CLASSPATH"`
  [ -n "$MAVEN_PROJECTBASEDIR" ] &&
    MAVEN_PROJECTBASEDIR=`cygpath --path --windows "$MAVEN_PROJECTBASEDIR"`
fi

# Provide a "standardized" way to retrieve the CLI args that will
# work with both Windows and non-Windows executions.
MAVEN_CMD_LINE_ARGS="$MAVEN_CONFIG $@"
export MAVEN_CMD_LINE_ARGS

WRAPPER_LAUNCHER=org.apache.maven.wrapper.MavenWrapperMain

exec "$JAVACMD" \
  $MAVEN_OPTS \
  $MAVEN_DEBUG_OPTS \
  -classpath "$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.jar" \
  "-Dmaven.home=${M2_HOME}" \
  "-Dmaven.multiModuleProjectDirectory=${MAVEN_PROJECTBASEDIR}" \
  ${WRAPPER_LAUNCHER} $MAVEN_CONFIG "$@"



================================================
FILE: mvnw.cmd
================================================
@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.  See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership.  The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in compliance
@REM with the License.  You may obtain a copy of the License at
@REM
@REM    https://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied.  See the License for the
@REM specific language governing permissions and limitations
@REM under the License.
@REM ----------------------------------------------------------------------------

@REM ----------------------------------------------------------------------------
@REM Maven Start Up Batch script
@REM
@REM Required ENV vars:
@REM JAVA_HOME - location of a JDK home dir
@REM
@REM Optional ENV vars
@REM M2_HOME - location of maven2's installed home dir
@REM MAVEN_BATCH_ECHO - set to 'on' to enable the echoing of the batch commands
@REM MAVEN_BATCH_PAUSE - set to 'on' to wait for a keystroke before ending
@REM MAVEN_OPTS - parameters passed to the Java VM when running Maven
@REM     e.g. to debug Maven itself, use
@REM set MAVEN_OPTS=-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000
@REM MAVEN_SKIP_RC - flag to disable loading of mavenrc files
@REM ----------------------------------------------------------------------------

@REM Begin all REM lines with '@' in case MAVEN_BATCH_ECHO is 'on'
@echo off
@REM set title of command window
title %0
@REM enable echoing by setting MAVEN_BATCH_ECHO to 'on'
@if "%MAVEN_BATCH_ECHO%" == "on"  echo %MAVEN_BATCH_ECHO%

@REM set %HOME% to equivalent of $HOME
if "%HOME%" == "" (set "HOME=%HOMEDRIVE%%HOMEPATH%")

@REM Execute a user defined script before this one
if not "%MAVEN_SKIP_RC%" == "" goto skipRcPre
@REM check for pre script, once with legacy .bat ending and once with .cmd ending
if exist "%USERPROFILE%\mavenrc_pre.bat" call "%USERPROFILE%\mavenrc_pre.bat" %*
if exist "%USERPROFILE%\mavenrc_pre.cmd" call "%USERPROFILE%\mavenrc_pre.cmd" %*
:skipRcPre

@setlocal

set ERROR_CODE=0

@REM To isolate internal variables from possible post scripts, we use another setlocal
@setlocal

@REM ==== START VALIDATION ====
if not "%JAVA_HOME%" == "" goto OkJHome

echo.
echo Error: JAVA_HOME not found in your environment. >&2
echo Please set the JAVA_HOME variable in your environment to match the >&2
echo location of your Java installation. >&2
echo.
goto error

:OkJHome
if exist "%JAVA_HOME%\bin\java.exe" goto init

echo.
echo Error: JAVA_HOME is set to an invalid directory. >&2
echo JAVA_HOME = "%JAVA_HOME%" >&2
echo Please set the JAVA_HOME variable in your environment to match the >&2
echo location of your Java installation. >&2
echo.
goto error

@REM ==== END VALIDATION ====

:init

@REM Find the project base dir, i.e. the directory that contains the folder ".mvn".
@REM Fallback to current working directory if not found.

set MAVEN_PROJECTBASEDIR=%MAVEN_BASEDIR%
IF NOT "%MAVEN_PROJECTBASEDIR%"=="" goto endDetectBaseDir

set EXEC_DIR=%CD%
set WDIR=%EXEC_DIR%
:findBaseDir
IF EXIST "%WDIR%"\.mvn goto baseDirFound
cd ..
IF "%WDIR%"=="%CD%" goto baseDirNotFound
set WDIR=%CD%
goto findBaseDir

:baseDirFound
set MAVEN_PROJECTBASEDIR=%WDIR%
cd "%EXEC_DIR%"
goto endDetectBaseDir

:baseDirNotFound
set MAVEN_PROJECTBASEDIR=%EXEC_DIR%
cd "%EXEC_DIR%"

:endDetectBaseDir

IF NOT EXIST "%MAVEN_PROJECTBASEDIR%\.mvn\jvm.config" goto endReadAdditionalConfig

@setlocal EnableExtensions EnableDelayedExpansion
for /F "usebackq delims=" %%a in ("%MAVEN_PROJECTBASEDIR%\.mvn\jvm.config") do set JVM_CONFIG_MAVEN_PROPS=!JVM_CONFIG_MAVEN_PROPS! %%a
@endlocal & set JVM_CONFIG_MAVEN_PROPS=%JVM_CONFIG_MAVEN_PROPS%

:endReadAdditionalConfig

SET MAVEN_JAVA_EXE="%JAVA_HOME%\bin\java.exe"
set WRAPPER_JAR="%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar"
set WRAPPER_LAUNCHER=org.apache.maven.wrapper.MavenWrapperMain

set DOWNLOAD_URL="https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.1.0/maven-wrapper-3.1.0.jar"

FOR /F "usebackq tokens=1,2 delims==" %%A IN ("%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.properties") DO (
    IF "%%A"=="wrapperUrl" SET DOWNLOAD_URL=%%B
)

@REM Extension to allow automatically downloading the maven-wrapper.jar from Maven-central
@REM This allows using the maven wrapper in projects that prohibit checking in binary data.
if exist %WRAPPER_JAR% (
    if "%MVNW_VERBOSE%" == "true" (
        echo Found %WRAPPER_JAR%
    )
) else (
    if not "%MVNW_REPOURL%" == "" (
        SET DOWNLOAD_URL="%MVNW_REPOURL%/org/apache/maven/wrapper/maven-wrapper/3.1.0/maven-wrapper-3.1.0.jar"
    )
    if "%MVNW_VERBOSE%" == "true" (
        echo Couldn't find %WRAPPER_JAR%, downloading it ...
        echo Downloading from: %DOWNLOAD_URL%
    )

    powershell -Command "&{"^
		"$webclient = new-object System.Net.WebClient;"^
		"if (-not ([string]::IsNullOrEmpty('%MVNW_USERNAME%') -and [string]::IsNullOrEmpty('%MVNW_PASSWORD%'))) {"^
		"$webclient.Credentials = new-object System.Net.NetworkCredential('%MVNW_USERNAME%', '%MVNW_PASSWORD%');"^
		"}"^
		"[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; $webclient.DownloadFile('%DOWNLOAD_URL%', '%WRAPPER_JAR%')"^
		"}"
    if "%MVNW_VERBOSE%" == "true" (
        echo Finished downloading %WRAPPER_JAR%
    )
)
@REM End of extension

@REM Provide a "standardized" way to retrieve the CLI args that will
@REM work with both Windows and non-Windows executions.
set MAVEN_CMD_LINE_ARGS=%*

%MAVEN_JAVA_EXE% ^
  %JVM_CONFIG_MAVEN_PROPS% ^
  %MAVEN_OPTS% ^
  %MAVEN_DEBUG_OPTS% ^
  -classpath %WRAPPER_JAR% ^
  "-Dmaven.multiModuleProjectDirectory=%MAVEN_PROJECTBASEDIR%" ^
  %WRAPPER_LAUNCHER% %MAVEN_CONFIG% %*
if ERRORLEVEL 1 goto error
goto end

:error
set ERROR_CODE=1

:end
@endlocal & set ERROR_CODE=%ERROR_CODE%

if not "%MAVEN_SKIP_RC%"=="" goto skipRcPost
@REM check for post script, once with legacy .bat ending and once with .cmd ending
if exist "%USERPROFILE%\mavenrc_post.bat" call "%USERPROFILE%\mavenrc_post.bat"
if exist "%USERPROFILE%\mavenrc_post.cmd" call "%USERPROFILE%\mavenrc_post.cmd"
:skipRcPost

@REM pause the script if MAVEN_BATCH_PAUSE is set to 'on'
if "%MAVEN_BATCH_PAUSE%"=="on" pause

if "%MAVEN_TERMINATE_CMD%"=="on" exit %ERROR_CODE%

cmd /C exit /B %ERROR_CODE%



================================================
FILE: pom.xml
================================================
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example</groupId>
    <artifactId>CW2025</artifactId>
    <version>1.0-SNAPSHOT</version>
    <name>demo3</name>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <junit.version>5.12.1</junit.version>
        <javafx.version>25</javafx.version>
        <javafx.platform>win</javafx.platform>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.openjfx</groupId>
            <artifactId>javafx-base</artifactId>
            <version>${javafx.version}</version>
            <classifier>${javafx.platform}</classifier>
        </dependency>
        <dependency>
            <groupId>org.openjfx</groupId>
            <artifactId>javafx-controls</artifactId>
            <version>${javafx.version}</version>
            <classifier>${javafx.platform}</classifier>
        </dependency>
        <dependency>
            <groupId>org.openjfx</groupId>
            <artifactId>javafx-graphics</artifactId>
            <version>${javafx.version}</version>
            <classifier>${javafx.platform}</classifier>
        </dependency>
        <dependency>
            <groupId>org.openjfx</groupId>
            <artifactId>javafx-fxml</artifactId>
            <version>${javafx.version}</version>
            <classifier>${javafx.platform}</classifier>
        </dependency>
        <dependency>
            <groupId>org.openjfx</groupId>
            <artifactId>javafx-media</artifactId>
            <version>${javafx.version}</version>
            <classifier>${javafx.platform}</classifier>
        </dependency>

        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter-api</artifactId>
            <version>${junit.version}</version>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter-engine</artifactId>
            <version>${junit.version}</version>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.13.0</version>
                <configuration>
                    <source>23</source>
                    <target>23</target>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>3.2.5</version>
                <configuration>
                    <useModulePath>false</useModulePath>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.openjfx</groupId>
                <artifactId>javafx-maven-plugin</artifactId>
                <version>0.0.8</version>
                <executions>
                    <execution>
                        <!-- Default configuration for running with: mvn clean javafx:run -->
                        <id>default-cli</id>
                        <configuration>
                            <mainClass>com.comp2042.app.Main</mainClass>
                            <launcher>app</launcher>
                            <jlinkZipName>app</jlinkZipName>
                            <jlinkImageName>app</jlinkImageName>
                            <noManPages>true</noManPages>
                            <stripDebug>true</stripDebug>
                            <noHeaderFiles>true</noHeaderFiles>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>



================================================
FILE: docs/AI Usage Log file.docx
================================================
[Binary file]


================================================
FILE: docs/ARCHITECTURE_ja.md
================================================
[Binary file]


================================================
FILE: docs/guideline-core-mechanics-summary-2025-11-11.md
================================================
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

## Input Settings (DAS / ARR / SDF / BGM)
- The Start Menu now has a **Settings** button that opens a JavaFX dialog to tune DAS delay (ms), ARR interval (ms), and the Soft Drop multiplier.
- Key bindings for move/rotate/drop/new game can be remapped in the same dialog; values are saved under `~/.tetrisjfx/settings.properties` so they persist across launches.
- GUI movement respects these settings: DAS governs the delay before repeats, ARR controls repeat cadence, and the soft drop multiplier accelerates `DOWN` key auto-shift.
- Background music (menu/gameplay) can be toggled via the same dialog; the selection is persisted so muted sessions stay quiet across restarts. Menu/Game themes use free audio from [Pixabay](https://pixabay.com/ja/music/search/tetris/), the Game Over jingle comes from [Pixabay SFX](https://pixabay.com/sound-effects/game-over-38511/), and the line-clear effect uses [Pop 94319](https://pixabay.com/sound-effects/game-over-38511/). All fall under Pixabay’s Content License (credit optional, but noted here).

## Level Progression
- Level increases every 10 cleared lines; the HUD now displays the current level.
- Each level shortens the gravity interval following a curve (L1≈400 ms → L10≈100 ms, capped at 80 ms from L12 onward), so pieces fall faster as the run continues.
- Soft-drop speed scales with the live gravity value, so tuning DAS/ARR still feels consistent as the game accelerates.

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



================================================
FILE: src/main/java/com/comp2042/app/HelpDialogController.java
================================================
package com.comp2042.app;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Window;

/**
 * Controller for the reusable help dialog.
 */
public class HelpDialogController {

    @FXML
    private TextArea helpContentArea;

    @FXML
    private Button closeButton;

    public void setContent(String markdown) {
        if (helpContentArea != null) {
            helpContentArea.setText(markdown != null ? markdown : "");
            helpContentArea.positionCaret(0);
        }
    }

    @FXML
    private void onClose(ActionEvent event) {
        if (closeButton != null) {
            Window window = closeButton.getScene() != null ? closeButton.getScene().getWindow() : null;
            if (window != null) {
                window.hide();
            }
        }
    }
}



================================================
FILE: src/main/java/com/comp2042/app/HelpDialogService.java
================================================
package com.comp2042.app;

import com.comp2042.config.ResourceManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Utility for displaying the help dialog from any controller.
 */
public final class HelpDialogService {

    private HelpDialogService() {
    }

    public static void showHelp(Stage owner, String markdown) {
        try {
            FXMLLoader loader = new FXMLLoader(ResourceManager.getUrl(ResourceManager.Asset.HELP_DIALOG_FXML));
            Parent root = loader.load();
            HelpDialogController controller = loader.getController();
            controller.setContent(markdown);

            Stage dialog = new Stage();
            dialog.setTitle("Help & Controls");
            if (owner != null) {
                dialog.initOwner(owner);
            }
            dialog.initModality(Modality.WINDOW_MODAL);
            dialog.setScene(new Scene(root, 520, 600));
            dialog.showAndWait();
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to load help dialog.", ex);
        }
    }
}



================================================
FILE: src/main/java/com/comp2042/app/Main.java
================================================
package com.comp2042.app;

import com.comp2042.config.ResourceManager;
import com.comp2042.game.GameConfig;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Map;

import javafx.scene.paint.Color;

/**
 * JavaFX entry point that loads the start menu and wires it to the primary
 * stage.
 */
public class Main extends Application {

    /**
     * Bootstraps the start menu scene and hands the primary stage to the
     * controller.
     *
     * @param primaryStage the stage created by the JavaFX runtime
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        GameConfig config = parseGameConfig();
        URL startMenu = ResourceManager.getUrl(ResourceManager.Asset.START_MENU_FXML);
        FXMLLoader fxmlLoader = new FXMLLoader(startMenu);
        Parent root = fxmlLoader.load();
        StartMenuController controller = fxmlLoader.getController();
        controller.setPrimaryStage(primaryStage);
        controller.setGameConfig(config);

        primaryStage.setTitle("TetrisJFX");
        Scene scene = new Scene(root, StartMenuController.MENU_WINDOW_WIDTH, StartMenuController.MENU_WINDOW_HEIGHT);
        scene.setFill(Color.web("#04060b"));
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private GameConfig parseGameConfig() {
        Map<String, String> namedParams = getParameters() != null ? getParameters().getNamed() : Map.of();
        String seedValue = namedParams.get("seed");
        if (seedValue == null) {
            return GameConfig.defaultConfig();
        }
        try {
            GameConfig config = GameConfig.fromSeedParameter(seedValue);
            System.out.println("[Game] Using deterministic seed: " + seedValue);
            return config;
        } catch (IllegalArgumentException ex) {
            System.err.println("[Game] Invalid --seed value '" + seedValue + "'. Falling back to random bag.");
            return GameConfig.defaultConfig();
        }
    }
}



================================================
FILE: src/main/java/com/comp2042/app/ModeSelectController.java
================================================
package com.comp2042.app;

import com.comp2042.game.GameConfig;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

/**
 * Modal controller that lets the player pick a {@link GameConfig.GameMode}.
 * Mirrors the Gemini-provided card layout so the JavaFX scene matches the design.
 */
public class ModeSelectController {

    @FXML
    private VBox modeList;

    @FXML
    private Label detailTitle;

    @FXML
    private Label detailDescription;

    @FXML
    private Label detailMeta;

    @FXML
    private Button startModeButton;

    private final Map<GameConfig.GameMode, VBox> modeCards = new EnumMap<>(GameConfig.GameMode.class);
    private Stage dialogStage;
    private GameConfig.GameMode selectedMode;
    private boolean confirmed;

    @FXML
    public void initialize() {
        createModeCards();
        updateDetail(null);
        startModeButton.setDisable(true);
    }

    private void createModeCards() {
        if (modeList == null) {
            return;
        }
        for (GameConfig.GameMode mode : GameConfig.GameMode.values()) {
            VBox card = buildCard(mode);
            modeCards.put(mode, card);
            modeList.getChildren().add(card);
        }
    }

    private VBox buildCard(GameConfig.GameMode mode) {
        ModeInfo info = ModeInfo.forMode(mode);

        Label title = new Label(info.title());
        title.getStyleClass().add("mode-card-title");

        Label summary = new Label(info.description());
        summary.setWrapText(true);
        summary.getStyleClass().add("mode-card-description");

        HBox badges = new HBox(8);
        badges.getStyleClass().add("mode-badges");
        for (String badge : info.badges()) {
            Label badgeLabel = new Label(badge);
            badgeLabel.getStyleClass().add("mode-badge");
            badges.getChildren().add(badgeLabel);
        }

        VBox card = new VBox(10, title, summary, badges);
        card.getStyleClass().add("mode-card");
        card.setFocusTraversable(true);
        card.setOnMouseClicked(event -> selectMode(mode));
        card.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.SPACE || event.getCode() == KeyCode.ENTER) {
                selectMode(mode);
            }
        });
        return card;
    }

    private void selectMode(GameConfig.GameMode mode) {
        selectedMode = mode;
        modeCards.forEach((candidate, card) -> {
            if (candidate == mode) {
                if (!card.getStyleClass().contains("selected")) {
                    card.getStyleClass().add("selected");
                }
            } else {
                card.getStyleClass().remove("selected");
            }
        });
        updateDetail(mode);
        startModeButton.setDisable(false);
    }

    private void updateDetail(GameConfig.GameMode mode) {
        if (mode == null) {
            detailTitle.setText("Select a mode to view details");
            detailDescription.setText("Each ruleset tunes speed ramps and objectives.");
            detailMeta.setText("");
            return;
        }
        ModeInfo info = ModeInfo.forMode(mode);
        detailTitle.setText(info.title());
        detailDescription.setText(info.longDescription());
        detailMeta.setText(info.meta());
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setInitialMode(GameConfig.GameMode mode) {
        if (mode == null) {
            return;
        }
        VBox card = modeCards.get(mode);
        if (card != null) {
            selectMode(mode);
        }
    }

    @FXML
    private void onCancel() {
        confirmed = false;
        if (dialogStage != null) {
            dialogStage.close();
        }
    }

    @FXML
    private void onConfirm() {
        confirmed = true;
        if (dialogStage != null) {
            dialogStage.close();
        }
    }

    public Optional<GameConfig.GameMode> getResult() {
        if (confirmed) {
            return Optional.ofNullable(selectedMode);
        }
        return Optional.empty();
    }

    private record ModeInfo(String title, String description, String longDescription, String meta, String[] badges) {

        static ModeInfo forMode(GameConfig.GameMode mode) {
            return switch (mode) {
                case ENDLESS -> new ModeInfo(
                        "Endless Marathon",
                        "Survive as long as you can; speed ramps every 10 lines.",
                        "Classic endless rules. Variable gravity keeps tension rising the longer you last.",
                        "Objective: Clear as many lines as possible.",
                        new String[]{"Relaxed", "Scaling Speed"}
                );
                case TIMED -> new ModeInfo(
                        "Timed Sprint",
                        "Score as much as possible before the 180s timer expires.",
                        "Combos and T-Spins are king in sprint. Stay aggressive and keep the board low.",
                        "Objective: Highest score within 180 seconds.",
                        new String[]{"180s", "High Pressure"}
                );
                case FIXED_LINES -> new ModeInfo(
                        "Fixed Lines",
                        "Clear exactly 40 lines with the best efficiency.",
                        "Speedrunner favourite. Trim misdrops quickly to keep splits green.",
                        "Objective: Reach 40 lines fast.",
                        new String[]{"40 Lines", "Speedrun"}
                );
            };
        }
    }
}



================================================
FILE: src/main/java/com/comp2042/app/SettingsController.java
================================================
package com.comp2042.app;

import com.comp2042.config.GameSettings;
import com.comp2042.config.GameSettingsStore;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Optional;

/**
 * Controller for the settings dialog that tunes DAS/ARR/SDF and key bindings.
 */
public class SettingsController {

    @FXML
    private TextField dasField;
    @FXML
    private TextField arrField;
    @FXML
    private TextField sdfField;
    @FXML
    private TextField leftKeyField;
    @FXML
    private TextField rightKeyField;
    @FXML
    private TextField downKeyField;
    @FXML
    private TextField hardDropKeyField;
    @FXML
    private TextField rotateCwField;
    @FXML
    private TextField rotateCcwField;
    @FXML
    private TextField newGameKeyField;
    @FXML
    private Label statusLabel;
    @FXML
    private Label infoLabel;
    @FXML
    private CheckBox bgmCheckBox;
    @FXML
    private Slider bgmVolumeSlider;
    @FXML
    private ComboBox<GameSettings.ColorAssistMode> colorAssistCombo;
    @FXML
    private CheckBox outlineCheckBox;

    private final Map<GameSettings.Action, TextField> keyFields = new EnumMap<>(GameSettings.Action.class);
    private final GameSettingsStore store = new GameSettingsStore();
    private final Map<String, String> infoMessages = Map.ofEntries(
            Map.entry("info-das", "Delayed Auto Shift (DAS): delay before a held move key begins repeating. Larger values feel heavier."),
            Map.entry("info-arr", "Auto Repeat Rate (ARR): interval between repeated moves after DAS. Smaller values slide pieces faster."),
            Map.entry("info-sdf", "Soft Drop multiplier: scales how quickly the piece falls while holding the soft drop key."),
            Map.entry("info-move-left", "Move Left: shifts the active piece one column to the left."),
            Map.entry("info-move-right", "Move Right: shifts the active piece one column to the right."),
            Map.entry("info-soft-drop", "Soft Drop: manually speeds up the descent without forcing a lock."),
            Map.entry("info-hard-drop", "Hard Drop: drops the piece instantly to the ghost position and locks it."),
            Map.entry("info-rotate-cw", "Rotate CW: clockwise rotation (90° to the right)."),
            Map.entry("info-rotate-ccw", "Rotate CCW: counter-clockwise rotation (90° to the left)."),
            Map.entry("info-new-game", "New Game: resets the playfield immediately and starts a new run."),
            Map.entry("info-color-assist", "Use High Contrast to apply bold palettes and stripe patterns tailored for common color vision deficiencies."),
            Map.entry("info-outline", "Adds a dark outline around pieces and previews so shapes remain readable regardless of fill colour.")
    );

    private GameSettings initialSettings = GameSettings.defaultSettings();
    private GameSettings resultSettings;
    private Stage dialogStage;

    @FXML
    private void initialize() {
        wireKeyField(leftKeyField, GameSettings.Action.MOVE_LEFT);
        wireKeyField(rightKeyField, GameSettings.Action.MOVE_RIGHT);
        wireKeyField(downKeyField, GameSettings.Action.SOFT_DROP);
        wireKeyField(hardDropKeyField, GameSettings.Action.HARD_DROP);
        wireKeyField(rotateCwField, GameSettings.Action.ROTATE_CW);
        wireKeyField(rotateCcwField, GameSettings.Action.ROTATE_CCW);
        wireKeyField(newGameKeyField, GameSettings.Action.NEW_GAME);
        populateFields();
        if (infoLabel != null) {
            infoLabel.setWrapText(true);
            infoLabel.setMaxWidth(Double.MAX_VALUE);
            infoLabel.setText("Select a ? icon to learn what each setting means.");
        }
        if (bgmVolumeSlider != null) {
            bgmVolumeSlider.setMin(0);
            bgmVolumeSlider.setMax(100);
        }
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setInitialSettings(GameSettings settings) {
        this.initialSettings = settings != null ? settings : GameSettings.defaultSettings();
        populateFields();
    }

    public Optional<GameSettings> getResult() {
        return Optional.ofNullable(resultSettings);
    }

    @FXML
    private void onSave(ActionEvent event) {
        try {
            GameSettings settings = collectSettings();
            store.save(settings);
            resultSettings = settings;
            if (dialogStage != null) {
                dialogStage.close();
            }
        } catch (IllegalArgumentException ex) {
            statusLabel.setText(ex.getMessage());
        }
    }

    @FXML
    private void onCancel(ActionEvent event) {
        resultSettings = null;
        if (dialogStage != null) {
            dialogStage.close();
        }
    }

    @FXML
    private void onResetDefaults(ActionEvent event) {
        GameSettings defaults = GameSettings.defaultSettings();
        setInitialSettings(defaults);
        statusLabel.setText("Default values restored.");
    }

    private void populateFields() {
        if (dasField == null) {
            return;
        }
        dasField.setText(Long.toString(initialSettings.getDasDelayMs()));
        arrField.setText(Long.toString(initialSettings.getArrIntervalMs()));
        sdfField.setText(Double.toString(initialSettings.getSoftDropMultiplier()));
        if (bgmCheckBox != null) {
            bgmCheckBox.setSelected(initialSettings.isBgmEnabled());
        }
        if (bgmVolumeSlider != null) {
            bgmVolumeSlider.setValue(initialSettings.getBgmVolume() * 100.0);
        }
        if (colorAssistCombo != null) {
            if (colorAssistCombo.getItems().isEmpty()) {
                colorAssistCombo.getItems().addAll(GameSettings.ColorAssistMode.values());
            }
            colorAssistCombo.setValue(initialSettings.getColorAssistMode());
        }
        if (outlineCheckBox != null) {
            outlineCheckBox.setSelected(initialSettings.isPieceOutlineEnabled());
        }
        for (Map.Entry<GameSettings.Action, TextField> entry : keyFields.entrySet()) {
            KeyCode keyCode = initialSettings.getKey(entry.getKey());
            entry.getValue().setText(keyCode != null ? keyCode.name() : "");
        }
        statusLabel.setText("");
    }

    private GameSettings collectSettings() {
        GameSettings.Builder builder = GameSettings.builder();
        builder.setDasDelayMs(parseLongField(dasField, "DAS"));
        builder.setArrIntervalMs(parseLongField(arrField, "ARR"));
        builder.setSoftDropMultiplier(parseDoubleField(sdfField, "Soft Drop"));
        builder.setBgmEnabled(bgmCheckBox == null || bgmCheckBox.isSelected());
        if (bgmVolumeSlider != null) {
            builder.setBgmVolume(bgmVolumeSlider.getValue() / 100.0);
        }
        GameSettings.ColorAssistMode assistMode = colorAssistCombo != null && colorAssistCombo.getValue() != null
                ? colorAssistCombo.getValue()
                : GameSettings.ColorAssistMode.CLASSIC;
        builder.setColorAssistMode(assistMode);
        builder.setPieceOutlineEnabled(outlineCheckBox != null && outlineCheckBox.isSelected());
        EnumSet<KeyCode> usedKeys = EnumSet.noneOf(KeyCode.class);
        for (Map.Entry<GameSettings.Action, TextField> entry : keyFields.entrySet()) {
            KeyCode keyCode = parseKey(entry.getValue(), entry.getKey().name());
            if (!usedKeys.add(keyCode)) {
                throw new IllegalArgumentException("キー設定が重複しています: " + keyCode.name());
            }
            builder.setKey(entry.getKey(), keyCode);
        }
        return builder.build();
    }

    private long parseLongField(TextField field, String label) {
        try {
            return Long.parseLong(field.getText().trim());
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(label + " は数値で指定してください。");
        }
    }

    private double parseDoubleField(TextField field, String label) {
        try {
            double value = Double.parseDouble(field.getText().trim());
            if (value <= 0) {
                throw new IllegalArgumentException(label + " は正の数で指定してください。");
            }
            return value;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(label + " は数値で指定してください。");
        }
    }

    private KeyCode parseKey(TextField field, String label) {
        String text = field.getText();
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException(label + " のキー設定が空です。");
        }
        try {
            return KeyCode.valueOf(text.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(label + " のキー名が不正です。");
        }
    }

    private void wireKeyField(TextField field, GameSettings.Action action) {
        keyFields.put(action, field);
        field.setEditable(false);
        field.setFocusTraversable(true);
        field.setOnMouseClicked(event -> field.clear());
        field.setOnKeyPressed(event -> {
            field.setText(event.getCode().name());
            event.consume();
        });
    }

    @FXML
    private void showInfo(ActionEvent event) {
        Object source = event.getSource();
        if (!(source instanceof Button button)) {
            return;
        }
        String message = infoMessages.getOrDefault(button.getId(), "");
        infoLabel.setText(message);
    }
}



================================================
FILE: src/main/java/com/comp2042/app/StartMenuController.java
================================================
package com.comp2042.app;

import com.comp2042.audio.BackgroundMusicManager;
import com.comp2042.config.GameSettings;
import com.comp2042.config.GameSettingsStore;
import com.comp2042.config.ResourceManager;
import com.comp2042.game.GameConfig;
import com.comp2042.game.GameController;
import com.comp2042.game.GameState;
import com.comp2042.game.stats.HighScoreEntry;
import com.comp2042.game.stats.HighScoreService;
import com.comp2042.help.HelpContentProvider;
import com.comp2042.ui.GuiController;
import com.comp2042.ui.anim.TetrisLogoView;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javafx.stage.Modality;

/**
 * Controller for the start menu. Handles navigation into the gameplay scene.
 */
public class StartMenuController {

    public static final double MENU_WINDOW_WIDTH = 1024;
    public static final double MENU_WINDOW_HEIGHT = 720;
    public static final double GAME_WINDOW_WIDTH = 1120;
    public static final double GAME_WINDOW_HEIGHT = 760;

    private Stage primaryStage;
    private GameConfig gameConfig = GameConfig.defaultConfig();
    private final GameSettingsStore settingsStore = new GameSettingsStore();
    private GameSettings gameSettings = settingsStore.load();
    private final BackgroundMusicManager musicManager = BackgroundMusicManager.getInstance();
    private final HighScoreService highScoreService = new HighScoreService();
    private final HelpContentProvider helpContentProvider = HelpContentProvider.getInstance();

    @FXML
    private VBox leaderboardSections;

    @FXML
    private Button clearScoresButton;
    @FXML
    private StackPane logoContainer;

    private final Map<GameConfig.GameMode, VBox> leaderboardLists = new EnumMap<>(GameConfig.GameMode.class);
    private final Map<GameConfig.GameMode, Label> leaderboardPlaceholders = new EnumMap<>(GameConfig.GameMode.class);
    private TetrisLogoView logoView;

    @FXML
    public void initialize() {
        buildLeaderboardSections();
        refreshLeaderboard();
        setupAnimatedLogo();
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setResizable(false);
        primaryStage.setMinWidth(MENU_WINDOW_WIDTH);
        primaryStage.setMinHeight(MENU_WINDOW_HEIGHT);
        primaryStage.setMaxWidth(MENU_WINDOW_WIDTH);
        primaryStage.setMaxHeight(MENU_WINDOW_HEIGHT);
        musicManager.setEnabled(gameSettings.isBgmEnabled());
        musicManager.setMasterVolume(gameSettings.getBgmVolume());
        musicManager.playMenuTheme();
    }

    private void setupAnimatedLogo() {
        if (logoContainer == null) {
            System.out.println("WARNING: logoContainer is null!");
            return;
        }
        System.out.println("Setting up animated logo...");
        logoView = new TetrisLogoView("TETRIS FX");
        logoContainer.getChildren().setAll(logoView);
        System.out.println("Logo view added to container, calling play()...");
        logoView.play();
        System.out.println("Animation started!");
    }

    private void stopLogoAnimation() {
        if (logoView != null) {
            logoView.stop();
        }
    }

    @FXML
    private void onStart(ActionEvent event) {
        GameConfig.GameMode selected = promptModeSelection();
        if (selected == null) {
            return;
        }
        gameConfig = gameConfig.withMode(selected);
        ensurePrimaryStageBound();
        stopLogoAnimation();
        try {
            URL layoutUrl = ResourceManager.getUrl(ResourceManager.Asset.GAME_LAYOUT_FXML);
            FXMLLoader loader = new FXMLLoader(layoutUrl);
            Parent root = loader.load();
            GuiController guiController = loader.getController();
            guiController.setPrimaryStage(primaryStage);
            Scene scene = new Scene(root, GAME_WINDOW_WIDTH, GAME_WINDOW_HEIGHT);
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.setMinWidth(GAME_WINDOW_WIDTH);
            primaryStage.setMinHeight(GAME_WINDOW_HEIGHT);
            primaryStage.setMaxWidth(GAME_WINDOW_WIDTH);
            primaryStage.setMaxHeight(GAME_WINDOW_HEIGHT);
            primaryStage.show();
            new GameController(guiController, gameConfig, gameSettings);
            guiController.setGameState(GameState.PLAYING);
            musicManager.playGameTheme();
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to load game layout.", ex);
        }
    }

    @FXML
    private void onExit(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    private void onSettings(ActionEvent event) {
        ensurePrimaryStageBound();
        try {
            URL settingsUrl = ResourceManager.getUrl(ResourceManager.Asset.SETTINGS_DIALOG_FXML);
            FXMLLoader loader = new FXMLLoader(settingsUrl);
            Parent root = loader.load();
            SettingsController controller = loader.getController();
            Stage dialog = new Stage();
            dialog.setTitle("Settings");
            dialog.initOwner(primaryStage);
            dialog.initModality(Modality.WINDOW_MODAL);
            controller.setDialogStage(dialog);
            controller.setInitialSettings(gameSettings);
            Scene scene = new Scene(root, 960, 640);
            scene.setFill(Color.web("#04060b"));
            dialog.setScene(scene);
            dialog.showAndWait();
            controller.getResult().ifPresent(result -> {
                gameSettings = result;
                musicManager.setEnabled(gameSettings.isBgmEnabled());
                musicManager.setMasterVolume(gameSettings.getBgmVolume());
                if (gameSettings.isBgmEnabled()) {
                    musicManager.playMenuTheme();
                }
            });
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to load settings dialog.", ex);
        }
    }

    @FXML
    private void onClearScores(ActionEvent event) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Clear Leaderboard");
        confirmation.setHeaderText("Remove all stored high scores?");
        confirmation.setContentText("This action cannot be undone.");
        if (primaryStage != null) {
            confirmation.initOwner(primaryStage);
        }
        Optional<ButtonType> result = confirmation.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            highScoreService.clear();
            refreshLeaderboard();
        }
    }

    @FXML
    private void onHelp(ActionEvent event) {
        ensurePrimaryStageBound();
        HelpDialogService.showHelp(primaryStage, helpContentProvider.getMarkdown());
    }

    private void ensurePrimaryStageBound() {
        if (primaryStage == null) {
            throw new IllegalStateException("Primary stage has not been injected into StartMenuController.");
        }
    }

    public void setGameConfig(GameConfig gameConfig) {
        this.gameConfig = gameConfig != null ? gameConfig : GameConfig.defaultConfig();
    }

    private void refreshLeaderboard() {
        List<HighScoreEntry> entries = highScoreService.fetchLeaderboard();
        Map<GameConfig.GameMode, List<HighScoreEntry>> grouped = new EnumMap<>(GameConfig.GameMode.class);
        for (GameConfig.GameMode mode : GameConfig.GameMode.values()) {
            grouped.put(mode, new ArrayList<>());
        }
        for (HighScoreEntry entry : entries) {
            grouped.get(resolveMode(entry)).add(entry);
        }
        boolean hasAny = false;
        for (GameConfig.GameMode mode : GameConfig.GameMode.values()) {
            List<HighScoreEntry> modeEntries = grouped.getOrDefault(mode, List.of());
            VBox listBox = leaderboardLists.get(mode);
            Label placeholder = leaderboardPlaceholders.get(mode);
            listBox.getChildren().clear();
            if (modeEntries.isEmpty()) {
                placeholder.setManaged(true);
                placeholder.setVisible(true);
                listBox.setManaged(false);
                listBox.setVisible(false);
            } else {
                placeholder.setManaged(false);
                placeholder.setVisible(false);
                listBox.setManaged(true);
                listBox.setVisible(true);
                int rank = 1;
                for (HighScoreEntry entry : modeEntries) {
                    Label row = new Label(formatEntry(rank++, entry));
                    row.getStyleClass().add("leaderboard-entry");
                    row.setAlignment(Pos.CENTER_LEFT);
                    row.setMaxWidth(Double.MAX_VALUE);
                    listBox.getChildren().add(row);
                }
                hasAny = true;
            }
        }
        if (clearScoresButton != null) {
            clearScoresButton.setDisable(!hasAny);
            clearScoresButton.setManaged(hasAny);
            clearScoresButton.setVisible(hasAny);
        }
    }

    private void buildLeaderboardSections() {
        if (leaderboardSections == null) {
            return;
        }
        leaderboardSections.getChildren().clear();
        leaderboardLists.clear();
        leaderboardPlaceholders.clear();
        for (GameConfig.GameMode mode : GameConfig.GameMode.values()) {
            VBox section = new VBox(8);
            section.getStyleClass().add("leaderboard-section");
            Label title = new Label(mode.toString());
            title.getStyleClass().add("leaderboard-section-title");

            VBox list = new VBox(4);
            list.getStyleClass().add("lb-list");

            Label placeholder = new Label("Play this mode to record a high score.");
            placeholder.getStyleClass().add("leaderboard-placeholder");
            placeholder.setWrapText(true);

            section.getChildren().addAll(title, list, placeholder);
            leaderboardSections.getChildren().add(section);
            leaderboardLists.put(mode, list);
            leaderboardPlaceholders.put(mode, placeholder);
        }
    }

    private GameConfig.GameMode resolveMode(HighScoreEntry entry) {
        String modeLabel = entry != null ? entry.getMode() : "";
        for (GameConfig.GameMode mode : GameConfig.GameMode.values()) {
            if (mode.toString().equalsIgnoreCase(modeLabel)) {
                return mode;
            }
        }
        return GameConfig.GameMode.ENDLESS;
    }

    private String formatEntry(int rank, HighScoreEntry entry) {
        return String.format("#%d %s pts · %s · %s",
                rank,
                entry.getScore(),
                entry.getMode(),
                entry.formattedDuration());
    }

    private GameConfig.GameMode promptModeSelection() {
        ensurePrimaryStageBound();
        try {
            URL modeSelectUrl = ResourceManager.getUrl(ResourceManager.Asset.MODE_SELECT_FXML);
            FXMLLoader loader = new FXMLLoader(modeSelectUrl);
            Parent root = loader.load();
            ModeSelectController controller = loader.getController();
            Stage dialog = new Stage();
            dialog.setTitle("Select Game Mode");
            dialog.initOwner(primaryStage);
            dialog.initModality(Modality.WINDOW_MODAL);
            controller.setDialogStage(dialog);
            controller.setInitialMode(gameConfig.getMode());
            Scene scene = new Scene(root);
            dialog.setScene(scene);
            dialog.setResizable(false);
            dialog.showAndWait();
            Optional<GameConfig.GameMode> result = controller.getResult();
            return result.orElse(null);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to load mode selection dialog.", ex);
        }
    }
}



================================================
FILE: src/main/java/com/comp2042/audio/BackgroundMusicManager.java
================================================
package com.comp2042.audio;

import com.comp2042.config.ResourceManager;
import javafx.application.Platform;
import javafx.scene.media.AudioClip;

import java.net.URL;
import java.util.Optional;

/**
 * Singleton helper that loops menu / gameplay music, exposes quick FX triggers, and gracefully handles missing assets.
 * All audio retrieval happens lazily, so missing WAV files or codec issues simply disable the respective sound instead
 * of crashing the UI. Calls are marshalled to the JavaFX thread to keep Media APIs happy.
 */
public final class BackgroundMusicManager {

    public enum Mode {
        MENU,
        GAME
    }

    private static final BackgroundMusicManager INSTANCE = new BackgroundMusicManager();
    private static final double DEFAULT_MASTER_VOLUME = 0.35;
    private static final double GAME_OVER_VOLUME_MULTIPLIER = 1.2;
    private static final double LINE_CLEAR_VOLUME_MULTIPLIER = 0.8;

    private final AudioClip menuClip;
    private final AudioClip gameClip;
    private final AudioClip gameOverClip;
    private final AudioClip lineClearClip;
    private volatile AudioClip currentClip;
    private Mode currentMode = Mode.MENU;
    private volatile boolean backgroundEnabled = true;
    private volatile double masterVolume = DEFAULT_MASTER_VOLUME;

    private BackgroundMusicManager() {
        menuClip = createLoopingClip(ResourceManager.Asset.AUDIO_MENU_THEME);
        gameClip = createLoopingClip(ResourceManager.Asset.AUDIO_GAME_THEME);
        gameOverClip = createEffectClip(ResourceManager.Asset.AUDIO_GAME_OVER, GAME_OVER_VOLUME_MULTIPLIER);
        lineClearClip = createEffectClip(ResourceManager.Asset.AUDIO_LINE_CLEAR, LINE_CLEAR_VOLUME_MULTIPLIER);
    }

    public static BackgroundMusicManager getInstance() {
        return INSTANCE;
    }

    /**
     * Enables or disables looping BGM. Disabling will stop the active clip immediately.
     *
     * @param enabled whether BGM should be audible
     */
    public void setEnabled(boolean enabled) {
        boolean wasEnabled = this.backgroundEnabled;
        this.backgroundEnabled = enabled;
        if (!enabled) {
            stopCurrent();
        } else if (!wasEnabled) {
            resumeCurrentMode();
        }
    }

    /**
     * Switches to the menu theme (looping).
     */
    public void playMenuTheme() {
        currentMode = Mode.MENU;
        playClip(menuClip);
    }

    /**
     * Switches to the gameplay loop.
     */
    public void playGameTheme() {
        currentMode = Mode.GAME;
        playClip(gameClip);
    }

    /**
     * Plays the one-shot game over jingle if available.
     */
    public void playGameOverJingle() {
        playOneShot(gameOverClip, "game over jingle");
    }

    /**
     * Plays the one-shot line clear sound if available.
     */
    public void playLineClear() {
        playOneShot(lineClearClip, "line clear sound");
    }

    /**
     * Updates the global master volume (0.0 - 1.0) and reapplies it to the cached clips.
     *
     * @param volume normalized volume
     */
    public void setMasterVolume(double volume) {
        masterVolume = Math.max(0.0, Math.min(1.0, volume));
        applyVolume(menuClip);
        applyVolume(gameClip);
        applyVolume(gameOverClip, GAME_OVER_VOLUME_MULTIPLIER);
        applyVolume(lineClearClip, LINE_CLEAR_VOLUME_MULTIPLIER);
    }

    /**
     * Stops the currently playing background loop, if any.
     */
    public void stopBackgroundMusic() {
        stopCurrent();
    }

    private void resumeCurrentMode() {
        switch (currentMode) {
            case GAME -> playClip(gameClip);
            case MENU -> playClip(menuClip);
            default -> {
            }
        }
    }

    private void playClip(AudioClip clip) {
        if (!backgroundEnabled || clip == null) {
            return;
        }
        runOnFxThread(() -> {
            try {
                AudioClip activeClip = currentClip;
                if (activeClip != null && activeClip != clip) {
                    activeClip.stop();
                }
                currentClip = clip;
                if (!clip.isPlaying()) {
                    clip.play();
                }
            } catch (Exception e) {
                System.err.println("[Audio] Failed to switch BGM: " + e.getMessage());
            }
        });
    }

    private void playOneShot(AudioClip clip, String clipLabel) {
        if (clip == null) {
            return;
        }
        runOnFxThread(() -> {
            try {
                clip.stop();
                clip.play();
            } catch (Exception e) {
                System.err.println("[Audio] Failed to play " + clipLabel + ": " + e.getMessage());
            }
        });
    }

    private void stopCurrent() {
        AudioClip clip = currentClip;
        if (clip == null) {
            return;
        }
        runOnFxThread(() -> {
            try {
                clip.stop();
            } catch (Exception e) {
                System.err.println("[Audio] Failed to stop BGM: " + e.getMessage());
            } finally {
                if (currentClip == clip) {
                    currentClip = null;
                }
            }
        });
    }

    private AudioClip createLoopingClip(ResourceManager.Asset asset) {
        return createClip(asset, AudioClip.INDEFINITE, 1.0);
    }

    private AudioClip createEffectClip(ResourceManager.Asset asset, double volumeMultiplier) {
        return createClip(asset, 1, volumeMultiplier);
    }

    private AudioClip createClip(ResourceManager.Asset asset, int cycles, double multiplier) {
        Optional<URL> resource = ResourceManager.findUrl(asset);
        if (resource.isEmpty()) {
            System.err.println("[Audio] Missing resource: " + asset.path());
            return null;
        }
        try {
            AudioClip clip = new AudioClip(resource.get().toExternalForm());
            clip.setCycleCount(cycles);
            applyVolume(clip, multiplier);
            return clip;
        } catch (RuntimeException ex) {
            System.err.println("[Audio] Failed to load clip " + asset.path() + ": " + ex.getMessage());
            return null;
        }
    }

    private void applyVolume(AudioClip clip) {
        applyVolume(clip, 1.0);
    }

    private void applyVolume(AudioClip clip, double multiplier) {
        if (clip == null) {
            return;
        }
        double clamped = Math.max(0.0, Math.min(1.0, masterVolume * multiplier));
        try {
            clip.setVolume(clamped);
        } catch (Exception ex) {
            System.err.println("[Audio] Failed to apply volume: " + ex.getMessage());
        }
    }

    private void runOnFxThread(Runnable action) {
        if (action == null) {
            return;
        }
        if (Platform.isFxApplicationThread()) {
            action.run();
        } else {
            Platform.runLater(action);
        }
    }
}



================================================
FILE: src/main/java/com/comp2042/board/Board.java
================================================
package com.comp2042.board;

import com.comp2042.game.Score;
import com.comp2042.logic.bricks.TetrominoType;

/**
 * Abstraction over the playfield state that exposes the operations required by {@link com.comp2042.game.GameLogic}.
 */
public interface Board {

    boolean moveBrickDown();

    boolean moveBrickLeft();

    boolean moveBrickRight();

    boolean rotateClockwise();

    boolean rotateCounterClockwise();

    boolean createNewBrick();

    int[][] getBoardMatrix();

    ViewData getViewData();

    void mergeBrickToBackground();

    ClearRow clearRows();

    Score getScore();

    void newGame();

    TetrominoType getActiveTetrominoType();

    int calculateDropDistance();
}



================================================
FILE: src/main/java/com/comp2042/board/BrickRotator.java
================================================
package com.comp2042.board;

import com.comp2042.logic.bricks.Brick;
import com.comp2042.logic.bricks.TetrominoType;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class BrickRotator {

    private static final int ORIENTATION_COUNT = 4;
    private static final Point[][][] STANDARD_KICKS = new Point[ORIENTATION_COUNT][ORIENTATION_COUNT][];
    private static final Point[][][] I_KICKS = new Point[ORIENTATION_COUNT][ORIENTATION_COUNT][];

    static {
        registerStandard(0, 1, points(
                0, 0,
                -1, 0,
                -1, 1,
                0, -2,
                -1, -2
        ));
        registerStandard(1, 0, points(
                0, 0,
                1, 0,
                1, -1,
                0, 2,
                1, 2
        ));
        registerStandard(1, 2, points(
                0, 0,
                1, 0,
                1, -1,
                0, 2,
                1, 2
        ));
        registerStandard(2, 1, points(
                0, 0,
                -1, 0,
                -1, 1,
                0, -2,
                -1, -2
        ));
        registerStandard(2, 3, points(
                0, 0,
                1, 0,
                1, 1,
                0, -2,
                1, -2
        ));
        registerStandard(3, 2, points(
                0, 0,
                -1, 0,
                -1, -1,
                0, 2,
                -1, 2
        ));
        registerStandard(3, 0, points(
                0, 0,
                -1, 0,
                -1, -1,
                0, 2,
                -1, 2
        ));
        registerStandard(0, 3, points(
                0, 0,
                1, 0,
                1, 1,
                0, -2,
                1, -2
        ));

        registerIKicks(0, 1, points(
                0, 0,
                -2, 0,
                1, 0,
                -2, -1,
                1, 2
        ));
        registerIKicks(1, 0, points(
                0, 0,
                2, 0,
                -1, 0,
                2, 1,
                -1, -2
        ));
        registerIKicks(1, 2, points(
                0, 0,
                -1, 0,
                2, 0,
                -1, 2,
                2, -1
        ));
        registerIKicks(2, 1, points(
                0, 0,
                1, 0,
                -2, 0,
                1, -2,
                -2, 1
        ));
        registerIKicks(2, 3, points(
                0, 0,
                2, 0,
                -1, 0,
                2, 1,
                -1, -2
        ));
        registerIKicks(3, 2, points(
                0, 0,
                -2, 0,
                1, 0,
                -2, -1,
                1, 2
        ));
        registerIKicks(3, 0, points(
                0, 0,
                1, 0,
                -2, 0,
                1, -2,
                -2, 1
        ));
        registerIKicks(0, 3, points(
                0, 0,
                -1, 0,
                2, 0,
                -1, 2,
                2, -1
        ));
    }

    private List<int[][]> rotations = new ArrayList<>();
    private TetrominoType type = TetrominoType.O;
    private int currentShape = 0;

    public NextShapeInfo getNextShapeClockwise() {
        return createNextShape(1);
    }

    public NextShapeInfo getNextShapeCounterClockwise() {
        return createNextShape(-1);
    }

    public int[][] getCurrentShape() {
        return MatrixOperations.copy(getShapeForOrientation(currentShape));
    }

    public void setCurrentShape(int currentShape) {
        this.currentShape = ((currentShape % ORIENTATION_COUNT) + ORIENTATION_COUNT) % ORIENTATION_COUNT;
    }

    public void setBrick(Brick brick) {
        this.rotations = brick.getShapeMatrix();
        this.type = brick.getType();
        currentShape = 0;
    }

    private int[][] getShapeForOrientation(int orientation) {
        if (rotations.isEmpty()) {
            return new int[0][0];
        }
        int index = Math.floorMod(orientation, rotations.size());
        return rotations.get(index);
    }

    public TetrominoType getType() {
        return type;
    }

    private NextShapeInfo createNextShape(int delta) {
        int from = currentShape;
        int to = Math.floorMod(currentShape + delta, ORIENTATION_COUNT);
        int[][] rotatedShape = MatrixOperations.copy(getShapeForOrientation(to));
        Point[] kicks = getKickData(from, to);
        return new NextShapeInfo(rotatedShape, to, kicks);
    }

    private Point[] getKickData(int from, int to) {
        Point[][][] table = type == TetrominoType.I ? I_KICKS : STANDARD_KICKS;
        Point[] result = table[from][to];
        if (result == null) {
            return new Point[]{new Point(0, 0)};
        }
        return result;
    }

    private static void registerStandard(int from, int to, Point[] kicks) {
        STANDARD_KICKS[from][to] = kicks;
    }

    private static void registerIKicks(int from, int to, Point[] kicks) {
        I_KICKS[from][to] = kicks;
    }

    private static Point[] points(int... coords) {
        Point[] result = new Point[coords.length / 2];
        for (int i = 0; i < coords.length; i += 2) {
            result[i / 2] = new Point(coords[i], coords[i + 1]);
        }
        return result;
    }
}



================================================
FILE: src/main/java/com/comp2042/board/ClearRow.java
================================================
package com.comp2042.board;

public final class ClearRow {

    private final int linesRemoved;
    private final int[][] newMatrix;
    private final int scoreBonus;

    public ClearRow(int linesRemoved, int[][] newMatrix, int scoreBonus) {
        this.linesRemoved = linesRemoved;
        this.newMatrix = newMatrix;
        this.scoreBonus = scoreBonus;
    }

    public int getLinesRemoved() {
        return linesRemoved;
    }

    public int[][] getNewMatrix() {
        return MatrixOperations.copy(newMatrix);
    }

    public int getScoreBonus() {
        return scoreBonus;
    }
}



================================================
FILE: src/main/java/com/comp2042/board/MatrixOperations.java
================================================
package com.comp2042.board;

import java.awt.Point;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility methods for copying, merging, and clearing board matrices.
 */
public class MatrixOperations {

    private MatrixOperations() {
    }

    public static boolean intersect(final int[][] matrix, final int[][] brick, int x, int y) {
        if (brick == null) {
            return false;
        }
        for (int row = 0; row < brick.length; row++) {
            int[] brickRow = brick[row];
            if (brickRow == null) {
                continue;
            }
            for (int col = 0; col < brickRow.length; col++) {
                int cell = brickRow[col];
                if (cell == 0) {
                    continue;
                }
                int targetX = x + col;
                int targetY = y + row;
                if (targetY < 0) {
                    continue;
                }
                if (checkOutOfBound(matrix, targetX, targetY) || matrix[targetY][targetX] != 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean intersect(final int[][] matrix, final int[][] brick, Point offset) {
        return intersect(matrix, brick, (int) offset.getX(), (int) offset.getY());
    }

    private static boolean checkOutOfBound(int[][] matrix, int targetX, int targetY) {
        boolean returnValue = true;
        if (targetX >= 0 && targetY < matrix.length && targetX < matrix[targetY].length) {
            returnValue = false;
        }
        return returnValue;
    }

    public static int[][] copy(int[][] original) {
        int[][] myInt = new int[original.length][];
        for (int i = 0; i < original.length; i++) {
            int[] aMatrix = original[i];
            int aLength = aMatrix.length;
            myInt[i] = new int[aLength];
            System.arraycopy(aMatrix, 0, myInt[i], 0, aLength);
        }
        return myInt;
    }

    public static int[][] merge(int[][] filledFields, int[][] brick, int x, int y) {
        int[][] copy = copy(filledFields);
        if (brick == null) {
            return copy;
        }
        for (int row = 0; row < brick.length; row++) {
            int[] brickRow = brick[row];
            if (brickRow == null) {
                continue;
            }
            for (int col = 0; col < brickRow.length; col++) {
                int cell = brickRow[col];
                if (cell == 0) {
                    continue;
                }
                int targetX = x + col;
                int targetY = y + row;
                if (targetY < 0) {
                    continue;
                }
                copy[targetY][targetX] = cell;
            }
        }
        return copy;
    }

    public static ClearRow checkRemoving(final int[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        int[][] tmp = new int[rows][cols];
        int writeRow = rows - 1;
        int cleared = 0;

        for (int source = rows - 1; source >= 0; source--) {
            boolean rowToClear = true;
            for (int col = 0; col < cols; col++) {
                if (matrix[source][col] == 0) {
                    rowToClear = false;
                    break;
                }
            }
            if (rowToClear) {
                cleared++;
            } else {
                System.arraycopy(matrix[source], 0, tmp[writeRow], 0, cols);
                writeRow--;
            }
        }

        while (writeRow >= 0) {
            Arrays.fill(tmp[writeRow], 0);
            writeRow--;
        }

        int scoreBonus = 50 * cleared * cleared;
        return new ClearRow(cleared, tmp, scoreBonus);
    }

    public static List<int[][]> deepCopyList(List<int[][]> list){
        return list.stream().map(MatrixOperations::copy).collect(Collectors.toList());
    }

}



================================================
FILE: src/main/java/com/comp2042/board/NextShapeInfo.java
================================================
package com.comp2042.board;

import java.awt.Point;

public final class NextShapeInfo {

    private final int[][] shape;
    private final int position;
    private final Point[] kicks;

    public NextShapeInfo(final int[][] shape, final int position, final Point[] kicks) {
        this.shape = shape;
        this.position = position;
        this.kicks = kicks.clone();
    }

    public int[][] getShape() {
        return MatrixOperations.copy(shape);
    }

    public int getPosition() {
        return position;
    }

    public Point[] getKicks() {
        Point[] copy = new Point[kicks.length];
        for (int i = 0; i < kicks.length; i++) {
            Point source = kicks[i];
            copy[i] = new Point(source);
        }
        return copy;
    }
}



================================================
FILE: src/main/java/com/comp2042/board/SimpleBoard.java
================================================
package com.comp2042.board;

import com.comp2042.game.Score;
import com.comp2042.logic.bricks.Brick;
import com.comp2042.logic.bricks.BrickGenerator;
import com.comp2042.logic.bricks.PieceGenerator;
import com.comp2042.logic.bricks.TetrominoType;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Default {@link Board} implementation that holds the matrix state, active
 * tetromino,
 * and helper objects (rotator/generator) needed for Guideline playfields.
 */
public class SimpleBoard implements Board {

    public static final int SPAWN_ROW = -1;
    public static final int GAME_OVER_ROW = 0;
    private static final int NEXT_PREVIEW_COUNT = 5;

    private final int width;
    private final int height;
    private final BrickGenerator brickGenerator;
    private final BrickRotator brickRotator;
    private int[][] currentGameMatrix;
    private Point currentOffset;
    private final Score score;

    public SimpleBoard(int width, int height) {
        this(width, height, new PieceGenerator());
    }

    public SimpleBoard(int width, int height, BrickGenerator brickGenerator) {
        this.width = width;
        this.height = height;
        this.brickGenerator = brickGenerator != null ? brickGenerator : new PieceGenerator();
        this.currentGameMatrix = new int[height][width];
        this.brickRotator = new BrickRotator();
        this.score = new Score();
    }

    @Override
    public boolean moveBrickDown() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(currentOffset);
        p.translate(0, 1);
        boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(),
                (int) p.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            return true;
        }
    }

    @Override
    public boolean moveBrickLeft() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(currentOffset);
        p.translate(-1, 0);
        boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(),
                (int) p.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            return true;
        }
    }

    @Override
    public boolean moveBrickRight() {
        int[][] currentMatrix = MatrixOperations.copy(currentGameMatrix);
        Point p = new Point(currentOffset);
        p.translate(1, 0);
        boolean conflict = MatrixOperations.intersect(currentMatrix, brickRotator.getCurrentShape(), (int) p.getX(),
                (int) p.getY());
        if (conflict) {
            return false;
        } else {
            currentOffset = p;
            return true;
        }
    }

    @Override
    public boolean rotateClockwise() {
        return attemptRotation(brickRotator.getNextShapeClockwise());
    }

    @Override
    public boolean rotateCounterClockwise() {
        return attemptRotation(brickRotator.getNextShapeCounterClockwise());
    }

    @Override
    public boolean createNewBrick() {
        Brick currentBrick = brickGenerator.getBrick();
        brickRotator.setBrick(currentBrick);
        int spawnColumn = Math.max(0, (currentGameMatrix[0].length / 2) - 1);
        currentOffset = new Point(spawnColumn, SPAWN_ROW);
        return MatrixOperations.intersect(currentGameMatrix, brickRotator.getCurrentShape(), (int) currentOffset.getX(),
                (int) currentOffset.getY());
    }

    @Override
    public int[][] getBoardMatrix() {
        return currentGameMatrix;
    }

    @Override
    public ViewData getViewData() {
        int ghostY = (int) currentOffset.getY() + calculateDropDistance();
        return new ViewData(
                brickRotator.getCurrentShape(),
                (int) currentOffset.getX(),
                (int) currentOffset.getY(),
                ghostY,
                collectNextPreview(),
                GAME_OVER_ROW);
    }

    @Override
    public void mergeBrickToBackground() {
        currentGameMatrix = MatrixOperations.merge(currentGameMatrix, brickRotator.getCurrentShape(),
                (int) currentOffset.getX(), (int) currentOffset.getY());
    }

    @Override
    public ClearRow clearRows() {
        ClearRow clearRow = MatrixOperations.checkRemoving(currentGameMatrix);
        currentGameMatrix = clearRow.getNewMatrix();
        return clearRow;

    }

    @Override
    public Score getScore() {
        return score;
    }

    @Override
    public void newGame() {
        currentGameMatrix = new int[height][width];
        score.reset();
        brickGenerator.reset();
        createNewBrick();
    }

    @Override
    public TetrominoType getActiveTetrominoType() {
        return brickRotator.getType();
    }

    @Override
    public int calculateDropDistance() {
        if (currentOffset == null) {
            return 0;
        }
        Point probe = new Point(currentOffset);
        int distance = 0;
        while (true) {
            probe.translate(0, 1);
            boolean conflict = MatrixOperations.intersect(currentGameMatrix, brickRotator.getCurrentShape(), probe);
            if (conflict) {
                break;
            }
            distance++;
        }
        return distance;
    }

    private List<int[][]> collectNextPreview() {
        return brickGenerator.peekUpcoming(NEXT_PREVIEW_COUNT)
                .stream()
                .map(brick -> {
                    List<int[][]> rotations = brick.getShapeMatrix();
                    if (rotations.isEmpty()) {
                        return new int[0][0];
                    }
                    return MatrixOperations.copy(rotations.get(0));
                })
                .collect(Collectors.toList());
    }

    private boolean attemptRotation(NextShapeInfo rotationInfo) {
        int[][] rotatedShape = rotationInfo.getShape();
        Point[] kicks = rotationInfo.getKicks();
        for (Point kick : kicks) {
            Point candidateOffset = new Point(currentOffset);
            candidateOffset.translate((int) kick.getX(), (int) kick.getY());
            boolean conflict = MatrixOperations.intersect(currentGameMatrix, rotatedShape, candidateOffset);
            if (!conflict) {
                brickRotator.setCurrentShape(rotationInfo.getPosition());
                currentOffset = candidateOffset;
                return true;
            }
        }
        return false;
    }
}



================================================
FILE: src/main/java/com/comp2042/board/ViewData.java
================================================
package com.comp2042.board;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class ViewData {

    private final int[][] brickData;
    private final int xPosition;
    private final int yPosition;
    private final int ghostYPosition;
    private final List<int[][]> nextBrickData;
    private final int gameOverRow;

    public ViewData(int[][] brickData,
                    int xPosition,
                    int yPosition,
                    int ghostYPosition,
                    List<int[][]> nextBrickData,
                    int gameOverRow) {
        this.brickData = brickData;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.ghostYPosition = ghostYPosition;
        this.nextBrickData = nextBrickData == null ? Collections.emptyList() : nextBrickData;
        this.gameOverRow = gameOverRow;
    }

    public int[][] getBrickData() {
        return MatrixOperations.copy(brickData);
    }

    public int getxPosition() {
        return xPosition;
    }

    public int getyPosition() {
        return yPosition;
    }

    public int getGhostYPosition() {
        return ghostYPosition;
    }

    public List<int[][]> getNextBricksData() {
        return nextBrickData.stream()
                .map(MatrixOperations::copy)
                .collect(Collectors.toList());
    }

    public int getGameOverRow() {
        return gameOverRow;
    }
}



================================================
FILE: src/main/java/com/comp2042/config/GameSettings.java
================================================
package com.comp2042.config;

import javafx.scene.input.KeyCode;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/**
 * Immutable snapshot capturing the player's gameplay preferences (key bindings, DAS/ARR tuning, audio options).
 * <p>
 * Instances are produced via the {@link Builder} to guarantee sensible defaults and simple cloning into a mutable
 * instance before persisting with {@link GameSettingsStore}.
 * </p>
 */
public final class GameSettings {

    /**
     * Color palette presets exposed in the settings dialog.
     */
    public enum ColorAssistMode {
        CLASSIC("Classic"),
        HIGH_CONTRAST("High Contrast");

        private final String displayName;

        ColorAssistMode(String displayName) {
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }

    /**
     * Logical key-bindable actions supported by the UI.
     */
    public enum Action {
        MOVE_LEFT,
        MOVE_RIGHT,
        SOFT_DROP,
        HARD_DROP,
        ROTATE_CW,
        ROTATE_CCW,
        NEW_GAME
    }

    private final long dasDelayMs;
    private final long arrIntervalMs;
    private final double softDropMultiplier;
    private final EnumMap<Action, KeyCode> keyBindings;
    private final boolean bgmEnabled;
    private final double bgmVolume;
    private final ColorAssistMode colorAssistMode;
    private final boolean pieceOutlineEnabled;

    private GameSettings(Builder builder) {
        this.dasDelayMs = builder.dasDelayMs;
        this.arrIntervalMs = builder.arrIntervalMs;
        this.softDropMultiplier = builder.softDropMultiplier;
        this.keyBindings = new EnumMap<>(builder.keyBindings);
        this.bgmEnabled = builder.bgmEnabled;
        this.bgmVolume = builder.bgmVolume;
        this.colorAssistMode = builder.colorAssistMode;
        this.pieceOutlineEnabled = builder.pieceOutlineEnabled;
    }

    /**
     * Creates a new builder with guideline defaults.
     *
     * @return builder pre-populated with safe defaults
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Shortcut to obtain a default, immutable settings object.
     *
     * @return default gameplay settings
     */
    public static GameSettings defaultSettings() {
        return builder().build();
    }

    public long getDasDelayMs() {
        return dasDelayMs;
    }

    public long getArrIntervalMs() {
        return arrIntervalMs;
    }

    public double getSoftDropMultiplier() {
        return softDropMultiplier;
    }

    public KeyCode getKey(Action action) {
        return keyBindings.get(action);
    }

    public Map<Action, KeyCode> getKeyBindings() {
        return Collections.unmodifiableMap(keyBindings);
    }

    public boolean isBgmEnabled() {
        return bgmEnabled;
    }

    public double getBgmVolume() {
        return bgmVolume;
    }

    public ColorAssistMode getColorAssistMode() {
        return colorAssistMode;
    }

    public boolean isPieceOutlineEnabled() {
        return pieceOutlineEnabled;
    }

    /**
     * Creates a new builder seeded from this immutable instance so callers can adjust a copy.
     *
     * @return mutable builder seeded with the current values
     */
    public Builder toBuilder() {
        return builder().from(this);
    }

    /**
     * Mutable builder with validation steps on every setter to guarantee sane persisted values.
     */
    public static final class Builder {

        private long dasDelayMs = 220;
        private long arrIntervalMs = 60;
        private double softDropMultiplier = 8.0;
        private final EnumMap<Action, KeyCode> keyBindings = new EnumMap<>(Action.class);
        private boolean bgmEnabled = true;
        private double bgmVolume = 0.35;
        private ColorAssistMode colorAssistMode = ColorAssistMode.CLASSIC;
        private boolean pieceOutlineEnabled = false;

        private Builder() {
            keyBindings.put(Action.MOVE_LEFT, KeyCode.LEFT);
            keyBindings.put(Action.MOVE_RIGHT, KeyCode.RIGHT);
            keyBindings.put(Action.SOFT_DROP, KeyCode.DOWN);
            keyBindings.put(Action.HARD_DROP, KeyCode.SPACE);
            keyBindings.put(Action.ROTATE_CW, KeyCode.UP);
            keyBindings.put(Action.ROTATE_CCW, KeyCode.Z);
            keyBindings.put(Action.NEW_GAME, KeyCode.N);
        }

        private Builder from(GameSettings settings) {
            if (settings == null) {
                return this;
            }
            setDasDelayMs(settings.dasDelayMs);
            setArrIntervalMs(settings.arrIntervalMs);
            setSoftDropMultiplier(settings.softDropMultiplier);
            keyBindings.clear();
            keyBindings.putAll(settings.keyBindings);
            setBgmEnabled(settings.isBgmEnabled());
            setBgmVolume(settings.getBgmVolume());
            setColorAssistMode(settings.getColorAssistMode());
            setPieceOutlineEnabled(settings.isPieceOutlineEnabled());
            return this;
        }

        /**
         * Sets the delay before horizontal auto-shift begins.
         *
         * @param dasDelayMs duration in milliseconds; clamped to non-negative values
         * @return self for chaining
         */
        public Builder setDasDelayMs(long dasDelayMs) {
            this.dasDelayMs = Math.max(0, dasDelayMs);
            return this;
        }

        /**
         * Sets the auto-repeat interval for horizontal movement.
         *
         * @param arrIntervalMs duration in milliseconds; clamped to non-negative values
         * @return self for chaining
         */
        public Builder setArrIntervalMs(long arrIntervalMs) {
            this.arrIntervalMs = Math.max(0, arrIntervalMs);
            return this;
        }

        /**
         * Controls the multiplier applied to soft-drop gravity.
         *
         * @param softDropMultiplier multiplier, clamped to 0.1 or higher
         * @return self for chaining
         */
        public Builder setSoftDropMultiplier(double softDropMultiplier) {
            this.softDropMultiplier = Math.max(0.1, softDropMultiplier);
            return this;
        }

        /**
         * Overrides the key binding for the requested logical action.
         *
         * @param action logical action
         * @param keyCode javaFX key code
         * @return self for chaining
         */
        public Builder setKey(Action action, KeyCode keyCode) {
            keyBindings.put(Objects.requireNonNull(action), Objects.requireNonNull(keyCode));
            return this;
        }

        /**
         * Enables or disables background music playback.
         *
         * @param enabled true when BGM should play
         * @return self for chaining
         */
        public Builder setBgmEnabled(boolean enabled) {
            this.bgmEnabled = enabled;
            return this;
        }

        /**
         * Sets the normalized background music volume (0.0 - 1.0).
         *
         * @param volume normalized volume; clamped into range
         * @return self for chaining
         */
        public Builder setBgmVolume(double volume) {
            this.bgmVolume = Math.max(0.0, Math.min(1.0, volume));
            return this;
        }

        /**
         * Selects the color-assist preset.
         *
         * @param mode selected color mode
         * @return self for chaining
         */
        public Builder setColorAssistMode(ColorAssistMode mode) {
            this.colorAssistMode = mode != null ? mode : ColorAssistMode.CLASSIC;
            return this;
        }

        /**
         * Toggles the visible outline that helps distinguish pieces on similar backgrounds.
         *
         * @param enabled true to show outlines
         * @return self for chaining
         */
        public Builder setPieceOutlineEnabled(boolean enabled) {
            this.pieceOutlineEnabled = enabled;
            return this;
        }

        /**
         * Produces the immutable snapshot.
         *
         * @return immutable {@link GameSettings}
         */
        public GameSettings build() {
            return new GameSettings(this);
        }
    }
}



================================================
FILE: src/main/java/com/comp2042/config/GameSettingsStore.java
================================================
package com.comp2042.config;

import javafx.scene.input.KeyCode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.OptionalDouble;
import java.util.OptionalLong;
import java.util.Properties;

/**
 * Persists {@link GameSettings} to a portable properties file (defaults to {@code ~/.tetrisjfx/settings.properties}).
 * <p>
 * Tests and power users can override the storage location with the following system properties (highest priority first):
 * </p>
 * <ol>
 *     <li>{@code tetris.settings.file} &mdash; explicit path to the file</li>
 *     <li>{@code tetris.settings.dir} &mdash; directory that will contain {@code settings.properties}</li>
 *     <li>{@code tetris.data.dir} &mdash; shared data directory used by both settings and high scores</li>
 * </ol>
 * Writes are performed via a temporary file rename so half-written files are avoided on crash or power loss.
 */
public class GameSettingsStore {

    public static final String SETTINGS_FILE_PROPERTY = "tetris.settings.file";
    public static final String SETTINGS_DIR_PROPERTY = "tetris.settings.dir";
    public static final String DATA_DIR_PROPERTY = "tetris.data.dir";

    private static final String FILE_NAME = "settings.properties";
    private static final String DEFAULT_DIRECTORY = ".tetrisjfx";
    private static final String KEY_PREFIX = "key.";
    private static final String COLOR_ASSIST_KEY = "colorAssistMode";
    private static final String OUTLINE_KEY = "outlineEnabled";

    private final Path baseDirectory;
    private final Path settingsFile;

    public GameSettingsStore() {
        this(resolveLocation());
    }

    GameSettingsStore(Path baseDirectory) {
        this(baseDirectory, baseDirectory.resolve(FILE_NAME));
    }

    GameSettingsStore(Path baseDirectory, Path explicitFile) {
        this(new Location(baseDirectory, explicitFile));
    }

    private GameSettingsStore(Location location) {
        this.baseDirectory = location.baseDir;
        this.settingsFile = location.file;
    }

    /**
     * Loads persisted settings or falls back to {@link GameSettings#defaultSettings()} if the file is missing
     * or unreadable.
     *
     * @return in-memory immutable settings snapshot
     */
    public GameSettings load() {
        if (!Files.exists(settingsFile)) {
            return GameSettings.defaultSettings();
        }
        Properties properties = new Properties();
        try (InputStream in = Files.newInputStream(settingsFile)) {
            properties.load(in);
            return fromProperties(properties);
        } catch (IOException ex) {
            System.err.println("[Settings] Failed to read " + settingsFile + ". Using defaults.");
            return GameSettings.defaultSettings();
        }
    }

    /**
     * Writes the provided settings atomically to the properties file, creating the target directory if necessary.
     *
     * @param settings snapshot to persist
     */
    public void save(GameSettings settings) {
        if (settings == null) {
            return;
        }
        Properties properties = toProperties(settings);
        Path tempFile = null;
        try {
            Files.createDirectories(baseDirectory);
            tempFile = Files.createTempFile(baseDirectory, FILE_NAME, ".tmp");
            try (OutputStream out = Files.newOutputStream(tempFile)) {
                properties.store(out, "TetrisJFX gameplay settings");
            }
            moveAtomically(tempFile, settingsFile);
        } catch (IOException ex) {
            System.err.println("[Settings] Failed to write " + settingsFile + ": " + ex.getMessage());
        } finally {
            if (tempFile != null) {
                try {
                    Files.deleteIfExists(tempFile);
                } catch (IOException ignored) {
                    // Nothing else we can do.
                }
            }
        }
    }

    private GameSettings fromProperties(Properties properties) {
        GameSettings.Builder builder = GameSettings.builder();
        parseLong(properties.getProperty("dasDelayMs")).ifPresent(builder::setDasDelayMs);
        parseLong(properties.getProperty("arrIntervalMs")).ifPresent(builder::setArrIntervalMs);
        parseDouble(properties.getProperty("softDropMultiplier")).ifPresent(builder::setSoftDropMultiplier);
        String bgmValue = properties.getProperty("bgmEnabled");
        if (bgmValue != null) {
            builder.setBgmEnabled(Boolean.parseBoolean(bgmValue));
        }
        parseDouble(properties.getProperty("bgmVolume")).ifPresent(builder::setBgmVolume);
        String assistValue = properties.getProperty(COLOR_ASSIST_KEY);
        if (assistValue != null) {
            try {
                builder.setColorAssistMode(GameSettings.ColorAssistMode.valueOf(assistValue.trim()));
            } catch (IllegalArgumentException ignored) {
                builder.setColorAssistMode(GameSettings.ColorAssistMode.CLASSIC);
            }
        }
        String outlineValue = properties.getProperty(OUTLINE_KEY);
        if (outlineValue != null) {
            builder.setPieceOutlineEnabled(Boolean.parseBoolean(outlineValue));
        }
        for (GameSettings.Action action : GameSettings.Action.values()) {
            String value = properties.getProperty(KEY_PREFIX + action.name());
            if (value == null || value.isBlank()) {
                continue;
            }
            try {
                builder.setKey(action, KeyCode.valueOf(value.trim().toUpperCase(Locale.ROOT)));
            } catch (IllegalArgumentException ignored) {
                // Ignore invalid key names and fall back to defaults.
            }
        }
        return builder.build();
    }

    private Properties toProperties(GameSettings settings) {
        Properties properties = new Properties();
        properties.setProperty("dasDelayMs", Long.toString(settings.getDasDelayMs()));
        properties.setProperty("arrIntervalMs", Long.toString(settings.getArrIntervalMs()));
        properties.setProperty("softDropMultiplier", Double.toString(settings.getSoftDropMultiplier()));
        properties.setProperty("bgmEnabled", Boolean.toString(settings.isBgmEnabled()));
        properties.setProperty("bgmVolume", Double.toString(settings.getBgmVolume()));
        properties.setProperty(COLOR_ASSIST_KEY, settings.getColorAssistMode().name());
        properties.setProperty(OUTLINE_KEY, Boolean.toString(settings.isPieceOutlineEnabled()));
        for (var entry : settings.getKeyBindings().entrySet()) {
            properties.setProperty(KEY_PREFIX + entry.getKey().name(), entry.getValue().name());
        }
        return properties;
    }

    private static OptionalLong parseLong(String value) {
        if (value == null) {
            return OptionalLong.empty();
        }
        try {
            return OptionalLong.of(Long.parseLong(value.trim()));
        } catch (NumberFormatException ex) {
            return OptionalLong.empty();
        }
    }

    private static OptionalDouble parseDouble(String value) {
        if (value == null) {
            return OptionalDouble.empty();
        }
        try {
            return OptionalDouble.of(Double.parseDouble(value.trim()));
        } catch (NumberFormatException ex) {
            return OptionalDouble.empty();
        }
    }

    /**
     * Returns the resolved base directory so callers (tests) can assert the location.
     *
     * @return directory containing the settings file
     */
    public Path getBaseDirectory() {
        return baseDirectory;
    }

    /**
     * Returns the resolved settings file which may be overridden by system properties.
     *
     * @return path to the backing file
     */
    public Path getSettingsFile() {
        return settingsFile;
    }

    private static void moveAtomically(Path source, Path target) throws IOException {
        try {
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException ex) {
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private static Location resolveLocation() {
        String fileOverride = System.getProperty(SETTINGS_FILE_PROPERTY);
        if (fileOverride != null && !fileOverride.isBlank()) {
            Path file = Paths.get(fileOverride.trim()).toAbsolutePath().normalize();
            Path parent = file.getParent();
            if (parent == null) {
                parent = file.toAbsolutePath().getParent();
            }
            return new Location(parent == null ? Paths.get(".").toAbsolutePath().normalize() : parent, file);
        }
        Path baseDir = resolveBaseDirectory();
        return new Location(baseDir, baseDir.resolve(FILE_NAME));
    }

    /**
     * Finds the settings directory either via system overrides or defaults to {@code ~/.tetrisjfx}.
     */
    private static Path resolveBaseDirectory() {
        String overrideDir = System.getProperty(SETTINGS_DIR_PROPERTY);
        if (overrideDir != null && !overrideDir.isBlank()) {
            return Paths.get(overrideDir.trim()).toAbsolutePath().normalize();
        }
        String dataDir = System.getProperty(DATA_DIR_PROPERTY);
        if (dataDir != null && !dataDir.isBlank()) {
            return Paths.get(dataDir.trim()).toAbsolutePath().normalize();
        }
        String userHome = System.getProperty("user.home", ".");
        return Paths.get(userHome, DEFAULT_DIRECTORY).toAbsolutePath().normalize();
    }

    private static final class Location {
        private final Path baseDir;
        private final Path file;

        private Location(Path baseDir, Path file) {
            this.baseDir = (baseDir == null ? Paths.get(".") : baseDir).toAbsolutePath().normalize();
            if (file == null) {
                this.file = this.baseDir.resolve(FILE_NAME);
            } else {
                this.file = file.toAbsolutePath().normalize();
            }
        }
    }
}



================================================
FILE: src/main/java/com/comp2042/config/ResourceManager.java
================================================
package com.comp2042.config;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;

/**
 * Central registry for application resources (FXML, fonts, markdown, audio).
 * <p>
 * Callers can request strongly typed {@link Asset}s or look up arbitrary classpath paths. Each accessor provides both
 * a safe optional variant (for optional audio/markdown) and a fail-fast variant (for mandatory layouts).
 * </p>
 */
public final class ResourceManager {

    /**
     * Known assets bundled inside {@code src/main/resources} and resolved relative to the classpath root.
     */
    public enum Asset {
        START_MENU_FXML("StartMenu.fxml"),
        MODE_SELECT_FXML("ModeSelect.fxml"),
        GAME_LAYOUT_FXML("gameLayout.fxml"),
        SETTINGS_DIALOG_FXML("SettingsDialog.fxml"),
        HELP_DIALOG_FXML("HelpDialog.fxml"),
        DIGITAL_FONT("digital.ttf"),
        HELP_MARKDOWN("help/help-content.md"),
        AUDIO_MENU_THEME("audio/menu_theme.wav"),
        AUDIO_GAME_THEME("audio/game_theme.wav"),
        AUDIO_GAME_OVER("audio/game_over.wav"),
        AUDIO_LINE_CLEAR("audio/line_clear.wav");

        private final String path;

        Asset(String path) {
            this.path = normalize(path);
        }

        /**
         * @return normalized resource path relative to the classpath root
         */
        public String path() {
            return path;
        }
    }

    private static final ClassLoader LOADER = ResourceManager.class.getClassLoader();

    private ResourceManager() {
    }

    /**
     * Attempts to resolve the raw resource path (useful for assets defined outside {@link Asset}).
     *
     * @param relativePath path relative to {@code src/main/resources}
     * @return optional URL if the resource exists
     */
    public static Optional<URL> findUrl(String relativePath) {
        if (relativePath == null || relativePath.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(LOADER.getResource(normalize(relativePath)));
    }

    /**
     * Returns an optional URL for the provided asset without throwing if the asset is missing.
     *
     * @param asset logical asset descriptor
     * @return optional URL that resolves to the resource
     */
    public static Optional<URL> findUrl(Asset asset) {
        Objects.requireNonNull(asset, "asset");
        return findUrl(asset.path());
    }

    /**
     * Returns a URL for the resource or fails fast with a descriptive exception if absent.
     *
     * @param asset logical asset descriptor
     * @return URL pointing to the asset
     */
    public static URL getUrl(Asset asset) {
        return findUrl(asset)
                .orElseThrow(() -> new IllegalStateException("Missing resource: " + asset.path()));
    }

    /**
     * Returns the external-form string for use with APIs that require textual URLs (e.g. Font loader).
     *
     * @param asset logical asset descriptor
     * @return external-form URL string
     */
    public static String getExternalForm(Asset asset) {
        return getUrl(asset).toExternalForm();
    }

    /**
     * Opens the resource as an {@link InputStream}, failing fast if the asset cannot be located.
     *
     * @param asset logical asset descriptor
     * @return readable input stream
     */
    public static InputStream openStream(Asset asset) {
        return openOptionalStream(asset)
                .orElseThrow(() -> new IllegalStateException("Missing resource: " + asset.path()));
    }

    /**
     * Opens the resource as an {@link InputStream} if available.
     *
     * @param asset logical asset descriptor
     * @return optional input stream which must be closed by the caller
     */
    public static Optional<InputStream> openOptionalStream(Asset asset) {
        Objects.requireNonNull(asset, "asset");
        return Optional.ofNullable(LOADER.getResourceAsStream(asset.path()));
    }

    /**
     * Reads the entire resource into UTF-8 text which is handy for Markdown or JSON fixtures.
     *
     * @param asset logical asset descriptor
     * @return optional text contents
     */
    public static Optional<String> readText(Asset asset) {
        Optional<InputStream> optional = openOptionalStream(asset);
        if (optional.isEmpty()) {
            return Optional.empty();
        }
        try (InputStream in = optional.get()) {
            return Optional.of(new String(in.readAllBytes(), StandardCharsets.UTF_8));
        } catch (IOException ex) {
            System.err.println("[ResourceManager] Failed to read " + asset.path() + ": " + ex.getMessage());
            return Optional.empty();
        }
    }

    private static String normalize(String path) {
        String trimmed = path == null ? "" : path.strip();
        if (trimmed.startsWith("/")) {
            return trimmed.substring(1);
        }
        return trimmed;
    }
}



================================================
FILE: src/main/java/com/comp2042/game/GameConfig.java
================================================
package com.comp2042.game;

import java.util.OptionalLong;

/**
 * Holds runtime configuration toggles (currently the deterministic seed option).
 */
public final class GameConfig {

    public enum GameMode {
        ENDLESS("Endless"),
        TIMED("Timed (180s)"),
        FIXED_LINES("Fixed 40 lines");

        private final String displayLabel;

        GameMode(String displayLabel) {
            this.displayLabel = displayLabel;
        }

        @Override
        public String toString() {
            return displayLabel;
        }
    }

    private final Long seedOverride;
    private final GameMode mode;

    private GameConfig(Long seedOverride, GameMode mode) {
        this.seedOverride = seedOverride;
        this.mode = mode != null ? mode : GameMode.ENDLESS;
    }

    public static GameConfig defaultConfig() {
        return new GameConfig(null, GameMode.ENDLESS);
    }

    public static GameConfig fromSeedParameter(String rawSeed) {
        if (rawSeed == null || rawSeed.isBlank()) {
            return defaultConfig();
        }
        try {
            return new GameConfig(Long.parseLong(rawSeed.trim()), GameMode.ENDLESS);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Seed must be a signed long, but got: " + rawSeed, ex);
        }
    }

    public OptionalLong seedOverride() {
        return seedOverride != null ? OptionalLong.of(seedOverride) : OptionalLong.empty();
    }

    public boolean hasSeedOverride() {
        return seedOverride != null;
    }

    public GameMode getMode() {
        return mode;
    }

    public GameConfig withMode(GameMode newMode) {
        return new GameConfig(seedOverride, newMode);
    }
}



================================================
FILE: src/main/java/com/comp2042/game/GameController.java
================================================
package com.comp2042.game;

import com.comp2042.board.Board;
import com.comp2042.board.SimpleBoard;
import com.comp2042.board.ViewData;
import com.comp2042.config.GameSettings;
import com.comp2042.game.events.DownData;
import com.comp2042.game.events.InputEventListener;
import com.comp2042.game.events.MoveEvent;
import com.comp2042.logic.bricks.PieceGenerator;
import com.comp2042.ui.GuiController;

/**
 * Wires {@link GuiController} input callbacks to {@link GameLogic} and keeps
 * the board/score in sync.
 */
public class GameController implements InputEventListener {

    private final GuiController viewGuiController;
    private final GameConfig config;
    private final PieceGenerator generator;
    private final Board board;
    private final GameLogic logic;

    public GameController(GuiController c, GameConfig config, GameSettings settings) {
        viewGuiController = c;
        this.config = config != null ? config : GameConfig.defaultConfig();
        GameSettings safeSettings = settings != null ? settings : GameSettings.defaultSettings();
        generator = this.config.seedOverride().isPresent()
                ? new PieceGenerator(this.config.seedOverride().getAsLong())
                : new PieceGenerator();
        board = new SimpleBoard(10, 22, generator);
        logic = new GameLogic(board);
        board.createNewBrick();
        viewGuiController.setEventListener(this);
        viewGuiController.setGameSettings(safeSettings);
        viewGuiController.setActiveGameConfig(this.config);
        viewGuiController.setGameMode(this.config.getMode());
        viewGuiController.initGameView(board.getBoardMatrix(), board.getViewData());
        viewGuiController.bindScore(logic.getScore());
        viewGuiController.prepareModeSession();
        publishSeedSummary();
    }

    @Override
    public DownData onDownEvent(MoveEvent event) {
        if (viewGuiController.getGameState() != GameState.PLAYING) {
            return new DownData(null, board.getViewData());
        }
        DownData downData = logic.moveDown(event);
        syncBoardState();
        return downData;
    }

    @Override
    public ViewData onLeftEvent(MoveEvent event) {
        if (viewGuiController.getGameState() != GameState.PLAYING) {
            return board.getViewData();
        }
        ViewData data = logic.moveLeft();
        syncBoardState();
        return data;
    }

    @Override
    public ViewData onRightEvent(MoveEvent event) {
        if (viewGuiController.getGameState() != GameState.PLAYING) {
            return board.getViewData();
        }
        ViewData data = logic.moveRight();
        syncBoardState();
        return data;
    }

    @Override
    public ViewData onRotateClockwise(MoveEvent event) {
        if (viewGuiController.getGameState() != GameState.PLAYING) {
            return board.getViewData();
        }
        ViewData data = logic.rotateClockwise();
        syncBoardState();
        return data;
    }

    @Override
    public ViewData onRotateCounterClockwise(MoveEvent event) {
        if (viewGuiController.getGameState() != GameState.PLAYING) {
            return board.getViewData();
        }
        ViewData data = logic.rotateCounterClockwise();
        syncBoardState();
        return data;
    }

    @Override
    public DownData onHardDrop(MoveEvent event) {
        if (viewGuiController.getGameState() != GameState.PLAYING) {
            return new DownData(null, board.getViewData());
        }
        DownData data = logic.hardDrop(event);
        syncBoardState();
        return data;
    }

    @Override
    public ViewData createNewGame() {
        ViewData viewData = logic.newGame();
        publishSeedSummary();
        syncBoardState();
        return viewData;
    }

    private void syncBoardState() {
        if (logic.consumeBoardRefreshFlag()) {
            viewGuiController.refreshGameBackground(board.getBoardMatrix());
        }
        if (logic.consumeGameOverFlag()) {
            logSeedForReplay();
            viewGuiController.gameOver();
        }
    }

    private void publishSeedSummary() {
        long seed = generator.getCurrentSeed();
        boolean deterministic = config.hasSeedOverride();
        viewGuiController.updateSeedInfo(seed, deterministic);
        System.out.println("[Game] Session seed: " + seed + (deterministic ? " (fixed)" : ""));
    }

    private void logSeedForReplay() {
        long seed = generator.getCurrentSeed();
        System.out.println("[Game] Game over. Replay this run with --seed=" + seed);
    }
}



================================================
FILE: src/main/java/com/comp2042/game/GameLogic.java
================================================
package com.comp2042.game;

import com.comp2042.board.Board;
import com.comp2042.board.ClearRow;
import com.comp2042.board.MatrixOperations;
import com.comp2042.board.ViewData;
import com.comp2042.game.events.DownData;
import com.comp2042.game.events.EventSource;
import com.comp2042.game.events.MoveEvent;
import com.comp2042.logic.bricks.TetrominoType;

/**
 * Encapsulates gameplay mechanics (movement, lock delay, scoring) so the controller
 * can remain focused on UI orchestration.
 */
public class GameLogic {

    private static final int LOCK_DELAY_TICKS = 2;
    private static final int LOCK_RESET_CAP = 5;
    private static final int HARD_DROP_POINTS_PER_CELL = 2;

    private enum LastAction {
        NONE,
        MOVE,
        ROTATE
    }

    private final Board board;
    private int lockDelayCounter = LOCK_DELAY_TICKS;
    private boolean lockPending = false;
    private boolean boardRefreshRequested = false;
    private boolean gameOverTriggered = false;
    private int lockResetsRemaining = LOCK_RESET_CAP;
    private LastAction lastAction = LastAction.NONE;

    public GameLogic(Board board) {
        this.board = board;
    }

    public DownData moveDown(MoveEvent event) {
        boolean canMove = board.moveBrickDown();
        ClearRow clearRow = null;
        if (!canMove) {
            clearRow = handleLockDelay();
        } else {
            if (event.getEventSource() == EventSource.USER) {
                board.getScore().add(1);
            }
            resetLockDelayState();
            lastAction = LastAction.MOVE;
        }
        return new DownData(clearRow, board.getViewData());
    }

    public ViewData moveLeft() {
        if (board.moveBrickLeft()) {
            refreshLockDelayIfGrounded();
            lastAction = LastAction.MOVE;
        }
        return board.getViewData();
    }

    public ViewData moveRight() {
        if (board.moveBrickRight()) {
            refreshLockDelayIfGrounded();
            lastAction = LastAction.MOVE;
        }
        return board.getViewData();
    }

    public ViewData rotateClockwise() {
        if (board.rotateClockwise()) {
            refreshLockDelayIfGrounded();
            lastAction = LastAction.ROTATE;
        }
        return board.getViewData();
    }

    public ViewData rotateCounterClockwise() {
        if (board.rotateCounterClockwise()) {
            refreshLockDelayIfGrounded();
            lastAction = LastAction.ROTATE;
        }
        return board.getViewData();
    }

    public DownData hardDrop(MoveEvent event) {
        int dropDistance = 0;
        while (board.moveBrickDown()) {
            dropDistance++;
        }
        if (dropDistance > 0) {
            board.getScore().add(dropDistance * HARD_DROP_POINTS_PER_CELL);
        }
        ClearRow clearRow = lockCurrentPiece();
        return new DownData(clearRow, board.getViewData());
    }

    public ViewData newGame() {
        board.newGame();
        resetLockDelayState();
        boardRefreshRequested = true;
        lastAction = LastAction.NONE;
        return board.getViewData();
    }

    public boolean consumeBoardRefreshFlag() {
        boolean result = boardRefreshRequested;
        boardRefreshRequested = false;
        return result;
    }

    public boolean consumeGameOverFlag() {
        boolean result = gameOverTriggered;
        gameOverTriggered = false;
        return result;
    }

    public Score getScore() {
        return board.getScore();
    }

    public Board getBoard() {
        return board;
    }

    private ClearRow handleLockDelay() {
        if (!lockPending) {
            lockPending = true;
            lockDelayCounter = LOCK_DELAY_TICKS;
            lockResetsRemaining = LOCK_RESET_CAP;
            return null;
        }
        lockDelayCounter--;
        if (lockDelayCounter <= 0) {
            return lockCurrentPiece();
        }
        return null;
    }

    private ClearRow lockCurrentPiece() {
        int[][] boardBeforeLock = MatrixOperations.copy(board.getBoardMatrix());
        ViewData activeView = board.getViewData();
        TetrominoType activeType = board.getActiveTetrominoType();

        board.mergeBrickToBackground();
        ClearRow clearRow = board.clearRows();
        LineClearStats stats = classifyLineClear(clearRow.getLinesRemoved(), activeType, activeView, boardBeforeLock);
        board.getScore().handleLineClear(stats);
        boolean newBrickConflict = board.createNewBrick();
        boardRefreshRequested = true;
        if (newBrickConflict) {
            gameOverTriggered = true;
        }
        resetLockDelayState();
        lastAction = LastAction.NONE;
        return clearRow;
    }

    private void resetLockDelayState() {
        lockPending = false;
        lockDelayCounter = LOCK_DELAY_TICKS;
        lockResetsRemaining = LOCK_RESET_CAP;
    }

    private void refreshLockDelayIfGrounded() {
        if (!lockPending || lockResetsRemaining <= 0) {
            return;
        }
        lockDelayCounter = LOCK_DELAY_TICKS;
        lockResetsRemaining--;
    }

    private LineClearStats classifyLineClear(int linesRemoved,
                                             TetrominoType activeType,
                                             ViewData activeView,
                                             int[][] boardBeforeLock) {
        if (linesRemoved <= 0 || activeView == null) {
            return new LineClearStats(LineClearType.NONE, 0);
        }
        boolean isTSpin = isTSpinClear(activeType, activeView, boardBeforeLock);
        LineClearType clearType = determineClearType(linesRemoved, isTSpin);
        return new LineClearStats(clearType, linesRemoved);
    }

    private boolean isTSpinClear(TetrominoType activeType, ViewData activeView, int[][] boardBeforeLock) {
        if (activeType != TetrominoType.T || lastAction != LastAction.ROTATE) {
            return false;
        }
        int anchorX = activeView.getxPosition() + 1;
        int anchorY = activeView.getyPosition() + 1;
        if (anchorY < 0) {
            return false;
        }
        int occupiedCorners = countOccupiedCorners(boardBeforeLock, anchorX, anchorY);
        return occupiedCorners >= 3;
    }

    private int countOccupiedCorners(int[][] boardMatrix, int anchorX, int anchorY) {
        int[][] offsets = {
                {-1, -1},
                {1, -1},
                {-1, 1},
                {1, 1}
        };
        int occupied = 0;
        for (int[] offset : offsets) {
            int x = anchorX + offset[0];
            int y = anchorY + offset[1];
            if (y < 0) {
                occupied++;
                continue;
            }
            if (x < 0 || y >= boardMatrix.length || x >= boardMatrix[y].length) {
                occupied++;
                continue;
            }
            if (boardMatrix[y][x] != 0) {
                occupied++;
            }
        }
        return occupied;
    }

    private LineClearType determineClearType(int linesRemoved, boolean isTSpin) {
        if (isTSpin) {
            switch (linesRemoved) {
                case 1:
                    return LineClearType.T_SPIN_SINGLE;
                case 2:
                    return LineClearType.T_SPIN_DOUBLE;
                case 3:
                    return LineClearType.T_SPIN_TRIPLE;
                default:
                    return LineClearType.NONE;
            }
        }
        switch (linesRemoved) {
            case 1:
                return LineClearType.SINGLE;
            case 2:
                return LineClearType.DOUBLE;
            case 3:
                return LineClearType.TRIPLE;
            case 4:
                return LineClearType.TETRIS;
            default:
                return LineClearType.NONE;
        }
    }
}



================================================
FILE: src/main/java/com/comp2042/game/GameState.java
================================================
package com.comp2042.game;

/**
 * Represents the high-level state of the Tetris game flow.
 */
public enum GameState {
    MENU,
    PLAYING,
    PAUSED,
    GAME_OVER
}



================================================
FILE: src/main/java/com/comp2042/game/LevelProgression.java
================================================
package com.comp2042.game;

import java.util.Arrays;

/**
 * Tracks Tetris guideline level progression and provides the gravity interval
 * associated with each level so GUI/control layers can remain agnostic.
 */
public final class LevelProgression {

    public static final int DEFAULT_LINES_PER_LEVEL = 10;
    public static final double DEFAULT_BASE_GRAVITY_MS = 400d;
    public static final double DEFAULT_MIN_GRAVITY_MS = 80d;
    private static final double[] DEFAULT_GRAVITY_TABLE = {
            400, 390, 380, 370, 360, 350, 340, 330, 320, 310,
            300, 290, 280, 270, 260, 250, 240, 230, 220, 210,
            200, 190, 185, 180, 175, 170, 165, 160, 155, 150,
            145, 140, 135, 130, 125, 120, 115, 110, 105, 100,
            95, 90, 85, 80
    };

    private final int linesPerLevel;
    private final double baseGravityMs;
    private final double minGravityMs;
    private final double[] gravityTable;

    private int currentLevel;
    private int linesUntilNextLevel;

    public LevelProgression() {
        this(DEFAULT_LINES_PER_LEVEL, DEFAULT_BASE_GRAVITY_MS, DEFAULT_MIN_GRAVITY_MS, DEFAULT_GRAVITY_TABLE);
    }

    public LevelProgression(int linesPerLevel,
                            double baseGravityMs,
                            double minGravityMs,
                            double[] gravityTable) {
        if (linesPerLevel <= 0) {
            throw new IllegalArgumentException("linesPerLevel must be positive");
        }
        this.linesPerLevel = linesPerLevel;
        this.baseGravityMs = baseGravityMs;
        this.minGravityMs = minGravityMs;
        this.gravityTable = gravityTable == null
                ? Arrays.copyOf(DEFAULT_GRAVITY_TABLE, DEFAULT_GRAVITY_TABLE.length)
                : Arrays.copyOf(gravityTable, gravityTable.length);
        reset();
    }

    /**
     * Resets the tracker to level 1 and returns the updated state.
     */
    public LevelState reset() {
        currentLevel = 1;
        linesUntilNextLevel = linesPerLevel;
        return snapshot(false);
    }

    /**
     * Applies the specified number of cleared lines and returns the resulting state.
     */
    public LevelState handleLinesCleared(int linesCleared) {
        if (linesCleared <= 0) {
            return snapshot(false);
        }
        linesUntilNextLevel -= linesCleared;
        boolean leveledUp = false;
        while (linesUntilNextLevel <= 0) {
            currentLevel++;
            linesUntilNextLevel += linesPerLevel;
            leveledUp = true;
        }
        return snapshot(leveledUp);
    }

    public LevelState getState() {
        return snapshot(false);
    }

    private LevelState snapshot(boolean leveledUp) {
        return new LevelState(currentLevel, linesUntilNextLevel, resolveGravityInterval(currentLevel), leveledUp);
    }

    private double resolveGravityInterval(int level) {
        if (level <= 0) {
            return baseGravityMs;
        }
        if (level > gravityTable.length) {
            return minGravityMs;
        }
        return Math.max(minGravityMs, gravityTable[level - 1]);
    }

    /**
     * Immutable view of the current level progression state.
     */
    public record LevelState(int level, int linesUntilNextLevel, double gravityIntervalMs, boolean leveledUp) {
    }
}



================================================
FILE: src/main/java/com/comp2042/game/LineClearStats.java
================================================
package com.comp2042.game;

/**
 * Immutable value object describing the outcome of a line clear.
 */
public final class LineClearStats {

    private final LineClearType clearType;
    private final int linesCleared;

    public LineClearStats(LineClearType clearType, int linesCleared) {
        this.clearType = clearType;
        this.linesCleared = linesCleared;
    }

    public LineClearType getClearType() {
        return clearType;
    }

    public int getLinesCleared() {
        return linesCleared;
    }
}



================================================
FILE: src/main/java/com/comp2042/game/LineClearType.java
================================================
package com.comp2042.game;

/**
 * Represents the classification of a line clear for scoring purposes.
 */
public enum LineClearType {
    NONE(0),
    SINGLE(100),
    DOUBLE(300),
    TRIPLE(500),
    TETRIS(800),
    T_SPIN_SINGLE(800),
    T_SPIN_DOUBLE(1200),
    T_SPIN_TRIPLE(1600);

    private final int baseScore;

    LineClearType(int baseScore) {
        this.baseScore = baseScore;
    }

    public int getBaseScore() {
        return baseScore;
    }

    public boolean isBackToBackEligible() {
        return this == TETRIS
                || this == T_SPIN_SINGLE
                || this == T_SPIN_DOUBLE
                || this == T_SPIN_TRIPLE;
    }
}



================================================
FILE: src/main/java/com/comp2042/game/Score.java
================================================
package com.comp2042.game;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Tracks score, combo, and back-to-back counters using JavaFX properties for UI binding.
 */
public final class Score {

    private static final int BASE_LINE_SCORE = 50;

    private final IntegerProperty score = new SimpleIntegerProperty(0);
    private final IntegerProperty combo = new SimpleIntegerProperty(0);
    private final IntegerProperty backToBack = new SimpleIntegerProperty(0);
    private boolean backToBackActive = false;

    public IntegerProperty scoreProperty() {
        return score;
    }

    public IntegerProperty comboProperty() {
        return combo;
    }

    public IntegerProperty backToBackProperty() {
        return backToBack;
    }

    public void add(int i){
        score.setValue(score.getValue() + i);
    }

    public void handleLineClear(LineClearStats stats) {
        if (stats == null || stats.getLinesCleared() <= 0 || stats.getClearType() == LineClearType.NONE) {
            combo.set(0);
            resetBackToBack();
            return;
        }

        combo.set(combo.get() + 1);
        updateBackToBack(stats.getClearType());
        add(calculateFlatScore(stats.getLinesCleared()));
    }

    public void reset() {
        score.setValue(0);
        combo.set(0);
        backToBack.set(0);
        backToBackActive = false;
    }

    private void resetBackToBack() {
        backToBackActive = false;
        backToBack.set(0);
    }

    private void updateBackToBack(LineClearType clearType) {
        if (clearType == null || !clearType.isBackToBackEligible()) {
            resetBackToBack();
            return;
        }
        if (backToBackActive) {
            backToBack.set(backToBack.get() + 1);
        } else {
            backToBackActive = true;
            backToBack.set(1);
        }
    }

    private int calculateFlatScore(int lines) {
        return BASE_LINE_SCORE * lines * lines;
    }
}



================================================
FILE: src/main/java/com/comp2042/game/events/DownData.java
================================================
package com.comp2042.game.events;

import com.comp2042.board.ClearRow;
import com.comp2042.board.ViewData;

public final class DownData {
    private final ClearRow clearRow;
    private final ViewData viewData;

    public DownData(ClearRow clearRow, ViewData viewData) {
        this.clearRow = clearRow;
        this.viewData = viewData;
    }

    public ClearRow getClearRow() {
        return clearRow;
    }

    public ViewData getViewData() {
        return viewData;
    }
}



================================================
FILE: src/main/java/com/comp2042/game/events/EventSource.java
================================================
package com.comp2042.game.events;

public enum EventSource {
    USER, THREAD
}



================================================
FILE: src/main/java/com/comp2042/game/events/EventType.java
================================================
package com.comp2042.game.events;

public enum EventType {
    DOWN,
    LEFT,
    RIGHT,
    ROTATE_CLOCKWISE,
    ROTATE_COUNTERCLOCKWISE,
    HARD_DROP
}



================================================
FILE: src/main/java/com/comp2042/game/events/InputEventListener.java
================================================
package com.comp2042.game.events;

import com.comp2042.board.ViewData;

public interface InputEventListener {

    DownData onDownEvent(MoveEvent event);

    ViewData onLeftEvent(MoveEvent event);

    ViewData onRightEvent(MoveEvent event);

    ViewData onRotateClockwise(MoveEvent event);

    ViewData onRotateCounterClockwise(MoveEvent event);

    DownData onHardDrop(MoveEvent event);

    ViewData createNewGame();
}



================================================
FILE: src/main/java/com/comp2042/game/events/MoveEvent.java
================================================
package com.comp2042.game.events;

public final class MoveEvent {
    private final EventType eventType;
    private final EventSource eventSource;

    public MoveEvent(EventType eventType, EventSource eventSource) {
        this.eventType = eventType;
        this.eventSource = eventSource;
    }

    public EventType getEventType() {
        return eventType;
    }

    public EventSource getEventSource() {
        return eventSource;
    }
}



================================================
FILE: src/main/java/com/comp2042/game/stats/HighScoreEntry.java
================================================
package com.comp2042.game.stats;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Immutable value object describing a single leaderboard row (score, mode, clear time, timestamp).
 * <p>
 * Instances are normally created by {@link HighScoreService} when a game completes but the class also exposes
 * {@link #recreate(int, String, long, long)} for reconstructing entries from disk.
 * </p>
 */
public final class HighScoreEntry {

    private static final DateTimeFormatter TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final int score;
    private final String mode;
    private final long durationSeconds;
    private final long recordedAtMillis;

    private HighScoreEntry(int score, String mode, long durationSeconds, long recordedAtMillis) {
        this.score = Math.max(0, score);
        this.mode = mode == null ? "Classic" : mode;
        this.durationSeconds = Math.max(0, durationSeconds);
        this.recordedAtMillis = recordedAtMillis;
    }

    /**
     * Creates a fresh entry at the current timestamp from runtime results.
     *
     * @param score            achieved score
     * @param mode             display label describing the active {@code GameMode}
     * @param duration         elapsed gameplay duration; may be {@code null}
     * @return immutable leaderboard entry
     */
    public static HighScoreEntry create(int score, String mode, Duration duration) {
        long recordedAt = System.currentTimeMillis();
        long seconds = duration != null ? Math.max(0L, duration.toSeconds()) : 0L;
        return new HighScoreEntry(score, normalizeMode(mode), seconds, recordedAt);
    }

    /**
     * Rebuilds an entry from persisted fields.
     *
     * @param score            stored score
     * @param mode             stored mode label
     * @param durationSeconds  stored duration in seconds
     * @param recordedAtMillis stored timestamp in millis since epoch
     * @return immutable leaderboard entry
     */
    public static HighScoreEntry recreate(int score, String mode, long durationSeconds, long recordedAtMillis) {
        return new HighScoreEntry(score, normalizeMode(mode), durationSeconds, recordedAtMillis);
    }

    public int getScore() {
        return score;
    }

    public String getMode() {
        return mode;
    }

    public long getDurationSeconds() {
        return durationSeconds;
    }

    public long getRecordedAtMillis() {
        return recordedAtMillis;
    }

    public Instant getRecordedAtInstant() {
        return Instant.ofEpochMilli(recordedAtMillis);
    }

    /**
     * Formats the duration as either {@code HH:mm:ss} or {@code mm:ss} depending on the length.
     *
     * @return human readable elapsed time
     */
    public String formattedDuration() {
        long totalSeconds = Math.max(0L, durationSeconds);
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        }
        return String.format("%02d:%02d", minutes, seconds);
    }

    /**
     * Formats the timestamp using the JVM's default time zone for display in the leaderboard.
     *
     * @return formatted timestamp string
     */
    public String formattedTimestamp() {
        LocalDateTime dateTime = LocalDateTime.ofInstant(getRecordedAtInstant(), ZoneId.systemDefault());
        return TIMESTAMP_FORMAT.format(dateTime);
    }

    private static String normalizeMode(String mode) {
        return (mode == null || mode.isBlank()) ? "Classic" : mode.trim();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HighScoreEntry entry)) {
            return false;
        }
        return score == entry.score
                && durationSeconds == entry.durationSeconds
                && recordedAtMillis == entry.recordedAtMillis
                && Objects.equals(mode, entry.mode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(score, mode, durationSeconds, recordedAtMillis);
    }

    @Override
    public String toString() {
        return "HighScoreEntry{" +
                "score=" + score +
                ", mode='" + mode + '\'' +
                ", durationSeconds=" + durationSeconds +
                ", recordedAtMillis=" + recordedAtMillis +
                '}';
    }
}



================================================
FILE: src/main/java/com/comp2042/game/stats/HighScoreService.java
================================================
package com.comp2042.game.stats;

import com.comp2042.game.GameConfig;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * Persists leaderboard records on the user's machine to survive new sessions.
 * <p>
 * The backing file defaults to {@code ~/.comp2042/highscores.dat} and can be overridden via:
 * </p>
 * <ol>
 *     <li>{@link #HIGHSCORES_FILE_PROPERTY} &mdash; explicit file path</li>
 *     <li>{@link #HIGHSCORES_DIR_PROPERTY} &mdash; directory that contains {@value #FILE_NAME}</li>
 *     <li>{@link #DATA_DIR_PROPERTY} &mdash; shared data directory also leveraged by {@link com.comp2042.config.GameSettingsStore}</li>
 * </ol>
 * Writes are flushed to a temporary file and renamed atomically to avoid corruption if the JVM exits mid-write.
 */
public final class HighScoreService {

    public static final String HIGHSCORES_FILE_PROPERTY = "tetris.highscores.file";
    public static final String HIGHSCORES_DIR_PROPERTY = "tetris.highscores.dir";
    public static final String DATA_DIR_PROPERTY = "tetris.data.dir";

    private static final String DEFAULT_DIRECTORY = ".comp2042";
    private static final String FILE_NAME = "highscores.dat";
    private static final int MAX_ENTRIES_PER_MODE = 10;
    private static final Comparator<HighScoreEntry> SORT_BY_SCORE =
            Comparator.comparingInt(HighScoreEntry::getScore).reversed()
                    .thenComparingLong(HighScoreEntry::getDurationSeconds)
                    .thenComparingLong(HighScoreEntry::getRecordedAtMillis);

    private final Path storagePath;

    public HighScoreService() {
        this(resolveStoragePath());
    }

    HighScoreService(Path storagePath) {
        this.storagePath = Objects.requireNonNull(storagePath).toAbsolutePath().normalize();
    }

    /**
     * Records a new entry, keeping only the top {@value #MAX_ENTRIES_PER_MODE} scores per mode.
     *
     * @param newEntry entry to persist
     * @return immutable leaderboard snapshot after insertion
     */
    public synchronized List<HighScoreEntry> recordScore(HighScoreEntry newEntry) {
        if (newEntry == null) {
            return fetchLeaderboard();
        }
        List<HighScoreEntry> entries = new ArrayList<>(readEntries());
        entries.add(newEntry);
        entries.sort(SORT_BY_SCORE);
        entries = pruneByMode(entries);
        writeEntries(entries);
        return Collections.unmodifiableList(entries);
    }

    /**
     * Convenience overload to create a {@link HighScoreEntry} from raw gameplay data.
     */
    public synchronized List<HighScoreEntry> recordScore(int score, String mode, Duration duration) {
        HighScoreEntry entry = HighScoreEntry.create(score, mode, duration);
        return recordScore(entry);
    }

    /**
     * Returns the entire leaderboard sorted by the configured comparator.
     */
    public synchronized List<HighScoreEntry> fetchLeaderboard() {
        return Collections.unmodifiableList(new ArrayList<>(readEntries()));
    }

    /**
     * Returns entries filtered by the provided mode label (string version).
     */
    public synchronized List<HighScoreEntry> fetchLeaderboardForMode(String modeLabel) {
        return fetchLeaderboardForMode(resolveModeKey(modeLabel));
    }

    /**
     * Returns entries filtered by the provided enum value.
     */
    public synchronized List<HighScoreEntry> fetchLeaderboardForMode(GameConfig.GameMode mode) {
        GameConfig.GameMode target = mode != null ? mode : GameConfig.GameMode.ENDLESS;
        List<HighScoreEntry> entries = new ArrayList<>(readEntries());
        List<HighScoreEntry> filtered = new ArrayList<>();
        for (HighScoreEntry entry : entries) {
            if (resolveModeKey(entry.getMode()) == target) {
                filtered.add(entry);
            }
        }
        return Collections.unmodifiableList(filtered);
    }

    /**
     * Deletes the leaderboard file from disk (best-effort).
     */
    public synchronized void clear() {
        try {
            Files.deleteIfExists(storagePath);
        } catch (IOException ex) {
            System.err.println("[HighScoreService] Failed to delete leaderboard file: " + ex.getMessage());
        }
    }

    /**
     * Returns the resolved storage path so diagnostics/tests can assert location.
     *
     * @return absolute path to the leaderboard file
     */
    public Path getStoragePath() {
        return storagePath;
    }

    private List<HighScoreEntry> readEntries() {
        if (!Files.exists(storagePath)) {
            return List.of();
        }
        List<HighScoreEntry> entries = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(storagePath, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                HighScoreEntry entry = parseLine(line);
                if (entry != null) {
                    entries.add(entry);
                }
            }
        } catch (IOException ex) {
            System.err.println("[HighScoreService] Failed to read leaderboard file: " + ex.getMessage());
            return List.of();
        }
        entries.sort(SORT_BY_SCORE);
        return pruneByMode(entries);
    }

    private void writeEntries(List<HighScoreEntry> entries) {
        Path parent = storagePath.getParent();
        Path tempDir = parent != null ? parent : storagePath.toAbsolutePath().getParent();
        if (tempDir == null) {
            tempDir = Paths.get(".").toAbsolutePath().normalize();
        }
        Path tempFile = null;
        try {
            Files.createDirectories(tempDir);
            tempFile = Files.createTempFile(tempDir, FILE_NAME, ".tmp");
            try (BufferedWriter writer = Files.newBufferedWriter(tempFile, StandardCharsets.UTF_8)) {
                for (HighScoreEntry entry : entries) {
                    writer.write(serialize(entry));
                    writer.newLine();
                }
            }
            moveAtomically(tempFile, storagePath);
        } catch (IOException ex) {
            System.err.println("[HighScoreService] Failed to write leaderboard file: " + ex.getMessage());
        } finally {
            if (tempFile != null) {
                try {
                    Files.deleteIfExists(tempFile);
                } catch (IOException ignored) {
                    // nothing else to do
                }
            }
        }
    }

    private static HighScoreEntry parseLine(String line) {
        if (line == null || line.isBlank()) {
            return null;
        }
        String[] parts = line.split("\\|", -1);
        if (parts.length < 4) {
            return null;
        }
        try {
            int score = Integer.parseInt(parts[0]);
            String mode = unescape(parts[1]);
            long duration = Long.parseLong(parts[2]);
            long recordedAt = Long.parseLong(parts[3]);
            return HighScoreEntry.recreate(score, mode, duration, recordedAt);
        } catch (NumberFormatException ex) {
            System.err.println("[HighScoreService] Skipping malformed record: " + line);
            return null;
        }
    }

    private static String serialize(HighScoreEntry entry) {
        return entry.getScore() + "|"
                + escape(entry.getMode()) + "|"
                + entry.getDurationSeconds() + "|"
                + entry.getRecordedAtMillis();
    }

    private static String escape(String value) {
        return value == null ? "" : value.replace("|", "/");
    }

    private static String unescape(String raw) {
        return raw == null ? "" : raw;
    }

    private static Path resolveStoragePath() {
        String overrideFile = System.getProperty(HIGHSCORES_FILE_PROPERTY);
        if (overrideFile != null && !overrideFile.isBlank()) {
            return Paths.get(overrideFile.trim()).toAbsolutePath().normalize();
        }
        Path directory = resolveStorageDirectory();
        return directory.resolve(FILE_NAME);
    }

    private static Path resolveStorageDirectory() {
        String overrideDir = System.getProperty(HIGHSCORES_DIR_PROPERTY);
        if (overrideDir != null && !overrideDir.isBlank()) {
            return Paths.get(overrideDir.trim()).toAbsolutePath().normalize();
        }
        String dataDir = System.getProperty(DATA_DIR_PROPERTY);
        if (dataDir != null && !dataDir.isBlank()) {
            return Paths.get(dataDir.trim()).toAbsolutePath().normalize();
        }
        String home = System.getProperty("user.home", ".");
        return Paths.get(home, DEFAULT_DIRECTORY).toAbsolutePath().normalize();
    }

    private static void moveAtomically(Path source, Path target) throws IOException {
        try {
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (AtomicMoveNotSupportedException ex) {
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private List<HighScoreEntry> pruneByMode(List<HighScoreEntry> entries) {
        Map<GameConfig.GameMode, Integer> perMode = new EnumMap<>(GameConfig.GameMode.class);
        List<HighScoreEntry> pruned = new ArrayList<>();
        for (HighScoreEntry entry : entries) {
            GameConfig.GameMode modeKey = resolveModeKey(entry.getMode());
            int count = perMode.getOrDefault(modeKey, 0);
            if (count < MAX_ENTRIES_PER_MODE) {
                pruned.add(entry);
                perMode.put(modeKey, count + 1);
            }
        }
        return pruned;
    }

    private GameConfig.GameMode resolveModeKey(String label) {
        if (label == null || label.isBlank()) {
            return GameConfig.GameMode.ENDLESS;
        }
        String trimmed = label.trim();
        for (GameConfig.GameMode mode : GameConfig.GameMode.values()) {
            if (mode.toString().equalsIgnoreCase(trimmed) || mode.name().equalsIgnoreCase(trimmed)) {
                return mode;
            }
        }
        String normalized = trimmed.toLowerCase(Locale.ROOT);
        if ("classic".equals(normalized) || normalized.startsWith("endless")) {
            return GameConfig.GameMode.ENDLESS;
        }
        if (normalized.startsWith("timed")) {
            return GameConfig.GameMode.TIMED;
        }
        if (normalized.startsWith("fixed") || normalized.contains("40")) {
            return GameConfig.GameMode.FIXED_LINES;
        }
        return GameConfig.GameMode.ENDLESS;
    }
}



================================================
FILE: src/main/java/com/comp2042/help/HelpContentProvider.java
================================================
package com.comp2042.help;

import com.comp2042.config.ResourceManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Loads the Markdown help content and exposes parsed shortcuts for reuse.
 */
public final class HelpContentProvider {

    private static final String SHORTCUTS_HEADING = "Shortcuts";

    private final String markdown;
    private final List<String> shortcutSummaries;

    private HelpContentProvider() {
        markdown = loadResourceMarkdown();
        shortcutSummaries = Collections.unmodifiableList(extractSection(markdown, SHORTCUTS_HEADING));
    }

    public static HelpContentProvider getInstance() {
        return Holder.INSTANCE;
    }

    public String getMarkdown() {
        return markdown;
    }

    public List<String> getShortcutSummaries() {
        return shortcutSummaries;
    }

    private static String loadResourceMarkdown() {
        try (InputStream in = ResourceManager.openStream(ResourceManager.Asset.HELP_MARKDOWN)) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line).append(System.lineSeparator());
                }
                return builder.toString().trim();
            }
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to read help content.", ex);
        }
    }

    private static List<String> extractSection(String markdown, String heading) {
        List<String> lines = new ArrayList<>();
        boolean inSection = false;
        try (BufferedReader reader = new BufferedReader(new StringReader(markdown))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                if (trimmed.startsWith("#")) {
                    String header = trimmed.replaceFirst("^#+\\s*", "").toLowerCase();
                    inSection = header.equals(heading.toLowerCase());
                    continue;
                }
                if (inSection) {
                    if (trimmed.startsWith("- ")) {
                        lines.add(trimmed.substring(2).trim());
                    } else if (!trimmed.isEmpty()) {
                        lines.add(trimmed);
                    }
                }
            }
        } catch (IOException ex) {
            return lines;
        }
        return lines;
    }

    private static final class Holder {
        private static final HelpContentProvider INSTANCE = new HelpContentProvider();
    }
}



================================================
FILE: src/main/java/com/comp2042/logic/bricks/Brick.java
================================================
package com.comp2042.logic.bricks;

import java.util.List;

public interface Brick {

    List<int[][]> getShapeMatrix();

    TetrominoType getType();
}



================================================
FILE: src/main/java/com/comp2042/logic/bricks/BrickGenerator.java
================================================
package com.comp2042.logic.bricks;

import java.util.List;

public interface BrickGenerator {

    Brick getBrick();

    Brick getNextBrick();

    List<Brick> peekUpcoming(int count);

    default void reset() {
        // Generators that support resetting can override.
    }
}



================================================
FILE: src/main/java/com/comp2042/logic/bricks/IBrick.java
================================================
package com.comp2042.logic.bricks;

import com.comp2042.board.MatrixOperations;

import java.util.ArrayList;
import java.util.List;

final class IBrick implements Brick {

    private final List<int[][]> brickMatrix = new ArrayList<>();

    public IBrick() {
        brickMatrix.add(new int[][]{
                {0, 0, 0, 0},
                {1, 1, 1, 1},
                {0, 0, 0, 0},
                {0, 0, 0, 0}
        });
        brickMatrix.add(new int[][]{
                {0, 1, 0, 0},
                {0, 1, 0, 0},
                {0, 1, 0, 0},
                {0, 1, 0, 0}
        });
        brickMatrix.add(new int[][]{
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {1, 1, 1, 1},
                {0, 0, 0, 0}
        });
        brickMatrix.add(new int[][]{
                {0, 1, 0, 0},
                {0, 1, 0, 0},
                {0, 1, 0, 0},
                {0, 1, 0, 0}
        });
    }

    @Override
    public List<int[][]> getShapeMatrix() {
        return MatrixOperations.deepCopyList(brickMatrix);
    }

    @Override
    public TetrominoType getType() {
        return TetrominoType.I;
    }
}



================================================
FILE: src/main/java/com/comp2042/logic/bricks/JBrick.java
================================================
package com.comp2042.logic.bricks;

import com.comp2042.board.MatrixOperations;

import java.util.ArrayList;
import java.util.List;

final class JBrick implements Brick {

    private final List<int[][]> brickMatrix = new ArrayList<>();

    public JBrick() {
        brickMatrix.add(new int[][]{
                {0, 0, 0, 0},
                {2, 2, 2, 0},
                {0, 0, 2, 0},
                {0, 0, 0, 0}
        });
        brickMatrix.add(new int[][]{
                {0, 0, 0, 0},
                {0, 2, 2, 0},
                {0, 2, 0, 0},
                {0, 2, 0, 0}
        });
        brickMatrix.add(new int[][]{
                {0, 0, 0, 0},
                {0, 2, 0, 0},
                {0, 2, 2, 2},
                {0, 0, 0, 0}
        });
        brickMatrix.add(new int[][]{
                {0, 0, 2, 0},
                {0, 0, 2, 0},
                {0, 2, 2, 0},
                {0, 0, 0, 0}
        });
    }

    @Override
    public List<int[][]> getShapeMatrix() {
        return MatrixOperations.deepCopyList(brickMatrix);
    }

    @Override
    public TetrominoType getType() {
        return TetrominoType.J;
    }
}



================================================
FILE: src/main/java/com/comp2042/logic/bricks/LBrick.java
================================================
package com.comp2042.logic.bricks;

import com.comp2042.board.MatrixOperations;

import java.util.ArrayList;
import java.util.List;

final class LBrick implements Brick {

    private final List<int[][]> brickMatrix = new ArrayList<>();

    public LBrick() {
        brickMatrix.add(new int[][]{
                {0, 0, 0, 0},
                {0, 3, 3, 3},
                {0, 3, 0, 0},
                {0, 0, 0, 0}
        });
        brickMatrix.add(new int[][]{
                {0, 0, 0, 0},
                {0, 3, 3, 0},
                {0, 0, 3, 0},
                {0, 0, 3, 0}
        });
        brickMatrix.add(new int[][]{
                {0, 0, 0, 0},
                {0, 0, 3, 0},
                {3, 3, 3, 0},
                {0, 0, 0, 0}
        });
        brickMatrix.add(new int[][]{
                {0, 3, 0, 0},
                {0, 3, 0, 0},
                {0, 3, 3, 0},
                {0, 0, 0, 0}
        });
    }

    @Override
    public List<int[][]> getShapeMatrix() {
        return MatrixOperations.deepCopyList(brickMatrix);
    }

    @Override
    public TetrominoType getType() {
        return TetrominoType.L;
    }
}



================================================
FILE: src/main/java/com/comp2042/logic/bricks/OBrick.java
================================================
package com.comp2042.logic.bricks;

import com.comp2042.board.MatrixOperations;

import java.util.ArrayList;
import java.util.List;

final class OBrick implements Brick {

    private final List<int[][]> brickMatrix = new ArrayList<>();

    public OBrick() {
        brickMatrix.add(new int[][]{
                {0, 0, 0, 0},
                {0, 4, 4, 0},
                {0, 4, 4, 0},
                {0, 0, 0, 0}
        });
    }

    @Override
    public List<int[][]> getShapeMatrix() {
        return MatrixOperations.deepCopyList(brickMatrix);
    }

    @Override
    public TetrominoType getType() {
        return TetrominoType.O;
    }

}



================================================
FILE: src/main/java/com/comp2042/logic/bricks/PieceGenerator.java
================================================
package com.comp2042.logic.bricks;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Implements the Guideline 7-bag randomiser. Every bag contains one instance of
 * each Tetromino; the bag is shuffled and consumed before a new bag is generated.
 */
public class PieceGenerator implements BrickGenerator {

    private final Deque<Brick> queue = new ArrayDeque<>();
    private final Random random = new Random();
    private final Long fixedSeed;
    private long activeSeed;

    public PieceGenerator() {
        this.fixedSeed = null;
        reseed(ThreadLocalRandom.current().nextLong());
    }

    public PieceGenerator(long seed) {
        this.fixedSeed = seed;
        reseed(seed);
    }

    @Override
    public Brick getBrick() {
        if (queue.isEmpty()) {
            refillBag();
        }
        Brick next = queue.poll();
        if (queue.isEmpty()) {
            refillBag();
        }
        return next;
    }

    @Override
    public Brick getNextBrick() {
        if (queue.isEmpty()) {
            refillBag();
        }
        return queue.peek();
    }

    @Override
    public List<Brick> peekUpcoming(int count) {
        if (count <= 0) {
            return Collections.emptyList();
        }
        ensureQueueSize(count);
        List<Brick> preview = new ArrayList<>(count);
        Iterator<Brick> iterator = queue.iterator();
        for (int i = 0; i < count && iterator.hasNext(); i++) {
            preview.add(iterator.next());
        }
        return preview;
    }

    @Override
    public void reset() {
        if (fixedSeed != null) {
            reseed(fixedSeed);
        } else {
            reseed(ThreadLocalRandom.current().nextLong());
        }
    }

    public long getCurrentSeed() {
        return activeSeed;
    }

    private void ensureQueueSize(int minSize) {
        while (queue.size() < minSize) {
            refillBag();
        }
    }

    private void refillBag() {
        List<Brick> bag = new ArrayList<>(7);
        bag.add(new IBrick());
        bag.add(new JBrick());
        bag.add(new LBrick());
        bag.add(new OBrick());
        bag.add(new SBrick());
        bag.add(new TBrick());
        bag.add(new ZBrick());
        Collections.shuffle(bag, random);
        queue.addAll(bag);
    }

    private void reseed(long seed) {
        activeSeed = seed;
        random.setSeed(seed);
        queue.clear();
        refillBag();
    }
}



================================================
FILE: src/main/java/com/comp2042/logic/bricks/SBrick.java
================================================
package com.comp2042.logic.bricks;

import com.comp2042.board.MatrixOperations;

import java.util.ArrayList;
import java.util.List;

final class SBrick implements Brick {

    private final List<int[][]> brickMatrix = new ArrayList<>();

    public SBrick() {
        brickMatrix.add(new int[][]{
                {0, 0, 0, 0},
                {0, 5, 5, 0},
                {5, 5, 0, 0},
                {0, 0, 0, 0}
        });
        brickMatrix.add(new int[][]{
                {5, 0, 0, 0},
                {5, 5, 0, 0},
                {0, 5, 0, 0},
                {0, 0, 0, 0}
        });
    }

    @Override
    public List<int[][]> getShapeMatrix() {
        return MatrixOperations.deepCopyList(brickMatrix);
    }

    @Override
    public TetrominoType getType() {
        return TetrominoType.S;
    }
}



================================================
FILE: src/main/java/com/comp2042/logic/bricks/TBrick.java
================================================
package com.comp2042.logic.bricks;

import com.comp2042.board.MatrixOperations;

import java.util.ArrayList;
import java.util.List;

final class TBrick implements Brick {

    private final List<int[][]> brickMatrix = new ArrayList<>();

    public TBrick() {
        brickMatrix.add(new int[][]{
                {0, 0, 0, 0},
                {6, 6, 6, 0},
                {0, 6, 0, 0},
                {0, 0, 0, 0}
        });
        brickMatrix.add(new int[][]{
                {0, 6, 0, 0},
                {0, 6, 6, 0},
                {0, 6, 0, 0},
                {0, 0, 0, 0}
        });
        brickMatrix.add(new int[][]{
                {0, 6, 0, 0},
                {6, 6, 6, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0}
        });
        brickMatrix.add(new int[][]{
                {0, 6, 0, 0},
                {6, 6, 0, 0},
                {0, 6, 0, 0},
                {0, 0, 0, 0}
        });
    }

    @Override
    public List<int[][]> getShapeMatrix() {
        return MatrixOperations.deepCopyList(brickMatrix);
    }

    @Override
    public TetrominoType getType() {
        return TetrominoType.T;
    }
}



================================================
FILE: src/main/java/com/comp2042/logic/bricks/TetrominoType.java
================================================
package com.comp2042.logic.bricks;

public enum TetrominoType {
    I,
    J,
    L,
    O,
    S,
    T,
    Z
}




================================================
FILE: src/main/java/com/comp2042/logic/bricks/ZBrick.java
================================================
package com.comp2042.logic.bricks;

import com.comp2042.board.MatrixOperations;

import java.util.ArrayList;
import java.util.List;

final class ZBrick implements Brick {

    private final List<int[][]> brickMatrix = new ArrayList<>();

    public ZBrick() {
        brickMatrix.add(new int[][]{
                {0, 0, 0, 0},
                {7, 7, 0, 0},
                {0, 7, 7, 0},
                {0, 0, 0, 0}
        });
        brickMatrix.add(new int[][]{
                {0, 7, 0, 0},
                {7, 7, 0, 0},
                {7, 0, 0, 0},
                {0, 0, 0, 0}
        });
    }

    @Override
    public List<int[][]> getShapeMatrix() {
        return MatrixOperations.deepCopyList(brickMatrix);
    }

    @Override
    public TetrominoType getType() {
        return TetrominoType.Z;
    }
}



================================================
FILE: src/main/java/com/comp2042/ui/BrickColorPalette.java
================================================
package com.comp2042.ui;

import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;

/**
 * Centralises the mapping between tile IDs and their display colours so multiple UI
 * components can stay in sync. Supports multiple palettes to aid accessibility.
 */
public final class BrickColorPalette {

    public enum PaletteProfile {
        CLASSIC(createClassicPalette(), createClassicGhostPalette()),
        HIGH_CONTRAST(createHighContrastPalette(), createHighContrastGhostPalette());

        private final Paint[] brickColors;
        private final Paint[] ghostColors;

        PaletteProfile(Paint[] brickColors, Paint[] ghostColors) {
            this.brickColors = brickColors;
            this.ghostColors = ghostColors;
        }

        Paint brickColor(int tileId) {
            return colorFor(tileId, brickColors);
        }

        Paint ghostColor(int tileId) {
            return colorFor(tileId, ghostColors);
        }
    }

    private static PaletteProfile currentProfile = PaletteProfile.CLASSIC;

    private BrickColorPalette() {
    }

    public static void setProfile(PaletteProfile profile) {
        currentProfile = profile != null ? profile : PaletteProfile.CLASSIC;
    }

    public static Paint resolve(int tileId) {
        return currentProfile.brickColor(tileId);
    }

    public static Paint resolveGhost(int tileId) {
        return currentProfile.ghostColor(tileId);
    }

    private static Paint colorFor(int tileId, Paint[] palette) {
        if (palette == null || palette.length == 0) {
            return Color.TRANSPARENT;
        }
        if (tileId < 0 || tileId >= palette.length) {
            return palette[0];
        }
        Paint paint = palette[tileId];
        return paint != null ? paint : palette[0];
    }

    private static Paint[] createClassicPalette() {
        return new Paint[]{
                Color.TRANSPARENT,
                Color.web("#00f3ff"),
                Color.web("#8a64ff"),
                Color.web("#45ff92"),
                Color.web("#ffe066"),
                Color.web("#ff4f81"),
                Color.web("#ffb347"),
                Color.web("#46b0ff")
        };
    }

    private static Paint[] createClassicGhostPalette() {
        return new Paint[]{
                Color.TRANSPARENT,
                withOpacity(Color.web("#00f3ff"), 0.55),
                withOpacity(Color.web("#8a64ff"), 0.55),
                withOpacity(Color.web("#45ff92"), 0.55),
                withOpacity(Color.web("#ffe066"), 0.55),
                withOpacity(Color.web("#ff4f81"), 0.55),
                withOpacity(Color.web("#ffb347"), 0.55),
                withOpacity(Color.web("#46b0ff"), 0.55)
        };
    }

    private static Paint[] createHighContrastPalette() {
        return new Paint[]{
                Color.TRANSPARENT,
                stripedPaint(Color.rgb(0, 45, 95), Color.rgb(0, 168, 255)),
                stripedPaint(Color.rgb(55, 0, 90), Color.rgb(220, 230, 80)),
                stripedPaint(Color.rgb(5, 75, 20), Color.rgb(255, 255, 255)),
                stripedPaint(Color.rgb(75, 50, 0), Color.rgb(255, 190, 0)),
                stripedPaint(Color.rgb(70, 0, 0), Color.rgb(255, 255, 255)),
                stripedPaint(Color.rgb(35, 35, 35), Color.rgb(255, 0, 150)),
                stripedPaint(Color.rgb(0, 0, 0), Color.rgb(255, 255, 255))
        };
    }

    private static Paint[] createHighContrastGhostPalette() {
        return new Paint[]{
                Color.TRANSPARENT,
                withOpacity(Color.rgb(0, 168, 255), 0.5),
                withOpacity(Color.rgb(220, 230, 80), 0.5),
                withOpacity(Color.rgb(255, 255, 255), 0.5),
                withOpacity(Color.rgb(255, 190, 0), 0.5),
                withOpacity(Color.rgb(255, 255, 255), 0.5),
                withOpacity(Color.rgb(255, 0, 150), 0.5),
                withOpacity(Color.rgb(220, 220, 220), 0.5)
        };
    }

    private static Color withOpacity(Color base, double opacity) {
        return new Color(base.getRed(), base.getGreen(), base.getBlue(), opacity);
    }

    private static LinearGradient stripedPaint(Color background, Color accent) {
        return new LinearGradient(
                0, 0, 1, 1, true, CycleMethod.REPEAT,
                new Stop(0.0, accent),
                new Stop(0.35, accent),
                new Stop(0.36, background),
                new Stop(0.65, background),
                new Stop(0.66, accent),
                new Stop(1.0, accent)
        );
    }
}



================================================
FILE: src/main/java/com/comp2042/ui/GameOverPanel.java
================================================
package com.comp2042.ui;

import com.comp2042.game.stats.HighScoreEntry;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

/**
 * Overlay shown when the game reaches a GAME OVER state.
 */
public class GameOverPanel extends BorderPane {

    private Runnable onRestart = () -> {
    };
    private Runnable onExit = () -> {
    };
    private Runnable onMainMenu = () -> {
    };
    private static final String DEFAULT_OUTCOME_MESSAGE = "GAME OVER";
    private static final String DEFAULT_LEADERBOARD_TITLE = "Top Scores";
    private final Label seedLabel = new Label();
    private final Label outcomeLabel = new Label(DEFAULT_OUTCOME_MESSAGE);
    private final Label summaryLabel = new Label("Drop back in when you're ready.");
    private final Label leaderboardTitle = new Label(DEFAULT_LEADERBOARD_TITLE);
    private final VBox leaderboardContainer = new VBox(4);
    private final Label emptyLeaderboardLabel = new Label("No runs recorded yet.");

    public GameOverPanel() {
        outcomeLabel.getStyleClass().addAll("overlay-title", "danger");
        seedLabel.getStyleClass().add("seedLabel");
        seedLabel.setVisible(false);

        summaryLabel.getStyleClass().add("sidebar-hint");
        summaryLabel.setWrapText(true);

        leaderboardTitle.getStyleClass().add("menu-subtitle");
        emptyLeaderboardLabel.getStyleClass().add("leaderboard-placeholder");
        leaderboardContainer.setAlignment(Pos.CENTER_LEFT);

        Button restartButton = new Button("Restart");
        restartButton.getStyleClass().add("btn-primary");
        restartButton.setOnAction(event -> onRestart.run());

        Button menuButton = new Button("Main Menu");
        menuButton.getStyleClass().add("btn-secondary");
        menuButton.setOnAction(event -> onMainMenu.run());

        Button exitButton = new Button("Exit");
        exitButton.getStyleClass().add("btn-danger");
        exitButton.setOnAction(event -> onExit.run());

        VBox headerBox = new VBox(6, outcomeLabel, summaryLabel, seedLabel);
        headerBox.setAlignment(Pos.CENTER);

        VBox leaderboardBox = new VBox(6, leaderboardTitle, emptyLeaderboardLabel, leaderboardContainer);
        leaderboardBox.setAlignment(Pos.CENTER_LEFT);

        HBox buttonRow = new HBox(12, restartButton, menuButton, exitButton);
        buttonRow.setAlignment(Pos.CENTER);

        VBox container = new VBox(18, headerBox, leaderboardBox, buttonRow);
        container.setAlignment(Pos.CENTER);
        container.setFillWidth(true);

        setCenter(container);
        getStyleClass().add("game-over-panel");
    }

    public void setOnRestart(Runnable onRestart) {
        this.onRestart = onRestart != null ? onRestart : () -> {
        };
    }

    public void setOnExit(Runnable onExit) {
        this.onExit = onExit != null ? onExit : () -> {
        };
    }

    public void setOnMainMenu(Runnable onMainMenu) {
        this.onMainMenu = onMainMenu != null ? onMainMenu : () -> {
        };
    }

    public void setSeedInfo(long seed, boolean deterministic) {
        seedLabel.setText("Seed: " + seed + (deterministic ? " (fixed)" : ""));
        seedLabel.setVisible(true);
    }

    public void setLeaderboard(List<HighScoreEntry> entries, HighScoreEntry highlight) {
        leaderboardContainer.getChildren().clear();
        boolean hasEntries = entries != null && !entries.isEmpty();
        leaderboardContainer.setManaged(hasEntries);
        leaderboardContainer.setVisible(hasEntries);
        emptyLeaderboardLabel.setManaged(!hasEntries);
        emptyLeaderboardLabel.setVisible(!hasEntries);
        if (!hasEntries) {
            return;
        }

        int rank = 1;
        for (HighScoreEntry entry : entries) {
            Label label = new Label(formatEntry(rank++, entry));
            label.getStyleClass().add("leaderboard-entry");
            if (highlight != null && highlight.equals(entry)) {
                label.getStyleClass().add("leaderboard-highlight");
            }
            leaderboardContainer.getChildren().add(label);
        }
    }

    private String formatEntry(int rank, HighScoreEntry entry) {
        return String.format("#%d %s pts · %s · %s",
                rank,
                entry.getScore(),
                entry.getMode(),
                entry.formattedDuration());
    }

    public void setOutcomeMessage(String message) {
        String text = (message == null || message.isBlank()) ? DEFAULT_OUTCOME_MESSAGE : message;
        outcomeLabel.setText(text);
    }

    public void setLeaderboardTitle(String title) {
        String text = (title == null || title.isBlank()) ? DEFAULT_LEADERBOARD_TITLE : title;
        leaderboardTitle.setText(text);
    }
}



================================================
FILE: src/main/java/com/comp2042/ui/GuiController.java
================================================
package com.comp2042.ui;

import com.comp2042.app.HelpDialogService;
import com.comp2042.app.SettingsController;
import com.comp2042.app.StartMenuController;
import com.comp2042.audio.BackgroundMusicManager;
import com.comp2042.board.ClearRow;
import com.comp2042.board.ViewData;
import com.comp2042.config.GameSettings;
import com.comp2042.config.GameSettings.ColorAssistMode;
import com.comp2042.config.GameSettingsStore;
import com.comp2042.config.ResourceManager;
import com.comp2042.game.GameConfig;
import com.comp2042.game.GameState;
import com.comp2042.game.LevelProgression;
import com.comp2042.game.Score;
import com.comp2042.game.events.DownData;
import com.comp2042.game.events.EventSource;
import com.comp2042.game.events.EventType;
import com.comp2042.game.events.InputEventListener;
import com.comp2042.game.events.MoveEvent;
import com.comp2042.game.stats.HighScoreEntry;
import com.comp2042.game.stats.HighScoreService;
import com.comp2042.help.HelpContentProvider;
import com.comp2042.ui.BrickColorPalette.PaletteProfile;
import com.comp2042.ui.input.AutoRepeatHandler;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.effect.Reflection;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.util.ResourceBundle;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * JavaFX controller for the gameplay scene. It renders the board layers,
 * handles input,
 * and delegates gameplay actions to {@link com.comp2042.game.GameController}.
 */
public class GuiController implements Initializable {

    private static final int BRICK_SIZE = 20;
    private static final int HIDDEN_ROWS = 2;
    private static final double GAME_OVER_GUIDE_OFFSET_ROWS = 0.5;
    private static final int TIMED_MODE_SECONDS = 180;
    private static final int FIXED_LINES_TARGET = 40;
    private static final Color OUTLINE_COLOR = Color.rgb(15, 15, 20, 0.7);

    @FXML
    private GridPane gamePanel;

    @FXML
    private Group groupNotification;

    @FXML
    private GridPane brickPanel;

    @FXML
    private GridPane ghostPanel;

    @FXML
    private HudPanel hudPanel;

    @FXML
    private NextQueuePanel nextQueuePanel;

    @FXML
    private GameOverPanel gameOverPanel;

    @FXML
    private StackPane gameLayer;

    @FXML
    private Pane guidePane;
    @FXML
    private Button helpHintButton;
    @FXML
    private VBox pauseOverlay;

    private Rectangle[][] displayMatrix;

    private InputEventListener eventListener;

    private Rectangle[][] rectangles;
    private Rectangle[][] ghostRectangles;
    private int[][] lastBoardMatrix;
    private ViewData lastViewData;
    private boolean outlinesEnabled;

    private Timeline timeLine;

    private final BooleanProperty isPause = new SimpleBooleanProperty();

    private final BooleanProperty isGameOver = new SimpleBooleanProperty();

    private GameState gameState = GameState.MENU;

    private Stage primaryStage;

    private Line gameOverGuideLine;
    private int cachedGameOverRow = Integer.MIN_VALUE;
    private GameSettings gameSettings = GameSettings.defaultSettings();
    private final GameSettingsStore settingsStore = new GameSettingsStore();
    private final Map<KeyCode, GameSettings.Action> actionByKey = new HashMap<>();
    private final Set<KeyCode> heldKeys = EnumSet.noneOf(KeyCode.class);
    private AutoRepeatHandler moveLeftRepeat;
    private AutoRepeatHandler moveRightRepeat;
    private AutoRepeatHandler softDropRepeat;
    private final LevelProgression levelProgression = new LevelProgression();
    private double currentGravityMs = LevelProgression.DEFAULT_BASE_GRAVITY_MS;
    private final HighScoreService highScoreService = new HighScoreService();
    private final HelpContentProvider helpContentProvider = HelpContentProvider.getInstance();
    private Score boundScore;
    private Instant sessionStartInstant;
    private long lastSeed;
    private boolean lastSeedDeterministic;
    private GameConfig activeGameConfig = GameConfig.defaultConfig();
    private GameConfig.GameMode gameMode = GameConfig.GameMode.ENDLESS;
    private Timeline modeTimer;
    private long remainingModeSeconds;
    private int fixedLinesCleared;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Font.loadFont(ResourceManager.getExternalForm(ResourceManager.Asset.DIGITAL_FONT), 38);
        gamePanel.setFocusTraversable(true);
        gamePanel.requestFocus();
        StackPane.setAlignment(gamePanel, Pos.TOP_LEFT);
        StackPane.setAlignment(guidePane, Pos.TOP_LEFT);
        StackPane.setAlignment(brickPanel, Pos.TOP_LEFT);
        if (ghostPanel != null) {
            StackPane.setAlignment(ghostPanel, Pos.TOP_LEFT);
        }
        gamePanel.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                KeyCode code = keyEvent.getCode();
                if (code == KeyCode.ESCAPE) {
                    handleEscapeKey();
                    keyEvent.consume();
                    return;
                }
                if (code == KeyCode.F1) {
                    onShowHelp(null);
                    keyEvent.consume();
                    return;
                }
                if (!heldKeys.add(code)) {
                    keyEvent.consume();
                    return;
                }
                handleKeyPressed(code);
                keyEvent.consume();
            }
        });
        gamePanel.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                KeyCode code = keyEvent.getCode();
                heldKeys.remove(code);
                handleKeyReleased(code);
            }
        });
        if (helpHintButton != null) {
            helpHintButton.setFocusTraversable(false);
            helpHintButton.setTooltip(buildHelpTooltip());
        }
        gameOverPanel.setVisible(false);
        gameOverPanel.setManaged(false);
        gameOverPanel.setOnRestart(this::startNewGameSession);
        gameOverPanel.setOnMainMenu(this::returnToMainMenu);
        gameOverPanel.setOnExit(Platform::exit);

        final Reflection reflection = new Reflection();
        reflection.setFraction(0.8);
        reflection.setTopOpacity(0.9);
        reflection.setTopOffset(-12);
        if (pauseOverlay != null) {
            pauseOverlay.setVisible(false);
            pauseOverlay.setManaged(false);
        }
    }

    public void initGameView(int[][] boardMatrix, ViewData brick) {
        displayMatrix = new Rectangle[boardMatrix.length][boardMatrix[0].length];
        for (int i = 2; i < boardMatrix.length; i++) {
            for (int j = 0; j < boardMatrix[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(Color.TRANSPARENT);
                applyOutline(rectangle);
                displayMatrix[i][j] = rectangle;
                gamePanel.add(rectangle, j, i - 2);
            }
        }

        rectangles = new Rectangle[brick.getBrickData().length][brick.getBrickData()[0].length];
        for (int i = 0; i < brick.getBrickData().length; i++) {
            for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                rectangle.setFill(BrickColorPalette.resolve(brick.getBrickData()[i][j]));
                applyOutline(rectangle);
                rectangles[i][j] = rectangle;
                brickPanel.add(rectangle, j, i);
            }
        }
        if (ghostPanel != null) {
            ghostRectangles = new Rectangle[brick.getBrickData().length][brick.getBrickData()[0].length];
            for (int i = 0; i < brick.getBrickData().length; i++) {
                for (int j = 0; j < brick.getBrickData()[i].length; j++) {
                    Rectangle rectangle = new Rectangle(BRICK_SIZE, BRICK_SIZE);
                    rectangle.setFill(Color.TRANSPARENT);
                    rectangle.getStyleClass().add("ghost-cell");
                    rectangle.setVisible(false);
                    ghostRectangles[i][j] = rectangle;
                    ghostPanel.add(rectangle, j, i);
                }
            }
        }
        rememberBoardState(boardMatrix);
        applyBrickView(brick);

        timeLine = new Timeline();
        timeLine.setCycleCount(Timeline.INDEFINITE);
        rebuildKeyBindings();
        resetLevelTracking();
        applyVisualPreferences();
    }

    private void refreshBrick(ViewData brick) {
        if (brick == null) {
            return;
        }
        if (gameState == GameState.PLAYING && isPause.getValue() == Boolean.FALSE) {
            applyBrickView(brick);
        }
    }

    public void refreshGameBackground(int[][] board) {
        if (board == null || displayMatrix == null) {
            return;
        }
        rememberBoardState(board);
        for (int i = 2; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                setRectangleData(board[i][j], displayMatrix[i][j]);
            }
        }
    }

    private void applyBrickView(ViewData brick) {
        if (brick == null || rectangles == null || brickPanel == null || gameLayer == null) {
            return;
        }

        double cellWidth = brickPanel.getHgap() + BRICK_SIZE;
        double cellHeight = brickPanel.getVgap() + BRICK_SIZE;

        // Calculate the position of the top-left of the grid (0,0) in scene coordinates
        // gamePanel contains the visible board (rows 2 to 21).
        // Row 0 in the logical board corresponds to -2 * cellHeight relative to
        // gamePanel's (0,0).
        Point2D gridOriginScene = gamePanel.localToScene(0, 0);

        // Convert that scene point to the coordinate space of brickPanel's parent
        // This gives us the (0,0) of the gamePanel in the brickPanel's parent's
        // coordinate system.
        Point2D gridOriginLocal = brickPanel.getParent().sceneToLocal(gridOriginScene);

        // The logical board starts at row 0, but gamePanel starts displaying at row 2.
        // So logical (0,0) is at (gridOriginLocal.x, gridOriginLocal.y - 2 *
        // cellHeight).
        double boardOriginX = gridOriginLocal.getX();
        double boardOriginY = gridOriginLocal.getY() - (HIDDEN_ROWS * cellHeight);

        double activeX = boardOriginX + brick.getxPosition() * cellWidth;
        double activeY = boardOriginY + brick.getyPosition() * cellHeight;

        brickPanel.setLayoutX(activeX);
        brickPanel.setLayoutY(activeY);
        int[][] brickData = brick.getBrickData();
        for (int i = 0; i < brickData.length; i++) {
            for (int j = 0; j < brickData[i].length; j++) {
                setRectangleData(brickData[i][j], rectangles[i][j]);
            }
        }
        if (ghostPanel != null && ghostRectangles != null) {
            ghostPanel.setLayoutX(activeX);
            double ghostY = boardOriginY + brick.getGhostYPosition() * cellHeight;
            ghostPanel.setLayoutY(ghostY);
            for (int i = 0; i < brickData.length; i++) {
                for (int j = 0; j < brickData[i].length; j++) {
                    setGhostRectangleData(brickData[i][j], ghostRectangles[i][j]);
                }
            }
        }
        if (nextQueuePanel != null) {
            nextQueuePanel.setQueue(brick.getNextBricksData());
        }
        updateGameOverGuide(brick.getGameOverRow());
        rememberViewData(brick);
    }

    private void setRectangleData(int color, Rectangle rectangle) {
        rectangle.setFill(BrickColorPalette.resolve(color));
        rectangle.setArcHeight(9);
        rectangle.setArcWidth(9);
        applyOutline(rectangle);
    }

    private void setGhostRectangleData(int color, Rectangle rectangle) {
        if (rectangle == null) {
            return;
        }
        if (color == 0) {
            rectangle.setVisible(false);
            rectangle.setFill(Color.TRANSPARENT);
        } else {
            rectangle.setVisible(true);
            rectangle.setFill(BrickColorPalette.resolveGhost(color));
        }
        rectangle.setArcHeight(9);
        rectangle.setArcWidth(9);
        applyOutline(rectangle);
    }

    private void applyOutline(Rectangle rectangle) {
        if (rectangle == null) {
            return;
        }
        if (outlinesEnabled) {
            rectangle.setStroke(OUTLINE_COLOR);
            rectangle.setStrokeWidth(0.9);
        } else {
            rectangle.setStrokeWidth(0);
            rectangle.setStroke(null);
        }
    }

    private void handleDropResult(DownData downData) {
        if (downData == null) {
            return;
        }
        ClearRow clearRow = downData.getClearRow();
        if (clearRow != null && clearRow.getLinesRemoved() > 0) {
            NotificationPanel notificationPanel = new NotificationPanel("+" + clearRow.getScoreBonus());
            groupNotification.getChildren().add(notificationPanel);
            notificationPanel.showScore(groupNotification.getChildren());
            BackgroundMusicManager.getInstance().playLineClear();
        }
        updateLevelProgress(clearRow);
        refreshBrick(downData.getViewData());
        gamePanel.requestFocus();
    }

    private void moveDown(MoveEvent event) {
        if (gameState != GameState.PLAYING || isPause.getValue() == Boolean.TRUE) {
            gamePanel.requestFocus();
            return;
        }
        DownData downData = eventListener.onDownEvent(event);
        handleDropResult(downData);
    }

    private void hardDrop(MoveEvent event) {
        if (gameState != GameState.PLAYING || isPause.getValue() == Boolean.TRUE) {
            gamePanel.requestFocus();
            return;
        }
        DownData downData = eventListener.onHardDrop(event);
        handleDropResult(downData);
    }

    private void performMoveLeft() {
        if (eventListener == null) {
            return;
        }
        refreshBrick(eventListener.onLeftEvent(new MoveEvent(EventType.LEFT, EventSource.USER)));
    }

    private void performMoveRight() {
        if (eventListener == null) {
            return;
        }
        refreshBrick(eventListener.onRightEvent(new MoveEvent(EventType.RIGHT, EventSource.USER)));
    }

    private void performRotateClockwise() {
        if (eventListener == null) {
            return;
        }
        refreshBrick(eventListener.onRotateClockwise(new MoveEvent(EventType.ROTATE_CLOCKWISE, EventSource.USER)));
    }

    private void performRotateCounterClockwise() {
        if (eventListener == null) {
            return;
        }
        refreshBrick(eventListener
                .onRotateCounterClockwise(new MoveEvent(EventType.ROTATE_COUNTERCLOCKWISE, EventSource.USER)));
    }

    private void performSoftDrop() {
        moveDown(new MoveEvent(EventType.DOWN, EventSource.USER));
    }

    private void performHardDrop() {
        hardDrop(new MoveEvent(EventType.HARD_DROP, EventSource.USER));
    }

    public void setEventListener(InputEventListener eventListener) {
        this.eventListener = eventListener;
    }

    public void bindScore(Score score) {
        boundScore = score;
        if (hudPanel != null && score != null) {
            hudPanel.bindScore(score);
        }
    }

    public void setLevel(int level) {
        if (hudPanel != null) {
            hudPanel.setLevel(level);
        }
    }

    public void setGameMode(GameConfig.GameMode mode) {
        this.gameMode = mode != null ? mode : GameConfig.GameMode.ENDLESS;
        updateModeStatus();
    }

    public void setActiveGameConfig(GameConfig config) {
        this.activeGameConfig = config != null ? config : GameConfig.defaultConfig();
    }

    public void setGameSettings(GameSettings settings) {
        this.gameSettings = settings != null ? settings : GameSettings.defaultSettings();
        applyAudioPreferences();
        heldKeys.clear();
        rebuildKeyBindings();
        applyVisualPreferences();
    }

    private void rebuildKeyBindings() {
        actionByKey.clear();
        if (gameSettings == null) {
            gameSettings = GameSettings.defaultSettings();
        }
        for (Map.Entry<GameSettings.Action, KeyCode> entry : gameSettings.getKeyBindings().entrySet()) {
            if (entry.getValue() != null) {
                actionByKey.put(entry.getValue(), entry.getKey());
            }
        }
        Duration das = Duration.millis(gameSettings.getDasDelayMs());
        Duration arr = Duration.millis(Math.max(1, gameSettings.getArrIntervalMs()));
        moveLeftRepeat = new AutoRepeatHandler(this::performMoveLeft, das, arr);
        moveRightRepeat = new AutoRepeatHandler(this::performMoveRight, das, arr);
        double softDropInterval = Math.max(10d, currentGravityMs / gameSettings.getSoftDropMultiplier());
        softDropRepeat = new AutoRepeatHandler(this::performSoftDrop, Duration.ZERO, Duration.millis(softDropInterval));
    }

    private void handleKeyPressed(KeyCode code) {
        GameSettings.Action action = actionByKey.get(code);
        if (action == null) {
            return;
        }
        if (action == GameSettings.Action.NEW_GAME) {
            newGame(null);
            return;
        }
        if (!isGameplayInputEnabled()) {
            return;
        }
        switch (action) {
            case MOVE_LEFT -> {
                if (moveLeftRepeat != null) {
                    moveLeftRepeat.start();
                } else {
                    performMoveLeft();
                }
            }
            case MOVE_RIGHT -> {
                if (moveRightRepeat != null) {
                    moveRightRepeat.start();
                } else {
                    performMoveRight();
                }
            }
            case SOFT_DROP -> {
                if (softDropRepeat != null) {
                    softDropRepeat.start();
                } else {
                    performSoftDrop();
                }
            }
            case HARD_DROP -> performHardDrop();
            case ROTATE_CW -> performRotateClockwise();
            case ROTATE_CCW -> performRotateCounterClockwise();
            default -> {
            }
        }
    }

    private void handleEscapeKey() {
        if (gameState == GameState.PLAYING) {
            pauseGame();
        } else if (gameState == GameState.PAUSED) {
            resumeGame();
        }
    }

    private void pauseGame() {
        if (gameState != GameState.PLAYING) {
            return;
        }
        setGameState(GameState.PAUSED);
    }

    private void resumeGame() {
        if (gameState != GameState.PAUSED) {
            return;
        }
        setGameState(GameState.PLAYING);
        BackgroundMusicManager.getInstance().playGameTheme();
        if (gamePanel != null) {
            gamePanel.requestFocus();
        }
    }

    private void handleKeyReleased(KeyCode code) {
        GameSettings.Action action = actionByKey.get(code);
        if (action == null) {
            return;
        }
        switch (action) {
            case MOVE_LEFT -> stopRepeat(moveLeftRepeat);
            case MOVE_RIGHT -> stopRepeat(moveRightRepeat);
            case SOFT_DROP -> stopRepeat(softDropRepeat);
            default -> {
            }
        }
    }

    private boolean isGameplayInputEnabled() {
        return gameState == GameState.PLAYING
                && Boolean.FALSE.equals(isPause.getValue())
                && Boolean.FALSE.equals(isGameOver.getValue());
    }

    private void stopRepeat(AutoRepeatHandler handler) {
        if (handler != null) {
            handler.stop();
        }
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void gameOver() {
        stopModeTimer();
        setGameState(GameState.GAME_OVER);
        BackgroundMusicManager.getInstance().playGameOverJingle();
        BackgroundMusicManager.getInstance().playMenuTheme();
        recordLeaderboardResult();
    }

    public void newGame(ActionEvent actionEvent) {
        startNewGameSession();
    }

    public void updateSeedInfo(long seed, boolean deterministic) {
        lastSeed = seed;
        lastSeedDeterministic = deterministic;
        if (gameOverPanel != null) {
            gameOverPanel.setSeedInfo(seed, deterministic);
        }
    }

    private void startNewGameSession() {
        stopAllRepeats();
        resetLevelTracking();
        setPauseOverlayVisible(false);
        if (timeLine != null) {
            timeLine.stop();
        }
        hideGameOverPanel();
        markSessionStart();
        resetModeObjectivesInternal();
        if (eventListener == null) {
            return;
        }
        ViewData freshState = eventListener.createNewGame();
        applyBrickView(freshState);
        setGameState(GameState.PLAYING);
        BackgroundMusicManager.getInstance().playGameTheme();
    }

    private void returnToMainMenu() {
        if (timeLine != null) {
            timeLine.stop();
        }
        stopModeTimer();
        setGameState(GameState.MENU);
        setPauseOverlayVisible(false);
        stopAllRepeats();
        if (gamePanel != null) {
            gamePanel.setFocusTraversable(false);
        }
        if (primaryStage == null) {
            throw new IllegalStateException("Primary stage has not been set on GuiController.");
        }
        try {
            FXMLLoader loader = new FXMLLoader(ResourceManager.getUrl(ResourceManager.Asset.START_MENU_FXML));
            Parent menuRoot = loader.load();
            StartMenuController menuController = loader.getController();
            menuController.setPrimaryStage(primaryStage);
            menuController.setGameConfig(activeGameConfig);
            Scene currentScene = primaryStage.getScene();
            double width = currentScene != null ? currentScene.getWidth() : StartMenuController.MENU_WINDOW_WIDTH;
            double height = currentScene != null ? currentScene.getHeight() : StartMenuController.MENU_WINDOW_HEIGHT;
            Scene menuScene = new Scene(menuRoot, width, height);
            primaryStage.setScene(menuScene);
            primaryStage.show();
            BackgroundMusicManager.getInstance().playMenuTheme();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load " + ResourceManager.Asset.START_MENU_FXML.path(), e);
        }
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState newGameState) {
        if (newGameState == null) {
            throw new IllegalArgumentException("GameState cannot be null");
        }
        if (gameState == newGameState) {
            updateTimelinePlayback();
            return;
        }
        GameState previousState = gameState;
        gameState = newGameState;
        if (gameState == GameState.PLAYING && previousState != GameState.PLAYING && sessionStartInstant == null) {
            markSessionStart();
        }
        switch (gameState) {
            case MENU:
                isPause.setValue(Boolean.TRUE);
                isGameOver.setValue(Boolean.FALSE);
                stopModeTimer();
                hideGameOverPanel();
                setPauseOverlayVisible(false);
                stopAllRepeats();
                break;
            case PLAYING:
                isPause.setValue(Boolean.FALSE);
                isGameOver.setValue(Boolean.FALSE);
                hideGameOverPanel();
                setPauseOverlayVisible(false);
                resumeModeTimerIfNeeded();
                break;
            case PAUSED:
                isPause.setValue(Boolean.TRUE);
                isGameOver.setValue(Boolean.FALSE);
                pauseModeTimer();
                if (gameOverPanel != null) {
                    gameOverPanel.setVisible(false);
                    gameOverPanel.setManaged(false);
                }
                setPauseOverlayVisible(true);
                stopAllRepeats();
                break;
            case GAME_OVER:
                isPause.setValue(Boolean.TRUE);
                isGameOver.setValue(Boolean.TRUE);
                stopModeTimer();
                if (gameOverPanel != null) {
                    gameOverPanel.setVisible(true);
                    gameOverPanel.setManaged(true);
                }
                setPauseOverlayVisible(false);
                stopAllRepeats();
                break;
            default:
                throw new IllegalStateException("Unhandled game state: " + gameState);
        }
        updateTimelinePlayback();
        if (gameState == GameState.PLAYING && gamePanel != null) {
            gamePanel.requestFocus();
        }
    }

    private void updateTimelinePlayback() {
        if (timeLine == null) {
            return;
        }
        if (gameState == GameState.PLAYING) {
            timeLine.play();
        } else {
            timeLine.stop();
        }
    }

    private void updateGameOverGuide(int gameOverRow) {
        if (guidePane == null || displayMatrix == null) {
            return;
        }
        if (cachedGameOverRow == gameOverRow && gameOverGuideLine != null) {
            return;
        }
        int columns = displayMatrix[0].length;
        int visibleRows = displayMatrix.length - HIDDEN_ROWS;
        double rowHeight = BRICK_SIZE + gamePanel.getVgap();
        double boardWidth = columns * BRICK_SIZE + Math.max(0, columns - 1) * gamePanel.getHgap();
        double boardHeight = visibleRows * BRICK_SIZE + Math.max(0, visibleRows - 1) * gamePanel.getVgap();
        guidePane.setPrefWidth(boardWidth);
        guidePane.setPrefHeight(boardHeight);
        double y = (gameOverRow - HIDDEN_ROWS + GAME_OVER_GUIDE_OFFSET_ROWS) * rowHeight;
        if (y < 0) {
            y = 0;
        }
        if (gameOverGuideLine == null) {
            gameOverGuideLine = new Line(0, y, boardWidth, y);
            gameOverGuideLine.setStroke(Color.rgb(220, 20, 60));
            gameOverGuideLine.setOpacity(0.5);
            gameOverGuideLine.setStrokeWidth(2);
            guidePane.getChildren().add(gameOverGuideLine);
        } else {
            gameOverGuideLine.setStartX(0);
            gameOverGuideLine.setEndX(boardWidth);
            gameOverGuideLine.setStartY(y);
            gameOverGuideLine.setEndY(y);
        }
        cachedGameOverRow = gameOverRow;
    }

    private void stopAllRepeats() {
        stopRepeat(moveLeftRepeat);
        stopRepeat(moveRightRepeat);
        stopRepeat(softDropRepeat);
        heldKeys.clear();
    }

    private void setPauseOverlayVisible(boolean visible) {
        if (pauseOverlay != null) {
            pauseOverlay.setVisible(visible);
            pauseOverlay.setManaged(visible);
        }
    }

    private void resetLevelTracking() {
        LevelProgression.LevelState state = levelProgression.reset();
        applyLevelState(state, true);
    }

    private void applyLevelState(LevelProgression.LevelState state, boolean updateTiming) {
        if (state == null) {
            return;
        }
        currentGravityMs = state.gravityIntervalMs();
        setLevel(state.level());
        if (updateTiming) {
            applyGravityInterval();
            updateSoftDropRepeatInterval();
        }
    }

    private void applyGravityInterval() {
        if (timeLine == null) {
            return;
        }
        timeLine.stop();
        timeLine.getKeyFrames().setAll(new KeyFrame(
                Duration.millis(currentGravityMs),
                ae -> moveDown(new MoveEvent(EventType.DOWN, EventSource.THREAD))));
        updateTimelinePlayback();
    }

    private void updateSoftDropRepeatInterval() {
        if (softDropRepeat == null) {
            return;
        }
        double softDropInterval = Math.max(10d, currentGravityMs / gameSettings.getSoftDropMultiplier());
        softDropRepeat.configure(Duration.ZERO, Duration.millis(softDropInterval));
    }

    private void updateLevelProgress(ClearRow clearRow) {
        if (clearRow == null) {
            return;
        }
        int removed = clearRow.getLinesRemoved();
        if (removed <= 0) {
            return;
        }
        LevelProgression.LevelState state = levelProgression.handleLinesCleared(removed);
        if (state.leveledUp()) {
            applyLevelState(state, true);
        }
        handleModeLineProgress(removed);
    }

    @FXML
    private void onPauseResume(ActionEvent event) {
        resumeGame();
    }

    @FXML
    private void onPauseSettings(ActionEvent event) {
        openSettingsDialog();
    }

    @FXML
    private void onPauseMainMenu(ActionEvent event) {
        returnToMainMenu();
    }

    @FXML
    private void onShowHelp(ActionEvent event) {
        Stage owner = primaryStage;
        if (owner == null && helpHintButton != null && helpHintButton.getScene() != null) {
            owner = (Stage) helpHintButton.getScene().getWindow();
        }
        HelpDialogService.showHelp(owner, helpContentProvider.getMarkdown());
    }

    private void openSettingsDialog() {
        if (primaryStage == null) {
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(ResourceManager.getUrl(ResourceManager.Asset.SETTINGS_DIALOG_FXML));
            Parent root = loader.load();
            SettingsController controller = loader.getController();
            Stage dialog = new Stage();
            dialog.setTitle("Settings");
            dialog.initOwner(primaryStage);
            dialog.initModality(Modality.WINDOW_MODAL);
            controller.setDialogStage(dialog);
            controller.setInitialSettings(gameSettings);
            Scene scene = new Scene(root, 520, 620);
            dialog.setScene(scene);
            dialog.showAndWait();
            controller.getResult().ifPresent(this::setGameSettings);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to load " + ResourceManager.Asset.SETTINGS_DIALOG_FXML.path(), ex);
        }
    }

    private void applyAudioPreferences() {
        BackgroundMusicManager manager = BackgroundMusicManager.getInstance();
        manager.setEnabled(gameSettings.isBgmEnabled());
        manager.setMasterVolume(gameSettings.getBgmVolume());
        if (!gameSettings.isBgmEnabled()) {
            manager.stopBackgroundMusic();
            return;
        }
        switch (gameState) {
            case PLAYING -> manager.playGameTheme();
            case MENU, GAME_OVER -> manager.playMenuTheme();
            case PAUSED -> {
                // keep current track while paused
            }
            default -> {
            }
        }
    }

    private void applyVisualPreferences() {
        BrickColorPalette.setProfile(resolvePalette(gameSettings.getColorAssistMode()));
        outlinesEnabled = gameSettings.isPieceOutlineEnabled();
        if (nextQueuePanel != null) {
            nextQueuePanel.setOutlinesEnabled(outlinesEnabled);
            nextQueuePanel.refreshColors();
        }
        refreshCachedLayers();
    }

    private PaletteProfile resolvePalette(ColorAssistMode mode) {
        if (mode == ColorAssistMode.HIGH_CONTRAST) {
            return PaletteProfile.HIGH_CONTRAST;
        }
        return PaletteProfile.CLASSIC;
    }

    private void refreshCachedLayers() {
        if (lastBoardMatrix != null && displayMatrix != null) {
            for (int i = 2; i < Math.min(lastBoardMatrix.length, displayMatrix.length); i++) {
                int[] boardRow = lastBoardMatrix[i];
                Rectangle[] rectanglesRow = displayMatrix[i];
                if (boardRow == null || rectanglesRow == null) {
                    continue;
                }
                for (int j = 0; j < Math.min(boardRow.length, rectanglesRow.length); j++) {
                    setRectangleData(boardRow[j], rectanglesRow[j]);
                }
            }
        }
        if (lastViewData != null) {
            applyBrickView(lastViewData);
        }
    }

    private void rememberBoardState(int[][] matrix) {
        lastBoardMatrix = copyMatrix(matrix);
    }

    private int[][] copyMatrix(int[][] source) {
        if (source == null) {
            return null;
        }
        int[][] copy = new int[source.length][];
        for (int i = 0; i < source.length; i++) {
            copy[i] = source[i] != null ? source[i].clone() : null;
        }
        return copy;
    }

    private void rememberViewData(ViewData data) {
        if (data == null) {
            lastViewData = null;
            return;
        }
        lastViewData = new ViewData(
                data.getBrickData(),
                data.getxPosition(),
                data.getyPosition(),
                data.getGhostYPosition(),
                data.getNextBricksData(),
                data.getGameOverRow());
    }

    private void recordLeaderboardResult() {
        List<HighScoreEntry> leaderboard;
        HighScoreEntry highlight = null;
        GameConfig.GameMode currentMode = resolveCurrentMode();
        String modeLabel = describeCurrentMode();
        if (boundScore != null) {
            java.time.Duration elapsed = computeSessionDuration();
            HighScoreEntry newEntry = HighScoreEntry.create(boundScore.scoreProperty().get(), modeLabel, elapsed);
            highScoreService.recordScore(newEntry);
            leaderboard = highScoreService.fetchLeaderboardForMode(currentMode);
            highlight = leaderboard.stream()
                    .filter(newEntry::equals)
                    .findFirst()
                    .orElse(null);
        } else {
            leaderboard = highScoreService.fetchLeaderboardForMode(currentMode);
        }
        sessionStartInstant = null;
        if (gameOverPanel != null) {
            gameOverPanel.setLeaderboardTitle(buildLeaderboardTitle(modeLabel));
            gameOverPanel.setLeaderboard(leaderboard, highlight);
        }
    }

    private java.time.Duration computeSessionDuration() {
        if (sessionStartInstant == null) {
            return java.time.Duration.ZERO;
        }
        return java.time.Duration.between(sessionStartInstant, Instant.now());
    }

    private void markSessionStart() {
        sessionStartInstant = Instant.now();
    }

    private String describeCurrentMode() {
        return gameMode != null ? gameMode.toString() : "Classic";
    }

    private Tooltip buildHelpTooltip() {
        List<String> shortcuts = helpContentProvider.getShortcutSummaries();
        String text = shortcuts.isEmpty()
                ? "Esc: Pause\nN: Restart"
                : String.join(System.lineSeparator(), shortcuts);
        Tooltip tooltip = new Tooltip(text);
        tooltip.setShowDelay(Duration.millis(200));
        tooltip.setHideDelay(Duration.ZERO);
        return tooltip;
    }

    public void prepareModeSession() {
        resetModeObjectivesInternal();
    }

    private void resetModeObjectivesInternal() {
        stopModeTimer();
        fixedLinesCleared = 0;
        switch (gameMode) {
            case TIMED -> startModeTimer();
            case FIXED_LINES -> {
                remainingModeSeconds = 0;
                updateModeStatus();
            }
            default -> updateModeStatus();
        }
    }

    private void startModeTimer() {
        stopModeTimer();
        remainingModeSeconds = TIMED_MODE_SECONDS;
        updateModeStatus();
        modeTimer = new Timeline(new KeyFrame(Duration.seconds(1), ae -> {
            remainingModeSeconds = Math.max(0, remainingModeSeconds - 1);
            updateModeStatus();
            if (remainingModeSeconds <= 0) {
                stopModeTimer();
                showNotification("Time up!");
                handleTimedModeExpiration();
            }
        }));
        modeTimer.setCycleCount(Timeline.INDEFINITE);
        modeTimer.play();
    }

    private void stopModeTimer() {
        if (modeTimer != null) {
            modeTimer.stop();
            modeTimer = null;
        }
    }

    private void pauseModeTimer() {
        if (modeTimer != null) {
            modeTimer.pause();
        }
    }

    private void resumeModeTimerIfNeeded() {
        if (modeTimer != null && gameMode == GameConfig.GameMode.TIMED) {
            modeTimer.play();
        }
    }

    private void handleModeLineProgress(int linesRemoved) {
        if (gameMode != GameConfig.GameMode.FIXED_LINES || linesRemoved <= 0) {
            return;
        }
        fixedLinesCleared += linesRemoved;
        if (fixedLinesCleared >= FIXED_LINES_TARGET) {
            showNotification("Mission complete!");
            setGameOverMessage("MISSION COMPLETE");
            gameOver();
        } else {
            updateModeStatus();
        }
    }

    private void updateModeStatus() {
        if (hudPanel == null) {
            return;
        }
        String status;
        switch (gameMode) {
            case TIMED -> status = String.format("Mode: Timed (%ds left)", Math.max(0, remainingModeSeconds));
            case FIXED_LINES ->
                status = String.format("Mode: 40 lines (%d left)", Math.max(0, FIXED_LINES_TARGET - fixedLinesCleared));
            default -> status = "Mode: Endless";
        }
        hudPanel.setModeStatus(status);
    }

    private void showNotification(String text) {
        if (groupNotification == null || text == null || text.isBlank()) {
            return;
        }
        NotificationPanel panel = new NotificationPanel(text);
        groupNotification.getChildren().add(panel);
        panel.showScore(groupNotification.getChildren());
    }

    private void hideGameOverPanel() {
        if (gameOverPanel == null) {
            return;
        }
        gameOverPanel.setVisible(false);
        gameOverPanel.setManaged(false);
        gameOverPanel.setOutcomeMessage(null);
        gameOverPanel.setLeaderboardTitle(null);
    }

    private void setGameOverMessage(String message) {
        if (gameOverPanel != null) {
            gameOverPanel.setOutcomeMessage(message);
        }
    }

    private void handleTimedModeExpiration() {
        setGameOverMessage("TIME OUT");
        gameOver();
    }

    private String buildLeaderboardTitle(String modeLabel) {
        if (modeLabel == null || modeLabel.isBlank()) {
            return null;
        }
        return String.format("Top %s Scores", modeLabel);
    }

    private GameConfig.GameMode resolveCurrentMode() {
        return gameMode != null ? gameMode : GameConfig.GameMode.ENDLESS;
    }

}



================================================
FILE: src/main/java/com/comp2042/ui/HudPanel.java
================================================
package com.comp2042.ui;

import com.comp2042.game.Score;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Stylised HUD panel that mirrors the "run telemetry" card in the HTML reference.
 * Handles scoreboard bindings while exposing extra slots for level/mode badges.
 */
public class HudPanel extends VBox {

    private final Label scoreValue;
    private final Label comboValue;
    private final Label b2bValue;
    private final Label levelValue;
    private final Label modeValue;
    private final VBox statsContainer = new VBox(12);

    public HudPanel() {
        setSpacing(16);
        setAlignment(Pos.TOP_LEFT);
        getStyleClass().add("hud-panel");

        Label kicker = new Label("RUN TELEMETRY");
        kicker.getStyleClass().add("hud-panel-kicker");

        Label title = new Label("Live Stack Data");
        title.getStyleClass().add("hud-panel-title");

        statsContainer.getStyleClass().add("hud-stats");

        scoreValue = addStat("Score", "0", true);
        comboValue = addStat("Combo", "0", false);
        b2bValue = addStat("Back-to-Back", "--", false);
        levelValue = addStat("Level", "1", false);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        modeValue = new Label("Mode: Endless");
        modeValue.getStyleClass().add("hud-mode-status");
        modeValue.setWrapText(true);

        Label hint = new Label("ESC pauses · F1 opens help");
        hint.getStyleClass().add("hud-footer-hint");

        getChildren().addAll(kicker, title, statsContainer, spacer, modeValue, hint);
    }

    public void bindScore(Score score) {
        if (score == null) {
            return;
        }
        bindScoreProperty(score.scoreProperty());
        bindComboProperty(score.comboProperty());
        bindB2BProperty(score.backToBackProperty());
    }

    private void bindScoreProperty(IntegerProperty scoreProperty) {
        scoreValue.textProperty().bind(scoreProperty.asString("%,d"));
    }

    private void bindComboProperty(IntegerProperty comboProperty) {
        comboValue.textProperty().bind(comboProperty.asString("%d"));
    }

    private void bindB2BProperty(IntegerProperty b2bProperty) {
        b2bValue.textProperty().bind(Bindings.createStringBinding(
                () -> b2bProperty.get() > 0
                        ? String.format("%d", b2bProperty.get())
                        : "--",
                b2bProperty));
    }

    public void setLevel(int level) {
        levelValue.setText(String.valueOf(level));
    }

    public void setModeStatus(String status) {
        modeValue.setText(status != null ? status : "");
    }

    private Label addStat(String title, String initialValue, boolean emphasize) {
        Label header = new Label(title.toUpperCase());
        header.getStyleClass().add("hud-label");

        Label value = new Label(initialValue);
        value.getStyleClass().add("hud-value");
        if (emphasize) {
            value.getStyleClass().add("hud-value-lg");
        }

        VBox block = new VBox(4, header, value);
        block.getStyleClass().addAll("hud-panel-row", "hud-stat-block");
        statsContainer.getChildren().add(block);
        return value;
    }
}



================================================
FILE: src/main/java/com/comp2042/ui/NextQueuePanel.java
================================================
package com.comp2042.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;

import java.util.List;

/**
 * Displays the Guideline-style NEXT queue (five previews) beside the board.
 * Nodes are created once and re-used to avoid frame drops when a new bag is drawn.
 */
public class NextQueuePanel extends VBox {

    private static final int MAX_QUEUE = 5;
    private static final int CELL_SIZE = 16;
    private static final int SLOT_ROWS = 4;
    private static final int SLOT_COLS = 4;

    private final VBox queueContainer = new VBox(8);
    private final QueueSlot[] slots = new QueueSlot[MAX_QUEUE];
    private boolean outlinesEnabled;

    public NextQueuePanel() {
        setSpacing(6);
        setAlignment(Pos.TOP_CENTER);
        getStyleClass().add("next-queue-panel");

        Label title = new Label("NEXT");
        title.getStyleClass().add("next-queue-title");

        queueContainer.setAlignment(Pos.TOP_CENTER);
        queueContainer.getStyleClass().add("next-queue-container");

        for (int i = 0; i < MAX_QUEUE; i++) {
            slots[i] = createSlot();
            queueContainer.getChildren().add(slots[i].grid);
        }

        getChildren().addAll(title, queueContainer);
    }

    public void setQueue(List<int[][]> nextBricks) {
        for (int i = 0; i < slots.length; i++) {
            int[][] data = (nextBricks != null && i < nextBricks.size()) ? nextBricks.get(i) : null;
            slots[i].apply(data);
        }
    }

    public void setOutlinesEnabled(boolean enabled) {
        outlinesEnabled = enabled;
        refreshAppearance();
    }

    public void refreshColors() {
        refreshAppearance();
    }

    private void refreshAppearance() {
        for (QueueSlot slot : slots) {
            slot.repaint();
        }
    }

    private QueueSlot createSlot() {
        GridPane grid = new GridPane();
        grid.setHgap(1);
        grid.setVgap(1);
        grid.setAlignment(Pos.CENTER);
        Rectangle[][] cells = new Rectangle[SLOT_ROWS][SLOT_COLS];
        for (int row = 0; row < SLOT_ROWS; row++) {
            for (int col = 0; col < SLOT_COLS; col++) {
                Rectangle cell = new Rectangle(CELL_SIZE, CELL_SIZE);
                cell.getStyleClass().add("next-queue-cell");
                cell.setArcHeight(8);
                cell.setArcWidth(8);
                cell.setFill(BrickColorPalette.resolve(0));
                cells[row][col] = cell;
                grid.add(cell, col, row);
            }
        }
        return new QueueSlot(grid, cells);
    }

    private void applyOutline(Rectangle cell) {
        if (outlinesEnabled) {
            cell.setStroke(Color.rgb(15, 15, 20, 0.7));
            cell.setStrokeWidth(0.6);
        } else {
            cell.setStrokeWidth(0);
            cell.setStroke(null);
        }
    }

    private static int[][] copyMatrix(int[][] matrix) {
        if (matrix == null) {
            return null;
        }
        int[][] copy = new int[matrix.length][];
        for (int i = 0; i < matrix.length; i++) {
            copy[i] = matrix[i] != null ? matrix[i].clone() : null;
        }
        return copy;
    }

    private final class QueueSlot {
        private final GridPane grid;
        private final Rectangle[][] cells;
        private int[][] lastMatrix;

        private QueueSlot(GridPane grid, Rectangle[][] cells) {
            this.grid = grid;
            this.cells = cells;
        }

        private void apply(int[][] matrix) {
            this.lastMatrix = copyMatrix(matrix);
            repaint();
        }

        private void repaint() {
            for (int row = 0; row < cells.length; row++) {
                for (int col = 0; col < cells[row].length; col++) {
                    int value = resolveValue(row, col);
                    Rectangle cell = cells[row][col];
                    cell.setFill(BrickColorPalette.resolve(value));
                    applyOutline(cell);
                }
            }
        }

        private int resolveValue(int row, int col) {
            if (lastMatrix == null
                    || row >= lastMatrix.length
                    || lastMatrix[row] == null
                    || col >= lastMatrix[row].length) {
                return 0;
            }
            return lastMatrix[row][col];
        }
    }
}



================================================
FILE: src/main/java/com/comp2042/ui/NotificationPanel.java
================================================
package com.comp2042.ui;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.Effect;
import javafx.scene.effect.Glow;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * Disposable overlay used to show combo/bonus notifications above the
 * playfield.
 */
public class NotificationPanel extends BorderPane {

    public NotificationPanel(String text) {
        setMinHeight(200);
        setMinWidth(220);
        setMouseTransparent(true);
        setPickOnBounds(false);
        setBackground(javafx.scene.layout.Background.EMPTY);
        getStyleClass().add("notification-panel");
        final Label score = new Label(text);
        score.getStyleClass().add("bonusStyle");
        final Effect glow = new Glow(0.6);
        score.setEffect(glow);
        score.setTextFill(Color.WHITE);
        setCenter(score);

    }

    public void showScore(ObservableList<Node> list) {
        FadeTransition ft = new FadeTransition(Duration.millis(2000), this);
        TranslateTransition tt = new TranslateTransition(Duration.millis(2500), this);
        tt.setToY(this.getLayoutY() - 40);
        ft.setFromValue(1);
        ft.setToValue(0);
        ParallelTransition transition = new ParallelTransition(tt, ft);
        transition.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                list.remove(NotificationPanel.this);
            }
        });
        transition.play();
    }
}



================================================
FILE: src/main/java/com/comp2042/ui/anim/TetrisLogoView.java
================================================
package com.comp2042.ui.anim;

import java.util.Random;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * Animated "Tetris FX" style logo with glitch overlays.
 */
public class TetrisLogoView extends StackPane {

    private static final Color COLOR_CYAN = Color.web("#00f3ff");
    private static final Color COLOR_MAGENTA = Color.web("#ff0055");
    private static final double GLITCH_INTERVAL_MS = 70;

    private final Random random = new Random();
    private final Text glitchMagenta;
    private final Text glitchCyan;
    private final Timeline glitchTimeline;

    private final Text mainText;
    private final Timeline colorTimeline;
    private double hueOffset = 0;

    public TetrisLogoView(String text) {
        setPickOnBounds(false);
        setMouseTransparent(true);

        Font font = loadLogoFont();
        mainText = buildMainText(text, font);
        glitchMagenta = buildGlitchLayer(text, font, COLOR_MAGENTA);
        glitchCyan = buildGlitchLayer(text, font, COLOR_CYAN);

        getChildren().addAll(glitchMagenta, glitchCyan, mainText);

        glitchTimeline = new Timeline(new KeyFrame(Duration.millis(GLITCH_INTERVAL_MS), e -> runGlitchCycle()));
        glitchTimeline.setCycleCount(Animation.INDEFINITE);

        colorTimeline = new Timeline(new KeyFrame(Duration.millis(50), e -> runColorCycle()));
        colorTimeline.setCycleCount(Animation.INDEFINITE);

        sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene == null) {
                glitchTimeline.stop();
                colorTimeline.stop();
            } else if (glitchTimeline.getStatus() != Animation.Status.RUNNING) {
                glitchTimeline.play();
                colorTimeline.play();
            }
        });
    }

    public void play() {
        if (glitchTimeline.getStatus() != Animation.Status.RUNNING) {
            glitchTimeline.play();
            colorTimeline.play();
        }
    }

    public void stop() {
        glitchTimeline.stop();
        colorTimeline.stop();
    }

    private Text buildMainText(String text, Font font) {
        Text mainText = new Text(text);
        mainText.setFont(font);
        updateMainTextColor(mainText, COLOR_CYAN);
        return mainText;
    }

    private void updateMainTextColor(Text text, Color baseColor) {
        text.setFill(new LinearGradient(
                0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0.0, Color.WHITE),
                new Stop(0.65, Color.WHITE),
                new Stop(1.0, baseColor)));

        DropShadow glow = new DropShadow();
        glow.setColor(baseColor.deriveColor(0, 1, 1, 0.65));
        glow.setRadius(30);
        glow.setSpread(0.25);
        text.setEffect(glow);
    }

    private Text buildGlitchLayer(String text, Font font, Color color) {
        Text glitchLayer = new Text(text);
        glitchLayer.setFont(font);
        glitchLayer.setFill(color);
        glitchLayer.setOpacity(0.85);
        glitchLayer.setBlendMode(BlendMode.ADD);
        return glitchLayer;
    }

    private void runGlitchCycle() {
        applyGlitch(glitchMagenta, -3.0, -0.5);
        applyGlitch(glitchCyan, 0.5, 3.0);
    }

    private void runColorCycle() {
        hueOffset += 2; // Shift hue by 2 degrees each frame
        if (hueOffset > 360)
            hueOffset -= 360;

        Color dynamicColor = COLOR_CYAN.deriveColor(hueOffset, 1.0, 1.0, 1.0);
        updateMainTextColor(mainText, dynamicColor);
    }

    private void applyGlitch(Text textNode, double minOffset, double maxOffset) {
        if (random.nextInt(100) > 15) {
            textNode.setVisible(false);
            textNode.setTranslateX(0);
            textNode.setClip(null);
            return;
        }

        textNode.setVisible(true);
        double offsetX = minOffset + (maxOffset - minOffset) * random.nextDouble();
        textNode.setTranslateX(offsetX);

        double textHeight = textNode.getLayoutBounds().getHeight();
        double textWidth = textNode.getLayoutBounds().getWidth();
        double sliceY = random.nextDouble() * textHeight;
        double sliceHeight = Math.max(4, random.nextDouble() * textHeight * 0.25);
        Rectangle clip = new Rectangle(0, sliceY, textWidth, sliceHeight);
        textNode.setClip(clip);
    }

    private Font loadLogoFont() {
        // Try loading Orbitron or Impact, fall back to Arial Black
        Font preferred = Font.font("Orbitron", FontWeight.EXTRA_BOLD, FontPosture.REGULAR, 56);
        if (preferred.getFamily().toLowerCase().contains("orbitron")) {
            return preferred;
        }

        // Try Impact (matches CSS design)
        Font impact = Font.font("Impact", FontWeight.BOLD, 56);
        if (impact.getFamily().toLowerCase().contains("impact")) {
            return impact;
        }

        // Final fallback
        return Font.font("Arial Black", FontWeight.EXTRA_BOLD, 56);
    }
}



================================================
FILE: src/main/java/com/comp2042/ui/input/AutoRepeatHandler.java
================================================
package com.comp2042.ui.input;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.util.Duration;

/**
 * Handles DAS/ARR style repeats for movement keys.
 */
public class AutoRepeatHandler {

    private final Runnable action;
    private final PauseTransition delayTransition = new PauseTransition();
    private Timeline repeatTimeline;
    private Duration arrDuration = Duration.millis(30);
    private Duration dasDuration = Duration.millis(150);

    public AutoRepeatHandler(Runnable action, Duration dasDuration, Duration arrDuration) {
        this.action = action;
        configure(dasDuration, arrDuration);
    }

    public void configure(Duration dasDuration, Duration arrDuration) {
        stop();
        this.dasDuration = dasDuration != null ? dasDuration : Duration.ZERO;
        Duration interval = (arrDuration != null && !arrDuration.isIndefinite())
                ? arrDuration
                : Duration.millis(1);
        if (interval.lessThanOrEqualTo(Duration.ZERO)) {
            interval = Duration.millis(1);
        }
        this.arrDuration = interval;
        repeatTimeline = new Timeline(new KeyFrame(this.arrDuration, e -> action.run()));
        repeatTimeline.setCycleCount(Animation.INDEFINITE);
        delayTransition.setDuration(this.dasDuration);
        delayTransition.setOnFinished(e -> {
            action.run();
            repeatTimeline.playFromStart();
        });
    }

    public void start() {
        stop();
        action.run();
        if (repeatTimeline == null) {
            return;
        }
        if (dasDuration.lessThanOrEqualTo(Duration.ZERO)) {
            repeatTimeline.playFromStart();
        } else {
            delayTransition.playFromStart();
        }
    }

    public void stop() {
        delayTransition.stop();
        if (repeatTimeline != null) {
            repeatTimeline.stop();
        }
    }
}



================================================
FILE: src/main/resources/digital.ttf
================================================
[Binary file]


================================================
FILE: src/main/resources/gameLayout.fxml
================================================
<?import com.comp2042.ui.GameOverPanel?>
<?import com.comp2042.ui.HudPanel?>
<?import com.comp2042.ui.NextQueuePanel?>
<?import javafx.scene.Group?>
<?import javafx.scene.control.Button?>
<?import javafx.scene.control.Label?>
<?import javafx.scene.layout.BorderPane?>
<?import javafx.scene.layout.GridPane?>
<?import javafx.scene.layout.HBox?>
<?import javafx.scene.layout.Pane?>
<?import javafx.scene.layout.Region?>
<?import javafx.scene.layout.StackPane?>
<?import javafx.scene.layout.VBox?>
<?import java.net.URL?>

<StackPane fx:controller="com.comp2042.ui.GuiController"
           xmlns:fx="http://javafx.com/fxml"
           styleClass="gameplay-root">
    <children>
        <Pane styleClass="grid-overlay"/>
        <Pane styleClass="scanline-overlay"/>
        <BorderPane styleClass="gameplay-shell">
            <top>
                <HBox alignment="CENTER_LEFT" spacing="12" styleClass="gameplay-header">
                    <VBox spacing="4">
                        <Label text="NEOTETRIS OPS" styleClass="hud-panel-kicker"/>
                        <Label text="Live Run · Press ESC to pause" styleClass="gameplay-subtitle"/>
                    </VBox>
                    <Region HBox.hgrow="ALWAYS"/>
                    <Button fx:id="helpHintButton"
                            text="Help Overlay"
                            onAction="#onShowHelp"
                            styleClass="help-icon-button"/>
                </HBox>
            </top>
            <center>
                <HBox spacing="28" alignment="TOP_CENTER" styleClass="gameplay-columns">
                    <StackPane styleClass="board-stack">
                        <Pane prefWidth="210"
                              prefHeight="420"
                              minWidth="210"
                              minHeight="420"
                              maxWidth="210"
                              maxHeight="420"
                              styleClass="board-stage">
                            <BorderPane styleClass="gameBoard" fx:id="gameBoard">
                                <center>
                                    <StackPane fx:id="gameLayer" alignment="TOP_LEFT" styleClass="game-layer">
                                        <GridPane fx:id="gamePanel" hgap="1" vgap="1"/>
                                        <Pane fx:id="guidePane" mouseTransparent="true" pickOnBounds="false"/>
                                    </StackPane>
                                </center>
                            </BorderPane>
                            <GridPane fx:id="ghostPanel"
                                      vgap="1"
                                      hgap="1"
                                      mouseTransparent="true"
                                      pickOnBounds="false"/>
                            <GridPane fx:id="brickPanel"
                                      vgap="1"
                                      hgap="1"/>
                        </Pane>
                        <Group fx:id="groupNotification" pickOnBounds="false" StackPane.alignment="CENTER">
                            <VBox alignment="CENTER">
                                <GameOverPanel fx:id="gameOverPanel"/>
                            </VBox>
                        </Group>
                    </StackPane>
                    <NextQueuePanel fx:id="nextQueuePanel"/>
                    <VBox spacing="18" styleClass="sidebar">
                        <HudPanel fx:id="hudPanel"/>
                        <VBox spacing="6" styleClass="sidebar-legends">
                            <Label text="Quick Legends" styleClass="sidebar-title"/>
                            <Label text="Shift hold: charge DAS" styleClass="sidebar-hint"/>
                            <Label text="Space: instant drop + lock" styleClass="sidebar-hint"/>
                            <Label text="N: start a new seed" styleClass="sidebar-hint"/>
                        </VBox>
                    </VBox>
                </HBox>
            </center>
        </BorderPane>
        <VBox fx:id="pauseOverlay"
              alignment="CENTER"
              spacing="12"
              visible="false"
              managed="false"
              StackPane.alignment="CENTER"
              styleClass="pause-overlay">
            <Label text="Paused" styleClass="overlay-title"/>
            <Label text="Anything you change here applies instantly."
                   styleClass="overlay-subtitle"
                   wrapText="true"/>
            <HBox spacing="10" alignment="CENTER">
                <Button text="Resume" onAction="#onPauseResume" styleClass="btn-primary"/>
                <Button text="Settings" onAction="#onPauseSettings" styleClass="btn-secondary"/>
                <Button text="Main Menu" onAction="#onPauseMainMenu" styleClass="btn-secondary"/>
            </HBox>
        </VBox>
    </children>
    <stylesheets>
        <URL value="@window_style.css"/>
    </stylesheets>
</StackPane>



================================================
FILE: src/main/resources/HelpDialog.fxml
================================================
<?import javafx.geometry.Insets?>
<?import javafx.scene.control.Button?>
<?import javafx.scene.control.Label?>
<?import javafx.scene.control.TextArea?>
<?import javafx.scene.layout.BorderPane?>
<?import javafx.scene.layout.HBox?>
<?import javafx.scene.layout.Pane?>
<?import javafx.scene.layout.Region?>
<?import javafx.scene.layout.StackPane?>
<?import javafx.scene.layout.VBox?>
<?import java.net.URL?>

<StackPane xmlns="http://javafx.com/javafx"
           xmlns:fx="http://javafx.com/fxml"
           fx:controller="com.comp2042.app.HelpDialogController"
           styleClass="help-dialog-root">
    <children>
        <Pane styleClass="grid-overlay"/>
        <Pane styleClass="scanline-overlay"/>
        <BorderPane styleClass="help-dialog">
            <top>
                <VBox spacing="6" styleClass="help-header">
                    <Label text="Help &amp; Controls" styleClass="help-title"/>
                    <Label text="Live markdown feed · mirrors docs/help-content.md"
                           wrapText="true"
                           styleClass="help-subtitle"/>
                </VBox>
            </top>
            <center>
                <TextArea fx:id="helpContentArea"
                          editable="false"
                          wrapText="true"
                          prefRowCount="18"
                          styleClass="help-text-area"/>
            </center>
            <bottom>
                <HBox alignment="CENTER_RIGHT" spacing="10" styleClass="help-footer">
                    <Label text="F1 opens this window while playing."
                           wrapText="true"
                           styleClass="hint-chip"/>
                    <Region HBox.hgrow="ALWAYS"/>
                    <Button fx:id="closeButton" text="Close" onAction="#onClose" styleClass="btn-primary"/>
                </HBox>
            </bottom>
            <padding>
                <Insets top="20" right="20" bottom="16" left="20"/>
            </padding>
        </BorderPane>
    </children>
    <stylesheets>
        <URL value="@window_style.css"/>
    </stylesheets>
</StackPane>



================================================
FILE: src/main/resources/ModeSelect.fxml
================================================
<?import javafx.geometry.Insets?>
<?import javafx.scene.control.Button?>
<?import javafx.scene.control.Label?>
<?import javafx.scene.control.ScrollPane?>
<?import javafx.scene.layout.BorderPane?>
<?import javafx.scene.layout.HBox?>
<?import javafx.scene.layout.Pane?>
<?import javafx.scene.layout.Region?>
<?import javafx.scene.layout.StackPane?>
<?import javafx.scene.layout.VBox?>
<?import java.net.URL?>

<StackPane xmlns="http://javafx.com/javafx"
           xmlns:fx="http://javafx.com/fxml"
           fx:controller="com.comp2042.app.ModeSelectController"
           styleClass="mode-select-root">
    <children>
        <Pane styleClass="grid-overlay"/>
        <Pane styleClass="scanline-overlay"/>
        <BorderPane styleClass="mode-select-shell" prefWidth="920" prefHeight="620">
            <top>
                <VBox spacing="6" styleClass="mode-header">
                    <Label text="Mode Command Console" styleClass="mode-kicker"/>
                    <Label text="Choose how the board behaves before heading into the arena."
                           wrapText="true"
                           styleClass="mode-title"/>
                    <Label text="Each scenario tweaks gravity, victory conditions, and how big bonuses pay out."
                           wrapText="true"
                           styleClass="mode-subtitle"/>
                </VBox>
            </top>
            <center>
                <HBox spacing="24" alignment="TOP_CENTER">
                    <ScrollPane fitToWidth="true"
                                prefViewportHeight="420"
                                hbarPolicy="NEVER"
                                styleClass="mode-scroll">
                        <content>
                            <VBox fx:id="modeList" spacing="16" styleClass="mode-list"/>
                        </content>
                    </ScrollPane>
                    <VBox spacing="10" styleClass="mode-detail-card">
                        <Label text="SPOTLIGHT" styleClass="mode-detail-kicker"/>
                        <Label fx:id="detailTitle" text="Select a mode to view details" styleClass="mode-detail-title"/>
                        <Label fx:id="detailDescription"
                               wrapText="true"
                               text="Each ruleset tweaks gravity, win conditions, and scoring bonuses."
                               styleClass="mode-detail-text"/>
                        <Label fx:id="detailMeta" wrapText="true" styleClass="mode-detail-meta"/>
                    </VBox>
                </HBox>
            </center>
            <bottom>
                <VBox spacing="8">
                    <HBox alignment="CENTER_RIGHT" spacing="12" styleClass="mode-footer">
                        <Label text="TAB cycles cards · ENTER confirms" styleClass="hint-chip"/>
                        <Region HBox.hgrow="ALWAYS"/>
                        <Button text="Back" onAction="#onCancel" styleClass="btn-secondary"/>
                        <Button fx:id="startModeButton" text="Start Mode" onAction="#onConfirm" styleClass="btn-primary"/>
                    </HBox>
                    <Label text="Modes can also be swapped mid-session from the pause overlay."
                           styleClass="mode-footer-subtext"
                           wrapText="true"/>
                </VBox>
            </bottom>
            <padding>
                <Insets top="28" right="28" bottom="24" left="28"/>
            </padding>
        </BorderPane>
    </children>
    <stylesheets>
        <URL value="@window_style.css"/>
    </stylesheets>
</StackPane>



================================================
FILE: src/main/resources/SettingsDialog.fxml
================================================
<?import javafx.geometry.Insets?>
<?import javafx.scene.control.Button?>
<?import javafx.scene.control.CheckBox?>
<?import javafx.scene.control.ComboBox?>
<?import javafx.scene.control.Label?>
<?import javafx.scene.control.ScrollPane?>
<?import javafx.scene.control.Slider?>
<?import javafx.scene.control.TextField?>
<?import javafx.scene.control.Tooltip?>
<?import javafx.scene.layout.BorderPane?>
<?import javafx.scene.layout.ColumnConstraints?>
<?import javafx.scene.layout.GridPane?>
<?import javafx.scene.layout.HBox?>
<?import javafx.scene.layout.Pane?>
<?import javafx.scene.layout.Region?>
<?import javafx.scene.layout.StackPane?>
<?import javafx.scene.layout.VBox?>
<?import java.net.URL?>

<StackPane xmlns="http://javafx.com/javafx"
           xmlns:fx="http://javafx.com/fxml"
           fx:controller="com.comp2042.app.SettingsController"
           styleClass="settings-dialog-root">
    <children>
        <Pane styleClass="grid-overlay"/>
        <Pane styleClass="scanline-overlay"/>
        <BorderPane styleClass="settings-dialog" prefWidth="960" prefHeight="640" maxWidth="1200">
            <top>
                <VBox spacing="6" styleClass="settings-header">
                    <Label text="Systems Console" styleClass="settings-kicker"/>
                    <Label text="Tune repeat timings, bindings, and presentation without leaving your run."
                           wrapText="true"
                           styleClass="settings-title"/>
                </VBox>
            </top>
            <center>
                <ScrollPane fitToWidth="true"
                            hbarPolicy="NEVER"
                            styleClass="settings-scroll-pane">
                    <content>
                        <GridPane hgap="24" vgap="18" styleClass="settings-content">
                            <columnConstraints>
                                <ColumnConstraints percentWidth="50"/>
                                <ColumnConstraints percentWidth="50"/>
                            </columnConstraints>

                            <VBox spacing="8" GridPane.columnSpan="2" GridPane.rowIndex="0" styleClass="callout-card">
                                <Label text="INFO" styleClass="settings-section-title"/>
                                <Label fx:id="infoLabel"
                                       text="Select any ? icon to learn how that option affects feel or readability."
                                       wrapText="true"
                                       styleClass="info-text"/>
                            </VBox>

                            <VBox spacing="12" GridPane.columnIndex="0" GridPane.rowIndex="1" styleClass="settings-section">
                                <Label text="GAMEPLAY TIMERS" styleClass="settings-section-title"/>
                                <VBox spacing="10">
                                    <HBox alignment="CENTER_LEFT" spacing="8" styleClass="form-pair">
                                        <Label text="DAS (ms)" styleClass="form-label">
                                            <tooltip><Tooltip text="Delay before a held move key begins repeating."/></tooltip>
                                        </Label>
                                        <Button text="?" focusTraversable="false" onAction="#showInfo" id="info-das" styleClass="btn-info"/>
                                    </HBox>
                                    <TextField fx:id="dasField"/>

                                    <HBox alignment="CENTER_LEFT" spacing="8" styleClass="form-pair">
                                        <Label text="ARR (ms)" styleClass="form-label">
                                            <tooltip><Tooltip text="Interval between repeats after DAS expires."/></tooltip>
                                        </Label>
                                        <Button text="?" focusTraversable="false" onAction="#showInfo" id="info-arr" styleClass="btn-info"/>
                                    </HBox>
                                    <TextField fx:id="arrField"/>

                                    <HBox alignment="CENTER_LEFT" spacing="8" styleClass="form-pair">
                                        <Label text="Soft Drop Multiplier" styleClass="form-label">
                                            <tooltip><Tooltip text="Scales fall speed while holding the soft drop key."/></tooltip>
                                        </Label>
                                        <Button text="?" focusTraversable="false" onAction="#showInfo" id="info-sdf" styleClass="btn-info"/>
                                    </HBox>
                                    <TextField fx:id="sdfField"/>
                                </VBox>
                            </VBox>

                            <VBox spacing="12" GridPane.columnIndex="0" GridPane.rowIndex="2" styleClass="settings-section">
                                <Label text="AUDIO" styleClass="settings-section-title"/>
                                <CheckBox fx:id="bgmCheckBox" text="Enable background music (menu &amp; gameplay)"/>
                                <HBox spacing="10" alignment="CENTER_LEFT">
                                    <Label text="BGM Volume" styleClass="form-label"/>
                                    <Slider fx:id="bgmVolumeSlider" min="0" max="100" prefWidth="200"/>
                                </HBox>
                            </VBox>

                            <VBox spacing="12" GridPane.columnIndex="0" GridPane.rowIndex="3" styleClass="settings-section">
                                <Label text="ACCESSIBILITY" styleClass="settings-section-title"/>
                                <HBox alignment="CENTER_LEFT" spacing="8" styleClass="form-pair">
                                    <Label text="Color Assist Mode" styleClass="form-label">
                                        <tooltip><Tooltip text="Swap to bold palettes tailored for common color vision deficiencies."/></tooltip>
                                    </Label>
                                    <Button text="?" focusTraversable="false" onAction="#showInfo" id="info-color-assist" styleClass="btn-info"/>
                                </HBox>
                                <ComboBox fx:id="colorAssistCombo" prefWidth="220"/>

                                <HBox alignment="CENTER_LEFT" spacing="8" styleClass="form-pair">
                                    <Label text="Piece Outlines" styleClass="form-label">
                                        <tooltip><Tooltip text="Adds dark borders so stacked pieces remain readable."/></tooltip>
                                    </Label>
                                    <Button text="?" focusTraversable="false" onAction="#showInfo" id="info-outline" styleClass="btn-info"/>
                                </HBox>
                                <CheckBox fx:id="outlineCheckBox" text="Enable outlines on active, ghost, and preview pieces"/>
                            </VBox>

                            <VBox spacing="12" GridPane.columnIndex="1" GridPane.rowIndex="1" GridPane.rowSpan="3" styleClass="settings-section">
                                <Label text="KEY BINDINGS" styleClass="settings-section-title"/>

                                <VBox spacing="10">
                                    <HBox styleClass="keybind-row">
                                        <HBox alignment="CENTER_LEFT" spacing="6" HBox.hgrow="ALWAYS">
                                            <Label text="Move Left" styleClass="form-label">
                                                <tooltip><Tooltip text="Shift the piece one column to the left."/></tooltip>
                                            </Label>
                                            <Button text="?" focusTraversable="false" onAction="#showInfo" id="info-move-left" styleClass="btn-info"/>
                                        </HBox>
                                        <TextField fx:id="leftKeyField" styleClass="key-badge" editable="false"/>
                                    </HBox>

                                    <HBox styleClass="keybind-row">
                                        <HBox alignment="CENTER_LEFT" spacing="6" HBox.hgrow="ALWAYS">
                                            <Label text="Move Right" styleClass="form-label">
                                                <tooltip><Tooltip text="Shift the piece one column to the right."/></tooltip>
                                            </Label>
                                            <Button text="?" focusTraversable="false" onAction="#showInfo" id="info-move-right" styleClass="btn-info"/>
                                        </HBox>
                                        <TextField fx:id="rightKeyField" styleClass="key-badge" editable="false"/>
                                    </HBox>

                                    <HBox styleClass="keybind-row">
                                        <HBox alignment="CENTER_LEFT" spacing="6" HBox.hgrow="ALWAYS">
                                            <Label text="Soft Drop" styleClass="form-label">
                                                <tooltip><Tooltip text="Speed up descent while held without forcing lock."/></tooltip>
                                            </Label>
                                            <Button text="?" focusTraversable="false" onAction="#showInfo" id="info-soft-drop" styleClass="btn-info"/>
                                        </HBox>
                                        <TextField fx:id="downKeyField" styleClass="key-badge" editable="false"/>
                                    </HBox>

                                    <HBox styleClass="keybind-row">
                                        <HBox alignment="CENTER_LEFT" spacing="6" HBox.hgrow="ALWAYS">
                                            <Label text="Hard Drop" styleClass="form-label">
                                                <tooltip><Tooltip text="Instantly drop to ghost position and lock."/></tooltip>
                                            </Label>
                                            <Button text="?" focusTraversable="false" onAction="#showInfo" id="info-hard-drop" styleClass="btn-info"/>
                                        </HBox>
                                        <TextField fx:id="hardDropKeyField" styleClass="key-badge" editable="false"/>
                                    </HBox>

                                    <HBox styleClass="keybind-row">
                                        <HBox alignment="CENTER_LEFT" spacing="6" HBox.hgrow="ALWAYS">
                                            <Label text="Rotate CW" styleClass="form-label">
                                                <tooltip><Tooltip text="Rotate 90° clockwise."/></tooltip>
                                            </Label>
                                            <Button text="?" focusTraversable="false" onAction="#showInfo" id="info-rotate-cw" styleClass="btn-info"/>
                                        </HBox>
                                        <TextField fx:id="rotateCwField" styleClass="key-badge" editable="false"/>
                                    </HBox>

                                    <HBox styleClass="keybind-row">
                                        <HBox alignment="CENTER_LEFT" spacing="6" HBox.hgrow="ALWAYS">
                                            <Label text="Rotate CCW" styleClass="form-label">
                                                <tooltip><Tooltip text="Rotate 90° counter-clockwise."/></tooltip>
                                            </Label>
                                            <Button text="?" focusTraversable="false" onAction="#showInfo" id="info-rotate-ccw" styleClass="btn-info"/>
                                        </HBox>
                                        <TextField fx:id="rotateCcwField" styleClass="key-badge" editable="false"/>
                                    </HBox>

                                    <HBox styleClass="keybind-row">
                                        <HBox alignment="CENTER_LEFT" spacing="6" HBox.hgrow="ALWAYS">
                                            <Label text="New Game" styleClass="form-label">
                                                <tooltip><Tooltip text="Reset the field instantly and begin a new run."/></tooltip>
                                            </Label>
                                            <Button text="?" focusTraversable="false" onAction="#showInfo" id="info-new-game" styleClass="btn-info"/>
                                        </HBox>
                                        <TextField fx:id="newGameKeyField" styleClass="key-badge" editable="false"/>
                                    </HBox>
                                </VBox>
                            </VBox>

                            <Label fx:id="statusLabel"
                                   GridPane.columnSpan="2"
                                   GridPane.rowIndex="4"
                                   styleClass="status-label"/>
                        </GridPane>
                    </content>
                </ScrollPane>
            </center>
            <bottom>
                <HBox spacing="10" alignment="CENTER_RIGHT" styleClass="settings-footer">
                    <Button text="Reset Defaults" onAction="#onResetDefaults" styleClass="btn-secondary"/>
                    <Region HBox.hgrow="ALWAYS"/>
                    <Button text="Cancel" onAction="#onCancel" styleClass="btn-ghost"/>
                    <Button text="Save Changes" onAction="#onSave" styleClass="btn-primary"/>
                </HBox>
            </bottom>
            <padding>
                <Insets top="28" right="28" bottom="24" left="28"/>
            </padding>
        </BorderPane>
    </children>
    <stylesheets>
        <URL value="@window_style.css"/>
    </stylesheets>
</StackPane>



================================================
FILE: src/main/resources/StartMenu.fxml
================================================
<?import javafx.geometry.Insets?>
<?import javafx.scene.control.Button?>
<?import javafx.scene.control.Label?>
<?import javafx.scene.control.ScrollPane?>
<?import javafx.scene.layout.BorderPane?>
<?import javafx.scene.layout.HBox?>
<?import javafx.scene.layout.Pane?>
<?import javafx.scene.layout.Region?>
<?import javafx.scene.layout.StackPane?>
<?import javafx.scene.layout.VBox?>
<?import java.net.URL?>

<StackPane xmlns="http://javafx.com/javafx"
           xmlns:fx="http://javafx.com/fxml"
           fx:controller="com.comp2042.app.StartMenuController"
           styleClass="start-menu-root">
    <children>
        <Pane styleClass="grid-overlay"/>
        <Pane styleClass="scanline-overlay"/>
        <BorderPane styleClass="start-menu-shell">
            <center>
                <HBox spacing="36" alignment="CENTER" styleClass="start-menu-container">
                    <VBox spacing="18" alignment="TOP_LEFT" styleClass="menu-left">
                        <Label text="NEOTETRIS OPS DECK" styleClass="menu-kicker"/>
                        <StackPane fx:id="logoContainer" styleClass="logo-stack"/>
                        <Label text="Neo-retro drop simulation suite" styleClass="menu-subtitle"/>

                        <VBox spacing="12" styleClass="menu-hero">
                            <HBox spacing="18" styleClass="hero-metric-row">
                                <VBox spacing="4" styleClass="hero-metric">
                                    <Label text="CURRENT STATUS" styleClass="hero-metric-title"/>
                                    <Label text="STANDBY" styleClass="hero-metric-value"/>
                                </VBox>
                                <VBox spacing="4" styleClass="hero-metric">
                                    <Label text="CARTRIDGE" styleClass="hero-metric-title"/>
                                    <Label text="REV X3" styleClass="hero-metric-value"/>
                                </VBox>
                            </HBox>
                            <HBox spacing="6" styleClass="accent-bar">
                                <Region/>
                            </HBox>
                            <Label text="Select a mode, tune your telemetry, then deploy."
                                   wrapText="true"
                                   styleClass="hero-description"/>
                        </VBox>

                        <VBox spacing="12" prefWidth="320" styleClass="menu-buttons">
                            <Button fx:id="startButton" text="Start Game" onAction="#onStart" styleClass="btn-primary"/>
                            <Button fx:id="settingsButton" text="Settings" onAction="#onSettings" styleClass="btn-secondary"/>
                            <Button fx:id="helpButton" text="Help &amp; Controls" onAction="#onHelp" styleClass="btn-secondary"/>
                            <Button fx:id="exitButton" text="Exit" onAction="#onExit" styleClass="btn-danger"/>
                        </VBox>

                        <VBox spacing="6" styleClass="menu-quick-hints">
                            <Label text="↵  confirm · ESC  quit" styleClass="hint-chip"/>
                            <Button text="F1 opens help anywhere" styleClass="hint-chip" onAction="#onHelp"/>
                        </VBox>

                        <Region VBox.vgrow="ALWAYS"/>
                        <Label text="Press Start to choose between Endless, Sprint, or 40 Lines."
                               wrapText="true"
                               styleClass="menu-hint"/>
                    </VBox>

                    <BorderPane prefWidth="360" styleClass="leaderboard-panel">
                        <top>
                            <VBox spacing="6">
                                <HBox alignment="CENTER_LEFT" spacing="12" styleClass="lb-header">
                                    <Label text="Leaderboard by Mode" styleClass="lb-title"/>
                                    <Region HBox.hgrow="ALWAYS"/>
                                    <Button fx:id="clearScoresButton"
                                            text="Clear"
                                            onAction="#onClearScores"
                                            styleClass="btn-ghost compact"
                                            disable="true"
                                            visible="false"
                                            managed="false"/>
                                </HBox>
                                <Label text="Latest uploads per ruleset" styleClass="lb-subtitle"/>
                            </VBox>
                        </top>
                        <center>
                            <ScrollPane fitToWidth="true"
                                        hbarPolicy="NEVER"
                                        vbarPolicy="AS_NEEDED"
                                        styleClass="lb-scroll">
                                <content>
                                    <VBox fx:id="leaderboardSections" spacing="16" styleClass="lb-sections"/>
                                </content>
                            </ScrollPane>
                        </center>
                        <bottom>
                            <Label text="Scores update after each completed run."
                                   wrapText="true"
                                   styleClass="lb-footer"/>
                        </bottom>
                    </BorderPane>
                </HBox>
            </center>
            <padding>
                <Insets top="32" right="32" bottom="32" left="32"/>
            </padding>
        </BorderPane>
    </children>
    <stylesheets>
        <URL value="@window_style.css"/>
    </stylesheets>
</StackPane>



================================================
FILE: src/main/resources/window_style.css
================================================
﻿/* ===========================================================
   COMP2042 Neo-Retro Theme
   Shared JavaFX stylesheet for menus, dialogs, and gameplay.
   =========================================================== */

/* ---------- Core Palette & Utilities ---------- */
.root {
    -fx-font-family: "Segoe UI", "Rajdhani", "Arial", sans-serif;
    -fx-font-smoothing-type: lcd;
    -fx-background-color: #04060b;
    -fx-background-image: url("background_image.png");
    -fx-background-size: cover;
    -fx-background-position: center;
}

.grid-overlay {
    -fx-background-color:
        rgba(6, 10, 24, 0.92),
        linear-gradient(from 0px 0px to 0px 40px, repeat, rgba(0, 243, 255, 0.06) 0%, rgba(0, 243, 255, 0.06) 1px, transparent 1px, transparent 100%),
        linear-gradient(from 0px 0px to 40px 0px, repeat, rgba(0, 243, 255, 0.04) 0%, rgba(0, 243, 255, 0.04) 1px, transparent 1px, transparent 100%);
}

.scanline-overlay {
    -fx-background-color: linear-gradient(from 0px 0px to 0px 3px, repeat, rgba(255, 255, 255, 0.04) 0%, rgba(255, 255, 255, 0.04) 0.5px, transparent 0.5px, transparent 100%);
    -fx-opacity: 0.35;
}

.start-menu-shell,
.mode-select-shell,
.settings-dialog,
.help-dialog,
.gameplay-shell {
    -fx-background-color: rgba(8, 12, 28, 0.95);
    -fx-background-radius: 24;
    -fx-border-color: rgba(0, 243, 255, 0.35);
    -fx-border-radius: 24;
    -fx-padding: 32;
    -fx-effect: dropshadow(gaussian, rgba(0, 10, 30, 0.8), 30, 0.45, 0, 12);
}

.hint-chip {
    -fx-background-color: rgba(0, 243, 255, 0.12);
    -fx-text-fill: #9adcf3;
    -fx-font-size: 12px;
    -fx-padding: 4 12;
    -fx-background-radius: 999;
    -fx-effect: dropshadow(gaussian, rgba(0, 243, 255, 0.2), 10, 0.2, 0, 0);
}

/* ---------- Button System ---------- */
.btn-primary,
.btn-secondary,
.btn-danger,
.btn-ghost {
    -fx-font-family: "Rajdhani", "Segoe UI", sans-serif;
    -fx-font-size: 15px;
    -fx-font-weight: 700;
    -fx-cursor: hand;
    -fx-background-radius: 14;
    -fx-border-radius: 14;
    -fx-padding: 12 28;
    -fx-min-width: 200;
    -fx-transition: all 200ms ease;
}

.btn-primary {
    -fx-background-color: linear-gradient(to right, #00f3ff, #14b7ff);
    -fx-text-fill: #04050b;
    -fx-effect: dropshadow(gaussian, rgba(0, 243, 255, 0.55), 20, 0.3, 0, 4);
}

.btn-primary:hover {
    -fx-translate-y: -1;
    -fx-background-color: linear-gradient(to right, #32fbff, #35c8ff);
}

.btn-secondary {
    -fx-background-color: rgba(255, 255, 255, 0.08);
    -fx-border-color: rgba(255, 255, 255, 0.25);
    -fx-border-width: 1.5;
    -fx-text-fill: #c2d2f2;
}

.btn-secondary:hover {
    -fx-background-color: rgba(255, 255, 255, 0.16);
}

.btn-ghost {
    -fx-background-color: transparent;
    -fx-border-color: rgba(0, 243, 255, 0.35);
    -fx-border-width: 1.5;
    -fx-text-fill: #84f2ff;
}

.btn-ghost:hover {
    -fx-background-color: rgba(0, 243, 255, 0.1);
}

.btn-danger {
    -fx-background-color: rgba(255, 0, 85, 0.15);
    -fx-border-color: rgba(255, 0, 85, 0.7);
    -fx-text-fill: #ff7ba6;
}

.btn-danger:hover {
    -fx-background-color: #ff0055;
    -fx-text-fill: #04050b;
}

.btn-secondary.compact,
.btn-ghost.compact {
    -fx-min-width: 90;
    -fx-padding: 6 16;
    -fx-font-size: 13px;
}

.btn-info {
    -fx-background-color: rgba(0, 243, 255, 0.12);
    -fx-border-color: rgba(0, 243, 255, 0.7);
    -fx-border-width: 1;
    -fx-background-radius: 12;
    -fx-border-radius: 12;
    -fx-text-fill: #00f3ff;
    -fx-font-size: 11;
    -fx-font-weight: bold;
    -fx-pref-width: 24;
    -fx-pref-height: 24;
    -fx-padding: 0;
}

.btn-info:hover {
    -fx-background-color: rgba(0, 243, 255, 0.35);
    -fx-text-fill: #050508;
}

/* ---------- Start Menu ---------- */
.start-menu-root {
    -fx-padding: 24;
}

.start-menu-container {
    -fx-background-color: rgba(4, 6, 12, 0.85);
    -fx-border-color: rgba(0, 243, 255, 0.25);
    -fx-border-width: 1;
    -fx-border-radius: 20;
    -fx-background-radius: 20;
    -fx-padding: 32 38;
    -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.7), 35, 0.3, 0, 10);
}

.menu-left {
    -fx-max-width: 420;
}

.menu-kicker {
    -fx-text-fill: #ff0055;
    -fx-font-size: 12px;
    -fx-letter-spacing: 4px;
}

.logo-stack {
    -fx-min-height: 140;
    -fx-alignment: center-left;
}

.menu-subtitle {
    -fx-text-fill: #99a8d6;
    -fx-font-size: 14px;
}

.menu-hero {
    -fx-background-color: rgba(255, 255, 255, 0.02);
    -fx-padding: 16;
    -fx-background-radius: 16;
    -fx-border-color: rgba(0, 243, 255, 0.15);
    -fx-border-radius: 16;
}

.hero-metric-row {
    -fx-alignment: center-left;
}

.hero-metric-title {
    -fx-text-fill: #7b89c8;
    -fx-font-size: 11px;
    -fx-letter-spacing: 2px;
}

.hero-metric-value {
    -fx-text-fill: #f5faff;
    -fx-font-size: 20px;
    -fx-font-weight: bold;
}

.accent-bar {
    -fx-background-color: linear-gradient(to right, #ff0055, transparent);
    -fx-min-height: 2px;
    -fx-background-radius: 2;
}

.hero-description {
    -fx-text-fill: #a5bde8;
    -fx-font-size: 13px;
}

.menu-buttons .button {
    -fx-max-width: Infinity;
}

.menu-quick-hints {
    -fx-padding: 12 0 0 0;
}

.menu-hint {
    -fx-text-fill: #6b7aa5;
    -fx-font-size: 12px;
}

.leaderboard-panel {
    -fx-background-color: rgba(10, 14, 32, 0.92);
    background-color: rgba(10, 14, 32, 0.92);
    -fx-background-radius: 18;
    -fx-border-radius: 18;
    border-radius: 18px;
    -fx-border-color: rgba(0, 243, 255, 0.2);
    border-color: rgba(0, 243, 255, 0.2);
    -fx-padding: 48 24 24 24;
    padding: 48px 24px 24px 24px;
    -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.6), 30, 0.3, 0, 8);
}

.lb-header {
    -fx-border-color: transparent transparent rgba(255, 255, 255, 0.08) transparent;
    border-color: transparent transparent rgba(255, 255, 255, 0.08) transparent;
    -fx-border-width: 0 0 1 0;
    border-width: 0 0 1px 0;
    -fx-padding: 0 0 12 0;
    padding: 0 0 12px 0;
}

.lb-title {
    -fx-text-fill: #00f3ff;
    -fx-font-size: 18;
    -fx-font-weight: bold;
}

.lb-subtitle {
    -fx-text-fill: #99b8d7;
    -fx-font-size: 12;
}

.lb-footer {
    -fx-text-fill: #6f88b4;
    -fx-font-size: 11;
    font-size: 11px;
    -fx-padding: 12 0 0 0;
    padding: 12px 0 0 0;
}

.lb-scroll {
    -fx-background-color: transparent;
    -fx-border-color: transparent;
}

.lb-scroll .viewport,
.lb-scroll .content {
    -fx-background-color: transparent;
}

.lb-sections {
    -fx-padding: 6 4 0 0;
    padding: 6px 4px 0 0;
}

.leaderboard-section {
    -fx-background-color: rgba(255, 255, 255, 0.02);
    -fx-background-radius: 14;
    -fx-border-color: rgba(255, 255, 255, 0.06);
    -fx-border-radius: 14;
    -fx-padding: 12;
}

.leaderboard-section-title {
    -fx-text-fill: #7fe9ff;
    -fx-font-size: 13;
    -fx-letter-spacing: 1;
}

.lb-list {
    -fx-spacing: 4;
}

.leaderboard-placeholder {
    -fx-text-fill: #93a1c1;
    -fx-font-size: 12;
}

.leaderboard-entry {
    -fx-text-fill: #f5f7ff;
    -fx-font-family: "Consolas", "JetBrains Mono", monospace;
    -fx-font-size: 13;
}

.leaderboard-highlight {
    -fx-text-fill: #fcee0a;
    -fx-font-weight: bold;
}

/* ---------- Mode Select ---------- */
.mode-select-root {
    -fx-padding: 32;
}

.mode-header {
    -fx-padding: 0 0 12 0;
}

.mode-kicker {
    -fx-text-fill: #ff0055;
    -fx-font-size: 12;
    font-size: 12px;
    -fx-letter-spacing: 4;
    letter-spacing: 4px;
}

.mode-title {
    -fx-text-fill: #f5faff;
    -fx-font-size: 26;
    -fx-font-weight: bold;
}

.mode-subtitle {
    -fx-text-fill: #9cb4d6;
    -fx-font-size: 13;
}

.mode-scroll {
    -fx-background-color: transparent;
    -fx-border-color: rgba(255, 255, 255, 0.04);
    -fx-border-radius: 16;
    -fx-background-radius: 16;
    -fx-padding: 12;
}

.mode-scroll .viewport {
    -fx-background-color: transparent;
}

.mode-list {
    -fx-padding: 6;
}

.mode-card {
    -fx-background-color: rgba(255, 255, 255, 0.02);
    -fx-background-radius: 16;
    -fx-border-color: rgba(255, 255, 255, 0.07);
    -fx-border-radius: 16;
    -fx-padding: 16;
    -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.4), 18, 0.12, 0, 6);
}

.mode-card:selected,
.mode-card:focused,
.mode-card.selected {
    -fx-border-color: #00f3ff;
    -fx-effect: dropshadow(gaussian, rgba(0, 243, 255, 0.45), 25, 0.4, 0, 8);
}

.mode-card-title {
    -fx-text-fill: #f5faff;
    -fx-font-size: 18;
    -fx-font-weight: bold;
}

.mode-card-description {
    -fx-text-fill: #9fb4d6;
    -fx-font-size: 13;
}

.mode-badges {
    -fx-alignment: center-left;
}

.mode-badge {
    -fx-background-color: rgba(0, 243, 255, 0.15);
    -fx-background-radius: 999;
    -fx-text-fill: #84f2ff;
    -fx-font-size: 11;
    -fx-padding: 2 10;
}

.mode-detail-card {
    -fx-background-color: rgba(255, 255, 255, 0.03);
    -fx-background-radius: 18;
    -fx-border-radius: 18;
    -fx-border-color: rgba(0, 243, 255, 0.18);
    -fx-padding: 18;
    -fx-pref-width: 320;
}

.mode-detail-kicker {
    -fx-text-fill: #ff0055;
    -fx-font-size: 11;
    -fx-letter-spacing: 3;
}

.mode-detail-title {
    -fx-text-fill: #ffffff;
    -fx-font-size: 20;
    -fx-font-weight: bold;
}

.mode-detail-text {
    -fx-text-fill: #c2d2f2;
    -fx-font-size: 13;
}

.mode-detail-meta {
    -fx-text-fill: #9adcf3;
    -fx-font-size: 12;
}

.mode-footer {
    -fx-padding: 6 0 0 0;
}

.mode-footer-subtext {
    -fx-text-fill: #7a8ab9;
    -fx-font-size: 11;
}

/* ---------- Settings Dialog ---------- */
.settings-dialog-root {
    -fx-padding: 24;
}

.settings-header {
    -fx-padding: 0 0 12 0;
}

.settings-header .settings-kicker {
    -fx-text-fill: #ff0055;
    -fx-letter-spacing: 4;
    -fx-font-size: 12;
}

.settings-title {
    -fx-text-fill: #f8fbff;
    -fx-font-size: 22;
    -fx-font-weight: bold;
}

.settings-scroll-pane {
    -fx-background-color: transparent;
}

.settings-scroll-pane .viewport {
    -fx-background-color: transparent;
}

.settings-content {
    -fx-padding: 8 0 16 0;
}

.callout-card {
    -fx-background-color: rgba(0, 243, 255, 0.07);
    -fx-background-radius: 18;
    -fx-border-color: rgba(0, 243, 255, 0.25);
    -fx-border-radius: 18;
    -fx-padding: 14;
}

.settings-section {
    -fx-background-color: rgba(255, 255, 255, 0.02);
    -fx-background-radius: 16;
    -fx-border-color: rgba(255, 255, 255, 0.05);
    -fx-border-radius: 16;
    -fx-padding: 14;
}

.settings-section-title {
    -fx-text-fill: #7fe9ff;
    -fx-font-size: 13;
    -fx-letter-spacing: 2;
}

.info-text {
    -fx-text-fill: #d1ecff;
    -fx-font-size: 12;
}

.form-label {
    -fx-text-fill: #c8d6f5;
    -fx-font-size: 13;
}

.form-pair {
    -fx-alignment: center-left;
}

.settings-section .text-field,
.settings-section .combo-box,
.settings-section .slider {
    -fx-background-color: rgba(255, 255, 255, 0.08);
    -fx-text-fill: #ffffff;
    -fx-background-radius: 10;
    -fx-border-radius: 10;
    -fx-border-color: rgba(255, 255, 255, 0.15);
    -fx-border-width: 1;
}

.keybind-row {
    -fx-alignment: center-left;
    -fx-spacing: 12;
}

.key-badge {
    -fx-background-color: rgba(0, 243, 255, 0.15);
    -fx-background-radius: 12;
    -fx-border-radius: 12;
    -fx-border-color: rgba(0, 243, 255, 0.3);
    -fx-border-width: 1;
    -fx-text-fill: #f5ffff;
    -fx-pref-width: 120;
    -fx-alignment: center;
}

.status-label {
    -fx-text-fill: #7fe9ff;
    -fx-font-style: italic;
    -fx-padding: 8 0 0 0;
}

.settings-footer {
    -fx-padding: 18 0 0 0;
}

/* ---------- Help Dialog ---------- */
.help-dialog-root {
    -fx-padding: 24;
}

.help-header {
    -fx-padding: 0 0 8 0;
}

.help-title {
    -fx-text-fill: #f5faff;
    -fx-font-size: 22;
    -fx-font-weight: bold;
}

.help-subtitle {
    -fx-text-fill: #9ab6da;
    -fx-font-size: 12;
}

.help-text-area {
    -fx-control-inner-background: rgba(5, 7, 12, 0.85);
    -fx-text-fill: #d5e8ff;
    -fx-highlight-fill: rgba(0, 243, 255, 0.35);
    -fx-highlight-text-fill: #04101a;
    -fx-border-color: rgba(0, 243, 255, 0.15);
    -fx-border-radius: 12;
    -fx-background-radius: 12;
}

.help-footer {
    -fx-padding: 12 0 0 0;
}

/* ---------- Gameplay Scene ---------- */
.gameplay-root {
    -fx-padding: 18;
}

.gameplay-header {
    -fx-padding: 0 0 18 0;
}

.gameplay-subtitle {
    -fx-text-fill: #9ab6da;
    -fx-font-size: 12;
}

.help-icon-button {
    -fx-background-color: rgba(0, 243, 255, 0.15);
    -fx-text-fill: #f5faff;
    -fx-border-color: rgba(0, 243, 255, 0.35);
    -fx-border-radius: 20;
    -fx-background-radius: 20;
    -fx-padding: 8 18;
}

.gameplay-columns {
    -fx-alignment: top-center;
}

.board-stack {
    -fx-padding: 18;
}

.board-stage {
    -fx-background-color: rgba(2, 4, 10, 0.9);
    background-color: rgba(2, 4, 10, 0.9);
    -fx-border-color: rgba(0, 243, 255, 0.2);
    border-color: rgba(0, 243, 255, 0.2);
    -fx-border-width: 1;
    border-width: 1px;
    -fx-border-radius: 14;
    border-radius: 14px;
    -fx-background-radius: 14;
}

.gameBoard {
    -fx-background-color: rgba(4, 6, 12, 0.9);
    -fx-background-radius: 12;
}

.game-layer {}

.ghost-cell {
    -fx-opacity: 0.7;
    -fx-stroke: rgba(255, 255, 255, 0.45);
    -fx-stroke-dash-array: 4 3;
    -fx-arc-width: 8;
    -fx-arc-height: 8;
}

.sidebar {
    -fx-background-color: rgba(3, 5, 10, 0.85);
    -fx-padding: 18;
    -fx-background-radius: 24;
    -fx-border-radius: 24;
    -fx-border-color: rgba(255, 255, 255, 0.04);
    -fx-spacing: 18;
}

.sidebar-legends {
    -fx-background-color: rgba(255, 255, 255, 0.02);
    -fx-background-radius: 16;
    -fx-padding: 12;
}

.sidebar-title {
    -fx-text-fill: #f5faff;
    -fx-font-size: 12;
    -fx-letter-spacing: 2;
}

.sidebar-hint {
    -fx-text-fill: #9cb4d6;
    -fx-font-size: 12;
}

/* ---------- HUD Panel ---------- */
.hud-panel {
    -fx-background-color: linear-gradient(to bottom, rgba(0, 243, 255, 0.08), rgba(2, 4, 12, 0.8));
    -fx-background-radius: 20;
    -fx-border-radius: 20;
    -fx-border-color: rgba(0, 243, 255, 0.2);
    -fx-padding: 18;
    -fx-spacing: 14;
}

.hud-panel-kicker {
    -fx-text-fill: #ff0055;
    -fx-letter-spacing: 4;
    -fx-font-size: 11;
}

.hud-panel-title {
    -fx-text-fill: #f5faff;
    -fx-font-size: 20;
    -fx-font-weight: bold;
}

.hud-stats {
    -fx-spacing: 10;
}

.hud-panel-row {
    -fx-spacing: 2;
}

.hud-stat-block {
    -fx-spacing: 2;
}

.hud-label {
    -fx-text-fill: #8aa7dc;
    -fx-font-size: 11;
    -fx-letter-spacing: 2;
}

.hud-value {
    -fx-text-fill: #ffffff;
    -fx-font-size: 18;
    -fx-font-weight: bold;
}

.hud-value-lg {
    -fx-font-size: 26;
}

.hud-mode-status {
    -fx-background-color: rgba(0, 0, 0, 0.35);
    -fx-background-radius: 14;
    -fx-text-fill: #9adcf3;
    -fx-padding: 8 12;
    -fx-font-size: 12;
}

.hud-footer-hint {
    -fx-text-fill: #6f88b4;
    -fx-font-size: 11;
}

/* ---------- Next Queue ---------- */
.next-queue-panel {
    -fx-background-color: rgba(3, 5, 12, 0.85);
    -fx-background-radius: 24;
    -fx-border-radius: 24;
    -fx-border-color: rgba(255, 255, 255, 0.04);
    -fx-padding: 18;
    -fx-alignment: top-center;
}

.next-queue-title {
    -fx-text-fill: #7fe9ff;
    -fx-font-size: 14;
    -fx-letter-spacing: 2;
}

.next-queue-container {
    -fx-spacing: 12;
}

.next-queue-cell {
    -fx-arc-height: 8;
    -fx-arc-width: 8;
}

/* ---------- Overlays & Notifications ---------- */
.pause-overlay {
    -fx-background-color: rgba(4, 6, 12, 0.88);
    -fx-padding: 28;
    -fx-background-radius: 20;
    -fx-border-radius: 20;
    -fx-border-color: rgba(0, 243, 255, 0.25);
}

.overlay-title {
    -fx-text-fill: #fcee0a;
    -fx-font-size: 32;
    -fx-font-weight: bold;
}

.overlay-subtitle {
    -fx-text-fill: #9cb4d6;
    -fx-font-size: 13;
}

.danger {
    -fx-text-fill: #ff0055;
}

.game-over-panel {
    -fx-background-color: rgba(4, 6, 12, 0.92);
    -fx-background-radius: 24;
    -fx-border-radius: 24;
    -fx-border-color: rgba(255, 0, 85, 0.35);
    -fx-padding: 28;
    -fx-spacing: 16;
    -fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.65), 30, 0.4, 0, 6);
}

.game-over-panel .button {
    -fx-min-width: 180;
}

.seedLabel {
    -fx-text-fill: #9adcf3;
    -fx-font-size: 12;
}

.notification-panel {
    -fx-background-color: transparent;
    -fx-padding: 12;
}

.bonusStyle {
    -fx-font-family: "Orbitron", "Segoe UI", sans-serif;
    font-family: "Orbitron", "Segoe UI", sans-serif;
    -fx-font-size: 26;
    font-size: 26px;
    -fx-text-fill: #fcee0a;
    -fx-effect: dropshadow(gaussian, rgba(252, 238, 10, 0.6), 15, 0.4, 0, 0);
}


================================================
FILE: src/m