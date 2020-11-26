package ru.nsu.fit.towerdefense.model.world;

import java.util.ArrayList;
import java.util.List;
import ru.nsu.fit.towerdefense.model.world.gameobject.Enemy;
import ru.nsu.fit.towerdefense.model.world.gameobject.Projectile;
import ru.nsu.fit.towerdefense.model.world.gameobject.Tower;
import ru.nsu.fit.towerdefense.model.world.map.WaveDescription;
import ru.nsu.fit.towerdefense.model.world.types.ProjectileType;

public class WorldControl {
  private static final double DELTA = 0.1;

  private World world;

  public World getWorld() {
    return world;
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
        if (tower.getCooldown() == 0) {
          ProjectileType projectileType = tower.getType().getProjectileType();
          world.getProjectiles().add(new Projectile(
              tower.getTarget(), tower.getType().getRange(), projectileType,
              new Vector2<>(
                  tower.getCell().getX() + 0.5, tower.getCell().getY() + 0.5),
              new Vector2<>(projectileType.getSpeed() * Math.cos(tower.getRotation()),
                  projectileType.getSpeed() * Math.sin(tower.getRotation()))));
          tower.setCooldown(tower.getType().getFireRate());
        }
      }

      if (tower.getCooldown() != 0) {
        tower.setCooldown(tower.getCooldown() - 1);
      }
    }

    List<Projectile> removedProjectiles = new ArrayList<>();
    for (Projectile projectile : world.getProjectiles()) {
      if (projectile.getType().isSelfGuided() && !projectile.getTarget().isDead()) {
        Vector2<Double> heh = new Vector2<>(
            projectile.getTarget().getPosition().getX() - projectile.getPosition().getX(),
            projectile.getTarget().getPosition().getY() - projectile.getPosition().getY());
        // rotate the bullet
      }

      if (false /* check for collisions */) {
        Enemy enemy = null; // get this enemy somehow
        int damage = projectile.getType().getEnemyTypeDamageMap().get(enemy.getType());

        removedProjectiles.add(projectile);

        enemy.setHealth(enemy.getHealth() - damage);
        if (enemy.getHealth() <= 0) {
          enemyDeath(enemy);
        }
      }
    }
    for (Projectile projectile : removedProjectiles) {
      world.getProjectiles().remove(projectile);
    }



    for (Enemy enemy : world.getEnemies()) {
      if (enemy.isDead()) continue;

      // get effects & update health

      // Trajectory!

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

    if (world.getCountdown() == 0) {
      WaveDescription description = null;
      Enemy enemy = null;
      // resolve hpw to call from map
    } else {
      world.setCountdown(world.getCountdown() - 1);
    }

  }

  private void enemyDeath(Enemy enemy) {
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
