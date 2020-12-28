package ru.nsu.fit.towerdefense.fx.controllers.techtree;

import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import ru.nsu.fit.towerdefense.fx.Images;
import ru.nsu.fit.towerdefense.fx.SceneManager;
import ru.nsu.fit.towerdefense.fx.controllers.Controller;
import ru.nsu.fit.towerdefense.fx.controllers.game.WorldCamera;
import ru.nsu.fit.towerdefense.fx.exceptions.RenderException;
import ru.nsu.fit.towerdefense.fx.util.AlertBuilder;
import ru.nsu.fit.towerdefense.metadata.GameMetaData;
import ru.nsu.fit.towerdefense.metadata.UserMetaData;
import ru.nsu.fit.towerdefense.metadata.techtree.Research;
import ru.nsu.fit.towerdefense.util.Vector2;

import java.util.ArrayList;
import java.util.List;

/**
 * TechTreeController class is used by JavaFX in javafx.fxml.FXMLLoader for showing a tech tree
 * scene.
 *
 * @author Oleg Markelov
 */
public class TechTreeController implements Controller {

    private static final String FXML_FILE_NAME = "tech-tree.fxml";

    @FXML private StackPane worldWrapperStackPane;
    @FXML private AnchorPane worldAnchorPane;

    @FXML private Label researchLabel;

    @FXML private ImageView menuImageView;

    private final SceneManager sceneManager;
//    private final MyTechTree techTree;

    private WorldCamera worldCamera;

    /**
     * Creates new TechTreeController with specified SceneManager.
     *
     * @param sceneManager scene manager.
     */
    public TechTreeController(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
        /*this.techTree = new MyTechTree(new Vector2<>(1600, 900), new ArrayList<>() {{
            add(new MyResearch("First", "First Info", "circle.png", 2,
                new Vector2<>(100, 100), new Vector2<>(100, 100)));
            add(new MyResearch("Second", "Second Info", "triangle.png", 13,
                new Vector2<>(200, 200), new Vector2<>(100, 100)));
            add(new MyResearch("Third", "Third Info", "uni.png", 1499,
                new Vector2<>(900, 200), new Vector2<>(100, 100)));
        }});*/
    }

    @FXML
    private void initialize() {
        menuImageView.setOnMouseClicked(mouseEvent -> sceneManager.switchToMenu());

        researchLabel.setText(UserMetaData.getResearchPoints() + "");

        worldCamera = new WorldCamera(worldWrapperStackPane, worldAnchorPane,
            sceneManager.getStageSize(), GameMetaData.getInstance().getTechTree().getSize());

        for (Research research : GameMetaData.getInstance().getTechTree().getResearch()) {
            worldAnchorPane.getChildren().add(createResearchGridPane(research));
        }
    }

    private GridPane createResearchGridPane(Research research) {
        double width = research.getSize().getX() * worldCamera.getPixelsPerGameCell();
        double height = research.getSize().getY() * worldCamera.getPixelsPerGameCell();
        double imageHeight = height * 0.35d;
        double fontSize = height * 0.15d;

        GridPane gridPane = new GridPane(); // todo border width
        gridPane.setOnMouseClicked(mouseEvent -> {
            System.out.println(research.getName());
            GameMetaData.getInstance().getTechTree().unlock(research.getName());
        });

        gridPane.getStyleClass().add("research-box");
        gridPane.getColumnConstraints().addAll(createColumn());
        gridPane.getRowConstraints().addAll(createRow(25), createRow(50), createRow(25));

        gridPane.setMinWidth(width);
        gridPane.setMaxWidth(width);
        gridPane.setMinHeight(height);
        gridPane.setMaxHeight(height);

        gridPane.relocate(
            research.getPosition().getX() * worldCamera.getPixelsPerGameCell(),
            research.getPosition().getY() * worldCamera.getPixelsPerGameCell()
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

        ImageView priceImageView = new ImageView();
        priceImageView.getStyleClass().add("research-progress-icon");
        priceImageView.setFitHeight(fontSize);
        priceImageView.setPickOnBounds(true);
        priceImageView.setPreserveRatio(true);

        Label priceLabel = new Label(research.getCost() + "");
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void runAfterSceneSet() {
        sceneManager.getScene().setOnScroll(scrollEvent -> {
            if (scrollEvent.getDeltaY() == 0) {
                return;
            }

            worldCamera.scale(scrollEvent.getDeltaY() > 0,
                scrollEvent.getSceneX(), scrollEvent.getSceneY());
        });

        sceneManager.getScene().setOnMousePressed(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                worldCamera.initMovement(mouseEvent.getSceneX(), mouseEvent.getSceneY());
            }
        });

        sceneManager.getScene().setOnMouseDragged(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                worldCamera.updateMovement(mouseEvent.getSceneX(), mouseEvent.getSceneY());
            }
        });

        sceneManager.getScene().setOnMouseReleased(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                worldCamera.finishMovement(mouseEvent.getSceneX(), mouseEvent.getSceneY());
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

    // ----- Stubs -----

    private static class MyTechTree {

        private Vector2<Integer> size;
        private List<MyResearch> researches;

        public MyTechTree(Vector2<Integer> size, List<MyResearch> researches) {
            this.size = size;
            this.researches = researches;
        }

        public Vector2<Integer> getSize() {
            return size;
        }

        public List<MyResearch> getResearches() {
            return researches;
        }
    }

    private static class MyResearch {

        private String name;
        private String info;
        private String image;
        private int cost;
        private Vector2<Integer> position;
        private Vector2<Integer> size;

        public MyResearch(String name, String info, String image, int cost, Vector2<Integer> position, Vector2<Integer> size) {
            this.name = name;
            this.info = info;
            this.image = image;
            this.cost = cost;
            this.position = position;
            this.size = size;
        }

        public String getName() {
            return name;
        }

        public String getInfo() {
            return info;
        }

        public String getImage() {
            return image;
        }

        public int getCost() {
            return cost;
        }

        public Vector2<Integer> getPosition() {
            return position;
        }

        public Vector2<Integer> getSize() {
            return size;
        }
    }
}
