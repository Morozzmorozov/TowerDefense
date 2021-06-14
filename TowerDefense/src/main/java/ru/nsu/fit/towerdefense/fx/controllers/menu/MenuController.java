package ru.nsu.fit.towerdefense.fx.controllers.menu;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
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
import ru.nsu.fit.towerdefense.replay.GameStateReader;
import ru.nsu.fit.towerdefense.replay.Replay;

import java.io.File;

import static javafx.scene.input.KeyCode.CONTROL;

/**
 * MenuController class is used by JavaFX in javafx.fxml.FXMLLoader for showing a menu scene.
 *
 * @author Oleg Markelov
 */
public class MenuController implements Controller {

    private static final String FXML_FILE_NAME = "menu.fxml";

    @FXML private Button clearButton;
    @FXML private Button addResearchPointsButton;
    @FXML private Button addMultiplayerPointsButton;

    @FXML private ImageView techTreeImageView;
    @FXML private ImageView eloRatingImageView;
    @FXML private ImageView lobbiesImageView;
    @FXML private HBox userHBox;
    @FXML private Label userLabel;
    @FXML private Label levelsLabel;
    @FXML private FlowPane levelsFlowPane;

    @FXML private Label researchLabel;
    @FXML private Label multiplayerLabel;

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
        clearButton.setOnMouseClicked(mouseEvent -> {
            UserMetaData.clear();
            researchLabel.setText(UserMetaData.getResearchPoints() + "");
            multiplayerLabel.setText(UserMetaData.getMultiplayerPoints() + "");
        });
        addResearchPointsButton.setOnMouseClicked(mouseEvent -> {
            UserMetaData.addResearchPoints(10);
            researchLabel.setText(UserMetaData.getResearchPoints() + "");
        });
        addMultiplayerPointsButton.setOnMouseClicked(mouseEvent -> {
            UserMetaData.addMultiplayerPoints(10);
            multiplayerLabel.setText(UserMetaData.getMultiplayerPoints() + "");
        });

        setLoggedIn(null);

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
            userLabel.setText(username);
            userHBox.setOnMouseClicked(mouseEvent -> logout());
        } else {
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

        gridPane.add(createLevelButton("resume-icon", "New game",
            mouseEvent -> sceneManager.switchToGame(gameMapName)), 0, 2);
        //gridPane.add(createLevelButton("skip-right-icon", "Resume", null), 1, 2); // todo uncomment
        gridPane.add(createLevelButton("camera-icon", "View replay",
            mouseEvent -> {
                comboBox.getItems().clear();
                comboBox.getItems().addAll(GameStateReader.getInstance().getReplays(gameMapName));
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
                    Replay replay = GameStateReader.getInstance().readReplay(gameMapName, comboBox.getValue());
                    if (replay != null) {
                        sceneManager.switchToGame(gameMapName, replay);
                    } else {
                        new AlertBuilder()
                            .setHeaderText("Replay is null!")
                            .setOwner(sceneManager.getWindowOwner())
                            .build().showAndWait();
                    }
                }
            }), 1, 2); // todo change to 0, 3
        //gridPane.add(createLevelButton("idle-icon", "Idle game", null), 1, 3); // todo uncomment

        gridPane.add(createLevelButton("cooperative-icon", "Cooperative",
            mouseEvent -> createCooperativeGame(gameMapName)), 0, 3);
        gridPane.add(createLevelButton("competition-icon", "Competition",
            mouseEvent -> System.out.println("competition")), 1, 3);
        gridPane.add(createLevelButton("leaderboard-icon", "Leaders",
            mouseEvent -> showLeaderboard(gameMapName)), 0, 4);

        return gridPane;
    }

    private ColumnConstraints createColumn() {
        return new ColumnConstraints() {{
            setHalignment(HPos.CENTER);
            setPercentWidth(50);
        }};
    }

    private HBox createLevelButton(String imageStyleClass, String labelText,
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

        return hBox;
    }

    private void createCooperativeGame(String gameMapName) {
        new Thread(() -> {
            String lobbyId = connectionManager.createLobby(gameMapName);
            if (lobbyId == null) {
                System.out.println("lobbyId is null");
                return;
            }

            String sessionToken = connectionManager.joinLobby(lobbyId);
            if (sessionToken == null) {
                System.out.println("sessionToken is null");
                return;
            }

            Platform.runLater(() -> {
                System.out.println("switchToLobby");
                sceneManager.switchToLobby(lobbyId, sessionToken);
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
    public void runAfterSceneSet() {
        sceneManager.getScene().setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().equals(CONTROL)) {
                clearButton.setManaged(true);
                clearButton.setVisible(true);
                addResearchPointsButton.setManaged(true);
                addResearchPointsButton.setVisible(true);
                addMultiplayerPointsButton.setManaged(true);
                addMultiplayerPointsButton.setVisible(true);
            }
        });

        sceneManager.getScene().setOnKeyReleased(keyEvent -> {
            if (keyEvent.getCode().equals(CONTROL)) {
                clearButton.setManaged(false);
                clearButton.setVisible(false);
                addResearchPointsButton.setManaged(false);
                addResearchPointsButton.setVisible(false);
                addMultiplayerPointsButton.setManaged(false);
                addMultiplayerPointsButton.setVisible(false);
            }
        });
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
