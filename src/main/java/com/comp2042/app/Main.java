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
        GameConfig config = parseGameConfig();
        URL startMenu = ResourceManager.getUrl(ResourceManager.Asset.START_MENU_FXML);
        FXMLLoader fxmlLoader = new FXMLLoader(startMenu);
        Parent root = fxmlLoader.load();
        StartMenuController controller = fxmlLoader.getController();
        controller.setPrimaryStage(primaryStage);
        controller.setGameConfig(config);

        primaryStage.setTitle("TetrisJFX");
        Scene scene = new Scene(root, StartMenuController.MENU_WINDOW_WIDTH, StartMenuController.MENU_WINDOW_HEIGHT);
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
