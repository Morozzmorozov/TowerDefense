package ru.nsu.fit.towerdefense.model.world;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import ru.nsu.fit.towerdefense.model.world.gameobject.Base;
import ru.nsu.fit.towerdefense.model.world.gameobject.Enemy;
import ru.nsu.fit.towerdefense.model.world.gameobject.Projectile;
import ru.nsu.fit.towerdefense.model.world.gameobject.Renderable;
import ru.nsu.fit.towerdefense.model.world.gameobject.Tower;

public class World {

  public int getCountdown() {
    return countdown;
  }

  public void setCountdown(int countdown) {
    this.countdown = countdown;
  }

  private int countdown;
  private List<Enemy> enemies = new ArrayList<>();
  private List<Tower> towers = new ArrayList<>();
  private List<Projectile> projectiles = new ArrayList<>();
  private Base base;
  private int money;
  private int currentWaveNumber;
  private Wave currentWave;

  public Wave getCurrentWave() {
    return currentWave;
  }

  public void setCurrentWave(Wave currentWave) {
    this.currentWave = currentWave;
  }

  public int getCurrentWaveNumber() {
    return currentWaveNumber;
  }

  public void setCurrentWaveNumber(int currentWaveNumber) {
    this.currentWaveNumber = currentWaveNumber;
  }

  public Base getBase() {
    return base;
  }

  public int getMoney() {
    return money;
  }

  public void setMoney(int money) {
    this.money = money;
  }

  public List<Enemy> getEnemies() {
    return enemies;
  }

  public List<Tower> getTowers() {
    return towers;
  }

  public List<Projectile> getProjectiles() {
    return projectiles;
  }

  public Iterable<Renderable> getRenderables() {
    return new Iterable<Renderable>() {
      @Override
      public Iterator<Renderable> iterator() {
        return new WorldIterator();
      }
    };
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
}
