package ru.nsu.fit.towerdefense.fx.controllers.lobby;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import ru.nsu.fit.towerdefense.fx.SceneManager;
import ru.nsu.fit.towerdefense.fx.controllers.Controller;
import ru.nsu.fit.towerdefense.fx.controllers.ServerMessageListener;
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
        Button startButton = new Button("Start");
        startButton.setOnAction(actionEvent -> {
            // todo send start

        });
        root.getChildren().add(startButton);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFXMLFileName() {
        return FXML_FILE_NAME;
    }

    @Override
    public void onServerMessageReceived(String message) {
        System.out.println(message);

        if (false) { // todo del
            Platform.runLater(() -> sceneManager.switchToCooperativeGame(lobby.getLevelName(), lobby.getPlayers()));
        }
    }
}
