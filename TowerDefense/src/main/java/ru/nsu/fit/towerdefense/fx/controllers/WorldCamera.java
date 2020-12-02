package ru.nsu.fit.towerdefense.fx.controllers;

import javafx.animation.ScaleTransition;
import javafx.scene.Cursor;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import ru.nsu.fit.towerdefense.model.util.Vector2;

/**
 * WorldCamera class is used for scaling and moving the world on the screen.
 *
 * @author Oleg Markelov
 */
public class WorldCamera {

    private static final double WORLD_TO_STAGE_INITIAL_RATIO = 0.9d;
    private static final double MAX_SCALE = 3.0d;
    private static final double MIN_SCALE = 0.4d;
    private static final double SCALE_DELTA = 0.1d;

    private final Pane rootPane;
    private final Pane worldPane;

    private double scale = 1d;
    private final Vector2<Double> initialTranslateDelta;

    private double pixelsPerGameCell;

    /**
     * Creates new WorldCamera with specified root and world panes. Sets world pane bounds
     * calculated with stage and world sizes. Calculates how many pixels fit one dimension of game
     * cell.
     *
     * @param rootPane  pane holding the world pane.
     * @param worldPane world pane.
     * @param stageSize stage size on pixels.
     * @param worldSize stage size in game units.
     */
    public WorldCamera(Pane rootPane, Pane worldPane, Vector2<Double> stageSize, Vector2<Integer> worldSize) {
        this.rootPane = rootPane;
        this.worldPane = worldPane;
        initialTranslateDelta = new Vector2<>(0d, 0d);

        initWorldPaneSize(stageSize, worldSize);
    }

    private void initWorldPaneSize(Vector2<Double> stageSize, Vector2<Integer> worldSize) {
        double width, height;

        if (worldSize.getX() > worldSize.getY()) {
            width = WORLD_TO_STAGE_INITIAL_RATIO * stageSize.getX();
            height = width * worldSize.getY() / worldSize.getX();
        } else {
            height = WORLD_TO_STAGE_INITIAL_RATIO * stageSize.getY();
            width = height * worldSize.getX() / worldSize.getY();
        }

        worldPane.setMinWidth(width);
        worldPane.setMaxWidth(width);
        worldPane.setMinHeight(height);
        worldPane.setMaxHeight(height);

        pixelsPerGameCell = width / worldSize.getX();
    }

    /**
     * Returns how many pixels fit one dimension of game cell.
     *
     * @return how many pixels fit one dimension of game cell.
     */
    public double getPixelsPerGameCell() {
        return pixelsPerGameCell;
    }

    /**
     * Scales the world by 20% if the scale > 100% or by 10% otherwise.
     *
     * @param zoomIn zoom in if true; zoom out if false.
     */
    public void scale(boolean zoomIn) {
        if (zoomIn) {
            if (scale >= MAX_SCALE) return;
            scale += SCALE_DELTA * (scale >= 1 ? 2 : 1);
        } else {
            if (scale <= MIN_SCALE) return;
            scale -= SCALE_DELTA * (scale > 1 ? 2 : 1);
        }

        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(250), worldPane);
        scaleTransition.setToX(scale);
        scaleTransition.setToY(scale);

        scaleTransition.play();
    }

    /**
     * Initiates world movement.
     *
     * @param x initial world x coordinate.
     * @param y initial world y coordinate.
     */
    public void initMovement(double x, double y) {
        initialTranslateDelta.setX(x - worldPane.getTranslateX());
        initialTranslateDelta.setY(y - worldPane.getTranslateY());
    }

    /**
     * Updates world movement.
     *
     * @param x updated world x coordinate.
     * @param y updated world y coordinate.
     */
    public void updateMovement(double x, double y) {
        worldPane.setTranslateX(x - initialTranslateDelta.getX());
        worldPane.setTranslateY(y - initialTranslateDelta.getY());
        rootPane.setCursor(Cursor.MOVE);
    }

    /**
     * Finishes world movement.
     *
     * @param x updated world x coordinate.
     * @param y updated world y coordinate.
     */
    public void finishMovement(double x, double y) {
        rootPane.setCursor(Cursor.DEFAULT);
    }
}
