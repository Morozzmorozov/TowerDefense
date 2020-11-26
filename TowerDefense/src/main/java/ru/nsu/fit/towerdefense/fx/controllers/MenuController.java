package ru.nsu.fit.towerdefense.fx.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import ru.nsu.fit.towerdefense.fx.SceneManager;

/**
 * MenuController class is used by JavaFX in javafx.fxml.FXMLLoader for showing a menu scene.
 *
 * @author Oleg Markelov
 */
public class MenuController implements Controller {

    private static final String FXML_FILE_NAME = "menu.fxml";

    @FXML private Button gameButton;

    private final SceneManager sceneManager;

    /**
     * Creates new MenuController with specified SceneManager.
     *
     * @param sceneManager scene manager.
     */
    public MenuController(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    @FXML
    private void initialize() {
        gameButton.setOnAction(actionEvent -> sceneManager.switchToGame());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFXMLFileName() {
        return FXML_FILE_NAME;
    }
}
