package ru.nsu.fit.towerdefense.fx.controllers.leaderboard;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ru.nsu.fit.towerdefense.fx.SceneManager;
import ru.nsu.fit.towerdefense.fx.controllers.Controller;
import ru.nsu.fit.towerdefense.multiplayer.ConnectionManager;
import ru.nsu.fit.towerdefense.multiplayer.entities.SLevelScore;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * LeaderboardController class is used by JavaFX in javafx.fxml.FXMLLoader for showing a leaderboard
 * scene.
 *
 * @author Oleg Markelov
 */
public class LeaderboardController implements Controller {

    private static final String FXML_FILE_NAME = "leaderboard.fxml";

    private static final Format DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    @FXML private VBox root;
    @FXML private VBox leaderboardVBox;

    private final SceneManager sceneManager;
    private final ConnectionManager connectionManager;
    private final String gameMapName;

    public LeaderboardController(SceneManager sceneManager, ConnectionManager connectionManager,
                                 String gameMapName) {

        this.sceneManager = sceneManager;
        this.connectionManager = connectionManager;
        this.gameMapName = gameMapName;
    }

    @FXML
    private void initialize() {
        new Thread(() -> {
            List<SLevelScore> levelScores = connectionManager.getLeaderboard(gameMapName, 0);

            Platform.runLater(() -> {
                for (SLevelScore levelScore : levelScores) {
                    HBox hBox = new HBox();
                    hBox.getChildren().add(new Label(levelScore.getPlayerName()));
                    hBox.getChildren().add(new Label(levelScore.getScore() + ""));
                    hBox.getChildren().add(new Label(DATE_FORMAT.format(new Date(levelScore.getTimestamp()))));
                    leaderboardVBox.getChildren().add(hBox);
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
