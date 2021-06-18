package ru.nsu.fit.towerdefense.fx.controllers.lobby;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import ru.nsu.fit.towerdefense.fx.SceneManager;
import ru.nsu.fit.towerdefense.fx.controllers.Controller;
import ru.nsu.fit.towerdefense.fx.controllers.ServerMessageListener;
import ru.nsu.fit.towerdefense.multiplayer.ConnectionManager;
import ru.nsu.fit.towerdefense.multiplayer.GameType;
import ru.nsu.fit.towerdefense.multiplayer.Message;
import ru.nsu.fit.towerdefense.multiplayer.entities.SGameSession;
import ru.nsu.fit.towerdefense.multiplayer.entities.SLobby;
import ru.nsu.fit.towerdefense.multiplayer.entities.SPlayer;

/**
 * LobbyController class is used by JavaFX in javafx.fxml.FXMLLoader for showing a lobby scene.
 *
 * @author Oleg Markelov
 */
public class LobbyController implements Controller, ServerMessageListener {

    private static final String FXML_FILE_NAME = "lobby.fxml";

    @FXML private StackPane root;
    @FXML private Label leveLabel;
    @FXML private ToggleButton readyToggleButton;
    @FXML private GridPane playersGridPane;

    @FXML private ImageView menuImageView;

    private final SceneManager sceneManager;
    private final ConnectionManager connectionManager;
    private final SGameSession gameSession;

    private Thread lobbyThread;
    private SLobby lobby;

    public LobbyController(SceneManager sceneManager, ConnectionManager connectionManager,
                           String lobbyId, String sessionToken) {

        this.sceneManager = sceneManager;
        this.connectionManager = connectionManager;
        gameSession = new SGameSession();
        gameSession.setSessionId(lobbyId);
        gameSession.setSessionToken(sessionToken);
    }

    @FXML
    private void initialize() {
        menuImageView.setOnMouseClicked(mouseEvent -> new Thread(() -> {
            connectionManager.leaveLobby();
            Platform.runLater(sceneManager::switchToMenu);
        }).start());

        lobbyThread = new Thread(() -> {
            while (!Thread.interrupted()) {
                try {
                    lobby = connectionManager.getLobby(gameSession.getSessionId());
                    if (lobby == null) {
                        System.out.println("lobby is null");
                        break;
                    }

                    drawLobby(lobby);

                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        lobbyThread.start();
        connectionManager.openSocketConnection(gameSession);
        connectionManager.setServerMessageListener(this);

        readyToggleButton.setOnAction(actionEvent -> {
            Message message = new Message();
            message.setType(Message.Type.READY);
            connectionManager.sendMessage(new Gson().toJson(message));
        });
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
        if (lobbyThread != null) {
            lobbyThread.interrupt();
        }
    }

    @Override
    public void onServerMessageReceived(String messageStr) {
        try {
            Message message = new Gson().fromJson(messageStr, Message.class);
            if (message.getType() == null) {
                System.err.println("message type is null");
                return;
            }

            switch (message.getType()) {
                case START -> {
                    System.out.println("START");
                    if (message.getPlayerNames() == null) {
                        System.err.println("player names is null");
                        return;
                    }

                    Platform.runLater(() -> sceneManager.switchToMultiplayerGame(
                        lobby.getLevelName(), message.getPlayerNames(), lobby.getGameType()));
                }
                default -> System.out.println("default");
            }
        } catch (JsonSyntaxException e) {
            System.err.println("json parsing error: " + messageStr);
        }
    }

    private void drawLobby(SLobby lobby) {
        Platform.runLater(() -> {
            leveLabel.setText(lobby.getLevelName() + " (" + GameType.capitalize(lobby.getGameType()) +
                ") - " + lobby.getPlayers().size() + "/" + lobby.getMaxPlayers());

            playersGridPane.getChildren().subList(playersGridPane.getColumnCount(),
                playersGridPane.getChildren().size()).clear();

            for (int i = 0; i < lobby.getPlayers().size(); i++) {
                SPlayer player = lobby.getPlayers().get(i);
                playersGridPane.add(new HBox(new Label(i + 1 + "")), 0, i + 1);
                playersGridPane.add(new HBox(new Label(player.getName())), 1, i + 1);
                playersGridPane.add(new HBox(new Label(player.getEloRating() + "")), 2, i + 1);
                playersGridPane.add(new HBox(new Label(player.getReady() ? "Yes" : "No")), 3, i + 1);
            }
        });
    }
}
