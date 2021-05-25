package ru.nsu.fit.towerdefense.fx.controllers.lobby;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import ru.nsu.fit.towerdefense.fx.SceneManager;
import ru.nsu.fit.towerdefense.fx.controllers.Controller;
import ru.nsu.fit.towerdefense.fx.controllers.ServerMessageListener;
import ru.nsu.fit.towerdefense.multiplayer.Message;
import ru.nsu.fit.towerdefense.multiplayer.MessageType;
import ru.nsu.fit.towerdefense.multiplayer.UserManager;
import ru.nsu.fit.towerdefense.multiplayer.entities.Lobby;

import java.util.List;

/**
 * LobbyController class is used by JavaFX in javafx.fxml.FXMLLoader for showing a lobby scene.
 *
 * @author Oleg Markelov
 */
public class LobbyController implements Controller, ServerMessageListener {

    private static final String FXML_FILE_NAME = "lobby.fxml";

    @FXML private VBox root;

    private final SceneManager sceneManager;
    private final UserManager userManager;

    private final Lobby lobby = new Lobby() {{
        setLevelName("Level 1");
        setMaxPlayers(2);
        setPlayers(List.of("admin"));
    }};

    /**
     * Creates new LobbyController with specified SceneManager and UserManager.
     *
     * @param sceneManager scene manager.
     * @param userManager  user manager.
     */
    public LobbyController(SceneManager sceneManager, UserManager userManager) {
        this.sceneManager = sceneManager;
        this.userManager = userManager;
    }

    @FXML
    private void initialize() {
        userManager.setServerMessageListener(this);
        root.getChildren().add(new Label(lobby.getLevelName()));
        root.getChildren().add(new Label("Players: " + lobby.getPlayers().size() + "/" + lobby.getMaxPlayers()));
        for (String playerName : lobby.getPlayers()) {
            root.getChildren().add(new Label(playerName));
        }
        Button readyButton = new Button("Start");
        readyButton.setOnAction(actionEvent -> {
            Message message = new Message();
            message.setType(MessageType.READY);
            userManager.sendMessage(new Gson().toJson(message));
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

    @Override
    public void onServerMessageReceived(String messageStr) {
        try {
            Message message = new Gson().fromJson(messageStr, Message.class);
            if (message.getType() == null) {
                return;
            }

            switch (message.getType()) {
                case START -> {
                    System.out.println("START");
                    //Platform.runLater(() -> sceneManager.switchToCooperativeGame(lobby.getLevelName(), lobby.getPlayers()));
                }
                default -> System.out.println("default");
            }
        } catch (JsonSyntaxException e) {
            System.err.println("json parsing error: " + messageStr);
        }
    }
}
