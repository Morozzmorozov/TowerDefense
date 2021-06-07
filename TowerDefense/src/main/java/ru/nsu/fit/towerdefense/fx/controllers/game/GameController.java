package ru.nsu.fit.towerdefense.fx.controllers.game;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import ru.nsu.fit.towerdefense.fx.Images;
import ru.nsu.fit.towerdefense.fx.SceneManager;
import ru.nsu.fit.towerdefense.fx.controllers.Camera;
import ru.nsu.fit.towerdefense.fx.controllers.Controller;
import ru.nsu.fit.towerdefense.fx.controllers.ServerMessageListener;
import ru.nsu.fit.towerdefense.fx.exceptions.RenderException;
import ru.nsu.fit.towerdefense.fx.util.AlertBuilder;
import ru.nsu.fit.towerdefense.fx.util.Snapshot;
import ru.nsu.fit.towerdefense.metadata.GameMetaData;
import ru.nsu.fit.towerdefense.metadata.gameobjecttypes.ProjectileType;
import ru.nsu.fit.towerdefense.metadata.gameobjecttypes.TowerType;
import ru.nsu.fit.towerdefense.metadata.map.GameMap;
import ru.nsu.fit.towerdefense.multiplayer.ConnectionManager;
import ru.nsu.fit.towerdefense.multiplayer.Message;
import ru.nsu.fit.towerdefense.replay.Replay;
import ru.nsu.fit.towerdefense.simulator.ReplayWorldControl;
import ru.nsu.fit.towerdefense.simulator.WorldControl;
import ru.nsu.fit.towerdefense.simulator.WorldObserver;
import ru.nsu.fit.towerdefense.simulator.events.BuildTowerEvent;
import ru.nsu.fit.towerdefense.simulator.events.Event;
import ru.nsu.fit.towerdefense.simulator.events.SellTowerEvent;
import ru.nsu.fit.towerdefense.simulator.events.TuneTowerEvent;
import ru.nsu.fit.towerdefense.simulator.events.UpgradeTowerEvent;
import ru.nsu.fit.towerdefense.simulator.exceptions.GameplayException;
import ru.nsu.fit.towerdefense.simulator.world.World;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.Base;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.ClickVisitor;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.Effect;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.Enemy;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.Portal;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.Projectile;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.Renderable;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.RoadTile;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.Tower;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.TowerPlatform;
import ru.nsu.fit.towerdefense.util.Vector2;

import java.io.File;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static javafx.scene.control.Alert.AlertType;
import static javafx.scene.input.KeyCode.ESCAPE;
import static ru.nsu.fit.towerdefense.fx.util.AlertBuilder.RENDER_WORLD_ERROR_HEADER;

/**
 * GameController class is used by JavaFX in javafx.fxml.FXMLLoader for showing the game scene.
 *
 * @author Oleg Markelov
 */
public class GameController implements Controller, WorldObserver, WorldRendererObserver, ClickVisitor, ServerMessageListener {

    private static final String FXML_FILE_NAME = "game.fxml";
    private static final int FRAMES_PER_SECOND = 60;
    private static final int DELTA_TIME = 1000 / FRAMES_PER_SECOND;
    private static final DecimalFormat DECIMAL_FORMAT =
        new DecimalFormat("#.##", new DecimalFormatSymbols() {{setDecimalSeparator('.');}});


    private static final ColorAdjust MONOCHROME_COLOR_ADJUST = new ColorAdjust() {{
        setBrightness(-0.5);
        setSaturation(-1);
    }};

    private static final String SLIDER_STYLE_FORMAT =
        "-slider-track-color: linear-gradient(to right, -slider-filled-track-color 0%%, "
            + "-slider-filled-track-color %1$f%%, -fx-base %1$f%%, -fx-base 100%%);";

    private enum State { PLAYING, PAUSED, FINISHED }

    @FXML private StackPane worldWrapperStackPane;
    @FXML private AnchorPane worldAnchorPane;

    @FXML private StackPane gameObjectSidePane;

    @FXML private VBox platformSideVBox;
    @FXML private FlowPane buildTowerFlowPane;
    @FXML private VBox buildTowerCharacteristicsVBox;
    @FXML private Text buildTowerNameText;
    @FXML private Text buildTowerDisplayInfoText;
    @FXML private Label buildTowerOmnidirectionalLabel;
    @FXML private Label buildTowerRangeLabel;
    @FXML private Label buildTowerFireRateLabel;
    @FXML private Label buildTowerRotationSpeedLabel;
    @FXML private Label buildTowerSelfGuidedLabel;
    @FXML private Label buildTowerProjectileSpeedLabel;
    @FXML private Label buildTowerDamageLabel;

    @FXML private VBox towerSideVBox;
    @FXML private Text towerNameText;
    @FXML private Text towerDisplayInfoText;
    @FXML private Text towerCharacteristicsText;
    @FXML private FlowPane upgradeTowerFlowPane;
    @FXML private Label upgradeTowerOmnidirectionalLabel;
    @FXML private Label upgradeTowerRangeLabel;
    @FXML private Label upgradeTowerFireRateLabel;
    @FXML private Label upgradeTowerRotationSpeedLabel;
    @FXML private Label upgradeTowerSelfGuidedLabel;
    @FXML private Label upgradeTowerProjectileSpeedLabel;
    @FXML private Label upgradeTowerDamageLabel;
    @FXML private HBox towerModeFirstHBox;
    @FXML private HBox towerModeWeakestHBox;
    @FXML private HBox towerModeNearestHBox;
    @FXML private HBox towerModeLastHBox;
    @FXML private HBox towerModeStrongestHBox;
    @FXML private HBox towerModeRandomHBox;
    @FXML private Label sellLabel;

    @FXML private VBox baseSideVBox;
    @FXML private Label baseInitialHealthLabel;

    @FXML private VBox enemySideVBox;
    @FXML private Text enemyNameText;
    @FXML private Text enemyDisplayInfoText;
    @FXML private Label enemyHealthLabel;
    @FXML private Label enemySpeedLabel;
    @FXML private Label enemyDamageLabel;

    @FXML private VBox portalSideVBox;

    @FXML private VBox projectileSideVBox;
    @FXML private Text projectileNameText;
    @FXML private Text projectileDisplayInfoText;
    @FXML private Label projectileSelfGuidedLabel;
    @FXML private Label projectileSpeedLabel;
    @FXML private Label projectileDamageLabel;

    @FXML private VBox roadSideVBox;

    @FXML private Label researchLabel;
    @FXML private Label healthLabel;
    @FXML private Label enemyLabel;
    @FXML private Label moneyLabel;
    @FXML private Label playingTimeLabel;

    @FXML private ImageView waveImageView;
    @FXML private Label nextWaveTimeLabel;
    @FXML private Label waveLabel;
    @FXML private Label speedLabel;
    @FXML private ImageView speed0xImageView;
    @FXML private ImageView speed1xImageView;
    @FXML private ImageView speed2xImageView;
    @FXML private ImageView speed3xImageView;

    @FXML private HBox replayHBox;
    @FXML private ImageView skipLeftImageView;
    @FXML private ImageView skipRightImageView;
    @FXML private Slider replaySlider;

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
    private final ConnectionManager connectionManager;
    private final File snapshotFile;
    private final List<String> playerNames;

    private ScheduledExecutorService worldSimulationExecutor;

    private final Vector2<Integer> worldSize;
    private final int baseInitialHealth;

    private final WorldControl worldControl;
    private Camera camera;
    private WorldRenderer worldRenderer;

    private Replay replay;

    private Map<Tower.Mode, Node> towerModeToUiNodeMap;

    private State state;
    private int speed;
    private VBox currentSideVBox;

    /**
     * Creates new GameController with specified SceneManager, GameMap and Replay.
     *
     * @param sceneManager scene manager.
     * @param gameMap      game map.
     * @param replay       replay.
     */
    public GameController(SceneManager sceneManager, ConnectionManager connectionManager, File snapshotFile, GameMap gameMap, List<String> playerNames, Replay replay) {
        this.sceneManager = sceneManager;
        this.connectionManager = connectionManager;
        this.snapshotFile = snapshotFile;
        this.playerNames = playerNames;

        worldSize = gameMap.getSize();
        baseInitialHealth = gameMap.getBaseDescription().getHealth();

        state = State.PLAYING;
        speed = 1; // todo think

        if (replay == null) {
            worldControl = new WorldControl(gameMap, DELTA_TIME, this, playerNames);
        } else {
            this.replay = replay;
            worldControl = new ReplayWorldControl(gameMap, DELTA_TIME, this, replay);
        }
    }

    /**
     * Creates new GameController with specified SceneManager and GameMap.
     *
     * @param sceneManager scene manager.
     * @param gameMap      game map.
     */
    public GameController(SceneManager sceneManager, ConnectionManager connectionManager, File snapshotFile, GameMap gameMap, List<String> playerNames) {
        this(sceneManager, connectionManager, snapshotFile, gameMap, playerNames, null);
    }

    @FXML
    private void initialize() {
        camera = new Camera(worldWrapperStackPane, worldAnchorPane, sceneManager.getStageSize(), worldSize);
        worldRenderer = new WorldRenderer(worldAnchorPane.getChildren(), camera.getPixelsPerGameCell(), this);

        towerModeToUiNodeMap = new HashMap<>() {{
            put(Tower.Mode.First, towerModeFirstHBox);
            put(Tower.Mode.Last, towerModeLastHBox);
            put(Tower.Mode.Weakest, towerModeWeakestHBox);
            put(Tower.Mode.Strongest, towerModeStrongestHBox);
            put(Tower.Mode.Nearest, towerModeNearestHBox);
            put(Tower.Mode.Random, towerModeRandomHBox);
        }};

        worldWrapperStackPane.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                hideSideBar();
                worldRenderer.hideTowerRangeCircle();
                worldRenderer.hideGameObjectSelection();
            }
        });

        bindUppercase(enemyNameText);
        bindUppercase(projectileNameText);
        bindUppercase(buildTowerNameText);
        bindUppercase(towerNameText);
        bindUppercase(towerCharacteristicsText);

        baseInitialHealthLabel.setText(baseInitialHealth + "");

        speed0xImageView.setOnMouseClicked(mouseEvent -> updateSpeed(0));
        speed1xImageView.setOnMouseClicked(mouseEvent -> updateSpeed(1));
        speed2xImageView.setOnMouseClicked(mouseEvent -> updateSpeed(2));
        speed3xImageView.setOnMouseClicked(mouseEvent -> updateSpeed(3));

        if (isReplaying()) {
            replayHBox.setVisible(true);
            replayHBox.setManaged(true);

            replaySlider.setMax(replay.getReplayLength());

            replaySlider.setOnMousePressed(mouseEvent -> onSliderPressedOrDragged());
            replaySlider.setOnMouseDragged(mouseEvent -> onSliderPressedOrDragged());
            replaySlider.setOnMouseReleased(mouseEvent -> onSliderReleased());
            replaySlider.styleProperty().bind(Bindings.createStringBinding(() -> {
                double percentage =
                    (replaySlider.getValue() - replaySlider.getMin()) / (replaySlider.getMax() - replaySlider.getMin()) * 100.0;
                return String.format(Locale.US, SLIDER_STYLE_FORMAT, percentage);
            }, replaySlider.valueProperty(), replaySlider.minProperty(), replaySlider.maxProperty()));
        }

        pauseImageView.setOnMouseClicked(mouseEvent -> pauseGame());
        resumeHBox.setOnMouseClicked(mouseEvent -> resumeGame());
        menuHBox.setOnMouseClicked(mouseEvent -> sceneManager.switchToMenu());

        resultsMenuHBox.setOnMouseClicked(mouseEvent -> sceneManager.switchToMenu());

        try {
            worldRenderer.update(new HashSet<>(worldControl.getWorld().getRenderables()));
            renderAll();
        } catch (RenderException e) {
            handleRenderException(e);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

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
                for (int i = 0; i < speed; i++) {
                    worldControl.simulateTick();
                }

                if (speed != 0) {
                    worldRenderer.update(new HashSet<>(worldControl.getWorld().getRenderables()));
                }

                Platform.runLater(this::renderAll);
            } catch (RenderException e) {
                handleRenderException(e);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }, 0, DELTA_TIME, TimeUnit.MILLISECONDS);
    }

    private void renderAll() {
        try {
            worldRenderer.render();

            researchLabel.setText(worldControl.getResearchPoints(connectionManager.getUsername()) + "");
            moneyLabel.setText(worldControl.getMoney(connectionManager.getUsername()) + "");
            healthLabel.setText(worldControl.getBaseHealth() + "");
            enemyLabel.setText(worldControl.getEnemiesKilled(connectionManager.getUsername()) + "");
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

            if (isReplaying()) {
                replaySlider.setValue(worldControl.getTick());
            }
        } catch (RenderException e) {
            handleRenderException(e);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private void handleRenderException(RenderException e) {
        sceneManager.switchToMenu();

        Platform.runLater(() -> new AlertBuilder()
            .setHeaderText(RENDER_WORLD_ERROR_HEADER)
            .setContentText(e.getMessage())
            .setOwner(sceneManager.getWindowOwner())
            .build().showAndWait());
    }

    private void handleGameplayException(GameplayException e) {
        new AlertBuilder()
            .setAlertType(AlertType.INFORMATION)
            .setHeaderText(e.getMessage())
            .setContentText("")
            .setOwner(sceneManager.getWindowOwner())
            .build().showAndWait();
    }

    private void updateSpeed(int speed) {
        this.speed = speed;
        speedLabel.setText(speed + "x");
    }

    private void onSliderPressedOrDragged() {
        worldSimulationExecutor.shutdown();
    }

    private void onSliderReleased() {
        try {
            ((ReplayWorldControl) worldControl).skipToTick((int) replaySlider.getValue());
            worldRenderer.update(new HashSet<>(worldControl.getWorld().getRenderables()));
            renderAll();
        } catch (RenderException e) {
            handleRenderException(e);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        activateGame();
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

            camera.scale(scrollEvent.getDeltaY() > 0,
                scrollEvent.getSceneX(), scrollEvent.getSceneY());
        });

        sceneManager.getScene().setOnMousePressed(mouseEvent -> {
            if (state != State.PLAYING) {
                return;
            }

            if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                camera.initMovement(mouseEvent.getSceneX(), mouseEvent.getSceneY());
            }
        });

        sceneManager.getScene().setOnMouseDragged(mouseEvent -> {
            if (state != State.PLAYING) {
                return;
            }

            if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                camera.updateMovement(mouseEvent.getSceneX(), mouseEvent.getSceneY());
            }
        });

        sceneManager.getScene().setOnMouseReleased(mouseEvent -> {
            if (state != State.PLAYING) {
                return;
            }

            if (mouseEvent.getButton().equals(MouseButton.SECONDARY)) {
                camera.finishMovement(mouseEvent.getSceneX(), mouseEvent.getSceneY());
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

        if (!snapshotFile.exists()) {
            Snapshot.make(snapshotFile, worldWrapperStackPane);
        }
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

    @Override
    public void onGameObjectClicked(Renderable renderable) {
        if (state != State.PLAYING) {
            return;
        }

        worldRenderer.hideTowerRangeCircle();
        worldRenderer.showGameObjectSelection(renderable);

        renderable.accept(this);
    }

    @Override
    public void onClicked(Base base) {
        showSideBar(baseSideVBox);
    }

    @Override
    public void onClicked(Enemy enemy) {
        updateEnemySideBar(enemy);
        showSideBar(enemySideVBox);
    }

    @Override
    public void onClicked(Effect effect) {
        onClicked(effect.getHost());
    }

    @Override
    public void onClicked(Portal portal) {
        showSideBar(portalSideVBox);
    }

    @Override
    public void onClicked(Projectile projectile) {
        updateProjectileSideBar(projectile);
        showSideBar(projectileSideVBox);
    }

    @Override
    public void onClicked(RoadTile roadTile) {
        showSideBar(roadSideVBox);
    }

    @Override
    public void onClicked(TowerPlatform towerPlatform) {
        Tower towerOnPlatform = worldControl.getTowerOnPlatform(towerPlatform);
        if (towerOnPlatform == null) {
            updatePlatformSideBar(towerPlatform);
            showSideBar(platformSideVBox);
        } else {
            onClicked(towerOnPlatform);
        }
    }

    @Override
    public void onClicked(Tower tower) {
        worldRenderer.showTowerRange(tower, tower.getType().getRange());
        updateTowerSideBar(tower);
        showSideBar(towerSideVBox);
    }

    @Override
    public void onServerMessageReceived(String messageStr) {
        try {
            Message message = new Gson().fromJson(messageStr, Message.class);
            if (message.getType() == null) {
                System.err.println("message type is null");
                return;
            }

            switch (message.getType()) {
                case EVENT -> {
                    if (message.getSerializedEvent() == null) {
                        System.err.println("event is null");
                        return;
                    }

                    System.out.println("onServerMessageReceived EVENT: " + message.getSerializedEvent());
                    Platform.runLater(() -> worldControl.submitEvent(Event.deserialize(message.getSerializedEvent())));
                }
                case STATE -> {
                    if (message.getSerializedWorld() == null) {
                        System.err.println("world is null");
                        return;
                    }

                    Platform.runLater(() -> worldControl.updateWorld(World.deserialize(message.getSerializedWorld())));
                }
                default -> System.out.println("default");
            }
        } catch (JsonSyntaxException e) {
            System.err.println("json parsing error: " + messageStr);
        }
    }

    private void updateEnemySideBar(Enemy enemy) {
        enemyNameText.setText(enemy.getType().getTypeName());
        enemyDisplayInfoText.setText(enemy.getType().getDisplayInfo());
        enemyHealthLabel.setText(DECIMAL_FORMAT.format(enemy.getType().getHealth()));
        enemySpeedLabel.setText(DECIMAL_FORMAT.format(enemy.getType().getSpeed() * 1000));
        enemyDamageLabel.setText(DECIMAL_FORMAT.format(enemy.getType().getDamage()));
    }

    private void updateProjectileSideBar(Projectile projectile) {
        projectileNameText.setText(projectile.getType().getTypeName());
        projectileDisplayInfoText.setText(projectile.getType().getDisplayInfo());
        projectileSelfGuidedLabel.setText(projectile.getType().isSelfGuided() ? "Yes" : "No");
        projectileSpeedLabel.setText(DECIMAL_FORMAT.format(projectile.getType().getSpeed() * 1000));
        projectileDamageLabel.setText(DECIMAL_FORMAT.format(projectile.getType().getBasicDamage()));
    }

    private void updateTowerSideBar(Tower tower) {
        towerNameText.setText(tower.getType().getTypeName());
        towerDisplayInfoText.setText(tower.getType().getDisplayInfo());
        setDefaultTowerCharacteristics(tower);

        upgradeTowerFlowPane.getChildren().clear();
        for (TowerType.Upgrade upgrade : tower.getType().getUpgrades()) {
            try {
                TowerType towerType = GameMetaData.getInstance().getTowerType(upgrade.getName());

                ImageView imageView = new ImageView(
                    Images.getInstance().getImage(towerType.getImage()));
                imageView.setFitWidth(48);
                imageView.setFitHeight(48);
                imageView.setPickOnBounds(true);
                imageView.setPreserveRatio(true);

                if (!GameMetaData.getInstance().getTechTree().getIsTypeAvailable(towerType.getTypeName())) {
                    imageView.setEffect(MONOCHROME_COLOR_ADJUST);
                    imageView.setCache(true);
                    imageView.setCacheHint(CacheHint.SPEED);
                }

                Label label = new Label("$" + upgrade.getCost());

                VBox towerTypeVBox = new VBox();
                towerTypeVBox.getStyleClass().add("tower-upgrade-box");
                towerTypeVBox.getChildren().addAll(imageView, label);
                towerTypeVBox.setOnMouseClicked(mouseEvent -> {
                    if (isReplaying()) {
                        return;
                    }

                    try {
                        UpgradeTowerEvent event = worldControl.upgradeTower(connectionManager.getUsername(), tower, upgrade);
                        sendEventToServer(event);

                        onClicked(tower);
                    } catch (GameplayException e) {
                        handleGameplayException(e);
                    }
                });
                towerTypeVBox.setOnMouseEntered(mouseEvent -> {
                    towerCharacteristicsText.setText(towerType.getTypeName());

                    ProjectileType projType = GameMetaData.getInstance()
                        .getProjectileType(tower.getType().getProjectileType());
                    ProjectileType newProjType = GameMetaData.getInstance()
                        .getProjectileType(towerType.getProjectileType());

                    updateLabelComparingValues(upgradeTowerOmnidirectionalLabel,
                        towerType.getFireType() == TowerType.FireType.OMNIDIRECTIONAL,
                        tower.getType().getFireType() == TowerType.FireType.OMNIDIRECTIONAL);

                    updateLabelComparingValues(upgradeTowerRangeLabel,
                        towerType.getRange(), tower.getType().getRange());

                    updateLabelComparingValues(upgradeTowerFireRateLabel,
                        1000d / towerType.getFireRate(), 1000d / tower.getType().getFireRate());

                    updateLabelComparingValues(upgradeTowerRotationSpeedLabel,
                        towerType.getRotationSpeed(), tower.getType().getRotationSpeed());

                    updateLabelComparingValues(upgradeTowerSelfGuidedLabel,
                        newProjType.isSelfGuided(), projType.isSelfGuided());

                    updateLabelComparingValues(upgradeTowerProjectileSpeedLabel,
                        newProjType.getSpeed() * 1000, projType.getSpeed() * 1000);

                    updateLabelComparingValues(upgradeTowerDamageLabel,
                        newProjType.getBasicDamage(), projType.getBasicDamage());

                    worldRenderer.showNewTowerRange(tower, towerType.getRange(),
                        compare(towerType.getRange(), tower.getType().getRange()));
                });
                towerTypeVBox.setOnMouseExited(mouseEvent -> {
                    setDefaultTowerCharacteristics(tower);
                    worldRenderer.hideNewTowerRangeCircle();
                });

                upgradeTowerFlowPane.getChildren().add(towerTypeVBox);
            } catch (RenderException e) {
                System.out.println(e.getMessage());
            }
        }

        if (upgradeTowerFlowPane.getChildren().isEmpty()) {
            upgradeTowerFlowPane.getChildren().add(new Label("No more upgrades for this tower"));
        }

        for (Map.Entry<Tower.Mode, Node> entry : towerModeToUiNodeMap.entrySet()) {
            Tower.Mode mode = entry.getKey();
            Node node = entry.getValue();

            node.setOnMouseClicked(mouseEvent -> {
                if (isReplaying()) {
                    return;
                }

                TuneTowerEvent event = worldControl.tuneTower(connectionManager.getUsername(), tower, mode);
                sendEventToServer(event);
                for (Node _node : towerModeToUiNodeMap.values()) {
                    _node.getStyleClass().removeIf(className -> className.equals("target-box-selected"));
                }
                node.getStyleClass().add("target-box-selected");
            });
            node.getStyleClass().removeIf(className -> className.equals("target-box-selected"));
        }
        towerModeToUiNodeMap.get(tower.getMode()).getStyleClass().add("target-box-selected");

        sellLabel.setText("$" + tower.getSellPrice());
        sellLabel.setOnMouseClicked(mouseEvent -> {
            if (isReplaying()) {
                return;
            }

            SellTowerEvent event = worldControl.sellTower(connectionManager.getUsername(), tower);
            sendEventToServer(event);

            TowerPlatform towerPlatform = event.getPlatform();
            worldRenderer.remove(tower);

            onClicked(towerPlatform);
        });
    }

    private void setDefaultTowerCharacteristics(Tower tower) {
        towerCharacteristicsText.setText("CHARACTERISTICS");
        upgradeTowerOmnidirectionalLabel.getStyleClass().clear();
        upgradeTowerOmnidirectionalLabel.setText(
            tower.getType().getFireType() == TowerType.FireType.OMNIDIRECTIONAL ? "Yes" : "No");
        upgradeTowerOmnidirectionalLabel.getStyleClass().clear();
        upgradeTowerRangeLabel.setText(DECIMAL_FORMAT.format(tower.getType().getRange()));
        upgradeTowerRangeLabel.getStyleClass().clear();
        upgradeTowerFireRateLabel.setText(DECIMAL_FORMAT.format(1000d / tower.getType().getFireRate()));
        upgradeTowerFireRateLabel.getStyleClass().clear();
        upgradeTowerRotationSpeedLabel.setText(DECIMAL_FORMAT.format(tower.getType().getRotationSpeed()));
        upgradeTowerRotationSpeedLabel.getStyleClass().clear();
        ProjectileType projectileType =
            GameMetaData.getInstance().getProjectileType(tower.getType().getProjectileType());
        upgradeTowerSelfGuidedLabel.setText(projectileType.isSelfGuided() ? "Yes" : "No");
        upgradeTowerSelfGuidedLabel.getStyleClass().clear();
        upgradeTowerProjectileSpeedLabel.setText(DECIMAL_FORMAT.format(projectileType.getSpeed() * 1000));
        upgradeTowerProjectileSpeedLabel.getStyleClass().clear();
        upgradeTowerDamageLabel.setText(DECIMAL_FORMAT.format(projectileType.getBasicDamage()));
        upgradeTowerDamageLabel.getStyleClass().clear();
    }

    private void updateLabelComparingValues(Label label, double newValue, double value) {
        int greater = compare(newValue, value);
        if (greater > 0) {
            label.setText(DECIMAL_FORMAT.format(newValue) + " (+" +
                DECIMAL_FORMAT.format(Math.abs(value - newValue)) + ")");
            label.getStyleClass().add("improvement-text");
        } else if (greater < 0) {
            label.setText(DECIMAL_FORMAT.format(newValue) + " (-" +
                DECIMAL_FORMAT.format(Math.abs(value - newValue)) + ")");
            label.getStyleClass().add("retrogression-text");
        }
    }

    private void updateLabelComparingValues(Label label, boolean newValue, boolean value) {
        label.setText(newValue ? "Yes" : "No");
        if (newValue && !value) {
            label.getStyleClass().add("improvement-text");
        } else if (!newValue && value) {
            label.getStyleClass().add("retrogression-text");
        }
    }

    private void updatePlatformSideBar(TowerPlatform towerPlatform) {
        List<TowerType> towerTypes = GameMetaData.getInstance().getTowerNames().stream()
            .map(towerName -> GameMetaData.getInstance().getTowerType(towerName))
            .sorted(Comparator.comparingInt(TowerType::getPrice))
            .collect(Collectors.toList());

        buildTowerFlowPane.getChildren().clear();
        for (TowerType towerType : towerTypes) {
            try {
                ImageView imageView = new ImageView(
                    Images.getInstance().getImage(towerType.getImage()));
                imageView.setFitWidth(48);
                imageView.setFitHeight(48);
                imageView.setPickOnBounds(true);
                imageView.setPreserveRatio(true);

                if (!GameMetaData.getInstance().getTechTree().getIsTypeAvailable(towerType.getTypeName())) {
                    imageView.setEffect(MONOCHROME_COLOR_ADJUST);
                    imageView.setCache(true);
                    imageView.setCacheHint(CacheHint.SPEED);
                }

                Label label = new Label("$" + towerType.getPrice());

                VBox towerTypeVBox = new VBox();
                towerTypeVBox.getStyleClass().add("tower-upgrade-box");
                towerTypeVBox.getChildren().addAll(imageView, label);
                towerTypeVBox.setOnMouseClicked(mouseEvent -> {
                    if (isReplaying()) {
                        return;
                    }

                    try {
                        BuildTowerEvent event = worldControl.buildTower(connectionManager.getUsername(), towerPlatform, towerType);
                        sendEventToServer(event);

                        Tower tower = event.getTower();
                        worldRenderer.add(tower);
                        onClicked(tower);
                    } catch (GameplayException e) {
                        handleGameplayException(e);
                    } catch (RenderException e) {
                        handleRenderException(e);
                    }
                });
                towerTypeVBox.setOnMouseEntered(mouseEvent -> {
                    buildTowerNameText.setText(towerType.getTypeName());
                    buildTowerDisplayInfoText.setText(towerType.getDisplayInfo());
                    buildTowerOmnidirectionalLabel.setText(
                        towerType.getFireType() == TowerType.FireType.OMNIDIRECTIONAL ? "Yes" : "No");
                    buildTowerRangeLabel.setText(DECIMAL_FORMAT.format(towerType.getRange()));
                    buildTowerFireRateLabel.setText(DECIMAL_FORMAT.format(1000d / towerType.getFireRate()));
                    buildTowerRotationSpeedLabel.setText(DECIMAL_FORMAT.format(towerType.getRotationSpeed()));
                    ProjectileType projectileType =
                        GameMetaData.getInstance().getProjectileType(towerType.getProjectileType());
                    buildTowerSelfGuidedLabel.setText(projectileType.isSelfGuided() ? "Yes" : "No");
                    buildTowerProjectileSpeedLabel.setText(DECIMAL_FORMAT.format(projectileType.getSpeed() * 1000));
                    buildTowerDamageLabel.setText(DECIMAL_FORMAT.format(projectileType.getBasicDamage()));

                    buildTowerCharacteristicsVBox.setVisible(true);

                    worldRenderer.showNewTowerRange(towerPlatform, towerType.getRange(), 1);
                });
                towerTypeVBox.setOnMouseExited(mouseEvent -> {
                    buildTowerCharacteristicsVBox.setVisible(false);
                    worldRenderer.hideNewTowerRangeCircle();
                });

                buildTowerFlowPane.getChildren().add(towerTypeVBox);
            } catch (RenderException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void showSideBar(VBox newBox) {
        if (newBox == currentSideVBox) {
            return;
        }

        hideCurrentSideBox();

        newBox.setManaged(true);
        newBox.setVisible(true);

        gameObjectSidePane.setVisible(true);

        currentSideVBox = newBox;
    }

    private void hideSideBar() {
        gameObjectSidePane.setVisible(false);
        hideCurrentSideBox();
        currentSideVBox = null;
    }

    private void hideCurrentSideBox() {
        if (currentSideVBox != null) {
            currentSideVBox.setVisible(false);
            currentSideVBox.setManaged(false);
        }
    }

    private void finishGame(boolean win) {
        if (worldSimulationExecutor != null) {
            worldSimulationExecutor.shutdown();
        }

        if (isReplaying()) {
            return;
        }

        state = State.FINISHED;

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

    private boolean isReplaying() {
        return replay != null;
    }

    private void bindUppercase(Text text) {
        text.textProperty().addListener((observable, oldValue, newValue) -> {
            text.setText(newValue.toUpperCase());
        });
    }

    private String formatWaveTime(long milliseconds) {
        return String.format("%d:%02d", (milliseconds / 60000) % 60, (milliseconds / 1000) % 60);
    }

    private String formatPlayingTime(long milliseconds) {
        return String.format(milliseconds < 3600000 ? "%2$02d:%3$02d" : "%02d:%02d:%02d",
            milliseconds / 3600000, (milliseconds / 60000) % 60, (milliseconds / 1000) % 60);
    }

    private int compare(double a, double b) {
        if (Math.abs(a - b) < 0.01d) return 0;
        if (a > b) return 1;
        return -1;
    }

    private void sendEventToServer(Event event) {
        Message message = new Message();
        message.setType(Message.Type.EVENT);
        message.setSerializedEvent(event.serialize());
        connectionManager.sendMessage(new Gson().toJson(message));
    }
}
