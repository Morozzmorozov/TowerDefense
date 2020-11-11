package ru.nsu.fit.towerdefense.model.world.types;

import java.util.Map;

public class ProjectileType {
  private float speed;
  private boolean selfGuided;
  private Map<EnemyType, Integer> enemyTypeDamageMap;
  private int hitBox;

  public float getSpeed() {
    return speed;
  }

  public boolean isSelfGuided() {
    return selfGuided;
  }

  public Map<EnemyType, Integer> getEnemyTypeDamageMap() {
    return enemyTypeDamageMap;
  }

  public int getHitBox() {
    return hitBox;
  }
}
