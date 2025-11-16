package com.comp2042.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

/**
 * JavaFX entry point that loads the start menu and wires it to the primary stage.
 */
public class Main extends Application {

    /**
     * Bootstraps the start menu scene and hands the primary stage to the controller.
     *
     * @param primaryStage the stage created by the JavaFX runtime
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        URL startMenu = getClass().getClassLoader().getResource("StartMenu.fxml");
        FXMLLoader fxmlLoader = new FXMLLoader(startMenu);
        Parent root = fxmlLoader.load();
        StartMenuController controller = fxmlLoader.getController();
        controller.setPrimaryStage(primaryStage);

        primaryStage.setTitle("TetrisJFX");
        Scene scene = new Scene(root, StartMenuController.WINDOW_WIDTH, StartMenuController.WINDOW_HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
