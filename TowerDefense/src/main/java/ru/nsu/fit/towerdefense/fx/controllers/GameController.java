package ru.nsu.fit.towerdefense.fx.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import ru.nsu.fit.towerdefense.fx.SceneManager;
import ru.nsu.fit.towerdefense.model.world.Vector2;
import ru.nsu.fit.towerdefense.model.world.World;
import ru.nsu.fit.towerdefense.model.world.WorldControl;
import ru.nsu.fit.towerdefense.model.world.gameobject.Renderable;
import ru.nsu.fit.towerdefense.model.world.map.GameMap;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * GameController class is used by JavaFX in javafx.fxml.FXMLLoader for showing the game scene.
 *
 * @author Oleg Markelov
 */
public class GameController implements Controller {

    private static final String FXML_FILE_NAME = "game.fxml";
    private static final int FRAMES_PER_SECOND = 60;
    private static final int SIMULATION_DELAY = 1000 / FRAMES_PER_SECOND;

    @FXML private StackPane rootStackPane;
    @FXML private AnchorPane worldAnchorPane;
    @FXML private Button menuButton;

    private final SceneManager sceneManager;
    private final GameMap gameMap;

    private ScheduledExecutorService worldSimulationExecutor;

    private WorldControl worldControl;
    private WorldRenderer worldRenderer;

    /**
     * Creates new GameController with specified SceneManager and GameMap.
     *
     * @param sceneManager scene manager.
     * @param gameMap      game map.
     */
    public GameController(SceneManager sceneManager, GameMap gameMap) {
        this.sceneManager = sceneManager;
        this.gameMap = gameMap;
    }

    @FXML
    private void initialize() {
        menuButton.setOnAction(actionEvent -> sceneManager.switchToMenu());

        worldAnchorPane.maxWidthProperty().bind(rootStackPane.widthProperty());
        worldAnchorPane.minWidthProperty().bind(rootStackPane.widthProperty());
        worldAnchorPane.maxHeightProperty().bind(rootStackPane.heightProperty());
        worldAnchorPane.minHeightProperty().bind(rootStackPane.heightProperty());

        worldControl = new WorldControlStub(gameMap);
        worldRenderer = new WorldRenderer(worldAnchorPane.getChildren());

        worldSimulationExecutor = Executors.newSingleThreadScheduledExecutor();
        worldSimulationExecutor.scheduleWithFixedDelay(() -> {
            try {
                worldControl.simulateTick();
//                worldRenderer.update(new HashSet<>((Collection<? extends Renderable>)
//                    worldControl.getWorld().getRenderables())); // todo compare performance
                Platform.runLater(() -> {
//                    worldRenderer.render();
                    worldRenderer.renderSimply(worldControl.getWorld().getRenderables());
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

    private static class WorldControlStub extends WorldControl {

        private final WorldStub world = new WorldStub();
        private int frame;

        public WorldControlStub(GameMap gameMap) {
            super(gameMap);

            for (int i = 0; i < 100; i++) {
                world.getGameObjectStubs().add(generateRandomGameObjectStub());
            }
        }

        @Override
        public void simulateTick() {
            if (frame++ % 10 == 0) {
                if (world.getGameObjectStubs().size() > 0) {
                    world.getGameObjectStubs().remove(
                        ThreadLocalRandom.current().nextInt(world.getGameObjectStubs().size()));
                }

                world.getGameObjectStubs().add(generateRandomGameObjectStub());
            }

            for (GameObjectStub gameObjectStub : world.getGameObjectStubs()) {
                gameObjectStub.getPosition().setX(gameObjectStub.getPosition().getX() +
                    gameObjectStub.getVelocity().getX() * SIMULATION_DELAY);
                gameObjectStub.getPosition().setY(gameObjectStub.getPosition().getY() +
                    gameObjectStub.getVelocity().getY() * SIMULATION_DELAY);
            }
        }

        @Override
        public WorldStub getWorld() {
            return world;
        }

        private GameObjectStub generateRandomGameObjectStub() {
            return new GameObjectStub() {{
                getPosition().setX(ThreadLocalRandom.current().nextDouble(107));
                getPosition().setY(ThreadLocalRandom.current().nextDouble(71));

                if (ThreadLocalRandom.current().nextDouble() < 0.9d) {
                    int dirX = ThreadLocalRandom.current().nextBoolean() ? 1 : -1;
                    int dirY = ThreadLocalRandom.current().nextBoolean() ? 1 : -1;
                    getVelocity().setX(
                        dirX * ThreadLocalRandom.current().nextDouble(0.001d,0.02d));
                    getVelocity().setY(
                        dirY * ThreadLocalRandom.current().nextDouble(0.001d,0.02d));
                }
            }};
        }

        private static class WorldStub extends World {

            private final List<GameObjectStub> gameObjectStubs = new ArrayList<>();

            public List<GameObjectStub> getGameObjectStubs() {
                return gameObjectStubs;
            }

            @SuppressWarnings("unchecked")
            public Iterable<Renderable> getRenderables() {
                return (List<Renderable>) (List<? extends Renderable>) gameObjectStubs;
            }
        }

        private static class GameObjectStub implements Renderable {

            private final Vector2<Double> position = new Vector2<>(-1d, 0d);
            private final Vector2<Double> velocity = new Vector2<>(0d, 0d);
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

            public Vector2<Double> getVelocity() {
                return velocity;
            }
        }
    }
}
