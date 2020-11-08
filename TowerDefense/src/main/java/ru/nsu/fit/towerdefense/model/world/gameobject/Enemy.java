package ru.nsu.fit.towerdefense.model.world.gameobject;

import ru.nsu.fit.towerdefense.model.world.Vector2;
import ru.nsu.fit.towerdefense.model.world.Wave;
import ru.nsu.fit.towerdefense.model.world.types.EnemyType;

public class Enemy extends GameObject {
  private int health;
  private EnemyType type;
  private float velocity; // TODO link with trajectory somehow
  private Wave wave;
  private boolean isDead = false;
  private Vector2<Double> position;

  public Vector2<Double> getPosition() {
    return position;
  }

  public void setPosition(Vector2<Double> position) {
    this.position = position;
  }

  public boolean isDead() {
    return isDead;
  }

  public void setDead(boolean dead) {
    isDead = dead;
  }

  public Wave getWave() {
    return wave;
  }

  public int getHealth() {
    return health;
  }

  public void setHealth(int health) {
    this.health = health;
  }

  public EnemyType getType() {
    return type;
  }

  public float getVelocity() {
    return velocity;
  }
}
