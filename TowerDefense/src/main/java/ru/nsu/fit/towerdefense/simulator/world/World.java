package ru.nsu.fit.towerdefense.simulator.world;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.Base;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.Enemy;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.Portal;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.Projectile;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.Renderable;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.RoadTile;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.Tower;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.TowerPlatform;

public class World {

  public String serialize() {
    return new Gson().toJson(new SerializableWorld(this));
  }

  public static World deserialize(String json) {
    Gson gson = new GsonBuilder().registerTypeAdapter(SerializableWorld.class, new SerializableWorld.WorldDeserializer()).create();
    return gson.fromJson(json, SerializableWorld.class).generateWorld();
  }

  public World() {

  }

  public World(World oldWorld) {
    countdown = oldWorld.countdown;
    enemies = oldWorld.enemies.stream().map(Enemy::new).collect(Collectors.toList());
    projectiles = oldWorld.projectiles.stream().map(projectile -> {
      var e = projectile.getTarget();
      if (e != null && !e.isDead()) {
        e = enemies.get(oldWorld.enemies.indexOf(e));
      }
      return new Projectile(projectile, e);
    }).collect(Collectors.toList());
    towerPlatforms = oldWorld.towerPlatforms.stream().map(TowerPlatform::new)
        .collect(Collectors.toList());
    roadTiles = oldWorld.roadTiles.stream().map(RoadTile::new).collect(Collectors.toList());
    portals = oldWorld.portals.stream().map(Portal::new).collect(Collectors.toList());
    base = new Base(oldWorld.base);
    currentWaveNumber = oldWorld.currentWaveNumber;

    currentWave = new Wave(oldWorld.currentWave);
    waveMap = new HashMap<>(waveMap);
    for (var e : oldWorld.waveMap.entrySet()) {
      Wave wave = new Wave(e.getValue());
      if (e.getValue() == oldWorld.currentWave) {
        currentWave = wave;
      }
      waveMap.put(e.getKey(), wave);
    }

    moneyMap = new HashMap<>(oldWorld.moneyMap);


    towers = oldWorld.towers.stream().map(tower -> {
      var e = tower.getTarget();
      if (e != null && !e.isDead()) {
        e = enemies.get(oldWorld.enemies.indexOf(e));
      }
      return new Tower(tower, e);
    }).collect(Collectors.toList());

    tick = oldWorld.tick;


  }

  public World(SerializableWorld world) {

  }

  public SerializableWorld getSerializableWorld() {
    return new SerializableWorld(this);// todo
  }

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
  private int currentWaveNumber = 0;
  private Wave currentWave;
  public Map<Integer, Wave> waveMap = new HashMap<>();
  public Map<String, Integer> moneyMap = new HashMap<>();
  private long tick = 0;

  public long getTick() {
    return tick;
  }

  public void setTick(long tick) {
    this.tick = tick;
  }

  public Wave getWaveByNumber(int number) {
    return waveMap.get(number);
  }

  public void setEnemies(List<Enemy> enemies) {
    this.enemies = enemies;
  }

  public void setTowers(List<Tower> towers) {
    this.towers = towers;
  }

  public void setProjectiles(
      List<Projectile> projectiles) {
    this.projectiles = projectiles;
  }

  public void setTowerPlatforms(
      List<TowerPlatform> towerPlatforms) {
    this.towerPlatforms = towerPlatforms;
  }

  public void setRoadTiles(
      List<RoadTile> roadTiles) {
    this.roadTiles = roadTiles;
  }

  public void setPortals(List<Portal> portals) {
    this.portals = portals;
  }

  public void clearWaves() {
    waveMap.clear();
    currentWave = null;
  }

  public void addWave(Wave wave) {
    if (waveMap.containsKey(wave.getNumber())) {
      return;
    }
    waveMap.put(wave.getNumber(), wave);
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

  public int getMoney(String player) { // TODO pass player id or something
    return moneyMap.get(player);
  }

  public void setMoney(String player, int money) {
    moneyMap.put(player, money);
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
