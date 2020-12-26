package ru.nsu.fit.towerdefense.fx.controllers.game;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
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
import ru.nsu.fit.towerdefense.fx.controllers.Controller;
import ru.nsu.fit.towerdefense.fx.exceptions.RenderException;
import ru.nsu.fit.towerdefense.fx.util.AlertBuilder;
import ru.nsu.fit.towerdefense.metadata.GameMetaData;
import ru.nsu.fit.towerdefense.simulator.WorldControl;
import ru.nsu.fit.towerdefense.simulator.WorldObserver;
import ru.nsu.fit.towerdefense.simulator.exceptions.GameplayException;
import ru.nsu.fit.towerdefense.metadata.map.GameMap;
import ru.nsu.fit.towerdefense.util.Vector2;
import ru.nsu.fit.towerdefense.simulator.world.World;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.Base;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.Enemy;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.Portal;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.Projectile;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.Renderable;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.RoadTile;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.Tower;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.TowerPlatform;
import ru.nsu.fit.towerdefense.metadata.gameobjecttypes.ProjectileType;
import ru.nsu.fit.towerdefense.metadata.gameobjecttypes.TowerType;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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
    private static final DecimalFormat DECIMAL_FORMAT =
        new DecimalFormat("#.##", new DecimalFormatSymbols() {{setDecimalSeparator('.');}});

    private enum State { PLAYING, PAUSED, FINISHED }

    @FXML private StackPane rootStackPane;
    @FXML private AnchorPane worldAnchorPane;

    @FXML private StackPane gameObjectSidePane;
    @FXML private ImageView closeSidePaneImageView;

    @FXML private VBox platformSideVBox;
    @FXML private FlowPane buildTowerFlowPane;
    @FXML private VBox buildTowerCharacteristicsVBox;
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
    private final int baseInitialHealth;

    private final WorldControl worldControl;
    private WorldCamera worldCamera;
    private WorldRenderer worldRenderer;

    private Map<Tower.Mode, Node> towerModeToUiNodeMap;

    private State state;
    private int speed;
    private VBox currentSideVBox;

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
        baseInitialHealth = gameMap.getBaseDescription().getHealth();

        state = State.PLAYING;
        speed = 1;
    }

    @FXML
    private void initialize() {
        worldCamera = new WorldCamera(rootStackPane, worldAnchorPane, sceneManager.getStageSize(), worldSize);
        worldRenderer = new WorldRenderer(worldAnchorPane.getChildren(), worldCamera.getPixelsPerGameCell(), this);

        towerModeToUiNodeMap = new HashMap<>() {{
            put(Tower.Mode.First, towerModeFirstHBox);
            put(Tower.Mode.Last, towerModeLastHBox);
            put(Tower.Mode.Weakest, towerModeWeakestHBox);
            put(Tower.Mode.Strongest, towerModeStrongestHBox);
            put(Tower.Mode.Nearest, towerModeNearestHBox);
            put(Tower.Mode.Random, towerModeRandomHBox);
        }};

        closeSidePaneImageView.setOnMouseClicked(mouseEvent -> {
            hideSideBar();
            worldRenderer.hideTowerRangeCircle();
        });

        bindUppercase(enemyNameText);
        bindUppercase(projectileNameText);
        bindUppercase(towerNameText);

        baseInitialHealthLabel.setText(baseInitialHealth + "");

        speed0xImageView.setOnMouseClicked(mouseEvent -> updateSpeed(0));
        speed1xImageView.setOnMouseClicked(mouseEvent -> updateSpeed(1));
        speed2xImageView.setOnMouseClicked(mouseEvent -> updateSpeed(2));
        speed3xImageView.setOnMouseClicked(mouseEvent -> updateSpeed(3));

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
                for (int i = 0; i < speed; i++) {
                    worldControl.simulateTick();
                }

                if (speed != 0) {
                    worldRenderer.update(new HashSet<>(worldControl.getWorld().getRenderables()));
                }

                Platform.runLater(() -> {
                    try {
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
                    } catch (RenderException e) {
                        handleRenderException(e);
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                });
            } catch (RenderException e) {
                handleRenderException(e);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }, 0, DELTA_TIME, TimeUnit.MILLISECONDS);
    }

    private void handleRenderException(RenderException e) {
        sceneManager.switchToMenu();

        Platform.runLater(() -> new AlertBuilder()
            .setHeaderText(RENDER_WORLD_ERROR_HEADER)
            .setContentText(e.getMessage())
            .setOwner(sceneManager.getWindowOwner())
            .build().showAndWait());
    }

    private void updateSpeed(int speed) {
        this.speed = speed;
        speedLabel.setText(speed + "x");
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

        worldRenderer.hideTowerRangeCircle();

        switch (renderable.getGameObjectType()) {
            case BASE:
                onBaseClicked((Base) renderable);
                break;
            case ENEMY:
                onEnemyClicked((Enemy) renderable);
                break;
            case ENEMY_PORTAL:
                onPortalClicked((Portal) renderable);
                break;
            case PROJECTILE:
                onProjectileClicked((Projectile) renderable);
                break;
            case ROAD_TILE:
                onRoadTileClicked((RoadTile) renderable);
                break;
            case TOWER:
                onTowerClicked((Tower) renderable);
                break;
            case TOWER_PLATFORM:
                onTowerPlatformClicked((TowerPlatform) renderable);
                break;
        }
    }

    private void onBaseClicked(Base base) {
        showSideBar(baseSideVBox);
    }

    private void onEnemyClicked(Enemy enemy) {
        updateEnemySideBar(enemy);
        showSideBar(enemySideVBox);
    }

    private void onPortalClicked(Portal portal) {
        showSideBar(portalSideVBox);
    }

    private void onProjectileClicked(Projectile projectile) {
        updateProjectileSideBar(projectile);
        showSideBar(projectileSideVBox);
    }

    private void onRoadTileClicked(RoadTile roadTile) {
        showSideBar(roadSideVBox);
    }

    private void onTowerPlatformClicked(TowerPlatform towerPlatform) {
        Tower towerOnPlatform = worldControl.getTowerOnPlatform(towerPlatform);
        if (towerOnPlatform == null) {
            updatePlatformSideBar(towerPlatform);
            showSideBar(platformSideVBox);
        } else {
            worldRenderer.showTowerRangeCircle(towerOnPlatform, towerOnPlatform.getType().getRange());
            updateTowerSideBar(towerOnPlatform);
            showSideBar(towerSideVBox);
        }
    }

    private void onTowerClicked(Tower tower) {
        worldRenderer.showTowerRangeCircle(tower, tower.getType().getRange());
        updateTowerSideBar(tower);
        showSideBar(towerSideVBox);
    }

    private void updateEnemySideBar(Enemy enemy) {
        enemyNameText.setText(enemy.getType().getTypeName());
        enemyDisplayInfoText.setText(enemy.getType().getDisplayInfo());
        enemyHealthLabel.setText(DECIMAL_FORMAT.format(enemy.getType().getHealth()));
        enemySpeedLabel.setText(DECIMAL_FORMAT.format(enemy.getType().getSpeed()));
        enemyDamageLabel.setText(DECIMAL_FORMAT.format(enemy.getType().getDamage()));
    }

    private void updateProjectileSideBar(Projectile projectile) {
        projectileNameText.setText(projectile.getType().getTypeName());
        projectileDisplayInfoText.setText(projectile.getType().getDisplayInfo());
        projectileSelfGuidedLabel.setText(projectile.getType().isSelfGuided() ? "Yes" : "No");
        projectileSpeedLabel.setText(DECIMAL_FORMAT.format(projectile.getType().getSpeed()));
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

                Label label = new Label("$" + upgrade.getCost());

                VBox towerTypeVBox = new VBox();
                towerTypeVBox.getStyleClass().add("tower-upgrade-box");
                towerTypeVBox.getChildren().addAll(imageView, label);
                towerTypeVBox.setOnMouseClicked(mouseEvent -> {
                    try {
                        worldControl.upgradeTower(tower, upgrade);
                        onTowerClicked(tower);
                    } catch (GameplayException e) {
                        System.out.println(e.getMessage());
                    }
                });
                towerTypeVBox.setOnMouseEntered(mouseEvent -> {
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
                        towerType.getFireRate(), tower.getType().getFireRate());

                    updateLabelComparingValues(upgradeTowerRotationSpeedLabel,
                        towerType.getRotationSpeed(), tower.getType().getRotationSpeed());

                    updateLabelComparingValues(upgradeTowerSelfGuidedLabel,
                        newProjType.isSelfGuided(), projType.isSelfGuided());

                    updateLabelComparingValues(upgradeTowerProjectileSpeedLabel,
                        newProjType.getSpeed(), projType.getSpeed());

                    updateLabelComparingValues(upgradeTowerDamageLabel,
                        newProjType.getBasicDamage(), projType.getBasicDamage());

                    worldRenderer.showNewTowerRangeCircle(tower, towerType.getRange(),
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
                worldControl.tuneTower(tower, mode);
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
            TowerPlatform towerPlatform = worldControl.sellTower(tower);
            worldRenderer.remove(tower);

            onTowerPlatformClicked(towerPlatform);
        });
    }

    private void setDefaultTowerCharacteristics(Tower tower) {
        upgradeTowerOmnidirectionalLabel.getStyleClass().clear();
        upgradeTowerOmnidirectionalLabel.setText(
            tower.getType().getFireType() == TowerType.FireType.OMNIDIRECTIONAL ? "Yes" : "No");
        upgradeTowerOmnidirectionalLabel.getStyleClass().clear();
        upgradeTowerRangeLabel.setText(DECIMAL_FORMAT.format(tower.getType().getRange()));
        upgradeTowerRangeLabel.getStyleClass().clear();
        upgradeTowerFireRateLabel.setText(DECIMAL_FORMAT.format(tower.getType().getFireRate()));
        upgradeTowerFireRateLabel.getStyleClass().clear();
        upgradeTowerRotationSpeedLabel.setText(DECIMAL_FORMAT.format(tower.getType().getRotationSpeed()));
        upgradeTowerRotationSpeedLabel.getStyleClass().clear();
        ProjectileType projectileType =
            GameMetaData.getInstance().getProjectileType(tower.getType().getProjectileType());
        upgradeTowerSelfGuidedLabel.setText(projectileType.isSelfGuided() ? "Yes" : "No");
        upgradeTowerSelfGuidedLabel.getStyleClass().clear();
        upgradeTowerProjectileSpeedLabel.setText(DECIMAL_FORMAT.format(projectileType.getSpeed()));
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
        String[] towerTypeNames = { "Archer", "Crossbowman", "Catapult", "RocketLauncher", "Wave" };
        buildTowerFlowPane.getChildren().clear();
        for (String towerTypeName : towerTypeNames) {
            try {
                TowerType towerType = GameMetaData.getInstance().getTowerType(towerTypeName);

                ImageView imageView = new ImageView(
                    Images.getInstance().getImage(towerType.getImage()));
                imageView.setFitWidth(48);
                imageView.setFitHeight(48);
                imageView.setPickOnBounds(true);
                imageView.setPreserveRatio(true);

                Label label = new Label("$" + towerType.getPrice());

                VBox towerTypeVBox = new VBox();
                towerTypeVBox.getStyleClass().add("tower-upgrade-box");
                towerTypeVBox.getChildren().addAll(imageView, label);
                towerTypeVBox.setOnMouseClicked(mouseEvent -> {
                    try {
                        Tower tower = worldControl.buildTower(towerPlatform, towerType);
                        worldRenderer.add(tower);
                        onTowerClicked(tower);
                    } catch (GameplayException e) {
                        System.out.println(e.getMessage());
                    } catch (RenderException e) {
                        handleRenderException(e);
                    }
                });
                towerTypeVBox.setOnMouseEntered(mouseEvent -> {
                    buildTowerOmnidirectionalLabel.setText(
                        towerType.getFireType() == TowerType.FireType.OMNIDIRECTIONAL ? "Yes" : "No");
                    buildTowerRangeLabel.setText(DECIMAL_FORMAT.format(towerType.getRange()));
                    buildTowerFireRateLabel.setText(DECIMAL_FORMAT.format(towerType.getFireRate()));
                    buildTowerRotationSpeedLabel.setText(DECIMAL_FORMAT.format(towerType.getRotationSpeed()));
                    ProjectileType projectileType =
                        GameMetaData.getInstance().getProjectileType(towerType.getProjectileType());
                    buildTowerSelfGuidedLabel.setText(projectileType.isSelfGuided() ? "Yes" : "No");
                    buildTowerProjectileSpeedLabel.setText(DECIMAL_FORMAT.format(projectileType.getSpeed()));
                    buildTowerDamageLabel.setText(DECIMAL_FORMAT.format(projectileType.getBasicDamage()));

                    buildTowerCharacteristicsVBox.setVisible(true);

                    worldRenderer.showNewTowerRangeCircle(towerPlatform, towerType.getRange(), 1);
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

    private void bindUppercase(Text text) {
        text.textProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue.equalsIgnoreCase(newValue)) {
                return;
            }

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
