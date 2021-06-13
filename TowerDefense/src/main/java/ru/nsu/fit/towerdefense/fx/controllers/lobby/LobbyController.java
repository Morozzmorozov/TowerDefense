package ru.nsu.fit.towerdefense.fx.controllers.lobby;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;
import ru.nsu.fit.towerdefense.fx.SceneManager;
import ru.nsu.fit.towerdefense.fx.controllers.Controller;
import ru.nsu.fit.towerdefense.fx.controllers.ServerMessageListener;
import ru.nsu.fit.towerdefense.multiplayer.ConnectionManager;
import ru.nsu.fit.towerdefense.multiplayer.Message;
import ru.nsu.fit.towerdefense.multiplayer.entities.Lobby;

/**
 * LobbyController class is used by JavaFX in javafx.fxml.FXMLLoader for showing a lobby scene.
 *
 * @author Oleg Markelov
 */
public class LobbyController implements Controller, ServerMessageListener {

    private static final String FXML_FILE_NAME = "lobby.fxml";

    @FXML private VBox root;
    @FXML private VBox lobbyVBox;

    private final SceneManager sceneManager;
    private final ConnectionManager connectionManager;
    private final String lobbyId;
    private final String sessionToken;

    private Thread lobbyThread;
    private Lobby lobby;

    public LobbyController(SceneManager sceneManager, ConnectionManager connectionManager,
                           String lobbyId, String sessionToken) {

        this.sceneManager = sceneManager;
        this.connectionManager = connectionManager;
        this.lobbyId = lobbyId;
        this.sessionToken = sessionToken;
    }

    @FXML
    private void initialize() {
        lobbyThread = new Thread(() -> {
            while (!Thread.interrupted()) {
                try {
                    lobby = connectionManager.getLobby(lobbyId);

                    Platform.runLater(() -> {
                        lobbyVBox.getChildren().clear();
                        lobbyVBox.getChildren().add(new Label(lobby.getLevelName()));
                        lobbyVBox.getChildren().add(new Label("Players: " + lobby.getPlayers().size() + "/" + lobby.getMaxPlayers()));
                        for (String playerName : lobby.getPlayers()) {
                            lobbyVBox.getChildren().add(new Label(playerName));
                        }
                    });

                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        lobbyThread.start();
        connectionManager.setServerMessageListener(this);
        connectionManager.openSocketConnection(lobbyId, sessionToken);

        ToggleButton readyButton = new ToggleButton("Start");
        readyButton.setOnAction(actionEvent -> {
            Message message = new Message();
            message.setType(Message.Type.READY);
            connectionManager.sendMessage(new Gson().toJson(message));
        });
        root.getChildren().add(readyButton);
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

                    Platform.runLater(() -> sceneManager.switchToCooperativeGame(lobby.getLevelName(), message.getPlayerNames()));
                }
                default -> System.out.println("default");
            }
        } catch (JsonSyntaxException e) {
            System.err.println("json parsing error: " + messageStr);
        }
    }
}
