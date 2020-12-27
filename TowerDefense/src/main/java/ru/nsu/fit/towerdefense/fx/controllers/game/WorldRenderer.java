package ru.nsu.fit.towerdefense.fx.controllers.game;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import ru.nsu.fit.towerdefense.fx.Images;
import ru.nsu.fit.towerdefense.fx.exceptions.RenderException;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.Renderable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * WorldRenderer class is used for rendering game objects on the game scene.
 *
 * @author Oleg Markelov
 */
public class WorldRenderer {

    private final ObservableList<Node> gameNodes;
    private final double pixelsPerGameCell;
    private final WorldRendererObserver observer;

    private final Map<Renderable, Node> renderableToGameNodeMap = new HashMap<>();
    private final Circle towerRangeCircle;
    private final Circle newTowerRangeCircle;
    private final Rectangle gameObjectSelectionRectangle;

    private Node selectedGameNode;

    /**
     * Creates new WorldRenderer with specified game nodes.
     *
     * @param gameNodes         list of game nodes.
     * @param pixelsPerGameCell how many pixels fit one dimension of game cell.
     * @param observer          world renderer observer.
     */
    public WorldRenderer(ObservableList<Node> gameNodes, double pixelsPerGameCell, WorldRendererObserver observer) {
        this.gameNodes = gameNodes;
        this.pixelsPerGameCell = pixelsPerGameCell;
        this.observer = observer;

        towerRangeCircle = createCircle();
        newTowerRangeCircle = createCircle();
        gameObjectSelectionRectangle = createRectangle();

        gameNodes.add(towerRangeCircle);
        gameNodes.add(newTowerRangeCircle);
        gameNodes.add(gameObjectSelectionRectangle);
    }

    private Circle createCircle() {
        Circle circle = new Circle();
        circle.setVisible(false);
        circle.setManaged(false);
        circle.setMouseTransparent(true);
        circle.setViewOrder(Double.NEGATIVE_INFINITY);
        circle.getStyleClass().add("tower-range");
        return circle;
    }

    private Rectangle createRectangle() {
        Rectangle rectangle = new Rectangle();
        rectangle.setVisible(false);
        rectangle.setManaged(false);
        rectangle.setMouseTransparent(true);
        rectangle.setViewOrder(Double.NEGATIVE_INFINITY);
        rectangle.getStyleClass().add("game-object-selection");
        return rectangle;
    }

    /**
     * Updates a map (Renderable -> game node) with new renderables.
     *
     * @param newRenderableSet updated renderables.
     */
    public void update(Set<Renderable> newRenderableSet) throws RenderException {
        for (Renderable renderable : newRenderableSet) {
            add(renderable);
        }

        renderableToGameNodeMap.entrySet().removeIf(entry -> !newRenderableSet.contains(entry.getKey()));
    }

    /**
     * Renders previously updated renderables.
     *
     * Must be called in JavaFX Application thread!
     */
    public void render() throws RenderException {
        gameNodes.removeIf(node -> node != gameObjectSelectionRectangle
            && node != towerRangeCircle && node != newTowerRangeCircle
            && !renderableToGameNodeMap.containsKey(node.getUserData()));

        if (!gameNodes.contains(selectedGameNode)) {
            hideGameObjectSelection();
            selectedGameNode = null;
        }

        for (Map.Entry<Renderable, Node> entry : new ArrayList<>(renderableToGameNodeMap.entrySet())) {
            Renderable renderable = entry.getKey();
            ImageView imageView = (ImageView) entry.getValue();

            Image image = Images.getInstance().getImage(renderable.getImageName());
            if (imageView.getImage() != image) {
                imageView.setImage(image);
            }

            imageView.relocate(
                renderable.getPosition().getX() * pixelsPerGameCell,
                renderable.getPosition().getY() * pixelsPerGameCell);

            imageView.setFitWidth(renderable.getSize().getX() * pixelsPerGameCell);
            imageView.setFitHeight(renderable.getSize().getY() * pixelsPerGameCell);

            imageView.setRotate(renderable.getRotation());
            imageView.setViewOrder(-renderable.getZIndex());

            if (!gameNodes.contains(imageView)) {
                gameNodes.add(imageView);
            }
        }
    }

    /**
     * Adds renderable for future render.
     *
     * @param renderable renderable.
     * @throws RenderException if no image was found for this renderable.
     */
    public void add(Renderable renderable) throws RenderException {
        if (!renderableToGameNodeMap.containsKey(renderable)) {
            ImageView imageView =
                new ImageView(Images.getInstance().getImage(renderable.getImageName()));

            imageView.setOnMouseClicked(mouseEvent -> {
                if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                    observer.onGameObjectClicked(renderable);
                    mouseEvent.consume();
                }
            });

            imageView.setUserData(renderable);
            renderableToGameNodeMap.put(renderable, imageView);
        }
    }

    /**
     * Removes renderable for future erasing.
     *
     * @param renderable renderable.
     */
    public void remove(Renderable renderable) {
        renderableToGameNodeMap.remove(renderable);
    }

    /**
     * Shows game object selection.
     *
     * @param renderable renderable to center on.
     */
    public void showGameObjectSelection(Renderable renderable) {
        gameObjectSelectionRectangle.setWidth(renderable.getSize().getX() * pixelsPerGameCell
            + gameObjectSelectionRectangle.getStrokeWidth());
        gameObjectSelectionRectangle.setHeight(renderable.getSize().getY() * pixelsPerGameCell
            + gameObjectSelectionRectangle.getStrokeWidth());

        selectedGameNode = renderableToGameNodeMap.get(renderable);
        gameObjectSelectionRectangle.layoutXProperty().bind(selectedGameNode.layoutXProperty()
            .subtract(gameObjectSelectionRectangle.getStrokeWidth() / 2));
        gameObjectSelectionRectangle.layoutYProperty().bind(selectedGameNode.layoutYProperty()
            .subtract(gameObjectSelectionRectangle.getStrokeWidth() / 2));

        gameObjectSelectionRectangle.setVisible(true);
    }

    /**
     * Shows tower range.
     *
     * @param renderable renderable to center on.
     * @param range      radius.
     */
    public void showTowerRange(Renderable renderable, int range) {
        showTowerRangeCircle(renderable, range, towerRangeCircle);
    }

    /**
     * Shows new tower range.
     *
     * @param renderable       renderable to center on.
     * @param range            radius.
     * @param rangesComparison new and current ranges comparison.
     */
    public void showNewTowerRange(Renderable renderable, int range, int rangesComparison) {
        if (rangesComparison == 0) {
            return;
        }

        newTowerRangeCircle.getStyleClass().clear();
        newTowerRangeCircle.getStyleClass().add(rangesComparison > 0 ?
            "improvement-tower-range" : "retrogression-tower-range");

        showTowerRangeCircle(renderable, range, newTowerRangeCircle);
    }

    private void showTowerRangeCircle(Renderable renderable, int range, Circle circle) {
        circle.setRadius(range * pixelsPerGameCell);
        circle.relocate(
            (renderable.getPosition().getX() + renderable.getSize().getX() / 2 - range) * pixelsPerGameCell,
            (renderable.getPosition().getY() + renderable.getSize().getY() / 2 - range) * pixelsPerGameCell);

        circle.setVisible(true);
    }

    /**
     * Hides game object selection.
     */
    public void hideGameObjectSelection() {
        gameObjectSelectionRectangle.setVisible(false);
    }

    /**
     * Hides tower range circle.
     */
    public void hideTowerRangeCircle() {
        towerRangeCircle.setVisible(false);
    }

    /**
     * Hides new tower range circle.
     */
    public void hideNewTowerRangeCircle() {
        newTowerRangeCircle.setVisible(false);
    }
}
