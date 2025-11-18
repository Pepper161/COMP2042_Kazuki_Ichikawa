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
