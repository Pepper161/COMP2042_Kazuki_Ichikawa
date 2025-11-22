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
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.EnumMap;
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
            VBox.setVgrow(infoLabel, Priority.NEVER);
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
        for (Map.Entry<GameSettings.Action, TextField> entry : keyFields.entrySet()) {
            builder.setKey(entry.getKey(), parseKey(entry.getValue(), entry.getKey().name()));
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
