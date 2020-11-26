package ru.nsu.fit.towerdefense.fx.controllers;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import ru.nsu.fit.towerdefense.model.world.gameobject.Renderable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class WorldRenderer {

    private static final double PIXELS_PER_GAME_CELL = 100;

    private final ObservableList<Node> gameNodes;
    private final Map<Renderable, Node> renderableToGameNodeMap = new HashMap<>();

    public WorldRenderer(ObservableList<Node> gameNodes) {
        this.gameNodes = gameNodes;
    }

    public void update(Set<Renderable> newRenderableSet) {
        for (Renderable renderable : newRenderableSet) {
            if (!renderableToGameNodeMap.containsKey(renderable)) {
                Rectangle rectangle = new Rectangle(
                    renderable.getSize().getX() * PIXELS_PER_GAME_CELL,
                    renderable.getSize().getY() * PIXELS_PER_GAME_CELL,
                    Color.DODGERBLUE);
                rectangle.setUserData(renderable);
                renderableToGameNodeMap.put(renderable, rectangle);
            }
        }

        renderableToGameNodeMap.entrySet().removeIf(entry -> !newRenderableSet.contains(entry.getKey()));
    }

    public void render() {
        gameNodes.removeIf(node -> !renderableToGameNodeMap.containsKey(node.getUserData()));

        for (Map.Entry<Renderable, Node> entry : renderableToGameNodeMap.entrySet()) {
            Renderable renderable = entry.getKey();
            Rectangle rectangle = (Rectangle) entry.getValue();

            if (!gameNodes.contains(rectangle)) {
                gameNodes.add(rectangle);
            }

            rectangle.relocate(
                renderable.getPosition().getX() * PIXELS_PER_GAME_CELL,
                renderable.getPosition().getY() * PIXELS_PER_GAME_CELL);

            rectangle.setWidth(renderable.getSize().getX() * PIXELS_PER_GAME_CELL);
            rectangle.setHeight(renderable.getSize().getY() * PIXELS_PER_GAME_CELL);
        }
    }
}
