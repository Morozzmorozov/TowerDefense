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

        lobbiesThread = new Thread(() -> {
            while (!Thread.interrupted()) {
                try {
                    List<SLobby> lobbies = connectionManager.getLobbies();

                    Platform.runLater(() -> {
                        lobbiesGridPane.getChildren().subList(lobbiesGridPane.getColumnCount(),
                            lobbiesGridPane.getChildren().size()).clear();
                        for (int i = 0; i < lobbies.size(); i++) {
                            SLobby lobby = lobbies.get(i);

                            HBox hBox1 = new HBox(new Label(i + 1 + ""));
                            hBox1.getStyleClass().add("clickable");
                            hBox1.setOnMouseClicked(mouseEvent -> goToLobby(lobby));
                            lobbiesGridPane.add(hBox1, 0, i + 1);

                            HBox hBox2 = new HBox(new Label(lobby.getLevelName()));
                            hBox2.getStyleClass().add("clickable");
                            hBox2.setOnMouseClicked(mouseEvent -> goToLobby(lobby));
                            lobbiesGridPane.add(hBox2, 1, i + 1);

                            HBox hBox3 = new HBox(new Label(capitalize(lobby.getGameType())));
                            hBox3.getStyleClass().add("clickable");
                            hBox3.setOnMouseClicked(mouseEvent -> goToLobby(lobby));
                            lobbiesGridPane.add(hBox3, 2, i + 1);

                            HBox hBox4 = new HBox(new Label(lobby.getPlayers().get(0).getName()));
                            hBox4.getStyleClass().add("clickable");
                            hBox4.setOnMouseClicked(mouseEvent -> goToLobby(lobby));
                            lobbiesGridPane.add(hBox4, 3, i + 1);

                            HBox hBox5 = new HBox(new Label(lobby.getPlayers().size() + "/" + lobby.getMaxPlayers()));
                            hBox5.getStyleClass().add("clickable");
                            hBox5.setOnMouseClicked(mouseEvent -> goToLobby(lobby));
                            lobbiesGridPane.add(hBox5, 4, i + 1);
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

    private void goToLobby(SLobby lobby) {
        new Thread(() -> {
            String sessionToken = connectionManager.joinLobby(lobby.getId());
            if (sessionToken == null) {
                System.out.println("sessionToken is null");
                return;
            }

            Platform.runLater(() -> sceneManager.switchToLobby(lobby.getId(), sessionToken));
        }).start();
    }

    private String capitalize(GameType gameType) {
        String gameTypeLowered = gameType.toString().toLowerCase();
        return gameTypeLowered.substring(0, 1).toUpperCase() + gameTypeLowered.substring(1);
    }
}
