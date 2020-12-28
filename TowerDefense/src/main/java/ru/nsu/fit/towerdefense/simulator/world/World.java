package ru.nsu.fit.towerdefense.simulator.world;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.Base;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.Enemy;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.Portal;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.Projectile;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.Renderable;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.RoadTile;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.Tower;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.TowerPlatform;

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
  private List<TowerPlatform> towerPlatforms = new ArrayList<>();
  private List<RoadTile> roadTiles = new ArrayList<>();
  private List<Portal> portals = new ArrayList<>();
  private Base base;
  private int money;
  private int currentWaveNumber = 0;
  private Wave currentWave;
  private List<Wave> waves = new ArrayList<>(); // probably not needed anymore
  private Map<Integer, Wave> waveMap = new HashMap<>();

  public List<Wave> getWaves() {
    return waves;
  }

  public Wave getWaveByNumber(int number) {
    return waveMap.get(number);
  }

  public void clearWaves() {
    waveMap.clear();
    waves.clear();
    currentWave = null;
  }

  public void addWave(Wave wave) {
    if (waveMap.containsKey(wave.getNumber())) {
      return;
    }
    waveMap.put(wave.getNumber(), wave);
    waves.add(wave);
  }

  public List<Portal> getPortals() {
    return portals;
  }

  public List<RoadTile> getRoadTiles() {
    return roadTiles;
  }

  public Wave getCurrentWave() {
    return currentWave;
  }

  public List<TowerPlatform> getTowerPlatforms() {
    return towerPlatforms;
  }

  public void setCurrentWave(Wave currentWave) {
    this.currentWave = currentWave;
    currentWaveNumber = currentWave.getNumber();
    addWave(currentWave);
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

  public void setBase(Base base) {
    this.base = base;
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

  public Collection<Renderable> getRenderables() {
    // todo resolve conflict with Collection
    List<Renderable> renderables = new ArrayList<>();
    renderables.addAll(roadTiles);
    renderables.addAll(towerPlatforms);
    renderables.addAll(towers);
    renderables.add(base);
    renderables.addAll(enemies);
    renderables.addAll(projectiles);
    renderables.addAll(portals);
    for (Enemy enemy : enemies) {
      renderables.addAll(enemy.getEffects());
    }
    return renderables;
    /*return new Iterable<Renderable>() {
      @Override
      public Iterator<Renderable> iterator() {
        return new WorldIterator();
      }
    };*/
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
