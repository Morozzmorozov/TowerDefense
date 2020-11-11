package ru.nsu.fit.towerdefense.model.world;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import ru.nsu.fit.towerdefense.model.world.gameobject.Projectile;
import ru.nsu.fit.towerdefense.model.world.gameobject.Renderable;
import ru.nsu.fit.towerdefense.model.world.gameobject.Tower;
import ru.nsu.fit.towerdefense.model.world.types.ProjectileType;

public class WorldControl implements Iterable<Renderable> {
  private World world;

  public World getWorld() {
    return world;
  }

  private class WorldControlIterator implements Iterator<Renderable> {
    private List<Iterator<? extends Renderable>> iterators;

    public WorldControlIterator() {
      iterators = new ArrayList<>();
      iterators.add(world.getEnemies().iterator());
      iterators.add(world.getProjectiles().iterator());
      iterators.add(world.getTowers().iterator());
    }

    @Override
    public boolean hasNext() {
      for (Iterator<? extends Renderable> iterator : iterators) {
        if (iterator.hasNext()) {
          return true;
        }
      }
      return false;
    }

    @Override
    public Renderable next() {
      for (Iterator<? extends Renderable> iterator : iterators) {
        if (iterator.hasNext()) {
          return iterator.next();
        }
      }
      throw new NoSuchElementException();
    }
  }

  @Override
  public Iterator<Renderable> iterator() {
    return new WorldControlIterator();
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
                  tower.getPosition().getX() + 0.5, tower.getPosition().getY() + 0.5),
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
