package ru.nsu.fit.towerdefense.model;

import java.util.ArrayList;
import java.util.List;
import ru.nsu.fit.towerdefense.model.map.RoadDescription;
import ru.nsu.fit.towerdefense.model.map.WaveEnemies;
import ru.nsu.fit.towerdefense.model.util.Vector2;
import ru.nsu.fit.towerdefense.model.world.Wave;
import ru.nsu.fit.towerdefense.model.world.World;
import ru.nsu.fit.towerdefense.model.world.gameobject.Base;
import ru.nsu.fit.towerdefense.model.world.gameobject.Enemy;
import ru.nsu.fit.towerdefense.model.world.gameobject.Projectile;
import ru.nsu.fit.towerdefense.model.world.gameobject.Tower;
import ru.nsu.fit.towerdefense.model.map.GameMap;
import ru.nsu.fit.towerdefense.model.map.WaveDescription;
import ru.nsu.fit.towerdefense.model.world.gameobject.TowerPlatform;
import ru.nsu.fit.towerdefense.model.world.types.ProjectileType;

public class WorldControl {
  private static final double DELTA = 0.001;

  private final GameMap gameMap;
  private final WorldObserver worldObserver; // todo use it
  private World world;

  private GameMetaData gameMetaData;

  public WorldControl(GameMap gameMap, WorldObserver worldObserver) {
    this.gameMap = gameMap;
    this.worldObserver = worldObserver;

    world = new World();

    for (Vector2<Integer> position : gameMap.getPositions().getPositions()) {
      Vector2<Integer> coordinates = new Vector2<>(position.getX(), position.getY());
      world.getTowerPlatforms().add(new TowerPlatform(coordinates, "circle")); // todo image name
    }

    Wave wave = new Wave();
    wave.setCurrentEnemyNumber(0);
    wave.setRemainingEnemiesCount(gameMap.getWaves().get(0).getEnemiesList().stream().mapToInt(WaveEnemies::getCount).sum());
    wave.setDescription(gameMap.getWaves().get(0));
    world.setCurrentWave(wave);
    world.setMoney(100); // todo starting money


    Base base = new Base(gameMap.getBaseDescription().getHealth(), gameMap.getBaseDescription().getImage(),
        new Vector2<>(gameMap.getBaseDescription().getPosition().getX(), gameMap.getBaseDescription().getPosition().getY()));
    world.setBase(base);
  }

  public World getWorld() {
    return world;
  }

  public void simulateTick(int deltaTime) {
    deltaTime = 1; // DEBUG! todo remove



    //System.out.println(deltaTime);
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
        if (tower.getCooldown() <= 0) {

          ProjectileType projectileType = gameMetaData.getProjectileType(tower.getType().getProjectileType());
          world.getProjectiles().add(new Projectile(
              tower.getTarget(), tower.getType().getRange(), projectileType,
              new Vector2<>(
                  tower.getCell().getX() + 0.5, tower.getCell().getY() + 0.5),
              new Vector2<>(projectileType.getSpeed() * Math.cos(tower.getRotation()),
                  projectileType.getSpeed() * Math.sin(tower.getRotation()))));
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
      projectile.getPosition().setX(projectile.getPosition().getY() + deltaTime * projectile.getVelocity().getY());
      projectile.setRemainingRange(projectile.getRemainingRange() - projectile.getType().getSpeed() * deltaTime);
      if (projectile.getRemainingRange() <= 0) {
        removedProjectiles.add(projectile);
        continue;
      }

      Enemy collidedEnemy = null;
      // Collision checking
      for (Enemy enemy : world.getEnemies()) {
        // TODO hit box size
        if (distance(enemy.getPosition(), projectile.getPosition()) < enemy.getSize().getX()) {
          collidedEnemy = enemy;
          break;
        }
      }

      if (collidedEnemy != null) {
        int damage = projectile.getType().getEnemyTypeDamageMap().get(collidedEnemy.getType().getTypeName());

        removedProjectiles.add(projectile);

        collidedEnemy.setHealth(collidedEnemy.getHealth() - damage);
        if (collidedEnemy.getHealth() <= 0) {
          enemyDeath(collidedEnemy);
        }
      }
    }
    for (Projectile projectile : removedProjectiles) {
      world.getProjectiles().remove(projectile);
    }

  //  System.out.println("---------------" + world.getEnemies().size());
    for (Enemy enemy : world.getEnemies()) {
      if (enemy.isDead()) continue;
      //System.out.println(enemy.getPosition());

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

        world.getBase().setHealth(world.getBase().getHealth() - damage);
        if (world.getBase().getHealth() <= 0) {
          finish();
        } else {
          enemyDeath(enemy);
        }

      }
    }

    if (world.getCountdown() <= 0) {
      if (gameMap.getWaves().size() > world.getCurrentWaveNumber()) {
        WaveDescription description = gameMap.getWaves().get(world.getCurrentWaveNumber());
        int enemyIndex = world.getCurrentWave().getCurrentEnemyNumber();
        Enemy enemy = null;
        for (WaveEnemies enemies : description.getEnemiesList()) {
          if (enemyIndex < enemies.getCount()) {
            //-------------------------------------------------------------
         /* int spawnPositionIndex = enemies.getSpawnPosition();
          RoadDescription roads = gameMap.getRoads();
          List<Vector2<Double>> road = roads.getRoad(spawnPositionIndex);
          Vector2<Double> pos = road.get(0);
          Wave wave = world.getCurrentWave();
          String enemyTypeName = enemies.getType();*/
            //-------------------------------------------------------------
            Vector2<Double> spawnPosition = gameMap.getRoads().getRoad(enemies.getSpawnPosition()).get(0);
            Vector2<Double> coordinates = new Vector2<>(spawnPosition.getX(), spawnPosition.getY());
            enemy = new Enemy(
                GameMetaData.getInstance().getEnemyType(enemies.getType()), world.getCurrentWave(), coordinates);
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
        System.out.println("Spawn!" + enemy.getPosition());

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
            world.getCurrentWave().setCurrentEnemyNumber(0);
            world.getCurrentWave().setRemainingEnemiesCount(gameMap.getWaves().get(
                world.getCurrentWaveNumber()).getEnemiesList().stream()
                .mapToInt(WaveEnemies::getCount).sum());
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

  private void enemyDeath(Enemy enemy) {
    world.getEnemies().remove(enemy);
    Wave wave = enemy.getWave();
    wave.setRemainingEnemiesCount(wave.getRemainingEnemiesCount() - 1);
    if (wave.getRemainingEnemiesCount() == 0) {
      world.setMoney(world.getMoney() + wave.getDescription().getMoneyReward());
    }
  }

  private void finish() {

  }

  private double distance(Vector2<Double> a, Vector2<Double> b) {
    return Math.sqrt(Math.pow(a.getX() - b.getX(), 2) + Math.pow(a.getY() - b.getY() , 2));
  }
}
