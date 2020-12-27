package ru.nsu.fit.towerdefense.fx.controllers.menu;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ru.nsu.fit.towerdefense.fx.SceneManager;
import ru.nsu.fit.towerdefense.fx.controllers.Controller;
import ru.nsu.fit.towerdefense.fx.util.AlertBuilder;
import ru.nsu.fit.towerdefense.metadata.GameMetaData;
import ru.nsu.fit.towerdefense.replay.GameStateReader;
import ru.nsu.fit.towerdefense.replay.Replay;

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
            Button gameButton = new Button(gameMapName);
            gameButton.setOnAction(actionEvent -> sceneManager.switchToGame(gameMapName));

            ComboBox<String> comboBox = new ComboBox<>(FXCollections.observableArrayList(
                GameStateReader.getInstance().getReplays(gameMapName)));
            comboBox.getSelectionModel().selectFirst();

            Button replayButton = new Button("Replay");
            replayButton.setOnAction(actionEvent -> {
                Replay replay = GameStateReader.getInstance().readReplay(gameMapName, comboBox.getValue());
                if (replay != null) {
                    sceneManager.switchToGame(gameMapName, replay);
                } else {
                    new AlertBuilder()
                        .setHeaderText("Replay is null!")
                        .setOwner(sceneManager.getWindowOwner())
                        .build().showAndWait();
                }
            });

            buttonsVBox.getChildren().add(new Group() {{
                getChildren().add(new HBox() {{
                    getChildren().addAll(gameButton, replayButton, comboBox);
                }});
            }});
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
