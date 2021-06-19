package ru.nsu.fit.towerdefense.fx.controllers.menu;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.util.Pair;
import ru.nsu.fit.towerdefense.fx.SceneManager;
import ru.nsu.fit.towerdefense.fx.controllers.Controller;
import ru.nsu.fit.towerdefense.fx.util.AlertBuilder;
import ru.nsu.fit.towerdefense.metadata.GameMetaData;
import ru.nsu.fit.towerdefense.metadata.UserMetaData;
import ru.nsu.fit.towerdefense.multiplayer.ConnectionManager;
import ru.nsu.fit.towerdefense.multiplayer.GameType;
import ru.nsu.fit.towerdefense.multiplayer.entities.SGameSession;
import ru.nsu.fit.towerdefense.replay.Replay;
import ru.nsu.fit.towerdefense.replay.ReplayManager;

import java.io.File;

/**
 * MenuController class is used by JavaFX in javafx.fxml.FXMLLoader for showing a menu scene.
 *
 * @author Oleg Markelov
 */
public class MenuController implements Controller {

    private static final String FXML_FILE_NAME = "menu.fxml";

    @FXML private ImageView techTreeImageView;
    @FXML private ImageView eloRatingImageView;
    @FXML private ImageView lobbiesImageView;
    @FXML private HBox userHBox;
    @FXML private Label userLabel;
    @FXML private Label levelsLabel;
    @FXML private FlowPane levelsFlowPane;

    @FXML private Label researchLabel;
    @FXML private Label multiplayerLabel;

    private final BooleanProperty loggedIn = new SimpleBooleanProperty(false);

    private final SceneManager sceneManager;
    private final ConnectionManager connectionManager;

    private final ComboBox<String> comboBox;

    /**
     * Creates new MenuController with specified SceneManager and UserManager.
     *
     * @param sceneManager scene manager.
     * @param connectionManager  user manager.
     */
    public MenuController(SceneManager sceneManager, ConnectionManager connectionManager) {
        this.sceneManager = sceneManager;
        this.connectionManager = connectionManager;

        comboBox = new ComboBox<>();
    }

    @FXML
    private void initialize() {
        setLoggedIn(connectionManager.getUsername());

        lobbiesImageView.visibleProperty().bind(loggedIn);
        lobbiesImageView.managedProperty().bind(loggedIn);
        eloRatingImageView.visibleProperty().bind(loggedIn);
        eloRatingImageView.managedProperty().bind(loggedIn);

        researchLabel.setText(UserMetaData.getResearchPoints() + "");
        multiplayerLabel.setText(UserMetaData.getMultiplayerPoints() + "");
        techTreeImageView.setOnMouseClicked(mouseEvent -> sceneManager.switchToTechTree());
        eloRatingImageView.setOnMouseClicked(mouseEvent -> sceneManager.switchToEloRating());
        lobbiesImageView.setOnMouseClicked(mouseEvent -> sceneManager.switchToLobbies());

        if (GameMetaData.getInstance().getGameMapNames().isEmpty()) {
            levelsLabel.setText("No levels");
        }

        for (String gameMapName : GameMetaData.getInstance().getGameMapNames()) {
            levelsFlowPane.getChildren().add(createLevelGridPane(gameMapName));
        }
    }

    private void showLoginDialog() {
        new LoginDialog(sceneManager.getWindowOwner()).show(this::login);
    }

    private void login(Pair<String, String> usernamePassword) {
        new Thread(() -> {
            String username = usernamePassword.getKey();
            String password = usernamePassword.getValue();

            Boolean loggedIn = connectionManager.login(username, password);

            Platform.runLater(() -> {
                if (loggedIn != null && loggedIn) {
                    setLoggedIn(username);
                    return;
                }

                new LoginDialog(sceneManager.getWindowOwner()).show(
                    loggedIn == null ? "Check your internet connection and try again" : "Wrong password",
                    usernamePassword, this::login);
            });
        }).start();
    }

    private void logout() {
        setLoggedIn(null);
        new Thread(connectionManager::logout).start();
    }

    private void setLoggedIn(String username) {
        if (username != null) {
            loggedIn.set(true);
            userLabel.setText(username);
            userHBox.setOnMouseClicked(mouseEvent -> logout());
        } else {
            loggedIn.set(false);
            userLabel.setText("User");
            userHBox.setOnMouseClicked(mouseEvent -> showLoginDialog());
        }
    }

    private GridPane createLevelGridPane(String gameMapName) {
        GridPane gridPane = new GridPane();
        gridPane.getStyleClass().add("level-grid-pane");
        gridPane.getColumnConstraints().addAll(createColumn(), createColumn());

        Label label = new Label(gameMapName);
        label.getStyleClass().add("level-header");
        GridPane.setColumnSpan(label, 2);
        gridPane.add(label, 0, 0);

        ImageView imageView;
        File snapshotFile = new File(".\\levelsnapshots\\" + gameMapName + ".png");
        if (snapshotFile.exists()) {
            imageView = new ImageView(new Image(snapshotFile.toURI().toString()));
        } else {
            imageView = new ImageView();
            imageView.getStyleClass().add("question-icon");
        }
        imageView.setFitHeight(192);
        imageView.setPickOnBounds(true);
        imageView.setPreserveRatio(true);
        GridPane.setColumnSpan(imageView, 2);
        gridPane.add(imageView, 0, 1);

        gridPane.add(createLevelButton(false, "resume-icon", "New game",
            mouseEvent -> sceneManager.switchToGame(gameMapName)), 0, 2);
        //gridPane.add(createLevelButton("skip-right-icon", "Resume", null), 1, 2); // todo uncomment
        gridPane.add(createLevelButton(false, "camera-icon", "View replay",
            mouseEvent -> showReplayChooserDialog(gameMapName)), 1, 2); // todo change to 0, 3
        //gridPane.add(createLevelButton("idle-icon", "Idle game", null), 1, 3); // todo uncomment

        HBox cooperativeHBox = createLevelButton(true, "cooperative-icon", "Cooperative",
            mouseEvent -> createMultiplayerGame(gameMapName, GameType.COOPERATIVE));
        cooperativeHBox.setDisable(
            !GameMetaData.getInstance().getMapDescription(gameMapName).isCooperativeAvailable());
        gridPane.add(cooperativeHBox, 0, 3);

        gridPane.add(createLevelButton(true, "competition-icon", "Competition",
            mouseEvent -> createMultiplayerGame(gameMapName, GameType.COMPETITIVE)), 1, 3);
        //gridPane.add(createLevelButton(true, "leaderboard-icon", "Leaders",
        //    mouseEvent -> showLeaderboard(gameMapName)), 0, 4); // todo uncomment

        return gridPane;
    }

    private ColumnConstraints createColumn() {
        return new ColumnConstraints() {{
            setHalignment(HPos.CENTER);
            setPercentWidth(50);
        }};
    }

    private HBox createLevelButton(boolean bindWithLoggedIn, String imageStyleClass, String labelText,
                                   EventHandler<? super MouseEvent> mouseEvent)
    {
        HBox hBox = new HBox();
        hBox.getStyleClass().add("level-button");
        hBox.setOnMouseClicked(mouseEvent);

        ImageView imageView = new ImageView();
        imageView.getStyleClass().add(imageStyleClass);
        imageView.setFitHeight(32);
        imageView.setPickOnBounds(true);
        imageView.setPreserveRatio(true);

        Label label = new Label(labelText);

        hBox.getChildren().addAll(imageView, label);

        if (bindWithLoggedIn) {
            hBox.visibleProperty().bind(loggedIn);
            hBox.managedProperty().bind(loggedIn);
        }

        return hBox;
    }

    private void showReplayChooserDialog(String gameMapName) {
        comboBox.getItems().clear();
        var replays = ReplayManager.getReplays(gameMapName);
        if (replays != null) {
            comboBox.getItems().addAll(replays);
        }
        comboBox.getSelectionModel().selectFirst();

        Alert alert = new AlertBuilder()
            .setAlertType(Alert.AlertType.CONFIRMATION)
            .setButtons(ButtonType.OK, ButtonType.CANCEL)
            .setHeaderText("Select a replay:")
            .setContent(new HBox() {{
                setStyle("-fx-alignment: center;");
                getChildren().add(comboBox);
            }})
            .setOwner(sceneManager.getWindowOwner())
            .build();

        alert.showAndWait();
        if (alert.getResult() == ButtonType.OK) {
            Replay replay = ReplayManager.readReplay(gameMapName, comboBox.getValue());
            if (replay != null) {
                sceneManager.switchToGame(gameMapName, replay);
            } else {
                new AlertBuilder()
                    .setHeaderText("Replay is null!")
                    .setOwner(sceneManager.getWindowOwner())
                    .build().showAndWait();
            }
        }
    }

    private void createMultiplayerGame(String gameMapName, GameType gameType) {
        new Thread(() -> {
            SGameSession gameSession = connectionManager.createLobby(gameMapName, gameType);
            if (gameSession == null) {
                System.out.println("gameSession is null");
                return;
            }

            Platform.runLater(() -> {
                System.out.println("switchToLobby");
                sceneManager.switchToLobby(gameSession.getSessionId(), gameSession.getSessionToken());
            });
        }).start();
    }

    private void showLeaderboard(String gameMapName) {
        sceneManager.switchToLeaderboard(gameMapName);
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
        sceneManager.getScene().setOnKeyPressed(null);
        sceneManager.getScene().setOnKeyReleased(null);
    }
}
