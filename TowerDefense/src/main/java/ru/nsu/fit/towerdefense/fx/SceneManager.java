package ru.nsu.fit.towerdefense.fx;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;
import javafx.stage.Window;
import ru.nsu.fit.towerdefense.fx.controllers.Controller;
import ru.nsu.fit.towerdefense.fx.controllers.GameController;
import ru.nsu.fit.towerdefense.fx.controllers.MenuController;
import ru.nsu.fit.towerdefense.fx.util.AlertBuilder;
import ru.nsu.fit.towerdefense.model.GameMetaData;

import java.io.IOException;

import static ru.nsu.fit.towerdefense.fx.util.AlertBuilder.LAYOUT_LOADING_ERROR_HEADER;

/**
 * SceneManager class is used for managing JavaFX stage.
 *
 * @author Oleg Markelov
 */
public class SceneManager {

    private static final String DEFAULT_TITLE = "Tower Defense";
    private static final String FXML_DIRECTORY = "/ru/nsu/fit/towerdefense/fxml/";

    private final Stage stage;
    private Controller controller;

    /**
     * Creates new SceneManager with specified JavaFX stage and file chooser manager.
     *
     * @param stage JavaFX stage.
     */
    public SceneManager(Stage stage)  {
        this.stage = stage;

        initStage();
    }

    private void initStage() {
        stage.setTitle(DEFAULT_TITLE);
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
    }

    /**
     * Returns current window owner.
     *
     * @return current window owner.
     */
    public Window getWindowOwner() {
        return stage;
    }

    /**
     * Returns current scene of the stage.
     *
     * @return current scene of the stage.
     */
    public Scene getScene() {
        return stage.getScene();
    }

    /**
     * Toggles current stage fullscreen state.
     */
    public void toggleFullScreen() {
        stage.setFullScreen(!isFullScreen());
    }

    /**
     * Returns whether current stage is fullscreen.
     *
     * @return whether current stage is fullscreen.
     */
    public boolean isFullScreen() {
        return stage.isFullScreen();
    }

    /**
     * Creates new MenuController and switches the scene to a menu.
     */
    public void switchToMenu() {
        switchScene(new MenuController(this));
    }

    /**
     * Creates new GameController with game map gotten by specified game map name and switches
     * the scene to a game.
     *
     * @param gameMapName game map name.
     */
    public void switchToGame(String gameMapName) {
        switchScene(new GameController(this,
            GameMetaData.getInstance().getMapDescription(gameMapName))); // todo handle Exception or null
    }

    /**
     * Disposes current controller.
     */
    public void dispose() {
        if (controller != null) {
            controller.dispose();
        }
    }

    private void switchScene(Controller controller) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource(
                FXML_DIRECTORY + controller.getFXMLFileName()));
            fxmlLoader.setController(controller);
            Parent root = fxmlLoader.load();

            if (this.controller != null) {
                this.controller.dispose();
            }
            this.controller = controller;

            if (stage.getScene() == null) {
                stage.setScene(new Scene(root, stage.getWidth(), stage.getHeight()));
            } else {
                stage.getScene().setRoot(root);
            }

            controller.runAfterSceneSet();
        } catch (IOException | IllegalStateException e) {
            new AlertBuilder()
                .setHeaderText(LAYOUT_LOADING_ERROR_HEADER).setException(e).setOwner(stage)
                .build().showAndWait();
        }
    }
}
