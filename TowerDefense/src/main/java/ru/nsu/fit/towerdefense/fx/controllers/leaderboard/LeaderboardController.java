package ru.nsu.fit.towerdefense.fx.controllers.leaderboard;

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

    @FXML private StackPane root;
    @FXML private GridPane leaderboardGridPane;

    @FXML private ImageView menuImageView;

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
        menuImageView.setOnMouseClicked(mouseEvent -> sceneManager.switchToMenu());

        new Thread(() -> {
            List<SLevelScore> levelScores = connectionManager.getLeaderboard(gameMapName, 0);

            Platform.runLater(() -> {
                for (int i = 0; i < levelScores.size(); i++) {
                    SLevelScore levelScore = levelScores.get(i);
                    leaderboardGridPane.add(new HBox(new Label(i + 1 + "")), 0, i + 1);
                    leaderboardGridPane.add(new HBox(new Label(levelScore.getPlayerName())), 1, i + 1);
                    leaderboardGridPane.add(new HBox(new Label(levelScore.getScore() + "")), 2, i + 1);
                    leaderboardGridPane.add(new HBox(new Label(
                        DATE_FORMAT.format(new Date(levelScore.getTimestamp())))), 3, i + 1);
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
