package ru.nsu.fit.towerdefense.fx.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
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

import static javafx.scene.input.KeyCode.ESCAPE;
import static ru.nsu.fit.towerdefense.fx.util.AlertBuilder.RENDER_WORLD_ERROR_HEADER;

/**
 * GameController class is used by JavaFX in javafx.fxml.FXMLLoader for showing the game scene.
 *
 * @author Oleg Markelov
 */
public class GameController implements Controller, WorldObserver, WorldRendererObserver {

    private static final String FXML_FILE_NAME = "game.fxml";
    private static final int FRAMES_PER_SECOND = 60;
    private static final int DELTA_TIME = 1000 / FRAMES_PER_SECOND;

    private enum State { PLAYING, PAUSED, FINISHED }

    @FXML private StackPane rootStackPane;
    @FXML private AnchorPane worldAnchorPane;

    @FXML private Label researchLabel;
    @FXML private Label healthLabel;
    @FXML private Label enemyLabel;
    @FXML private Label moneyLabel;
    @FXML private Label nextWaveTimeLabel;
    @FXML private Label waveLabel;
    @FXML private Label playingTimeLabel;

    @FXML private ImageView waveImageView;
    @FXML private ImageView pauseImageView;

    @FXML private GridPane pausePopupGridPane;
    @FXML private HBox resumeHBox;
    @FXML private HBox restartHBox;
    @FXML private HBox idleHBox;
    @FXML private HBox finishHBox;
    @FXML private HBox menuHBox;

    @FXML private GridPane resultsPopupGridPane;
    @FXML private Label resultsHeaderLabel;
    @FXML private Label resultsWavesLabel;
    @FXML private Label resultsEnemyLabel;
    @FXML private Label resultsTimeLabel;
    @FXML private HBox resultsSaveReplayHBox;
    @FXML private HBox resultsRestartHBox;
    @FXML private HBox resultsMenuHBox;

    private final SceneManager sceneManager;

    private ScheduledExecutorService worldSimulationExecutor;

    private final Vector2<Integer> worldSize;

    private final WorldControl worldControl;
    private WorldCamera worldCamera;
    private WorldRenderer worldRenderer;

    private State state;

    /**
     * Creates new GameController with specified SceneManager and GameMap.
     *
     * @param sceneManager scene manager.
     * @param gameMap      game map.
     */
    public GameController(SceneManager sceneManager, GameMap gameMap) {
        this.sceneManager = sceneManager;

        worldControl = new WorldControl(gameMap, DELTA_TIME, GameController.this);

        worldSize = gameMap.getSize();

        state = State.PLAYING;
    }

    @FXML
    private void initialize() {
        worldCamera = new WorldCamera(rootStackPane, worldAnchorPane, sceneManager.getStageSize(), worldSize);
        worldRenderer = new WorldRenderer(worldAnchorPane.getChildren(), worldCamera.getPixelsPerGameCell(), this);

        pauseImageView.setOnMouseClicked(mouseEvent -> pauseGame());
        resumeHBox.setOnMouseClicked(mouseEvent -> resumeGame());
        menuHBox.setOnMouseClicked(mouseEvent -> sceneManager.switchToMenu());

        resultsMenuHBox.setOnMouseClicked(mouseEvent -> sceneManager.switchToMenu());

        activateGame();
    }

    private void pauseGame() {
        state = State.PAUSED;

        worldSimulationExecutor.shutdown();
        pausePopupGridPane.setVisible(true);
    }

    private void resumeGame() {
        state = State.PLAYING;

        pausePopupGridPane.setVisible(false);
        activateGame();
    }

    private void activateGame() {
        worldSimulationExecutor = Executors.newSingleThreadScheduledExecutor();
        worldSimulationExecutor.scheduleWithFixedDelay(() -> {
            try {
                worldControl.simulateTick();
                worldRenderer.update(new HashSet<>(worldControl.getWorld().getRenderables()));
                Platform.runLater(() -> {
                    worldRenderer.render();

                    researchLabel.setText(worldControl.getResearchPoints() + "");
                    moneyLabel.setText(worldControl.getMoney() + "");
                    healthLabel.setText(worldControl.getBaseHealth() + "");
                    enemyLabel.setText(worldControl.getEnemiesKilled() + "");
                    long ticksTillNextWave = worldControl.getTicksTillNextWave();
                    if (ticksTillNextWave > 0) {
                        nextWaveTimeLabel.setText(formatWaveTime(ticksTillNextWave * DELTA_TIME));
                        nextWaveTimeLabel.setManaged(true);
                        nextWaveTimeLabel.setVisible(true);
                    } else {
                        nextWaveTimeLabel.setVisible(false);
                        nextWaveTimeLabel.setManaged(false);
                    }
                    waveLabel.setText(worldControl.getWaveNumber() + "");
                    playingTimeLabel.setText(formatPlayingTime(worldControl.getTick() * DELTA_TIME));
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
            if (state != State.PLAYING) {
                return;
            }

            if (scrollEvent.getDeltaY() == 0) {
                return;
            }

            worldCamera.scale(scrollEvent.getDeltaY() > 0,
                scrollEvent.getSceneX(), scrollEvent.getSceneY());
        });

        sceneManager.getScene().setOnMousePressed(mouseEvent -> {
            if (state != State.PLAYING) {
                return;
            }

            if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                worldCamera.initMovement(mouseEvent.getSceneX(), mouseEvent.getSceneY());
            }
        });

        sceneManager.getScene().setOnMouseDragged(mouseEvent -> {
            if (state != State.PLAYING) {
                return;
            }

            if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                worldCamera.updateMovement(mouseEvent.getSceneX(), mouseEvent.getSceneY());
            }
        });

        sceneManager.getScene().setOnMouseReleased(mouseEvent -> {
            if (state != State.PLAYING) {
                return;
            }

            if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                worldCamera.finishMovement(mouseEvent.getSceneX(), mouseEvent.getSceneY());
            }
        });

        sceneManager.getScene().setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode().equals(ESCAPE)) {
                if (state == State.PLAYING) {
                    pauseGame();
                } else if (state == State.PAUSED) {
                    resumeGame();
                }
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
        sceneManager.getScene().setOnKeyPressed(null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDefeat() {
        finishGame(false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onVictory() {
        finishGame(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onGameObjectClicked(Renderable renderable) {
        if (state != State.PLAYING) {
            return;
        }

        switch (renderable.getGameObjectType()) {
            case BASE:
                System.out.println("BASE");
                break;
            case ENEMY:
                System.out.println("ENEMY");
                break;
            case ENEMY_PORTAL:
                System.out.println("ENEMY_PORTAL");
                break;
            case PROJECTILE:
                System.out.println("PROJECTILE");
                break;
            case ROAD_TILE:
                System.out.println("ROAD_TILE");
                break;
            case TOWER:
                System.out.println("TOWER");
                break;
            case TOWER_PLATFORM:
                System.out.println("TOWER_PLATFORM");
                break;
        }
    }

    private void finishGame(boolean win) {
        state = State.FINISHED;

        if (worldSimulationExecutor != null) {
            worldSimulationExecutor.shutdown();
        }

        Platform.runLater(() -> {
            if (win) {
                resultsHeaderLabel.setText("You win!");
            }

            resultsWavesLabel.setText(worldControl.getWavesDefeated() + "");
            resultsEnemyLabel.setText(enemyLabel.getText());
            resultsTimeLabel.setText(playingTimeLabel.getText());

            resultsPopupGridPane.setVisible(true);
        });
    }

    private String formatWaveTime(long milliseconds) {
        return String.format("%d:%02d", (milliseconds / 60000) % 60, (milliseconds / 1000) % 60);
    }

    private String formatPlayingTime(long milliseconds) {
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
            super(gameMap, DELTA_TIME, GameController.this);

            for (int i = 0; i < 100; i++) {
                world.getGameObjectStubs().add(generateRandomGameObject());
            }
        }

        @Override
        public void simulateTick() {
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
            public Collection<Renderable> getRenderables() {
                return (List<Renderable>) (List<? extends Renderable>) gameObjects;
            }
        }

        private abstract class GameObject implements Renderable {

            private final Vector2<Double> position = new Vector2<>(-1d, 0d);
            private final Vector2<Double> velocity = new Vector2<>(0d, 0d);
            private final Vector2<Double> size = new Vector2<>(GAME_OBJECT_SIZE, GAME_OBJECT_SIZE);

            @Override
            public Type getGameObjectType() {
                return null;
            }

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

            @Override
            public double getZIndex() {
                return 0;
            }
        }

        private class Circle extends GameObject {
            @Override
            public String getImageName() {
                return "circle";
            }

            @Override
            public double getZIndex() {
                return 0;
            }
        }
    }
}
