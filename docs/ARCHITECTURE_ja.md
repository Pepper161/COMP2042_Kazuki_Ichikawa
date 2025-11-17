# ARCHITECTURE（日本語要約）

## 概要（本アプリの構成と層の分離）
JavaFX落下パズル。表示層(GuiController,GameOverPanel,NotificationPanel)・制御層(GameController,InputEventListener)・盤面層(Board,SimpleBoard)を分離し、Timelineイベントループ/event loopがViewDataとDownDataで状態同期する。

## ディレクトリ構成
- `src/main/java/com/comp2042/app`：JavaFX起動点 (`Main`/`StartMenuController`)。
- `src/main/java/com/comp2042/game`：`GameController`/`GameLogic`/`Score` など Guideline ロジック一式。
- `src/main/java/com/comp2042/game/events`：`MoveEvent`/`EventType`/`DownData` 等のDTO。
- `src/main/java/com/comp2042/board`：`SimpleBoard`/`MatrixOperations`/`BrickRotator` による盤面。
- `src/main/java/com/comp2042/ui`：`GuiController`/各種パネル等のJavaFX表示層。
- `src/main/java/com/comp2042/logic/bricks`：テトリミノ定義と 7-Bag ジェネレータ。
- `src/main/resources`：`gameLayout.fxml`ほか静的資産。
- `pom.xml`：依存とJavaFX設定。
- `target/`：生成物（追跡外）。

## 起動フロー
MainがJavaFXを起動しFXMLをロード。`fx:controller="com.comp2042.ui.GuiController"`でフィールドを結線し、GuiController.initializeがGrid初期化とTimelineを設定する。GuiControllerのTimeline（gravity tick）が`moveDown`を定期実行し、各 tick で GameController へ通知して Board を進める。GameController は Board/Score を初期化し、GuiController への ViewData 更新を担当する。

## 入力処理
GuiControllerがキー入力をMoveEvent化し、EventTypeとEventSourceでUSER/THREADを識別してInputEventListener実装のGameControllerへ渡す。GameControllerは種別に応じて移動・回転・加点を処理する。

### 状態遷移
- `Menu`(StartMenuController) → Startボタンで `Playing` へ。`GuiController` が Timeline を起動し、GameController が Board を駆動。
- `Playing` → ピース衝突で `GameOver` フラグを受けると `GuiController.gameOver()` が呼ばれ、GameOverPanel を表示して Timeline を停止。
- `GameOver` からは `Restart` ボタンで `Playing` に戻る（`startNewGameSession()`）、`Main Menu` で StartMenu に戻り `Menu` 状態へ遷移。`Menu` ではキー入力は無効化。
- いずれの状態でも `N` キー（New Game）で `Playing` を再初期化できる。状態ごとに `GuiController.setGameState` が HUD/Timeline/パネルの表示を切り替える。

```
   +------+  Start   +---------+  GameOver flag  +----------+
   | Menu | -------> | Playing | --------------> | GameOver |
   +------+           +---------+                 +----------+
      ^  |               |   ^                        |
      |  | Main Menu     |   | Restart / N key        |
      +------------------+   +------------------------+
```

|状態|制御クラス|主な入力|出力/遷移|
|---|---|---|---|
|Menu|StartMenuController|Start/Exit/Settings ボタン|GameController 初期化 or アプリ終了|
|Playing|GuiController + GameController|キーボード入力、タイマー|Board 操作、HUD 更新、GameOver フラグ|
|GameOver|GuiController + GameOverPanel|Restart/MainMenu/Exit|新ゲーム開始 or StartMenu に戻る|

### Debug Seed
- `Main` は `--seed=<long>` パラメータを解析し、指定があれば PieceGenerator に固定シードを渡す。
- 指定が無い場合は毎回ランダムシードを生成してログ／GameOverパネルに表示するため、後から `--seed=<表示値>` で同じプレイを再生できる。

### 入力設定 (DAS/ARR/SDF)
- StartMenu に追加した Settings ダイアログで DAS 遅延 / ARR 間隔 / ソフトドロップ倍率とキーバインドを編集できる。
- `com.comp2042.config.GameSettings` で設定を保持し、`GameSettingsStore` が `~/.tetrisjfx/settings.properties` へ永続化。
- `GuiController` は `GameSettings` を受け取り、`AutoRepeatHandler` で長押し挙動を再現（左/右/ソフトドロップ）。New Game キーもここでリマップされる。

### レベル進行 / 重力カーブ
- `GuiController` が合計ライン数を監視し、10行ごとにレベル+1。Level1=400ms から Level10≈100ms まで徐々に落下間隔を短縮するテーブルを持つ。
- HUD (`HudPanel`) に Level 表示を追加し、レベルアップ時に即座に描画とタイムライン速度を更新。
- ソフトドロップのリピート間隔も現在の重力に合わせて再計算するため、ゲームが速くなっても入力感度が自然。

## 盤面・ピース・行消去
GameControllerはBoard経由でSimpleBoardを操作。SimpleBoardはBrickRotatorとMatrixOperationsで形状演算し、着地時にClearRowが行数とボーナスを返してScoreへ加算。DownDataがViewDataとNextShapeInfoをGuiControllerへ送り背景と予告を更新する。

## クラス一覧表
|クラス|1行説明|主な責務|主な関連|
|---|---|---|---|
|Board|盤面API|移動抽象|GameController,SimpleBoard|
|BrickRotator|回転計算|行列操作|SimpleBoard|
|ClearRow|消去結果|行数加点|Score|
|DownData|落下DTO|表示同期|GuiController|
|EventSource|入力源|USER判別|MoveEvent|
|EventType|操作種別|方向分類|MoveEvent|
|GameController|制御核|入力調停|GuiController|
|GameOverPanel|終了UI|表示切替|GuiController|
|GuiController|表示制御|イベント受付|GameController|
|InputEventListener|入力IF|コールバック|GuiController|
|Main|起動点|FX開始|GuiController|
|MatrixOperations|行列補助|コピー整形|SimpleBoard|
|MoveEvent|入力DTO|種別保持|GameController|
|NextShapeInfo|次ピース|情報供給|GuiController|
|NotificationPanel|通知UI|演出表示|GuiController|
|Score|スコア|Property管理|GameController|
|SimpleBoard|盤面実装|衝突判定|GameController|
|ViewData|描画データ|形状提供|GuiController|

## 今後の拡張・テスト観点
- ClearRow多行パターンの境界テスト。
- MoveEventのEventSource分岐検証。
- SimpleBoard衝突判定のデータ駆動テスト。
- NextShapeInfo表示のJavaFXテスト化。
- Timeline停止/再開時の例外処理強化。
