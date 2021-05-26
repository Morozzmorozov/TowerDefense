package ru.nsu.fit.towerdefense.simulator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import ru.nsu.fit.towerdefense.metadata.GameMetaData;
import ru.nsu.fit.towerdefense.metadata.UserMetaData;
import ru.nsu.fit.towerdefense.metadata.gameobjecttypes.EffectType;
import ru.nsu.fit.towerdefense.metadata.gameobjecttypes.ProjectileType;
import ru.nsu.fit.towerdefense.metadata.gameobjecttypes.TowerType;
import ru.nsu.fit.towerdefense.metadata.gameobjecttypes.TowerType.FireType;
import ru.nsu.fit.towerdefense.metadata.gameobjecttypes.TowerType.Upgrade;
import ru.nsu.fit.towerdefense.metadata.map.GameMap;
import ru.nsu.fit.towerdefense.metadata.map.WaveDescription;
import ru.nsu.fit.towerdefense.metadata.map.WaveEnemies;
import ru.nsu.fit.towerdefense.replay.GameStateWriter;
import ru.nsu.fit.towerdefense.simulator.events.BuildTowerEvent;
import ru.nsu.fit.towerdefense.simulator.events.Event;
import ru.nsu.fit.towerdefense.simulator.events.SellTowerEvent;
import ru.nsu.fit.towerdefense.simulator.events.TuneTowerEvent;
import ru.nsu.fit.towerdefense.simulator.events.UpgradeTowerEvent;
import ru.nsu.fit.towerdefense.simulator.exceptions.GameplayException;
import ru.nsu.fit.towerdefense.simulator.world.SerializableWorld;
import ru.nsu.fit.towerdefense.simulator.world.Wave;
import ru.nsu.fit.towerdefense.simulator.world.World;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.Base;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.Effect;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.Enemy;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.Portal;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.Projectile;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.RoadTile;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.Tower;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.TowerPlatform;
import ru.nsu.fit.towerdefense.util.Vector2;

public class WorldControl implements ServerSimulator {

  public static final int DEBUG_MONEY = 600;
  public static final float SELL_MULTIPLIER = 0.4f;
  public static final double EPS = 1e-12;
  public static final int DEBUG_TICK_RATE = 60;

  //protected long tick; // Beware: this field shows the index of the FOLLOWING frame
  protected final GameMap gameMap;
  protected final int deltaTime;
  protected final WorldObserver worldObserver;
  protected World world;
  protected int enemiesKilled = 0; // todo move this to World
  protected int wavesDefeated = 0;
  protected boolean isReplay = false;
  protected int scienceEarned = 0;

  private final EventContainer eventContainer = new EventContainer();
  private final StateContainer stateContainer = new StateContainer(50);

  private static class EventContainer {
    private final Map<Long, List<Event>> eventMap = new HashMap<>();
    private final List<Event> allEvents = new ArrayList<>();

    public void putEvent(Event event) {
      if (!eventMap.containsKey(event.getFrameNumber())) {
        List<Event> list = new ArrayList<>();
        list.add(event);
        eventMap.put(event.getFrameNumber(), list);
      } else {
        eventMap.get(event.getFrameNumber()).add(event);
      }
    }

    public List<Event> getEvents(long tick) {
      if (eventMap.containsKey(tick)) {
        return eventMap.get(tick);
      } else {
        return new ArrayList<>();
      }
    }

    public List<Event> getAllEvents() {
      return allEvents;
    }
  }

  /**
   * Contains world states before events for them fire on their respective ticks
   */
  private static class StateContainer {
    private final long interval;
    private final Map<Long, World> stateMap = new HashMap<>();

    public StateContainer(long interval) {
      this.interval = interval;
    }

    public void putState(World world) {
      if (world.getTick() % interval == 0) {
        stateMap.put(world.getTick(), new World(world));
      }
    }

    public World getLatestState(long tick) {
      return stateMap.get(tick - (tick % interval));
    }
  }

  // todo generate money and other stuff for each player
  public WorldControl(GameMap gameMap, int deltaTime, WorldObserver worldObserver, List<String> players) {
    this.gameMap = gameMap;
    this.deltaTime = deltaTime;
    this.worldObserver = worldObserver;

    world = new World();

    for (Vector2<Integer> position : gameMap.getPositions().getPositions()) {
      Vector2<Integer> coordinates = new Vector2<>(position);
      world.getTowerPlatforms()
          .add(new TowerPlatform(coordinates, "platform.png")); // todo image name
    }

    Wave wave = new Wave();
    wave.setNumber(0);
    wave.setCurrentEnemyNumber(0);
    wave.setRemainingEnemiesCount(
        gameMap.getWaves().get(0).getEnemiesList().stream().mapToInt(WaveEnemies::getCount).sum());
    wave.setDescription(gameMap.getWaves().get(0));
    world.setCurrentWave(wave);

    for (var player : players) {
      world.setMoney(player, DEBUG_MONEY);
    }

    Base base = new Base(gameMap.getBaseDescription().getHealth(),
        gameMap.getBaseDescription().getImage(),
        new Vector2<>(gameMap.getBaseDescription().getPosition()));
    world.setBase(base);

    for (int i = 0; i < gameMap.getRoads().getRoadCount(); ++i) {
      List<Vector2<Double>> road = gameMap.getRoads().getRoad(i);

      world.getPortals().add(new Portal("portal.png", new Vector2<>(
          (int) Math.round(road.get(0).getX()), (int) Math.round(road.get(0).getY()))));

      for (int j = 0; j < road.size() - 1; ++j) { // all nodes except last
        int x1 = (int) Math.round(road.get(j).getX());
        int x2 = (int) Math.round(road.get(j + 1).getX());
        int y1 = (int) Math.round(road.get(j).getY());
        int y2 = (int) Math.round(road.get(j + 1).getY());

        if (x1 == x2) { // vertical
          for (int y = Integer.min(y1, y2); y <= Integer.max(y1, y2); ++y) {
            boolean newTileNeeded = true;
            for (RoadTile roadTile : world.getRoadTiles()) {
              if (Math.round(roadTile.getPosition().getX()) == x1
                  && Math.round(roadTile.getPosition().getY()) == y) {
                newTileNeeded = false;
                break;
              }
            }
            if (newTileNeeded) {
              world.getRoadTiles().add(new RoadTile("road.png", new Vector2<>(x1, y)));
            }
          }
        } else if (y1 == y2) { // horizontal
          for (int x = Integer.min(x1, x2); x <= Integer.max(x1, x2); ++x) {
            boolean newTileNeeded = true;
            for (RoadTile roadTile : world.getRoadTiles()) {
              if (Math.round(roadTile.getPosition().getX()) == x
                  && Math.round(roadTile.getPosition().getY()) == y1) {
                newTileNeeded = false;
                break;
              }
            }
            if (newTileNeeded) {
              world.getRoadTiles().add(new RoadTile("road.png", new Vector2<>(x, y1)));
            }
          }
        }
      }
    }

    GameMetaData.getInstance().getGameMapNames().stream()
        .dropWhile(name -> GameMetaData.getInstance().getMapDescription(name) != gameMap)
        .findFirst()
        .ifPresent(name -> GameStateWriter.getInstance().startNewReplay(DEBUG_TICK_RATE, name));

    GameStateWriter.getInstance().newFrame();

    stateContainer.putState(world);
  }

  public void updateWorld(SerializableWorld world) {
    synchronized(this) {
      this.world = new World(world);
    }
  }

  @Override
  public void submitEvent(Event event) {
    synchronized (this) {
      eventContainer.putEvent(event);
      if (event.getFrameNumber() <= world.getTick()) {
        try {
          simulateForNewEvents(event.getFrameNumber());
        } catch (GameplayException e) {
          e.printStackTrace();
        }
      }
    }
  }

  @Override
  public List<Event> getEvents() {
    return eventContainer.getAllEvents();
  }

  @Override
  public SerializableWorld getState() {
    synchronized (this) {
      return world.getSerializableWorld();
    }
  }

  public SellTowerEvent sellTower(Tower tower) {
    synchronized (this) {
      SellTowerEvent event = new SellTowerEvent(getTick() + 1, tower, "player");
      event.fire(world);
      eventContainer.putEvent(event);
      return event;
    }
  }

  public BuildTowerEvent buildTower(TowerPlatform towerPlatform, TowerType towerType)
      throws GameplayException {

    synchronized (this) {
      var event = new BuildTowerEvent((int) getTick(), towerPlatform, towerType, "player");
      event.fire(world);
      eventContainer.putEvent(event);
      return event;
    }
  }


  public UpgradeTowerEvent upgradeTower(Tower tower, Upgrade upgrade) throws GameplayException {
    synchronized (this) {
      var event = new UpgradeTowerEvent(upgrade, tower, getTick(), "player");
      event.fire(world);
      eventContainer.putEvent(event);
      return event;
    }
  }

  public Tower getTowerOnPlatform(TowerPlatform towerPlatform) {
    for (Tower candidate : world.getTowers()) {
      if (Vector2.distance(candidate.getPosition(), towerPlatform.getPosition()) < EPS) {
        return candidate;
      }
    }
    return null;
  }

  public TuneTowerEvent tuneTower(Tower tower, Tower.Mode towerMode) {
    synchronized (this) {
      var event = new TuneTowerEvent(getTick(), towerMode, tower, "player");
      event.fire(world);
      eventContainer.putEvent(event);
      return event;
    }
  }

  public long getTicksTillNextWave() {
    if (world.getCurrentWave().getCurrentEnemyNumber() == 0) {
      return world.getCountdown() / deltaTime;
    } else {
      return 0;
    }
  }

  public World getWorld() {
    return world;
  }

  public int getResearchPoints(String player) {
    return scienceEarned; // todo for each player
  }

  public int getBaseHealth() {
    return world.getBase().getHealth();
  }

  public int getEnemiesKilled(String player) {
    return enemiesKilled; // todo for each player
  }

  public int getMoney(String player) {
    return world.getMoney(player); // todo change signature
  }

  public int getWaveNumber() {
    if (world.getCurrentWaveNumber() < gameMap.getWaves().size()) {
      return world.getCurrentWaveNumber() + 1;
    }
    return world.getCurrentWaveNumber();
  }

  public long getTick() {
    return world.getTick();
  }

  public int getWavesDefeated() {
    return wavesDefeated;
  }

  private void simulateForNewEvents(long from) throws GameplayException {
    long tick = world.getTick();
    world = stateContainer.getLatestState(from);

    while (world.getTick() < tick) {
      simulateTick();
    }
  }


  public void simulateTick() {
    synchronized (this) {
      towerSequence();
      projectileSequence();
      enemySequence();
      spawnSequence();

      /*GameStateWriter.getInstance()
          .fullCopy(world.getEnemies(), world.getTowers(), world.getProjectiles(),
              world.getBase(), world.getMoney("player"), world.getCurrentWaveNumber(),
              world.getCurrentWave().getCurrentEnemyNumber(),
              world.getCountdown(), scienceEarned, enemiesKilled); // todo remake replays for multiplayer
      GameStateWriter.getInstance().endFrame();

      GameStateWriter.getInstance().newFrame();*/

      world.setTick(world.getTick() + 1);

      stateContainer.putState(world);

      for (var event : eventContainer.getEvents(world.getTick())) {
        try {
          event.fire(world); // todo remove event if unable to fire (e.g. not enough money)
        } catch (GameplayException e) {
          e.printStackTrace();
        }
      }
    }
  }

  private void enemyDeath(Enemy enemy) {
    enemy.setDead(true);
    Wave wave = world.getWaveByNumber(enemy.getWaveNumber());
    wave.setRemainingEnemiesCount(wave.getRemainingEnemiesCount() - 1);
    if (wave.getRemainingEnemiesCount() == 0) {
      wavesDefeated++;
      scienceEarned += gameMap.getScienceReward();
      if (!isReplay) {
        UserMetaData.addResearchPoints(gameMap.getScienceReward());
      }
      if ((world.getEnemies().isEmpty() || (world.getEnemies().contains(enemy)
          && world.getEnemies().size() == 1))
          && world.getCurrentWaveNumber() >= gameMap.getWaves().size()) {
        GameStateWriter.getInstance().endFrame();
        GameStateWriter.getInstance().close();
        if (worldObserver != null) {
          worldObserver.onVictory(); // todo fix temporary solution
        }
      }
    }
  }

  private void projectileSequence() {
    List<Projectile> removedProjectiles = new ArrayList<>();

    for (Projectile projectile : world.getProjectiles()) {
      if (projectile.getTarget() != null && projectile.getType().isSelfGuided() && projectile
          .getTarget().isDead()) {
        Enemy newEnemy = null;
        double dist = 0;
        for (Enemy enemy : world.getEnemies()) {
          if (newEnemy == null
              || Vector2.distance(projectile.getPosition(), enemy.getPosition()) < dist) {
            newEnemy = enemy;
            dist = Vector2.distance(projectile.getPosition(), enemy.getPosition());
          }
        }
        if (newEnemy != null) {
          projectile.setTarget(newEnemy);

        }
      }

      if (projectile.getFireType().equals(FireType.UNIDIRECTIONAL)
          && projectile.getType().isSelfGuided() && !(projectile.getTarget() == null || projectile
          .getTarget().isDead())) {

        double newRotation = (getNewDirection(projectile.getRotation() - 90,
            projectile.getPosition(), projectile.getTarget().getPosition(),
            projectile.getRotationSpeed()) + 90) % 360;

        Vector2<Double> newDirection = new Vector2<>(
            projectile.getType().getSpeed() * Math.cos(Math.toRadians(newRotation - 90)),
            projectile.getType().getSpeed() * Math.sin(Math.toRadians(newRotation - 90)));

        projectile.setVelocity(newDirection);

        projectile.setRotation(newRotation);
      }

      List<Enemy> affectedEnemies = new ArrayList<>();

      switch (projectile.getFireType()) {
        case UNIDIRECTIONAL:
          Vector2<Double> newPosition = Vector2
              .add(projectile.getPosition(), Vector2.multiply(deltaTime, projectile.getVelocity()));
          for (Enemy enemy : world.getEnemies()) {
            // Collision checking
            Vector2<Double> oldVectorToEnemy = Vector2
                .subtract(enemy.getPosition(), projectile.getPosition());
            Vector2<Double> pathVector = Vector2.subtract(newPosition, projectile.getPosition());

            double angle = Math.acos(Vector2.dotProduct(oldVectorToEnemy, pathVector)
                / Vector2.norm(pathVector) / Vector2.norm(oldVectorToEnemy));

            double encounterDistance = Math.sin(angle) * Vector2.norm(oldVectorToEnemy);

            double pathToEncounter = Math.cos(angle) * Vector2.norm(oldVectorToEnemy);

            if (Vector2.distance(newPosition, enemy.getPosition()) < enemy.getType().getHitBox()
                || (encounterDistance < enemy.getType().getHitBox() && pathToEncounter < projectile
                .getType().getSpeed()
                && Vector2.dotProduct(oldVectorToEnemy, pathVector) > 0)) {
              affectedEnemies.add(enemy);
              removedProjectiles.add(projectile);
              break;
            }
          }

          projectile.setPosition(newPosition);

          projectile.setRemainingRange(
              projectile.getRemainingRange() - projectile.getType().getSpeed() * deltaTime);
          if (projectile.getRemainingRange() <= 0) {
            removedProjectiles.add(projectile);
          }

          break;
        case OMNIDIRECTIONAL:
          double newScale = projectile.getScale() + Math
              .min(projectile.getRemainingRange() * 2, projectile.getType().getSpeed() * 2);

          for (Enemy enemy : world.getEnemies()) {
            double distanceToEnemy = Vector2
                .distance(projectile.getParentPosition(), enemy.getPosition());
            if (distanceToEnemy >= projectile.getScale() / 2 && distanceToEnemy < newScale / 2) {
              affectedEnemies.add(enemy);
            }
          }
          projectile
              .setRemainingRange(projectile.getRemainingRange() - projectile.getType().getSpeed());
          if (projectile.getRemainingRange() <= 0) {
            removedProjectiles.add(projectile);
          } else {
            projectile.setScale(newScale);
          }
      }

      for (Enemy enemy : affectedEnemies) {
        int damage =
            projectile.getType().getEnemyTypeDamageMap().containsKey(enemy.getType().getTypeName())
                ?
                projectile.getType().getEnemyTypeDamageMap().get(enemy.getType().getTypeName()) :
                projectile.getType().getBasicDamage();

        List<EffectType> effects = projectile.getType().getEffects().stream()
            .map(name -> GameMetaData.getInstance().getEffectType(name))
            .collect(Collectors.toList());

        for (EffectType effectType : effects) {
          Optional<Effect> existingEffect = enemy.getEffects().stream()
              .filter(effect -> effect.getType() == effectType).findFirst();
          if (existingEffect.isPresent()) {
            existingEffect.get().setRemainingTicks(effectType.getDuration());
          } else {
            enemy.getEffects().add(new Effect(enemy, effectType, projectile.getOwner()));
          }
        }

        damage = Math.min(enemy.getHealth(), damage);
        enemy.setHealth(enemy.getHealth() - damage);
        enemy.addDamageToMap(projectile.getOwner(), damage);
        if (enemy.getHealth() <= 0) {
          enemiesKilled++;

          for (var entry : enemy.getDamageMap().entrySet()) {
            world.setMoney(entry.getKey(), world.getMoney(entry.getKey()) + (int)Math.round(enemy.getMoneyReward() * ((double)entry.getValue() / enemy.getType().getHealth())));
          }
        //  world.setMoney("player", world.getMoney("player") + enemy.getMoneyReward());
          enemyDeath(enemy);
          world.getEnemies().remove(enemy);
        }
      }
    }
    for (Projectile projectile : removedProjectiles) {
      world.getProjectiles().remove(projectile);
    }
  }

  private void enemySequence() {
    List<Enemy> removedEnemies = new ArrayList<>();
    for (Enemy enemy : world.getEnemies()) {
      if (enemy.isDead()) {
        continue;
      }

      double speed = enemy.getVelocity() * enemy.getEffects()
          .stream().mapToDouble((effect) -> effect.getType().getSpeedMultiplier())
          .reduce(1, (a, b) -> a * b);

      double remainingDistance = speed * deltaTime;
      while (remainingDistance > EPS && !enemy.getTrajectory().isEmpty()) {
        double dist = Vector2.distance(enemy.getPosition(), enemy.getTrajectory().get(0));
        if (Double.compare(remainingDistance, dist) >= 0) {
          // enemy reaches next vertex
          remainingDistance -= dist;
          enemy.setPosition(enemy.getTrajectory().get(0));
          enemy.getTrajectory().remove(0);
        } else {
          // enemy does not reach next vertex
          enemy.setPosition(Vector2.add(enemy.getPosition(), Vector2
              .multiply(remainingDistance / dist,
                  Vector2.subtract(enemy.getTrajectory().get(0), enemy.getPosition()))));
          remainingDistance = 0;
        }
      }

      if (Vector2.distance(enemy.getPosition(), world.getBase().getPosition()) < EPS) {
        int damage = enemy.getType().getDamage();

        world.getBase().setHealth(Math.max(world.getBase().getHealth() - damage, 0));

        if (world.getBase().getHealth() <= 0) {
          removedEnemies.add(enemy);
          GameStateWriter.getInstance().endFrame();
          GameStateWriter.getInstance().close();
          if (worldObserver != null) {
            worldObserver.onDefeat(); // todo fix temporary solution
          }
        } else {
          removedEnemies.add(enemy); // not sure if I should leave this
          enemyDeath(enemy);
        }

      }

      for (Effect effect : enemy.getEffects()) {
        int effectDamage = effect.getType().getDamagePerTick() * deltaTime;
        effectDamage = Math.min(enemy.getHealth(), effectDamage);

        enemy.setHealth(enemy.getHealth() - effectDamage);
        enemy.addDamageToMap(effect.getOwner(), effectDamage);

        if (enemy.getHealth() <= 0) {
          enemiesKilled++;
          for (var entry : enemy.getDamageMap().entrySet()) {
            world.setMoney(entry.getKey(), world.getMoney(entry.getKey()) + (int)Math.round(enemy.getMoneyReward() * ((double)entry.getValue() / enemy.getType().getHealth())));
          }
          enemyDeath(enemy);
          removedEnemies.add(enemy);
        }
      }

      for (Effect effect : enemy.getEffects()) {
        effect.setRemainingTicks(effect.getRemainingTicks() - deltaTime);
      }

      enemy.getEffects().removeAll(enemy.getEffects().stream()
          .filter((effect -> effect.getRemainingTicks() <= 0))
          .collect(Collectors.toList()));
    }
    for (Enemy enemy : removedEnemies) {
      world.getEnemies().remove(enemy);
    }
  }

  private void towerSequence() {

    for (Tower tower : world.getTowers()) {
      if (tower.getTarget() == null || tower.getTarget().isDead()
          || Vector2.distance(tower.getPosition(), tower.getTarget().getPosition()) > tower
          .getType().getRange()) {
        tower.setTarget(null);
        // finds the closest enemy for now
        List<Enemy> candidates = world.getEnemies().stream()
            .filter((enemy -> Vector2.distance(tower.getPosition(), enemy.getPosition()) <= tower
                .getType().getRange()))
            .collect(Collectors.toList());
        if (candidates.isEmpty()) {
          continue;
        }
        switch (tower.getMode()) {
          case Nearest:
            for (Enemy enemy : candidates) {
              double dist = Vector2.distance(tower.getPosition(), enemy.getPosition());
              if (tower.getTarget() == null
                  || dist < Vector2
                  .distance(tower.getPosition(), tower.getTarget().getPosition())) {
                tower.setTarget(enemy);
              }
            }
            break;
          case Farthest:
            for (Enemy enemy : candidates) {
              double dist = Vector2.distance(tower.getPosition(), enemy.getPosition());
              if (tower.getTarget() == null
                  || dist > Vector2
                  .distance(tower.getPosition(), tower.getTarget().getPosition())) {
                tower.setTarget(enemy);
              }
            }
            break;
          case Random:
            Random random = new Random();
            tower.setTarget(candidates.get(random.nextInt(candidates.size())));
            break;
          case Strongest:
            tower.setTarget(
                candidates.stream().max(Comparator.comparingInt(Enemy::getHealth)).get());
            break;
          case Weakest:
            tower.setTarget(
                candidates.stream().min(Comparator.comparingInt(Enemy::getHealth)).get());
            break;
          case First:
            tower.setTarget(
                candidates.stream().min(Comparator.comparingDouble(this::distanceToFinish)).get());
            break;
          case Last:
            tower.setTarget(
                candidates.stream().max(Comparator.comparingDouble(this::distanceToFinish)).get());
            break;
          default:
            tower.setTarget(candidates.get(0)); // Just in case
        }


      }

      if (tower.getTarget() != null) {
        switch (tower.getType().getFireType()) {
          case UNIDIRECTIONAL:
            Vector2<Double> direction = new Vector2<>(
                tower.getTarget().getPosition().getX() - tower.getPosition().getX(),
                tower.getTarget().getPosition().getY() - tower.getPosition().getY());

            double targetAngle = Math
                .toDegrees(Math.atan2(direction.getY(), direction.getX())) + 90;

            tower.setRotation(getNewDirection(tower.getRotation() - 90, tower.getPosition(),
                tower.getTarget().getPosition(), tower.getType().getRotationSpeed()) + 90);

            if (tower.getCooldown() <= 0 && tower.getRotation() == targetAngle) {

              ProjectileType projectileType = GameMetaData.getInstance()
                  .getProjectileType(tower.getType().getProjectileType());

              direction = Vector2.multiply(projectileType.getSpeed() / Vector2.distance(
                  tower.getTarget().getPosition(), tower.getPosition()), direction);

              Projectile projectile = new Projectile(
                  tower.getTarget(), tower.getType().getRange(), projectileType,
                  new Vector2<>((double) tower.getCell().getX(), (double) tower.getCell().getY()),
                  direction, tower);
              projectile.setParentPosition(new Vector2<>(tower.getPosition()));
              projectile.setOwner(tower.getOwner());
              projectile.setRotation(
                  Math.toDegrees(Math.atan2(direction.getY(), direction.getX())) + 90);
              projectile.setFireType(FireType.UNIDIRECTIONAL);
              world.getProjectiles().add(projectile);
              tower.setCooldown(tower.getType().getFireRate() + tower.getCooldown());
            }
            break;
          case OMNIDIRECTIONAL:
            if (tower.getCooldown() <= 0) {
              ProjectileType projectileType = GameMetaData.getInstance()
                  .getProjectileType(tower.getType().getProjectileType());

              Projectile projectile = new Projectile(null, tower.getType().getRange(),
                  projectileType,
                  new Vector2<>((double) tower.getCell().getX(), (double) tower.getCell().getY()),
                  new Vector2<>(0d, 0d), tower);
              projectile.setOwner(tower.getOwner());
              projectile.setFireType(FireType.OMNIDIRECTIONAL);
              projectile.setScale(0);
              projectile.setParentPosition(new Vector2<>(tower.getPosition()));
              world.getProjectiles().add(projectile);
              tower.setCooldown(tower.getType().getFireRate() + tower.getCooldown());
            }
        }
      }
      if (tower.getCooldown() > 0) {
        tower.setCooldown(tower.getCooldown() - deltaTime);
      }
    }
  }

  private void spawnSequence() {
    if (world.getCountdown() <= 0) {
      if (gameMap.getWaves().size() > world.getCurrentWaveNumber()) {
        WaveDescription description = gameMap.getWaves().get(world.getCurrentWaveNumber());
        int enemyIndex = world.getCurrentWave().getCurrentEnemyNumber();
        Enemy enemy = null;
        for (WaveEnemies enemies : description.getEnemiesList()) {
          if (enemyIndex < enemies.getCount()) {
            Vector2<Double> spawnPosition = gameMap.getRoads().getRoad(enemies.getSpawnPosition())
                .get(0);
            Vector2<Double> coordinates = new Vector2<>(spawnPosition);
            enemy = new Enemy(
                GameMetaData.getInstance().getEnemyType(enemies.getType()),
                world.getCurrentWaveNumber(), coordinates, enemies.getMoneyReward());
            for (Vector2<Double> position : gameMap.getRoads()
                .getRoad(enemies.getSpawnPosition())) {
              enemy.getTrajectory().add(new Vector2<>(position));
            }
            break;
          } else {
            enemyIndex -= enemies.getCount();
          }
        }

        world.getEnemies().add(enemy);

        world.getCurrentWave()
            .setCurrentEnemyNumber(world.getCurrentWave().getCurrentEnemyNumber() + 1);
        int enemiesInWave = description.getEnemiesList().stream().mapToInt(WaveEnemies::getCount)
            .sum();

        if (world.getCurrentWave().getCurrentEnemyNumber() >= enemiesInWave) {
          // next wave
          world.setCountdown((int) Math
              .round(gameMap.getWaves().get(world.getCurrentWaveNumber()).getTimeTillNextWave()));
          world.setCurrentWaveNumber(world.getCurrentWaveNumber() + 1);
          if (world.getCurrentWaveNumber() < gameMap.getWaves().size()) {
            Wave wave = new Wave();
            wave.setNumber(world.getCurrentWaveNumber());
            wave.setCurrentEnemyNumber(0);
            wave.setRemainingEnemiesCount(gameMap.getWaves().get(
                world.getCurrentWaveNumber()).getEnemiesList().stream()
                .mapToInt(WaveEnemies::getCount).sum());
            wave.setDescription(gameMap.getWaves().get(
                world.getCurrentWaveNumber()));
            world.setCurrentWave(wave);

          }
        } else {
          // next enemy in this wave
          world.setCountdown((int) Math
              .round(gameMap.getWaves().get(world.getCurrentWaveNumber()).getSpawnInterval()));
        }
      }
    } else {
      world.setCountdown(world.getCountdown() - deltaTime);
    }
  }

  private double distanceToFinish(Enemy enemy) {
    double ans = 0;
    ans += Vector2.distance(enemy.getPosition(), enemy.getTrajectory().get(0));
    for (int i = 1; i < enemy.getTrajectory().size(); ++i) {
      ans += Vector2.distance(enemy.getTrajectory().get(i), enemy.getTrajectory().get(i - 1));
    }
    return ans;
  }

  private double getNewDirection(double oldDirection, Vector2<Double> position,
      Vector2<Double> targetPosition, double rotationSpeed) {
    Vector2<Double> direction = new Vector2<>(
        targetPosition.getX() - position.getX(),
        targetPosition.getY() - position.getY());

    double targetAngle = Math
        .toDegrees(Math.atan2(direction.getY(), direction.getX()));

    double deltaAngle = (targetAngle - oldDirection + 720.0) % 360.0;

    if (deltaAngle <= rotationSpeed || 360.0 - deltaAngle <= rotationSpeed) {
      return targetAngle;
    } else {
      if (deltaAngle < 180.0) {
        return (oldDirection + rotationSpeed) % 360.0;
      } else {
        return (oldDirection - rotationSpeed + 360.0) % 360.0;
      }
    }
  }


}
