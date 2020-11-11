package ru.nsu.fit.towerdefense.model.world.types;

import java.util.List;

public class TowerType {
  private int price;
  private List<TowerType> upgrades;
  private int range;
  private int fireRate;
  private FireType fireType;
  private ProjectileType projectileType;

  public int getPrice() {
    return price;
  }

  public List<TowerType> getUpgrades() {
    return upgrades;
  }

  public int getRange() {
    return range;
  }

  public int getFireRate() {
    return fireRate;
  }

  public FireType getFireType() {
    return fireType;
  }

  public ProjectileType getProjectileType() {
    return projectileType;
  }

  public enum FireType {
    UNIDIRECTIONAL, OMNIDIRECTIONAL
  }
}
