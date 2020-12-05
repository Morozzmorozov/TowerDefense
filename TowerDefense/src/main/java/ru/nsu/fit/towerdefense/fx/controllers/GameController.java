package ru.nsu.fit.towerdefense.fx.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import ru.nsu.fit.towerdefense.fx.SceneManager;
import ru.nsu.fit.towerdefense.fx.exceptions.RenderException;
import ru.nsu.fit.towerdefense.fx.util.AlertBuilder;
import ru.nsu.fit.towerdefense.model.WorldControl;
import ru.nsu.fit.towerdefense.model.WorldObserver;
import ru.nsu.fit.towerdefense.model.map.GameMap;
import ru.nsu.fit.towerdefense.model.util.Vector2;
import ru.nsu.fit.towerdefense.model.world.World;
import ru.nsu.fit.towerdefense.model.world.gameobject.Renderable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static ru.nsu.fit.towerdefense.fx.util.AlertBuilder.RENDER_WORLD_ERROR_HEADER;

/**
 * GameController class is used by JavaFX in javafx.fxml.FXMLLoader for showing the game scene.
 *
 * @author Oleg Markelov
 */
public class GameController implements Controller, WorldObserver {

    private static final String FXML_FILE_NAME = "game.fxml";
    private static final int FRAMES_PER_SECOND = 60;
    private static final int DELTA_TIME = 1000 / FRAMES_PER_SECOND;

    @FXML private StackPane rootStackPane;
    @FXML private AnchorPane worldAnchorPane;

    @FXML private Label researchLabel;
    @FXML private Label healthLabel;
    @FXML private Label enemyLabel;
    @FXML private Label moneyLabel;
    @FXML private Label waveLabel;
    @FXML private Label playingTimeLabel;

    @FXML private ImageView waveImageView;
    @FXML private ImageView pauseImageView;

    @FXML private GridPane popupGridPane;
    @FXML private HBox resumeHBox;
    @FXML private HBox restartHBox;
    @FXML private HBox idleHBox;
    @FXML private HBox finishHBox;
    @FXML private HBox menuHBox;

    private final SceneManager sceneManager;

    private ScheduledExecutorService worldSimulationExecutor;

    private final Vector2<Integer> worldSize;

    private final WorldControl worldControl;
    private WorldCamera worldCamera;
    private WorldRenderer worldRenderer;

    /**
     * Creates new GameController with specified SceneManager and GameMap.
     *
     * @param sceneManager scene manager.
     * @param gameMap      game map.
     */
    public GameController(SceneManager sceneManager, GameMap gameMap) {
        this.sceneManager = sceneManager;

        worldControl = new WorldControl(gameMap, GameController.this);

        worldSize = gameMap.getSize();
    }

    @FXML
    private void initialize() {
        worldCamera = new WorldCamera(rootStackPane, worldAnchorPane, sceneManager.getStageSize(), worldSize);
        worldRenderer = new WorldRenderer(worldAnchorPane.getChildren(), worldCamera.getPixelsPerGameCell());

        pauseImageView.setOnMouseClicked(mouseEvent -> {
            worldSimulationExecutor.shutdown();
            popupGridPane.setVisible(true);
        });
        resumeHBox.setOnMouseClicked(mouseEvent -> {
            popupGridPane.setVisible(false);
            activateGame();
        });
        menuHBox.setOnMouseClicked(mouseEvent -> sceneManager.switchToMenu());

        activateGame();
    }

    private void activateGame() {
        worldSimulationExecutor = Executors.newSingleThreadScheduledExecutor();
        worldSimulationExecutor.scheduleWithFixedDelay(() -> {
            try {
                worldControl.simulateTick(DELTA_TIME);
                worldRenderer.update(new HashSet<>((Collection<? extends Renderable>)
                    worldControl.getWorld().getRenderables()));
                Platform.runLater(() -> {
                    worldRenderer.render();

                    researchLabel.setText(worldControl.getResearchPoints() + "");
                    moneyLabel.setText(worldControl.getMoney() + "");
                    healthLabel.setText(worldControl.getBaseHealth() + "");
                    enemyLabel.setText(worldControl.getEnemiesKilled() + "");
                    waveLabel.setText(worldControl.getWaveNumber() + "");
                    playingTimeLabel.setText(formatTime(worldControl.getTick() * DELTA_TIME));
                });
            } catch (RenderException e) {
                sceneManager.switchToMenu();

                Platform.runLater(() -> new AlertBuilder()
                    .setHeaderText(RENDER_WORLD_ERROR_HEADER)
                    .setContentText(e.getMessage())
                    .setOwner(sceneManager.getWindowOwner())
                    .build().showAndWait());
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }, 0, DELTA_TIME, TimeUnit.MILLISECONDS);
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
    public void runAfterSceneSet() {
        sceneManager.getScene().setOnScroll(scrollEvent -> {
            if (scrollEvent.getDeltaY() == 0) {
                return;
            }

            worldCamera.scale(scrollEvent.getDeltaY() > 0,
                scrollEvent.getSceneX(), scrollEvent.getSceneY());
        });

        sceneManager.getScene().setOnMousePressed(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                worldCamera.initMovement(mouseEvent.getSceneX(), mouseEvent.getSceneY());
            }
        });

        sceneManager.getScene().setOnMouseDragged(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                worldCamera.updateMovement(mouseEvent.getSceneX(), mouseEvent.getSceneY());
            }
        });

        sceneManager.getScene().setOnMouseReleased(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                worldCamera.finishMovement(mouseEvent.getSceneX(), mouseEvent.getSceneY());
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose() {
        if (worldSimulationExecutor != null) {
            worldSimulationExecutor.shutdownNow();
        }

        sceneManager.getScene().setOnScroll(null);
        sceneManager.getScene().setOnMousePressed(null);
        sceneManager.getScene().setOnMouseDragged(null);
        sceneManager.getScene().setOnMouseReleased(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onGameLoosing() {
        System.out.println("You lose!");

        if (worldSimulationExecutor != null) {
            worldSimulationExecutor.shutdown();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onGameWinning() {
        System.out.println("You win!");

        if (worldSimulationExecutor != null) {
            worldSimulationExecutor.shutdown();
        }
    }

    private String formatTime(long milliseconds) {
        return String.format(milliseconds < 3600000 ? "%2$02d:%3$02d" : "%02d:%02d:%02d",
            milliseconds / 3600000, (milliseconds / 60000) % 60, (milliseconds / 1000) % 60);
    }

    // ---------- Stubs ----------

    private static final int WORLD_WIDTH = 20;
    private static final int WORLD_HEIGHT = 10;
    private static final double GAME_OBJECT_SIZE = 0.25d;

    private class WorldControlStub extends WorldControl {

        private final WorldStub world = new WorldStub();
        private int frame;

        public WorldControlStub(GameMap gameMap) {
            super(gameMap, GameController.this);

            for (int i = 0; i < 100; i++) {
                world.getGameObjectStubs().add(generateRandomGameObject());
            }
        }

        @Override
        public void simulateTick(int deltaTime) {
            if (frame++ % 10 == 0) {
                if (world.getGameObjectStubs().size() > 0) {
                    world.getGameObjectStubs().remove(
                        ThreadLocalRandom.current().nextInt(world.getGameObjectStubs().size()));
                }

                world.getGameObjectStubs().add(generateRandomGameObject());
            }

            for (GameObject gameObject : world.getGameObjectStubs()) {
                gameObject.getPosition().setX(gameObject.getPosition().getX() +
                    gameObject.getVelocity().getX() * DELTA_TIME);
                gameObject.getPosition().setY(gameObject.getPosition().getY() +
                    gameObject.getVelocity().getY() * DELTA_TIME);
            }
        }

        @Override
        public WorldStub getWorld() {
            return world;
        }

        private GameObject generateRandomGameObject() {
            GameObject gameObject =
                ThreadLocalRandom.current().nextBoolean() ? new Triangle() : new Circle();

            gameObject.getPosition().setX(ThreadLocalRandom.current().nextDouble(WORLD_WIDTH - GAME_OBJECT_SIZE));
            gameObject.getPosition().setY(ThreadLocalRandom.current().nextDouble(WORLD_HEIGHT - GAME_OBJECT_SIZE));

            if (ThreadLocalRandom.current().nextDouble() < 0.9d) {
                int dirX = ThreadLocalRandom.current().nextBoolean() ? 1 : -1;
                int dirY = ThreadLocalRandom.current().nextBoolean() ? 1 : -1;
                gameObject.getVelocity().setX(
                    dirX * ThreadLocalRandom.current().nextDouble(0.001d * GAME_OBJECT_SIZE,0.02d * GAME_OBJECT_SIZE));
                gameObject.getVelocity().setY(
                    dirY * ThreadLocalRandom.current().nextDouble(0.001d * GAME_OBJECT_SIZE,0.02d * GAME_OBJECT_SIZE));
            }

            return gameObject;
        }

        private class WorldStub extends World {

            private final List<GameObject> gameObjects = new ArrayList<>();

            public List<GameObject> getGameObjectStubs() {
                return gameObjects;
            }

            @SuppressWarnings("unchecked")
            public Iterable<Renderable> getRenderables() {
                return (List<Renderable>) (List<? extends Renderable>) gameObjects;
            }
        }

        private abstract class GameObject implements Renderable {

            private final Vector2<Double> position = new Vector2<>(-1d, 0d);
            private final Vector2<Double> velocity = new Vector2<>(0d, 0d);
            private final Vector2<Double> size = new Vector2<>(GAME_OBJECT_SIZE, GAME_OBJECT_SIZE);

            @Override
            public Vector2<Double> getPosition() {
                return position;
            }

            @Override
            public Vector2<Double> getSize() {
                return size;
            }

            @Override
            public abstract String getImageName();

            public Vector2<Double> getVelocity() {
                return velocity;
            }
        }

        private class Triangle extends GameObject {
            @Override
            public String getImageName() {
                return "triangle";
            }
        }

        private class Circle extends GameObject {
            @Override
            public String getImageName() {
                return "circle";
            }
        }
    }
}
