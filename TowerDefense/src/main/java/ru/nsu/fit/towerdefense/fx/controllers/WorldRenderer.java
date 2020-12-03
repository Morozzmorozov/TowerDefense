package ru.nsu.fit.towerdefense.fx.controllers;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import ru.nsu.fit.towerdefense.fx.Images;
import ru.nsu.fit.towerdefense.fx.exceptions.RenderException;
import ru.nsu.fit.towerdefense.model.world.gameobject.Renderable;

import java.util.NoSuchElementException;

/**
 * WorldRenderer class is used for rendering game objects on the game scene.
 *
 * @author Oleg Markelov
 */
public class WorldRenderer {

    private static final double PIXELS_PER_GAME_CELL = 50;

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
     * @param newRenderables updated renderables.
     */
    public void render(Iterable<Renderable> newRenderables) throws RenderException {
        gameNodes.clear();

        for (Renderable renderable : newRenderables) {
            try {
                ImageView imageView =
                    new ImageView(Images.getInstance().getImage(renderable.getImageName()));

                imageView.setFitWidth(renderable.getSize().getX() * PIXELS_PER_GAME_CELL);
                imageView.setFitHeight(renderable.getSize().getY() * PIXELS_PER_GAME_CELL);

                imageView.relocate(
                    renderable.getPosition().getX() * PIXELS_PER_GAME_CELL,
                    renderable.getPosition().getY() * PIXELS_PER_GAME_CELL);

                gameNodes.add(imageView);
            } catch (NoSuchElementException e) {
                throw new RenderException("No image was found by name \"" + renderable.getImageName() + "\".");
            }
        }
    }
}
