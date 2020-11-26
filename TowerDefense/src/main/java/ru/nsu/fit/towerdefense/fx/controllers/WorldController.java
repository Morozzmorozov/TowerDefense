package ru.nsu.fit.towerdefense.fx.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import ru.nsu.fit.towerdefense.fx.SceneManager;

/**
 * WorldController class is used by JavaFX in javafx.fxml.FXMLLoader for showing a world scene.
 *
 * @author Oleg Markelov
 */
public class WorldController implements Controller {

    private static final String FXML_FILE_NAME = "world.fxml";

    @FXML private Button menuButton;

    private final SceneManager sceneManager;

    /**
     * Creates new MenuController with specified SceneManager.
     *
     * @param sceneManager scene manager.
     */
    public WorldController(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    @FXML
    private void initialize() {
        menuButton.setOnAction(actionEvent -> sceneManager.switchToMenu());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFXMLFileName() {
        return FXML_FILE_NAME;
    }
}
