package ru.nsu.fit.towerdefense.fx.controllers.menu;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import ru.nsu.fit.towerdefense.fx.SceneManager;
import ru.nsu.fit.towerdefense.fx.controllers.Controller;
import ru.nsu.fit.towerdefense.metadata.GameMetaData;

/**
 * MenuController class is used by JavaFX in javafx.fxml.FXMLLoader for showing a menu scene.
 *
 * @author Oleg Markelov
 */
public class MenuController implements Controller {

    private static final String FXML_FILE_NAME = "menu.fxml";

    @FXML private Label levelsLabel;
    @FXML private ScrollPane buttonsScrollPane;
    @FXML private VBox buttonsVBox;

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
        if (GameMetaData.getInstance().getGameMapNames().isEmpty()) {
            levelsLabel.setText("No levels");
            ((VBox) buttonsScrollPane.getParent()).getChildren().remove(buttonsScrollPane);
        }

        for (String gameMapName : GameMetaData.getInstance().getGameMapNames()) {
            Button button = new Button(gameMapName);

            button.setOnAction(actionEvent -> sceneManager.switchToGame(gameMapName));

            buttonsVBox.getChildren().add(button);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFXMLFileName() {
        return FXML_FILE_NAME;
    }
}
