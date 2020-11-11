package ru.nsu.fit.towerdefense.model.world;

import ru.nsu.fit.towerdefense.model.world.gameobject.Projectile;
import ru.nsu.fit.towerdefense.model.world.gameobject.Tower;
import ru.nsu.fit.towerdefense.model.world.types.ProjectileType;

public class WorldControl {
  private World world;

  public World getWorld() {
    return world;
  }

  public void simulateTick() {

    for (Tower tower : world.getTowers()) {
      if (tower.getTarget() == null || tower.getTarget().isDead()) {
        // generate target
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

    for (Projectile projectile : world.getProjectiles()) {
      if (projectile.getType().isSelfGuided() && !projectile.getTarget().isDead()) {
        Vector2<Double> heh = new Vector2<>(projectile.getTarget().getPosition().getX() - projectile.getPosition().getX(), projectile.getTarget().getPosition().getY() - projectile.getPosition().getY());
        // rotate the bullet
      }
    }

  }
}
