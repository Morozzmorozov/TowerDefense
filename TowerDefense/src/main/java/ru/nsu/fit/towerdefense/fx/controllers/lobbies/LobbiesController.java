package ru.nsu.fit.towerdefense.fx.controllers.lobbies;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import ru.nsu.fit.towerdefense.fx.SceneManager;
import ru.nsu.fit.towerdefense.fx.controllers.Controller;
import ru.nsu.fit.towerdefense.multiplayer.ConnectionManager;
import ru.nsu.fit.towerdefense.multiplayer.GameType;
import ru.nsu.fit.towerdefense.multiplayer.entities.SLobby;

import java.util.List;

/**
 * LobbiesController class is used by JavaFX in javafx.fxml.FXMLLoader for showing a lobbies scene.
 *
 * @author Oleg Markelov
 */
public class LobbiesController implements Controller {

    private static final String FXML_FILE_NAME = "lobbies.fxml";

    @FXML private StackPane root;
    @FXML private GridPane lobbiesGridPane;

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

        int lobbiesGridPaneColumnsNumber = lobbiesGridPane.getChildren().size();
        lobbiesThread = new Thread(() -> {
            while (!Thread.interrupted()) {
                try {
                    List<SLobby> lobbies = connectionManager.getLobbies();

                    Platform.runLater(() -> {
                        lobbiesGridPane.getChildren().subList(lobbiesGridPaneColumnsNumber,
                            lobbiesGridPane.getChildren().size()).clear();
                        for (int i = 0; i < lobbies.size(); i++) {
                            SLobby lobby = lobbies.get(i);
                            lobbiesGridPane.add(new HBox(new Label(i + 1 + "")), 0, i + 1);
                            lobbiesGridPane.add(new HBox(new Label(lobby.getLevelName())), 1, i + 1);
                            lobbiesGridPane.add(new HBox(
                                new Label(capitalize(lobby.getGameType()))), 2, i + 1);
                            lobbiesGridPane.add(new HBox(new Label(
                                lobby.getPlayers().get(0).getName())), 3, i + 1);
                            lobbiesGridPane.add(new HBox(new Label(
                                lobby.getPlayers().size() + "/" + lobby.getMaxPlayers())), 4, i + 1);
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

    private String capitalize(GameType gameType) {
        String gameTypeLowered = gameType.toString().toLowerCase();
        return gameTypeLowered.substring(0, 1).toUpperCase() + gameTypeLowered.substring(1);
    }
}
