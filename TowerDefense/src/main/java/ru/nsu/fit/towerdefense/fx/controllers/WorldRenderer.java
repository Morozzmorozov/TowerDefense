package ru.nsu.fit.towerdefense.fx.controllers;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import ru.nsu.fit.towerdefense.model.world.gameobject.Renderable;


/**
 * WorldRenderer class is used for rendering game objects on the game scene.
 *
 * @author Oleg Markelov
 */
public class WorldRenderer {

    private static final double PIXELS_PER_GAME_CELL = 10;

    private final ObservableList<Node> gameNodes;

    /**
     * Creates new WorldRenderer with specified game nodes.
     *
     * @param gameNodes list of game nodes.
     */
    public WorldRenderer(ObservableList<Node> gameNodes) {
        this.gameNodes = gameNodes;
    }

    /**
     * Removes all the current renderables and renders the new ones.
     *
     * Must be called in JavaFX Application thread!
     *
     * @param newRenderableSet updated renderables.
     */
    public void renderSimply(Iterable<Renderable> newRenderableSet) {
        gameNodes.clear();

        for (Renderable renderable : newRenderableSet) {
            Rectangle rectangle = new Rectangle(
                renderable.getSize().getX() * PIXELS_PER_GAME_CELL,
                renderable.getSize().getY() * PIXELS_PER_GAME_CELL,
                Color.DODGERBLUE);

            rectangle.relocate(
                renderable.getPosition().getX() * PIXELS_PER_GAME_CELL,
                renderable.getPosition().getY() * PIXELS_PER_GAME_CELL);

            rectangle.setWidth(renderable.getSize().getX() * PIXELS_PER_GAME_CELL);
            rectangle.setHeight(renderable.getSize().getY() * PIXELS_PER_GAME_CELL);

            gameNodes.add(rectangle);
        }
    }
}
