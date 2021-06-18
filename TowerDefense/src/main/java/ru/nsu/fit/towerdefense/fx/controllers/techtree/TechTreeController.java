package ru.nsu.fit.towerdefense.fx.controllers.techtree;

import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import ru.nsu.fit.towerdefense.fx.Images;
import ru.nsu.fit.towerdefense.fx.SceneManager;
import ru.nsu.fit.towerdefense.fx.controllers.Camera;
import ru.nsu.fit.towerdefense.fx.controllers.Controller;
import ru.nsu.fit.towerdefense.fx.exceptions.RenderException;
import ru.nsu.fit.towerdefense.fx.util.AlertBuilder;
import ru.nsu.fit.towerdefense.metadata.GameMetaData;
import ru.nsu.fit.towerdefense.metadata.UserMetaData;
import ru.nsu.fit.towerdefense.metadata.gameobjecttypes.ProjectileType;
import ru.nsu.fit.towerdefense.metadata.gameobjecttypes.TowerType;
import ru.nsu.fit.towerdefense.metadata.techtree.Research;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static javafx.scene.input.KeyCode.CONTROL;
import static ru.nsu.fit.towerdefense.fx.util.AlertBuilder.MAP_LOADING_ERROR_HEADER;

/**
 * TechTreeController class is used by JavaFX in javafx.fxml.FXMLLoader for showing a tech tree
 * scene.
 *
 * @author Oleg Markelov
 */
public class TechTreeController implements Controller {

    private static final String FXML_FILE_NAME = "tech-tree.fxml";
    private static final DecimalFormat DECIMAL_FORMAT =
        new DecimalFormat("#.##", new DecimalFormatSymbols() {{setDecimalSeparator('.');}});

    @FXML private Button clearButton;
    @FXML private Button addResearchPointsButton;
    @FXML private Button addMultiplayerPointsButton;

    @FXML private StackPane worldWrapperStackPane;
    @FXML private AnchorPane worldAnchorPane;

    @FXML private Label researchLabel;
    @FXML private Label multiplayerLabel;

    @FXML private ImageView menuImageView;

    @FXML private StackPane gameObjectSidePane;
    @FXML private VBox platformSideVBox;
    @FXML private Text researchNameText;
    @FXML private Text researchDisplayInfoText;
    @FXML private HBox researchBuyHBox;
    @FXML private Label researchBuyLabel;
    @FXML private FlowPane buildTowerFlowPane;
    @FXML private VBox buildTowerCharacteristicsVBox;
    @FXML private Text buildTowerNameText;
    @FXML private Text buildTowerDisplayInfoText;
    @FXML private Label buildTowerOmnidirectionalLabel;
    @FXML private Label buildTowerRangeLabel;
    @FXML private Label buildTowerFireRateLabel;
    @FXML private Label buildTowerRotationSpeedLabel;
    @FXML private Label buildTowerSelfGuidedLabel;
    @FXML private Label buildTowerProjectileSpeedLabel;
    @FXML private Label buildTowerDamageLabel;

    private final SceneManager sceneManager;

    private Camera camera;

    /**
     * Creates new TechTreeController with specified SceneManager.
     *
     * @param sceneManager scene manager.
     */
    public TechTreeController(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    @FXML
    private void initialize() {
        clearButton.setOnMouseClicked(mouseEvent -> {
            UserMetaData.clear();
            new AlertBuilder()
                .setAlertType(Alert.AlertType.WARNING)
                .setHeaderText("Saves are cleared, restart the app.")
                .setContentText("Seriously, restart!")
                .setOwner(sceneManager.getWindowOwner())
                .build().showAndWait();
        });
        addResearchPointsButton.setOnMouseClicked(mouseEvent -> {
            UserMetaData.addResearchPoints(10);
            researchLabel.setText(UserMetaData.getResearchPoints() + "");
        });
        addMultiplayerPointsButton.setOnMouseClicked(mouseEvent -> {
            UserMetaData.addMultiplayerPoints(10);
            multiplayerLabel.setText(UserMetaData.getMultiplayerPoints() + "");
        });

        menuImageView.setOnMouseClicked(mouseEvent -> sceneManager.switchToMenu());

        researchLabel.setText(UserMetaData.getResearchPoints() + "");
        multiplayerLabel.setText(UserMetaData.getMultiplayerPoints() + "");

        camera = new Camera(worldWrapperStackPane, worldAnchorPane,
            sceneManager.getStageSize(), GameMetaData.getInstance().getTechTree().getSize());

        bindUppercase(researchNameText);
        bindUppercase(buildTowerNameText);

        worldWrapperStackPane.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                gameObjectSidePane.setVisible(false);
            }
        });

        drawTechTree();
    }

    private void drawTechTree() {
        worldAnchorPane.getChildren().clear();
        for (Research research : GameMetaData.getInstance().getTechTree().getResearch()) {
            worldAnchorPane.getChildren().add(createResearchGridPane(research));
            worldAnchorPane.getChildren().addAll(createLines(research));
        }
    }

    private List<Line> createLines(Research research) {
        List<Line> lines = new ArrayList<>();

        boolean isResearched = UserMetaData.isResearched(research.getName());

        for (Research destinationResearch : research.getInfluence()) {
            Line line = new Line(
                (research.getPosition().getX() + research.getSize().getX()) * camera.getPixelsPerGameCell(),
                (research.getPosition().getY() + research.getSize().getY() * 0.5d) * camera.getPixelsPerGameCell(),
                destinationResearch.getPosition().getX() * camera.getPixelsPerGameCell(),
                (destinationResearch.getPosition().getY() + destinationResearch.getSize().getY() * 0.5d) * camera.getPixelsPerGameCell()
            );

            line.setStrokeWidth(research.getSize().getY() * camera.getPixelsPerGameCell() * 0.01d);
            line.setStroke(isResearched ? Color.web("rgba(0, 255, 0, 0.5)") : Color.web("rgba(255, 255, 255, 0.5)"));
            line.setViewOrder(Double.POSITIVE_INFINITY);

            lines.add(line);
        }

        return lines;
    }

    private GridPane createResearchGridPane(Research research) {
        double width = research.getSize().getX() * camera.getPixelsPerGameCell();
        double height = research.getSize().getY() * camera.getPixelsPerGameCell();
        double imageHeight = height * 0.35d;
        double fontSize = height * 0.15d;
        double borderWidth = height * 0.01d;

        GridPane gridPane = new GridPane();
        gridPane.setOnMouseClicked(mouseEvent -> {
            if (!mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                return;
            }

            mouseEvent.consume();

            gameObjectSidePane.setVisible(true);
            updateSideBar(research);
        });

        gridPane.getStyleClass().add("research-box");
        if (UserMetaData.isResearched(research.getName())) {
            gridPane.getStyleClass().add("research-box-researched");
        } else if (GameMetaData.getInstance().getTechTree().canUnlock(research.getName())) {
            gridPane.getStyleClass().add("research-box-available");
        }
        gridPane.setStyle("-fx-border-width: " + borderWidth + ";");
        gridPane.getColumnConstraints().addAll(createColumn());
        gridPane.getRowConstraints().addAll(createRow(25), createRow(50), createRow(25));

        gridPane.setMinWidth(width);
        gridPane.setMaxWidth(width);
        gridPane.setMinHeight(height);
        gridPane.setMaxHeight(height);

        gridPane.relocate(
            research.getPosition().getX() * camera.getPixelsPerGameCell(),
            research.getPosition().getY() * camera.getPixelsPerGameCell()
        );

        Label label = new Label(research.getName());
        label.setFont(new Font(fontSize));
        gridPane.add(label, 0, 0);

        ImageView imageView = new ImageView();
        try {
            imageView.setImage(Images.getInstance().getImage(research.getImage()));
        } catch (RenderException e) {
            System.out.println(e.getMessage());
        }
        imageView.setFitHeight(imageHeight);
        imageView.setPickOnBounds(true);
        imageView.setPreserveRatio(true);
        gridPane.add(imageView, 0, 1);

        HBox hBox = new HBox();
        hBox.getStyleClass().add("research-price");

        boolean researchPoints = research.getMultiplayerCost() == 0;

        ImageView priceImageView = new ImageView();
        priceImageView.getStyleClass().add(
            researchPoints ? "research-progress-icon" : "multiplayer-progress-icon");
        priceImageView.setFitHeight(fontSize);
        priceImageView.setPickOnBounds(true);
        priceImageView.setPreserveRatio(true);

        Label priceLabel = new Label((researchPoints ?
            research.getCost() : research.getMultiplayerCost()) + "");
        priceLabel.setFont(new Font(fontSize));

        hBox.getChildren().addAll(priceImageView, priceLabel);

        gridPane.add(hBox, 0, 2);

        return gridPane;
    }

    private ColumnConstraints createColumn() {
        return new ColumnConstraints() {{
            setHalignment(HPos.CENTER);
        }};
    }

    private RowConstraints createRow(double percent) {
        return new RowConstraints() {{
            setValignment(VPos.CENTER);
            setPercentHeight(percent);
        }};
    }

    private void updateSideBar(Research research) {
        researchNameText.setText(research.getName());
        researchDisplayInfoText.setText(research.getInfo());

        if (UserMetaData.isResearched(research.getName())) {
            researchBuyLabel.setText("Researched");
            researchBuyHBox.setStyle("-fx-background-color: green;");
            researchBuyHBox.setOnMouseClicked(null);
        } else if (GameMetaData.getInstance().getTechTree().canUnlock(research.getName())) {
            researchBuyLabel.setText("Research");
            researchBuyHBox.setStyle("-fx-background-color: firebrick;");
            researchBuyHBox.setOnMouseClicked(mouseEvent -> {
                if (research.getMultiplayerCost() == 0) {
                    if (UserMetaData.getResearchPoints() >= research.getCost()) {
                        GameMetaData.getInstance().getTechTree().unlock(research.getName());
                        UserMetaData.subtractResearchPoints(research.getCost());

                        researchLabel.setText(UserMetaData.getResearchPoints() + "");
                        drawTechTree();

                        researchBuyLabel.setText("Researched");
                        researchBuyHBox.setStyle("-fx-background-color: green;");
                        researchBuyHBox.setOnMouseClicked(null);
                    } else {
                        new AlertBuilder()
                            .setAlertType(Alert.AlertType.INFORMATION)
                            .setHeaderText("Not enough research points")
                            .setContentText("")
                            .setOwner(sceneManager.getWindowOwner())
                            .build().showAndWait();
                    }
                } else {
                    if (UserMetaData.getMultiplayerPoints() >= research.getMultiplayerCost()) {
                        GameMetaData.getInstance().getTechTree().unlock(research.getName());
                        UserMetaData.subtractMultiplayerPoints(research.getMultiplayerCost());

                        multiplayerLabel.setText(UserMetaData.getMultiplayerPoints() + "");
                        drawTechTree();

                        researchBuyLabel.setText("Researched");
                        researchBuyHBox.setStyle("-fx-background-color: green;");
                        researchBuyHBox.setOnMouseClicked(null);
                    } else {
                        new AlertBuilder()
                            .setAlertType(Alert.AlertType.INFORMATION)
                            .setHeaderText("Not enough multiplayer points")
                            .setContentText("")
                            .setOwner(sceneManager.getWindowOwner())
                            .build().showAndWait();
                    }
                }
            });
        } else {
            researchBuyLabel.setText("Not available");
            researchBuyHBox.setStyle("-fx-background-color: grey;");
            researchBuyHBox.setOnMouseClicked(null);
        }

        List<TowerType> towerTypes = research.getTowerNames().stream()
            .map(towerName -> GameMetaData.getInstance().getTowerType(towerName))
            .sorted(Comparator.comparingInt(TowerType::getPrice))
            .collect(Collectors.toList());

        buildTowerFlowPane.getChildren().clear();
        for (TowerType towerType : towerTypes) {
            try {
                ImageView imageView = new ImageView(
                    Images.getInstance().getImage(towerType.getImage()));
                imageView.setFitWidth(48);
                imageView.setFitHeight(48);
                imageView.setPickOnBounds(true);
                imageView.setPreserveRatio(true);

                Label label = new Label("$" + towerType.getPrice());

                VBox towerTypeVBox = new VBox();
                towerTypeVBox.getStyleClass().add("tower-upgrade-box");
                towerTypeVBox.getChildren().addAll(imageView, label);
                towerTypeVBox.setOnMouseEntered(mouseEvent -> {
                    buildTowerNameText.setText(towerType.getTypeName());
                    buildTowerDisplayInfoText.setText(towerType.getDisplayInfo());
                    buildTowerOmnidirectionalLabel.setText(
                        towerType.getFireType() == TowerType.FireType.OMNIDIRECTIONAL ? "Yes" : "No");
                    buildTowerRangeLabel.setText(DECIMAL_FORMAT.format(towerType.getRange()));
                    buildTowerFireRateLabel.setText(DECIMAL_FORMAT.format(1000d / towerType.getFireRate()));
                    buildTowerRotationSpeedLabel.setText(DECIMAL_FORMAT.format(towerType.getRotationSpeed()));
                    ProjectileType projectileType =
                        GameMetaData.getInstance().getProjectileType(towerType.getProjectileType());
                    buildTowerSelfGuidedLabel.setText(projectileType.isSelfGuided() ? "Yes" : "No");
                    buildTowerProjectileSpeedLabel.setText(DECIMAL_FORMAT.format(projectileType.getSpeed() * 1000));
                    buildTowerDamageLabel.setText(DECIMAL_FORMAT.format(projectileType.getBasicDamage()));

                    buildTowerCharacteristicsVBox.setVisible(true);
                });
                towerTypeVBox.setOnMouseExited(mouseEvent -> {
                    buildTowerCharacteristicsVBox.setVisible(false);
                });

                buildTowerFlowPane.getChildren().add(towerTypeVBox);
            } catch (RenderException e) {
                System.out.println(e.getMessage());
            }
        }
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

        sceneManager.getScene().setOnScroll(scrollEvent -> {
            if (scrollEvent.getDeltaY() == 0) {
                return;
            }

            camera.scale(scrollEvent.getDeltaY() > 0,
                scrollEvent.getSceneX(), scrollEvent.getSceneY());
        });

        sceneManager.getScene().setOnMousePressed(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                camera.initMovement(mouseEvent.getSceneX(), mouseEvent.getSceneY());
            }
        });

        sceneManager.getScene().setOnMouseDragged(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                camera.updateMovement(mouseEvent.getSceneX(), mouseEvent.getSceneY());
            }
        });

        sceneManager.getScene().setOnMouseReleased(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                camera.finishMovement(mouseEvent.getSceneX(), mouseEvent.getSceneY());
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFXMLFileName() {
        return FXML_FILE_NAME;
    }

    private void bindUppercase(Text text) {
        text.textProperty().addListener((observable, oldValue, newValue) -> {
            text.setText(newValue.toUpperCase());
        });
    }
}
