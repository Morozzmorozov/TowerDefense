package ru.nsu.fit.towerdefense.fx;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.Window;
import ru.nsu.fit.towerdefense.fx.controllers.Controller;
import ru.nsu.fit.towerdefense.fx.controllers.game.GameController;
import ru.nsu.fit.towerdefense.fx.controllers.lobbies.LobbiesController;
import ru.nsu.fit.towerdefense.fx.controllers.lobby.LobbyController;
import ru.nsu.fit.towerdefense.fx.controllers.menu.MenuController;
import ru.nsu.fit.towerdefense.fx.controllers.techtree.TechTreeController;
import ru.nsu.fit.towerdefense.fx.util.AlertBuilder;
import ru.nsu.fit.towerdefense.metadata.GameMetaData;
import ru.nsu.fit.towerdefense.multiplayer.ConnectionManager;
import ru.nsu.fit.towerdefense.replay.Replay;
import ru.nsu.fit.towerdefense.util.Vector2;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

import static javafx.scene.input.KeyCode.ENTER;
import static javafx.scene.input.KeyCombination.ALT_DOWN;
import static ru.nsu.fit.towerdefense.fx.util.AlertBuilder.LAYOUT_LOADING_ERROR_HEADER;
import static ru.nsu.fit.towerdefense.fx.util.AlertBuilder.MAP_LOADING_ERROR_HEADER;

/**
 * SceneManager class is used for managing JavaFX stage.
 *
 * @author Oleg Markelov
 */
public class SceneManager {

    private static final String DEFAULT_TITLE = "Tower Defense";
    private static final String FXML_DIRECTORY = "/ru/nsu/fit/towerdefense/fx/fxml/";
    private static final String APP_ICON = "/ru/nsu/fit/towerdefense/fx/icons/kremlin.png";

    private static final KeyCodeCombination EXPAND_COMBINATION = new KeyCodeCombination(ENTER, ALT_DOWN);

    private final Stage stage;
    private final ConnectionManager connectionManager;
    private Controller controller;

    /**
     * Creates new SceneManager with specified JavaFX stage and file chooser manager.
     *
     * @param stage JavaFX stage.
     */
    public SceneManager(Stage stage, ConnectionManager connectionManager)  {
        this.stage = stage;
        this.connectionManager = connectionManager;

        initStage();
    }

    private void initStage() {
        stage.setTitle(DEFAULT_TITLE);
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        try {
            stage.getIcons().add(new Image(APP_ICON));
        } catch (NullPointerException | IllegalArgumentException ignored) {}
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
     * Returns stage size in pixels.
     *
     * @return stage size in pixels.
     */
    public Vector2<Double> getStageSize() {
        return new Vector2<>(stage.getWidth(), stage.getHeight());
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
     * Loads .fxml file from the resources by specified filename and returns its root as Parent.
     *
     * @param fxmlFileName .fxml filename.
     * @return root of .fxml file.
     */
    public Parent loadFXML(String fxmlFileName) {
        try {
            return new FXMLLoader(getClass().getResource(FXML_DIRECTORY + fxmlFileName)).load();
        } catch (IOException e) {
            new AlertBuilder()
                .setHeaderText(LAYOUT_LOADING_ERROR_HEADER).setException(e).setOwner(stage)
                .build().showAndWait();
            return null;
        }
    }

    /**
     * Creates new MenuController and switches the scene to a menu.
     */
    public void switchToMenu() {
        switchScene(new MenuController(this, connectionManager));
    }

    public void switchToLobbies() {
        switchScene(new LobbiesController(this, connectionManager));
    }

    public void switchToLobby() {
        switchScene(new LobbyController(this, connectionManager));
    }

    /**
     * Creates new GameController with game map gotten by specified game map name and switches
     * the scene to a game.
     *
     * @param gameMapName game map name.
     */
    public void switchToGame(String gameMapName) {
        try {
            switchScene(new GameController(this, connectionManager,
                new File(".\\levelsnapshots\\" + gameMapName + ".png"),
                GameMetaData.getInstance().getMapDescription(gameMapName)));
        } catch (NoSuchElementException e) {
            new AlertBuilder()
                .setHeaderText(MAP_LOADING_ERROR_HEADER).setException(e).setOwner(stage)
                .build().showAndWait();
        }
    }

    public void switchToCooperativeGame(String gameMapName, List<String> playerNames) {
        try {
            GameController gameController = new GameController(this, connectionManager,
                new File(".\\levelsnapshots\\" + gameMapName + ".png"),
                GameMetaData.getInstance().getMapDescription(gameMapName), playerNames);
            connectionManager.setServerMessageListener(gameController);
            switchScene(gameController);
        } catch (NoSuchElementException e) {
            new AlertBuilder()
                .setHeaderText(MAP_LOADING_ERROR_HEADER).setException(e).setOwner(stage)
                .build().showAndWait();
        }
    }

    /**
     * Creates new GameController with replay and game map gotten by specified game map name and
     * switches the scene to a game.
     *
     * @param gameMapName game map name.
     */
    public void switchToGame(String gameMapName, Replay replay) {
        try {
            switchScene(new GameController(this, connectionManager,
                new File(".\\levelsnapshots\\" + gameMapName + ".png"),
                GameMetaData.getInstance().getMapDescription(gameMapName), replay));
        } catch (NoSuchElementException e) {
            new AlertBuilder()
                .setHeaderText(MAP_LOADING_ERROR_HEADER).setException(e).setOwner(stage)
                .build().showAndWait();
        }
    }

    /**
     * Creates new TechTreeController with and switches the scene to a game.
     */
    public void switchToTechTree() {
        try {
            switchScene(new TechTreeController(this));
        } catch (NoSuchElementException e) {
            new AlertBuilder()
                .setHeaderText(MAP_LOADING_ERROR_HEADER).setException(e).setOwner(stage)
                .build().showAndWait();
        }
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
                Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
                scene.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
                    if (EXPAND_COMBINATION.match(keyEvent)) {
                        toggleFullScreen();
                    }
                });
                stage.setScene(scene);
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
