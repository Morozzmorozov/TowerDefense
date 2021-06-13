package ru.nsu.fit.towerdefense.fx.controllers.lobbies;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ru.nsu.fit.towerdefense.fx.SceneManager;
import ru.nsu.fit.towerdefense.fx.controllers.Controller;
import ru.nsu.fit.towerdefense.multiplayer.ConnectionManager;
import ru.nsu.fit.towerdefense.multiplayer.entities.Lobby;

import java.util.List;

/**
 * LobbiesController class is used by JavaFX in javafx.fxml.FXMLLoader for showing a lobbies scene.
 *
 * @author Oleg Markelov
 */
public class LobbiesController implements Controller {

    private static final String FXML_FILE_NAME = "lobbies.fxml";

    @FXML private VBox root;

    private final SceneManager sceneManager;
    private final ConnectionManager connectionManager;

    /**
     * Creates new LobbiesController with specified SceneManager and UserManager.
     *
     * @param sceneManager scene manager.
     * @param connectionManager  user manager.
     */
    public LobbiesController(SceneManager sceneManager, ConnectionManager connectionManager) {
        this.sceneManager = sceneManager;
        this.connectionManager = connectionManager;
    }

    @FXML
    private void initialize() {
        new Thread(() -> {
            List<Lobby> lobbies = connectionManager.getLobbies();

            Platform.runLater(() -> {
                for (Lobby lobby : lobbies) {
                    HBox hBox = new HBox();
                    hBox.getChildren().add(new Label(lobby.getLevelName()));
                    hBox.getChildren().add(new Label("Players: " + lobby.getPlayers().size() + "/" + lobby.getMaxPlayers()));
                    for (String playerName : lobby.getPlayers()) {
                        hBox.getChildren().add(new Label(playerName));
                    }
                    hBox.setOnMouseClicked(mouseEvent -> new Thread(() -> {
                        String sessionToken = connectionManager.joinLobby(lobby.getId());
                        if (sessionToken == null) {
                            return;
                        }

                        connectionManager.openSocketConnection(lobby.getId(), sessionToken);

                        Platform.runLater(() -> sceneManager.switchToLobby(""/*todo*/));
                    }).start());
                    root.getChildren().add(hBox);
                }
            });
        }).start();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFXMLFileName() {
        return FXML_FILE_NAME;
    }
}
