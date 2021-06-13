package ru.nsu.fit.towerdefense.fx.controllers.lobbies;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
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

    @FXML private StackPane root;
    @FXML private VBox lobbiesVBox;

    @FXML private ImageView menuImageView;

    private final SceneManager sceneManager;
    private final ConnectionManager connectionManager;

    private Thread lobbiesThread;

    public LobbiesController(SceneManager sceneManager, ConnectionManager connectionManager) {
        this.sceneManager = sceneManager;
        this.connectionManager = connectionManager;
    }

    @FXML
    private void initialize() {
        menuImageView.setOnMouseClicked(mouseEvent -> sceneManager.switchToMenu());

        lobbiesThread = new Thread(() -> {
            while (!Thread.interrupted()) {
                try {
                    List<Lobby> lobbies = connectionManager.getLobbies();

                    Platform.runLater(() -> {
                        lobbiesVBox.getChildren().clear();
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

                                Platform.runLater(() -> sceneManager.switchToLobby(lobby.getId(), sessionToken));
                            }).start());
                            lobbiesVBox.getChildren().add(hBox);
                        }
                    });

                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        lobbiesThread.start();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFXMLFileName() {
        return FXML_FILE_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose() {
        if (lobbiesThread != null) {
            lobbiesThread.interrupt();
        }
    }
}
