package com.comp2042.app;

import com.comp2042.config.GameSettings;
import com.comp2042.config.GameSettingsStore;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
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

    private final Map<GameSettings.Action, TextField> keyFields = new EnumMap<>(GameSettings.Action.class);
    private final GameSettingsStore store = new GameSettingsStore();

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
}
