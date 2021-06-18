package ru.nsu.fit.towerdefense.fx.controllers.elorating;

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
import ru.nsu.fit.towerdefense.multiplayer.entities.SEloRating;

import java.util.List;

/**
 * EloRatingController class is used by JavaFX in javafx.fxml.FXMLLoader for showing a ELO rating
 * scene.
 *
 * @author Oleg Markelov
 */
public class EloRatingController implements Controller {

    private static final String FXML_FILE_NAME = "elo-rating.fxml";

    @FXML private StackPane root;
    @FXML private GridPane eloRatingGridPane;

    @FXML private ImageView menuImageView;

    private final SceneManager sceneManager;
    private final ConnectionManager connectionManager;

    public EloRatingController(SceneManager sceneManager, ConnectionManager connectionManager) {
        this.sceneManager = sceneManager;
        this.connectionManager = connectionManager;
    }

    @FXML
    private void initialize() {
        menuImageView.setOnMouseClicked(mouseEvent -> sceneManager.switchToMenu());

        new Thread(() -> {
            List<SEloRating> eloRatings = connectionManager.getEloLeaderboard(0);

            Platform.runLater(() -> {
                for (int i = 0; i < eloRatings.size(); i++) {
                    SEloRating eloRating = eloRatings.get(i);
                    eloRatingGridPane.add(new HBox(new Label(i + 1 + "")), 0, i + 1);
                    eloRatingGridPane.add(new HBox(new Label(eloRating.getPlayerName())), 1, i + 1);
                    eloRatingGridPane.add(new HBox(new Label(eloRating.getRating() + "")), 2, i + 1);
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
