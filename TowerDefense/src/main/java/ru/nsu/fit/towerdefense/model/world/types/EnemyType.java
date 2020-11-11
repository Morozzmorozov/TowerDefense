package ru.nsu.fit.towerdefense.model.world.types;

import ru.nsu.fit.towerdefense.model.world.Vector2;

public class EnemyType {
  private int health;
  private float speed;
  private float hitBox;
  private Vector2<Double> size;
  private String image;

  public String getImage() {
    return image;
  }

  public Vector2<Double> getSize() {
    return size;
  }

  public int getHealth() {
    return health;
  }

  public float getSpeed() {
    return speed;
  }

  public float getHitBox() {
    return hitBox;
  }
}
