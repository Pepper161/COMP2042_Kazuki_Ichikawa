package com.comp2042.app;

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

    private static final String HELP_DIALOG_FXML = "HelpDialog.fxml";

    private HelpDialogService() {
    }

    public static void showHelp(Stage owner, String markdown) {
        try {
            FXMLLoader loader = new FXMLLoader(HelpDialogService.class.getClassLoader().getResource(HELP_DIALOG_FXML));
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
