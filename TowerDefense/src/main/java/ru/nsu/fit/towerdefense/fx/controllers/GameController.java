package ru.nsu.fit.towerdefense.fx.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import ru.nsu.fit.towerdefense.fx.SceneManager;
import ru.nsu.fit.towerdefense.model.world.Vector2;
import ru.nsu.fit.towerdefense.model.world.World;
import ru.nsu.fit.towerdefense.model.world.WorldControl;
import ru.nsu.fit.towerdefense.model.world.gameobject.Renderable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * WorldController class is used by JavaFX in javafx.fxml.FXMLLoader for showing a world scene.
 *
 * @author Oleg Markelov
 */
public class GameController implements Controller {

    private static final String FXML_FILE_NAME = "game.fxml";
    private static final long SIMULATION_DELAY = 1000 / 60;

    @FXML private AnchorPane worldAnchorPane;
    @FXML private Button menuButton;

    private final SceneManager sceneManager;

    private ScheduledExecutorService worldSimulationExecutor;

    private WorldControl worldControl;
    private WorldRenderer worldRenderer;

    /**
     * Creates new MenuController with specified SceneManager.
     *
     * @param sceneManager scene manager.
     */
    public GameController(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    @FXML
    private void initialize() {
        menuButton.setOnAction(actionEvent -> sceneManager.switchToMenu());

        worldControl = new WorldControl();
        worldRenderer = new WorldRenderer(worldAnchorPane.getChildren());

        WorldStub worldStub = new WorldStub();

        worldSimulationExecutor = Executors.newSingleThreadScheduledExecutor();
        worldSimulationExecutor.scheduleWithFixedDelay(() -> {
            try {
//                worldControl.simulateTick();
//                worldRenderer.update(new HashSet<>((Collection<? extends Renderable>)
//                    worldControl.getWorld().getRenderables()));
                worldRenderer.update(new HashSet<>((Collection<? extends Renderable>)
                    worldStub.getRenderables()));

                Platform.runLater(() -> {
                    worldRenderer.render();
                });
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }, 0, SIMULATION_DELAY, TimeUnit.MILLISECONDS);
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
    public void dispose() {
        if (worldSimulationExecutor != null) {
            worldSimulationExecutor.shutdownNow();
        }
    }

    // ---------- Stubs ----------

    private static class WorldStub {

        private final List<GameObjectStub> gameObjectStubs = new ArrayList<>();
        private int i;

        public WorldStub() {
            for (int i = 0; i < 2; i++) {
                double doubleI = i;
                gameObjectStubs.add(
                    new GameObjectStub() {{
                        getPosition().setY(200 * doubleI + 100);
                    }}
                );
            }
        }

        @SuppressWarnings("unchecked")
        public Iterable<Renderable> getRenderables() {
            if (i == 120) {
                gameObjectStubs.add(
                    new GameObjectStub() {{
                        getPosition().setY(500d);
                    }}
                );
            }

            if (i == 240) {
                gameObjectStubs.remove(1);
            }

            if (i == 360) {
                gameObjectStubs.remove(0);
            }

            if (i == 480) {
                gameObjectStubs.remove(0);
            }

            if (gameObjectStubs.size() > 0)
                gameObjectStubs.get(0).getPosition().setX(gameObjectStubs.get(0).getPosition().getX() + 1);
            if (gameObjectStubs.size() > 1)
                gameObjectStubs.get(1).getPosition().setX(gameObjectStubs.get(1).getPosition().getX() + 1);
            if (gameObjectStubs.size() > 2)
                gameObjectStubs.get(2).getPosition().setX(gameObjectStubs.get(2).getPosition().getX() + 1);

            System.out.println(i++);
            return (List<Renderable>) (List<? extends Renderable>) gameObjectStubs;
        }
    }

    private static class GameObjectStub implements Renderable {

        private final Vector2<Double> position = new Vector2<>(1d, 1d);
        private final Vector2<Double> size = new Vector2<>(1d, 1d);

        @Override
        public Vector2<Double> getPosition() {
            return position;
        }

        @Override
        public Vector2<Double> getSize() {
            return size;
        }

        @Override
        public String getImageName() {
            return null;
        }
    }
}
