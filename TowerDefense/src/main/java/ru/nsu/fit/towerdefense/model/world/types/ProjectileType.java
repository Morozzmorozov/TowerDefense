package ru.nsu.fit.towerdefense.model.world.types;

import java.util.Map;
import ru.nsu.fit.towerdefense.model.world.Vector2;

public class ProjectileType {
  private float speed;
  private boolean selfGuided;
  private Map<EnemyType, Integer> enemyTypeDamageMap;
  private int hitBox;
  private Vector2<Double> size;
  private String image;

  public Vector2<Double> getSize() {
    return size;
  }

  public String getImage() {
    return image;
  }

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
