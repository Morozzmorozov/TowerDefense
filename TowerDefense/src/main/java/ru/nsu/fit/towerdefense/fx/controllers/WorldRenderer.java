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

    private final ObservableList<Node> gameNodes;
    private final double pixelsPerGameCell;

    /**
     * Creates new WorldRenderer with specified game nodes.
     *
     * @param gameNodes         list of game nodes.
     * @param pixelsPerGameCell how many pixels fit one dimension of game cell.
     */
    public WorldRenderer(ObservableList<Node> gameNodes, double pixelsPerGameCell) {
        this.gameNodes = gameNodes;
        this.pixelsPerGameCell = pixelsPerGameCell;
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

                imageView.setFitWidth(renderable.getSize().getX() * pixelsPerGameCell);
                imageView.setFitHeight(renderable.getSize().getY() * pixelsPerGameCell);

                imageView.relocate(
                    renderable.getPosition().getX() * pixelsPerGameCell,
                    renderable.getPosition().getY() * pixelsPerGameCell);

                gameNodes.add(imageView);
            } catch (NoSuchElementException e) {
                throw new RenderException("No image was found by name \"" + renderable.getImageName() + "\".");
            }
        }
    }
}
