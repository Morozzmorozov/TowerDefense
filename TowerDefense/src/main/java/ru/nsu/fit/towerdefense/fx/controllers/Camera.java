package ru.nsu.fit.towerdefense.fx.controllers;

import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Cursor;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import ru.nsu.fit.towerdefense.util.Vector2;

/**
 * Camera class is used for scaling and moving the target pane on the screen.
 *
 * @author Oleg Markelov
 */
public class Camera {

    private static final double WORLD_TO_STAGE_INITIAL_RATIO = 0.9d;
    private static final double MAX_SCALE = 4.0d;
    private static final double MIN_SCALE = 0.4d;
    private static final double SCALE_DELTA = 1.2d;
    private static final long SCROLL_TIME = 250;

    private final Pane wrapperPane;
    private final Pane worldPane;

    private double scale = 1d;
    private final Vector2<Double> initialTranslateDelta;

    private double pixelsPerGameCell;

    /**
     * Creates new Camera with specified wrapper and world panes. Sets world pane bounds
     * calculated with stage and world sizes. Calculates how many pixels fit one dimension of game
     * cell.
     *
     * @param wrapperPane  pane holding the world pane.
     * @param worldPane world pane.
     * @param stageSize stage size on pixels.
     * @param worldSize stage size in game units.
     */
    public Camera(Pane wrapperPane, Pane worldPane, Vector2<Double> stageSize, Vector2<Integer> worldSize) {
        this.wrapperPane = wrapperPane;
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
     * Scales the world relative to the zoom point.
     *
     * @param zoomIn     zoom in if true; zoom out if false.
     * @param zoomPointX zoom point x-coordinate.
     * @param zoomPointY zoom point y-coordinate.
     */
    public void scale(boolean zoomIn, double zoomPointX, double zoomPointY) {
        if (zoomIn) {
            if (scale >= MAX_SCALE) return;
            scale *= SCALE_DELTA;
        } else {
            if (scale <= MIN_SCALE) return;
            scale /= SCALE_DELTA;
        }

        double f = (scale / worldPane.getScaleY()) - 1;
        double dx = (zoomPointX - (worldPane.getBoundsInParent().getWidth() / 2 + worldPane.getBoundsInParent().getMinX()));
        double dy = (zoomPointY - (worldPane.getBoundsInParent().getHeight() / 2 + worldPane.getBoundsInParent().getMinY()));

        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(SCROLL_TIME), worldPane);
        scaleTransition.setToX(scale);
        scaleTransition.setToY(scale);

        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(SCROLL_TIME), worldPane);
        translateTransition.setToX(worldPane.getTranslateX() - f * dx);
        translateTransition.setToY(worldPane.getTranslateY() - f * dy);

        translateTransition.play();
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
        wrapperPane.setCursor(Cursor.MOVE);
    }

    /**
     * Finishes world movement.
     *
     * @param x updated world x coordinate.
     * @param y updated world y coordinate.
     */
    public void finishMovement(double x, double y) {
        wrapperPane.setCursor(Cursor.DEFAULT);
    }
}
