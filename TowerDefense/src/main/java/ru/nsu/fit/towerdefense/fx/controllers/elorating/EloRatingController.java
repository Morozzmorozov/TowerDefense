package ru.nsu.fit.towerdefense.fx.controllers.elorating;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import ru.nsu.fit.towerdefense.fx.SceneManager;
import ru.nsu.fit.towerdefense.fx.controllers.Controller;
import ru.nsu.fit.towerdefense.multiplayer.ConnectionManager;
import ru.nsu.fit.towerdefense.multiplayer.entities.EloRating;
import ru.nsu.fit.towerdefense.multiplayer.entities.LevelScore;

import java.util.Date;
import java.util.List;

/**
 * EloRatingController class is used by JavaFX in javafx.fxml.FXMLLoader for showing a ELO rating
 * scene.
 *
 * @author Oleg Markelov
 */
public class EloRatingController implements Controller {

    private static final String FXML_FILE_NAME = "elo-rating.fxml";

    @FXML private VBox root;
    @FXML private VBox eloRatingVBox;

    private final SceneManager sceneManager;
    private final ConnectionManager connectionManager;

    public EloRatingController(SceneManager sceneManager, ConnectionManager connectionManager) {
        this.sceneManager = sceneManager;
        this.connectionManager = connectionManager;
    }

    @FXML
    private void initialize() {
        new Thread(() -> {
            List<EloRating> eloRatings = connectionManager.getEloLeaderboard();

            Platform.runLater(() -> {
                for (int i = 0; i < eloRatings.size(); i++) {
                    EloRating eloRating = eloRatings.get(i);
                    HBox hBox = new HBox();
                    hBox.getChildren().add(new Label(i + 1 + ""));
                    hBox.getChildren().add(new Label(eloRating.getPlayerName()));
                    hBox.getChildren().add(new Label(eloRating.getRating() + ""));
                    eloRatingVBox.getChildren().add(hBox);
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
