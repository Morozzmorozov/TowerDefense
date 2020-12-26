package ru.nsu.fit.towerdefense.simulator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import ru.nsu.fit.towerdefense.metadata.GameMetaData;
import ru.nsu.fit.towerdefense.replay.GameStateWriter;
import ru.nsu.fit.towerdefense.simulator.exceptions.GameplayException;
import ru.nsu.fit.towerdefense.metadata.map.WaveEnemies;
import ru.nsu.fit.towerdefense.replay.WorldState;
import ru.nsu.fit.towerdefense.util.Vector2;
import ru.nsu.fit.towerdefense.simulator.world.Wave;
import ru.nsu.fit.towerdefense.simulator.world.World;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.Base;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.Enemy;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.Portal;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.Projectile;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.RoadTile;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.Tower;
import ru.nsu.fit.towerdefense.metadata.map.GameMap;
import ru.nsu.fit.towerdefense.metadata.map.WaveDescription;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.TowerPlatform;
import ru.nsu.fit.towerdefense.metadata.gameobjecttypes.ProjectileType;
import ru.nsu.fit.towerdefense.metadata.gameobjecttypes.TowerType;
import ru.nsu.fit.towerdefense.metadata.gameobjecttypes.TowerType.FireType;
import ru.nsu.fit.towerdefense.metadata.gameobjecttypes.TowerType.Upgrade;

public class WorldControl {

  private final int DEBUG_MONEY = 600;
  private final float SELL_MULTIPLIER = 0.4f;

  public TowerPlatform sellTower(Tower tower) {
    world.setMoney(world.getMoney() + tower.getSellPrice());
    removedTowers.add(tower);
    for (TowerPlatform platform : world.getTowerPlatforms()) {
      if (Math.abs(tower.getPosition().getX() - platform.getPosition().getX()) < DELTA
        && Math.abs(tower.getPosition().getY() - platform.getPosition().getY()) < DELTA) {
        return platform;
      }
    }
    return null;
  }

  public Tower buildTower(TowerPlatform towerPlatform, TowerType towerType)
      throws GameplayException {
    if (world.getMoney() < towerType.getPrice()) {
      throw new GameplayException("Not enough money to build the tower");
    }
    world.setMoney(world.getMoney() - towerType.getPrice());
    Tower tower = new Tower();
    tower.setPosition(new Vector2<>((int)Math.round(towerPlatform.getPosition().getX()),
        (int)Math.round(towerPlatform.getPosition().getY())));
    tower.setType(towerType);
    tower.setRotation(0);
    tower.setCooldown(towerType.getFireRate());
    tower.setSellPrice(Math.round(towerType.getPrice() * SELL_MULTIPLIER));
    newTowers.add(tower);
    GameStateWriter.getInstance().buildTower(world.getTowerPlatforms().indexOf(towerPlatform), towerType.getTypeName());
    return tower;
  }

  public void upgradeTower(Tower tower, Upgrade upgrade) throws GameplayException {
    if (world.getMoney() < upgrade.getCost()) {
      throw new GameplayException("Not enough money to upgrade the tower");
    }
    world.setMoney(world.getMoney() - upgrade.getCost());
    TowerType type = GameMetaData.getInstance().getTowerType(upgrade.getName());
    tower.setType(type);
    tower.setCooldown(type.getFireRate());
    tower.setTarget(null);
    tower.setSellPrice(tower.getSellPrice() + Math.round(upgrade.getCost() + SELL_MULTIPLIER));
    GameStateWriter.getInstance().upgradeTower(world.getTowers().indexOf(tower), upgrade.getName()); // todo ask about platform
  }

  List<Tower> newTowers = new ArrayList<>();
  List<Tower> removedTowers = new ArrayList<>();

  public Tower getTowerOnPlatform(TowerPlatform towerPlatform) {
    for (Tower candidate : world.getTowers()) {
      if (!removedTowers.contains(candidate)
          && Math.abs(candidate.getPosition().getX() - towerPlatform.getPosition().getX()) < DELTA
          && Math.abs(candidate.getPosition().getY() - towerPlatform.getPosition().getY()) < DELTA) {
        return candidate;
      }
    }
    return null;
  }

  public void tuneTower(Tower tower, Tower.Mode towerMode) {
    tower.setMode(towerMode);
    tower.setTarget(null);
    GameStateWriter.getInstance().switchMode(tower, towerMode);
  }

  /**
   * Returns remaining ticks till the next wave (must be > 0).
   *
   * In case this info is not yet available - returns 0.
   *
   * @return remaining ticks till the next wave.
   */
  public long getTicksTillNextWave() { // todo
    if (world.getCurrentWave().getCurrentEnemyNumber() == 0) {
      return world.getCountdown();
    } else {
      return 0;
    }
  }
  private static final double DELTA = 0.001;

  private final GameMap gameMap;
  private final int deltaTime;
  private final WorldObserver worldObserver; // todo use it
  private World world;

  private GameMetaData gameMetaData;

  private int enemiesKilled = 0;
  private int wavesDefeated = 0;

  private double debugRotationSpeed = 3.0;

  public WorldControl(GameMap gameMap, WorldState worldState, int deltaTime, WorldObserver worldObserver) {
    // todo init worldState

    this.gameMap = gameMap;
    this.deltaTime = 1; // DEBUG! todo remove
//    this.deltaTime = deltaTime; // DEBUG! todo uncomment
    this.worldObserver = worldObserver;
  }

  public WorldControl(GameMap gameMap, int deltaTime, WorldObserver worldObserver) {
    this.gameMap = gameMap;
    this.deltaTime = 1; // DEBUG! todo remove
//    this.deltaTime = deltaTime; // DEBUG! todo uncomment
    this.worldObserver = worldObserver;

    world = new World();

    for (Vector2<Integer> position : gameMap.getPositions().getPositions()) {
      Vector2<Integer> coordinates = new Vector2<>(position.getX(), position.getY());
      world.getTowerPlatforms().add(new TowerPlatform(coordinates, "platform.png")); // todo image name
    }

    Wave wave = new Wave();
    wave.setNumber(0);
    wave.setCurrentEnemyNumber(0);
    wave.setRemainingEnemiesCount(gameMap.getWaves().get(0).getEnemiesList().stream().mapToInt(WaveEnemies::getCount).sum());
    wave.setDescription(gameMap.getWaves().get(0));
    world.setCurrentWave(wave);
    world.setMoney(DEBUG_MONEY); // todo starting money


    Base base = new Base(gameMap.getBaseDescription().getHealth(), gameMap.getBaseDescription().getImage(),
        new Vector2<>(gameMap.getBaseDescription().getPosition().getX(), gameMap.getBaseDescription().getPosition().getY()));
    world.setBase(base);

    Tower tower = new Tower(); // DEBUG! todo remove
    tower.setType(GameMetaData.getInstance().getTowerType("RocketLauncher"));
    tower.setCooldown(0);
    tower.setRotation(0);
    tower.setPosition(new Vector2<>(3, 4));
    tower.setSellPrice(Math.round(tower.getType().getPrice() * SELL_MULTIPLIER));
    world.getTowers().add(tower);


    for (int i = 0; i < gameMap.getRoads().getRoadCount(); ++i) {
      List<Vector2<Double>> road = gameMap.getRoads().getRoad(i);

      world.getPortals().add(new Portal("portal.png", new Vector2<>(
          (int)Math.round(road.get(0).getX()), (int)Math.round(road.get(0).getY()))));

      for (int j = 0; j < road.size() - 1; ++j) { // all nodes except last
        int x1 = (int)Math.round(road.get(j).getX());
        int x2 = (int)Math.round(road.get(j + 1).getX());
        int y1 = (int)Math.round(road.get(j).getY());
        int y2 = (int)Math.round(road.get(j + 1).getY());


        if (x1 == x2) { // vertical
          for (int y = Integer.min(y1, y2); y <= Integer.max(y1, y2); ++y) {
            boolean flag = true;
            for (RoadTile roadTile : world.getRoadTiles()) {
              if (Math.round(roadTile.getPosition().getX()) == x1 && Math.round(roadTile.getPosition().getY()) == y) {
                flag = false;
                break;
              }
            }
            if (flag) {
              world.getRoadTiles().add(new RoadTile("road.png", new Vector2<>(x1, y)));
            }
          }
        } else if (y1 == y2) { // horizontal
          for (int x = Integer.min(x1, x2); x <= Integer.max(x1, x2); ++x) {
            boolean flag = true;
            for (RoadTile roadTile : world.getRoadTiles()) {
              if (Math.round(roadTile.getPosition().getX()) == x && Math.round(roadTile.getPosition().getY()) == y1) {
                flag = false;
                break;
              }
            }
            if (flag) {
              world.getRoadTiles().add(new RoadTile("road.png", new Vector2<>(x, y1)));
            }
          }
        }
      }
    }
  }

  public World getWorld() {
    return world;
  }

  public int getResearchPoints() {
    return gameMap.getScienceReward();
  }

  public int getBaseHealth() {
    return world.getBase().getHealth();
  }

  public int getEnemiesKilled() {
    return enemiesKilled;
  }

  public int getMoney() {
    return world.getMoney();
  }

  public int getWaveNumber() {
    if (world.getCurrentWaveNumber() < gameMap.getWaves().size()) {
      return world.getCurrentWaveNumber() + 1;
    }
    return world.getCurrentWaveNumber();
  }

  private long tick;
  public long getTick() {
    return tick;
  }

  public int getWavesDefeated() {
    return wavesDefeated;
  }

  public void simulateTick() {
    towerSequence();
    projectileSequence();
    enemySequence();
    spawnSequence();

    world.getTowers().addAll(newTowers);
    world.getTowers().removeAll(removedTowers);
    newTowers.clear();
    removedTowers.clear();

    ++tick;
  }

  private void enemyDeath(Enemy enemy) {
    enemy.setDead(true);
    Wave wave = enemy.getWave();
    wave.setRemainingEnemiesCount(wave.getRemainingEnemiesCount() - 1);
    if (wave.getRemainingEnemiesCount() == 0) {
      wavesDefeated++;

      //world.setMoney(world.getMoney() + wave.getDescription().getMoneyReward()); TODO fix me
      if ((world.getEnemies().isEmpty() || (world.getEnemies().contains(enemy) && world.getEnemies().size() == 1))
          && world.getCurrentWaveNumber() >= gameMap.getWaves().size()) {
        worldObserver.onVictory();
      }
    }
  }

  private double distance(Vector2<Double> a, Vector2<Double> b) {
    return Math.sqrt(Math.pow(a.getX() - b.getX(), 2) + Math.pow(a.getY() - b.getY() , 2));
  }


  private void projectileSequence() {
    List<Projectile> removedProjectiles = new ArrayList<>();
    for (Projectile projectile : world.getProjectiles()) {
      if (projectile.getTarget() != null && projectile.getType().isSelfGuided() && projectile.getTarget().isDead()) {
        Enemy newEnemy = null;
        double dist = 0;
        for (Enemy enemy : world.getEnemies()) {
          if (newEnemy == null
              || distance(projectile.getPosition(), enemy.getPosition()) < dist) {
            newEnemy = enemy;
            dist = distance(projectile.getPosition(), enemy.getPosition());
          }
        }
        if (newEnemy != null) {
          projectile.setTarget(newEnemy);
        }
      }
      if (projectile.getParent().getType().getFireType().equals(FireType.UNIDIRECTIONAL)
          && projectile.getType().isSelfGuided() && !projectile.getTarget().isDead()) {

        double newRotation = getNewDirection(projectile.getRotation() - 90, projectile.getPosition(), projectile.getTarget().getPosition(), projectile.getRotationSpeed()) + 90;

        Vector2<Double> newDirection = new Vector2<>(
            projectile.getType().getSpeed() * Math.cos(Math.toRadians(newRotation - 90)),
            projectile.getType().getSpeed() * Math.sin(Math.toRadians(newRotation - 90)));

        projectile.setVelocity(newDirection);

        projectile.setRotation(newRotation);
      }

      List<Enemy> affectedEnemies = new ArrayList<>();

      switch (projectile.getParent().getType().getFireType()) {
        case UNIDIRECTIONAL:
          Vector2<Double> newPosition = new Vector2<>(
              projectile.getPosition().getX() + deltaTime * projectile.getVelocity().getX(),
              projectile.getPosition().getY() + deltaTime * projectile.getVelocity().getY());
          for (Enemy enemy : world.getEnemies()) {
            // Collision checking
            Vector2<Double> oldVectorToEnemy = new Vector2<>(enemy.getPosition().getX() - projectile.getPosition().getX(),
                enemy.getPosition().getY() - projectile.getPosition().getY());
            Vector2<Double> pathVector = new Vector2<>(
                newPosition.getX() - projectile.getPosition().getX(),
                newPosition.getY() - projectile.getPosition().getY());

            double angle = Math.acos((oldVectorToEnemy.getX() * pathVector.getX() + oldVectorToEnemy.getY() + pathVector.getY())
                / distance(newPosition, projectile.getPosition()) / distance(projectile.getPosition(), enemy.getPosition()));

            double encounterDistance = Math.sin(angle) * distance(enemy.getPosition(), projectile.getPosition());

            double pathToEncounter = Math.cos(angle) * distance(enemy.getPosition(), projectile.getPosition());

            if (distance(newPosition, enemy.getPosition()) < enemy.getType().getHitBox()
                || (encounterDistance < enemy.getType().getHitBox() && pathToEncounter < projectile.getType().getSpeed())) {
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
          double newScale = projectile.getScale() + Math.min(projectile.getRemainingRange() * 2, projectile.getType().getSpeed() * 2);

          for (Enemy enemy : world.getEnemies()) {
            double distanceToEnemy = distance(projectile.getParent().getPosition(), enemy.getPosition());
            if (distanceToEnemy >= projectile.getScale() / 2 && distanceToEnemy < newScale / 2) {
              affectedEnemies.add(enemy);
            }
          }
          projectile.setRemainingRange(projectile.getRemainingRange() - projectile.getType().getSpeed());
          if (projectile.getRemainingRange() <= 0) {
            removedProjectiles.add(projectile);
          } else {
            projectile.setScale(newScale);
          }
      }

      for (Enemy enemy : affectedEnemies) {
        int damage = projectile.getType().getEnemyTypeDamageMap()
            .get(enemy.getType().getTypeName());



        enemy.setHealth(enemy.getHealth() - damage);
        if (enemy.getHealth() <= 0) {
          enemiesKilled++;
          world.setMoney(world.getMoney() + enemy.getMoneyReward());
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
      if (enemy.isDead()) continue;

      // TODO get effects & update health

      double remainingDistance = enemy.getVelocity() * deltaTime;
      while (remainingDistance > DELTA && !enemy.getTrajectory().isEmpty()) {
        double dist = distance(enemy.getPosition(), enemy.getTrajectory().get(0));
        if (Double.compare(remainingDistance, dist) >= 0) {
          // enemy reaches next vertex
          remainingDistance -= dist;
          enemy.setPosition(enemy.getTrajectory().get(0));
          enemy.getTrajectory().remove(0);
        } else {
          // enemy does not reach next vertex
          enemy.getPosition().setX(
              enemy.getPosition().getX() + (enemy.getTrajectory().get(0).getX() - enemy.getPosition().getX()) * remainingDistance / dist);
          enemy.getPosition().setY(
              enemy.getPosition().getY() + (enemy.getTrajectory().get(0).getY() - enemy.getPosition().getY()) * remainingDistance / dist);
          remainingDistance = 0;
        }
      }

      if (Math.abs(enemy.getPosition().getX() - world.getBase().getPosition().getX()) < DELTA &&
          Math.abs(enemy.getPosition().getY() - world.getBase().getPosition().getY()) < DELTA) {
        int damage = enemy.getType().getDamage();

        world.getBase().setHealth(Math.max(world.getBase().getHealth() - damage, 0));

        if (world.getBase().getHealth() <= 0) {
          removedEnemies.add(enemy);
          worldObserver.onDefeat();
        } else {
          removedEnemies.add(enemy); // not sure if I should leave this
          enemyDeath(enemy);
        }

      }
    }
    for (Enemy enemy : removedEnemies) {
      world.getEnemies().remove(enemy);
    }
  }

  private void towerSequence() {

    for (Tower tower : world.getTowers()) {
      if (tower.getTarget() == null || tower.getTarget().isDead()
          || distance(tower.getPosition(), tower.getTarget().getPosition()) > tower.getType().getRange()) {
        tower.setTarget(null);
        // finds the closest enemy for now
        List<Enemy> candidates = world.getEnemies().stream()
            .filter((enemy -> distance(tower.getPosition(), enemy.getPosition()) <= tower.getType().getRange()))
            .collect(Collectors.toList());
        if (candidates.isEmpty()) {
          continue;
        }
        switch (tower.getMode()) {
          case Nearest:
            for (Enemy enemy : candidates) {
              double dist = distance(tower.getPosition(), enemy.getPosition());
              if (tower.getTarget() == null
                  || dist < distance(tower.getPosition(), tower.getTarget().getPosition())) {
                tower.setTarget(enemy);
              }
            }
            break;
          case Farthest:
            for (Enemy enemy : candidates) {
              double dist = distance(tower.getPosition(), enemy.getPosition());
              if (tower.getTarget() == null
                  || dist > distance(tower.getPosition(), tower.getTarget().getPosition())) {
                tower.setTarget(enemy);
              }
            }
            break;
          case Random:
            Random random = new Random();
            tower.setTarget(candidates.get(random.nextInt(candidates.size())));
            break;
          case Strongest:
            tower.setTarget(candidates.stream().max(Comparator.comparingInt(Enemy::getHealth)).get());
            break;
          case Weakest:
            tower.setTarget(candidates.stream().min(Comparator.comparingInt(Enemy::getHealth)).get());
            break;
          case First:
            tower.setTarget(candidates.stream().min(Comparator.comparingDouble(this::distanceToFinish)).get());
            break;
          case Last:
            tower.setTarget(candidates.stream().max(Comparator.comparingDouble(this::distanceToFinish)).get());
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

            tower.setRotation(getNewDirection(tower.getRotation() - 90, tower.getPosition(), tower.getTarget().getPosition(), debugRotationSpeed) + 90);

            if (tower.getCooldown() <= 0 && tower.getRotation() == targetAngle) {

              ProjectileType projectileType = GameMetaData.getInstance()
                  .getProjectileType(tower.getType().getProjectileType());

              direction.setX(direction.getX() * projectileType.getSpeed() / distance(
                  tower.getTarget().getPosition(), tower.getPosition()));
              direction.setY(direction.getY() * projectileType.getSpeed() / distance(
                  tower.getTarget().getPosition(), tower.getPosition()));

              Projectile projectile = new Projectile(
                  tower.getTarget(), tower.getType().getRange(), projectileType,
                  new Vector2<>((double) tower.getCell().getX(), (double) tower.getCell().getY()),
                  direction, tower);
              projectile.setRotation(
                  Math.toDegrees(Math.atan2(direction.getY(), direction.getX())) + 90);
              world.getProjectiles().add(projectile);
              tower.setCooldown(tower.getType().getFireRate() + tower.getCooldown());
            }
            break;
          case OMNIDIRECTIONAL:
            if (tower.getCooldown() <= 0) {
              ProjectileType projectileType = GameMetaData.getInstance().getProjectileType(tower.getType().getProjectileType());

              Projectile projectile = new Projectile(null, tower.getType().getRange(),
                  projectileType, new Vector2<>((double) tower.getCell().getX(), (double) tower.getCell().getY()),
                  null, tower);
              projectile.setScale(0);
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
            Vector2<Double> spawnPosition = gameMap.getRoads().getRoad(enemies.getSpawnPosition()).get(0);
            Vector2<Double> coordinates = new Vector2<>(spawnPosition.getX(), spawnPosition.getY());
            enemy = new Enemy(
                GameMetaData.getInstance().getEnemyType(enemies.getType()), world.getCurrentWave(), coordinates, enemies.getMoneyReward());
            for (Vector2<Double> position : gameMap.getRoads()
                .getRoad(enemies.getSpawnPosition())) {
              enemy.getTrajectory().add(new Vector2<>(position.getX(), position.getY()));
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
            world.setCurrentWave(new Wave());
            world.getCurrentWave().setNumber(world.getCurrentWaveNumber());
            world.getCurrentWave().setCurrentEnemyNumber(0);
            world.getCurrentWave().setRemainingEnemiesCount(gameMap.getWaves().get(
                world.getCurrentWaveNumber()).getEnemiesList().stream()
                .mapToInt(WaveEnemies::getCount).sum());
            world.getCurrentWave().setDescription(gameMap.getWaves().get(
                world.getCurrentWaveNumber()));
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
    ans += distance(enemy.getPosition(), enemy.getTrajectory().get(0));
    for (int i = 1; i < enemy.getTrajectory().size(); ++i) {
      ans += distance(enemy.getTrajectory().get(i), enemy.getTrajectory().get(i - 1));
    }
    return ans;
  }

  private double getNewDirection(double oldDirection, Vector2<Double> position, Vector2<Double> targetPosition, double rotationSpeed) {
    Vector2<Double> direction = new Vector2<>(
        targetPosition.getX() - position.getX(),
        targetPosition.getY() - position.getY());

    double targetAngle = Math
        .toDegrees(Math.atan2(direction.getY(), direction.getX()));

    double deltaAngle = (targetAngle - oldDirection + 360.0) % 360.0;

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
