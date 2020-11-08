package ru.nsu.fit.towerdefense.model.world;

import java.util.ArrayList;
import java.util.List;
import ru.nsu.fit.towerdefense.model.world.gameobject.Enemy;
import ru.nsu.fit.towerdefense.model.world.gameobject.Projectile;
import ru.nsu.fit.towerdefense.model.world.gameobject.Tower;

public class World {
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
}
