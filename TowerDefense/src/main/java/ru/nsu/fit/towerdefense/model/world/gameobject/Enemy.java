package ru.nsu.fit.towerdefense.model.world.gameobject;

import java.util.ArrayList;
import java.util.List;
import ru.nsu.fit.towerdefense.model.util.Vector2;
import ru.nsu.fit.towerdefense.model.world.Wave;
import ru.nsu.fit.towerdefense.model.world.types.EnemyType;

public class Enemy implements Renderable {
  private int health;
  private EnemyType type;
  private float velocity;
  private Wave wave;
  private boolean isDead = false;
  private Vector2<Double> position;
  private List<Vector2<Double>> trajectory = new ArrayList<>();

  public Enemy(EnemyType type, Wave wave, Vector2<Double> position) {
    this.type = type;
    this.wave = wave;
    this.position = position;
    health = type.getHealth();
    velocity = type.getSpeed();
  }

  public Vector2<Double> getCell() {
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

  @Override
  public Vector2<Double> getPosition() {
    return position;
  }

  @Override
  public Vector2<Double> getSize() {
    return type.getSize();
  }

  @Override
  public String getImageName() {
    return type.getImage();
  }

  public List<Vector2<Double>> getTrajectory() {
    return trajectory;
  }
}
