package ru.nsu.fit.towerdefense.fx.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import ru.nsu.fit.towerdefense.fx.SceneManager;
import ru.nsu.fit.towerdefense.model.world.Vector2;
import ru.nsu.fit.towerdefense.model.world.World;
import ru.nsu.fit.towerdefense.model.world.WorldControl;
import ru.nsu.fit.towerdefense.model.world.gameobject.Enemy;
import ru.nsu.fit.towerdefense.model.world.gameobject.Renderable;

import java.util.ArrayList;
import java.util.Iterator;
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

    @FXML private Button menuButton;

    private final SceneManager sceneManager;

    private ScheduledExecutorService worldSimulationExecutor;

    private final WorldControl worldControl;
    private final WorldRenderer worldRenderer;

    /**
     * Creates new MenuController with specified SceneManager.
     *
     * @param sceneManager scene manager.
     */
    public GameController(SceneManager sceneManager) {
        this.sceneManager = sceneManager;

        worldControl = new WorldControl();
        worldRenderer = new WorldRenderer();
    }

    @FXML
    private void initialize() {
        menuButton.setOnAction(actionEvent -> sceneManager.switchToMenu());

        WorldStub worldStub = new WorldStub();

        worldSimulationExecutor = Executors.newSingleThreadScheduledExecutor();
        worldSimulationExecutor.scheduleWithFixedDelay(() -> {
            try {
//                worldControl.simulateTick();
//                worldRenderer.render(worldControl.getWorld().getRenderables());
                worldRenderer.render(worldStub.getRenderables());
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

        public WorldStub() {
            for (int i = 0; i < 2; i++) {
                int finalI = i;
                gameObjectStubs.add(
                    new GameObjectStub() {{
                        getPosition().setY((double) (3 * finalI + 1));
                    }}
                );
            }
        }

        @SuppressWarnings("unchecked")
        public Iterable<Renderable> getRenderables() {
            gameObjectStubs.get(0).getPosition().setX(gameObjectStubs.get(0).getPosition().getX() + 0.1d);
            gameObjectStubs.get(1).getPosition().setX(gameObjectStubs.get(1).getPosition().getX() + 0.05d);

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
