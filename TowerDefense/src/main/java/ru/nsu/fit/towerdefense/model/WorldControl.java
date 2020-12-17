package ru.nsu.fit.towerdefense.model;

import java.util.ArrayList;
import java.util.List;
import ru.nsu.fit.towerdefense.model.exceptions.GameplayException;
import ru.nsu.fit.towerdefense.model.map.WaveEnemies;
import ru.nsu.fit.towerdefense.model.util.Vector2;
import ru.nsu.fit.towerdefense.model.world.Wave;
import ru.nsu.fit.towerdefense.model.world.World;
import ru.nsu.fit.towerdefense.model.world.gameobject.Base;
import ru.nsu.fit.towerdefense.model.world.gameobject.Enemy;
import ru.nsu.fit.towerdefense.model.world.gameobject.Portal;
import ru.nsu.fit.towerdefense.model.world.gameobject.Projectile;
import ru.nsu.fit.towerdefense.model.world.gameobject.RoadTile;
import ru.nsu.fit.towerdefense.model.world.gameobject.Tower;
import ru.nsu.fit.towerdefense.model.map.GameMap;
import ru.nsu.fit.towerdefense.model.map.WaveDescription;
import ru.nsu.fit.towerdefense.model.world.gameobject.TowerPlatform;
import ru.nsu.fit.towerdefense.model.world.types.ProjectileType;
import ru.nsu.fit.towerdefense.model.world.types.TowerType;
import ru.nsu.fit.towerdefense.model.world.types.TowerType.Upgrade;

public class WorldControl {
  List<Tower> newTowers = new ArrayList<>();

  public void buildTower(TowerPlatform towerPlatform, TowerType towerType)
      throws GameplayException {
    if (world.getMoney() < towerType.getPrice()) {
      throw new GameplayException("Not enough money to build the tower");
    }
    Tower tower = new Tower();
    tower.setPosition(new Vector2<>((int)Math.round(towerPlatform.getPosition().getX()),
        (int)Math.round(towerPlatform.getPosition().getY())));
    tower.setType(towerType);
    tower.setRotation(0);
    tower.setCooldown(towerType.getFireRate());
    newTowers.add(tower);
  }

  public void upgradeTower(Tower tower, Upgrade upgrade) throws GameplayException {
    if (world.getMoney() < upgrade.getCost()) {
      throw new GameplayException("Not enough money to upgrade the tower");
    }

    TowerType type = GameMetaData.getInstance().getTowerType(upgrade.getName());
    tower.setType(type);
    tower.setCooldown(type.getFireRate());
    tower.setTarget(null);
  }

  public void tuneTower(Tower tower, Tower.Mode towerMode) {
    tower.setMode(towerMode);
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
    world.setMoney(100); // todo starting money


    Base base = new Base(gameMap.getBaseDescription().getHealth(), gameMap.getBaseDescription().getImage(),
        new Vector2<>(gameMap.getBaseDescription().getPosition().getX(), gameMap.getBaseDescription().getPosition().getY()));
    world.setBase(base);

    Tower tower = new Tower(); // DEBUG! todo remove
    tower.setType(GameMetaData.getInstance().getTowerType("Archer"));
    tower.setCooldown(0);
    tower.setRotation(0);
    tower.setPosition(new Vector2<>(3, 4));
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


    for (Tower tower : world.getTowers()) {
      if (tower.getTarget() == null || tower.getTarget().isDead()
          || distance(tower.getPosition(), tower.getTarget().getPosition()) > tower.getType().getRange()) {
        tower.setTarget(null);
        // finds the closest enemy for now
        for (Enemy enemy : world.getEnemies()) {
          double dist = distance(tower.getPosition(), enemy.getPosition());
          if (dist > tower.getType().getRange()) {
            continue;
          }
          if (tower.getTarget() == null
              || dist < distance(tower.getPosition(), tower.getTarget().getPosition())) {
            tower.setTarget(enemy);
          }
        }
      }


      if (tower.getTarget() != null) {
        Vector2<Double> direction = new Vector2<>(
            tower.getTarget().getPosition().getX() - tower.getPosition().getX(),
            tower.getTarget().getPosition().getY() - tower.getPosition().getY());

        double targetAngle = Math.toDegrees(Math.atan2(direction.getY(), direction.getX()) + Math.PI / 2);

        double deltaAngle = (targetAngle - tower.getRotation() + 360.0) % 360.0;

        if (deltaAngle <= debugRotationSpeed || 360.0 - deltaAngle <= debugRotationSpeed) {
          tower.setRotation(targetAngle);
        } else {
          if (deltaAngle < 180.0) {
            tower.setRotation((tower.getRotation() + debugRotationSpeed) % 360.0);
          } else {
            tower.setRotation((tower.getRotation() - debugRotationSpeed + 360.0) % 360.0);
          }
        }

        //tower.setRotation(targetAngle);

        if (tower.getCooldown() <= 0 && tower.getRotation() == targetAngle) {

          ProjectileType projectileType = GameMetaData.getInstance().getProjectileType(tower.getType().getProjectileType());

          // Instant rotation for now

          direction.setX(direction.getX() * projectileType.getSpeed() / distance(tower.getTarget().getPosition(), tower.getPosition()));
          direction.setY(direction.getY() * projectileType.getSpeed() / distance(tower.getTarget().getPosition(), tower.getPosition()));

          world.getProjectiles().add(new Projectile(
              tower.getTarget(), tower.getType().getRange(), projectileType,
              new Vector2<>(
                  (double)tower.getCell().getX()/* + 0.5*/, (double)tower.getCell().getY() /*+ 0.5*/),
             // new Vector2<>(projectileType.getSpeed() * Math.cos(tower.getRotation()),
             //     projectileType.getSpeed() * Math.sin(tower.getRotation())))
              direction
          ));
          tower.setCooldown(tower.getType().getFireRate() + tower.getCooldown());
        }
      }

      if (tower.getCooldown() > 0) {
        tower.setCooldown(tower.getCooldown() - deltaTime);
      }
    }

    List<Projectile> removedProjectiles = new ArrayList<>();
    for (Projectile projectile : world.getProjectiles()) {
      if (projectile.getType().isSelfGuided() && !projectile.getTarget().isDead()) {
        Vector2<Double> newDirection = new Vector2<>(
            projectile.getTarget().getPosition().getX() - projectile.getPosition().getX(),
            projectile.getTarget().getPosition().getY() - projectile.getPosition().getY());

        double length = distance(projectile.getPosition(), projectile.getTarget().getPosition());
        newDirection.setX(newDirection.getX() / length * projectile.getType().getSpeed());
        newDirection.setY(newDirection.getY() / length * projectile.getType().getSpeed());

        projectile.setVelocity(newDirection);
      }

      projectile.getPosition().setX(projectile.getPosition().getX() + deltaTime * projectile.getVelocity().getX());
      projectile.getPosition().setY(projectile.getPosition().getY() + deltaTime * projectile.getVelocity().getY());
      projectile.setRemainingRange(projectile.getRemainingRange() - projectile.getType().getSpeed() * deltaTime);
      if (projectile.getRemainingRange() <= 0) {
        removedProjectiles.add(projectile);
        continue;
      }

      Enemy collidedEnemy = null;
      // Collision checking
      for (Enemy enemy : world.getEnemies()) {
        if (distance(enemy.getPosition(), projectile.getPosition()) < enemy.getType().getHitBox()) {
          collidedEnemy = enemy;

          break;
        }
      }

      if (collidedEnemy != null) {
        int damage = projectile.getType().getEnemyTypeDamageMap().get(collidedEnemy.getType().getTypeName());

        removedProjectiles.add(projectile);

        collidedEnemy.setHealth(collidedEnemy.getHealth() - damage);
        if (collidedEnemy.getHealth() <= 0) {
          enemiesKilled++;
          world.setMoney(world.getMoney() + collidedEnemy.getMoneyReward());
          enemyDeath(collidedEnemy);
          world.getEnemies().remove(collidedEnemy);
        }
      }
    }
    for (Projectile projectile : removedProjectiles) {
      world.getProjectiles().remove(projectile);
    }

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
            System.out.println(world.getCurrentWaveNumber());
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

    world.getTowers().addAll(newTowers);
    newTowers.clear();

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
}
