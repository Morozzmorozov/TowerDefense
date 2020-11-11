package ru.nsu.fit.towerdefense.model.world;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import ru.nsu.fit.towerdefense.model.world.gameobject.Enemy;
import ru.nsu.fit.towerdefense.model.world.gameobject.Projectile;
import ru.nsu.fit.towerdefense.model.world.gameobject.Renderable;
import ru.nsu.fit.towerdefense.model.world.gameobject.Tower;

public class World implements Iterable<Renderable> {
  private int countdown;
  private List<Enemy> enemies = new ArrayList<>();
  private List<Tower> towers = new ArrayList<>();
  private List<Projectile> projectiles = new ArrayList<>();

  public List<Enemy> getEnemies() {
    return enemies;
  }

  public List<Tower> getTowers() {
    return towers;
  }

  public List<Projectile> getProjectiles() {
    return projectiles;
  }


  private class WorldIterator implements Iterator<Renderable> {
    private List<Iterator<? extends Renderable>> iterators;

    public WorldIterator() {
      iterators = new ArrayList<>();
      iterators.add(enemies.iterator());
      iterators.add(projectiles.iterator());
      iterators.add(towers.iterator());
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
    return new WorldIterator();
  }
}
