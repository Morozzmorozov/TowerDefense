package ru.nsu.fit.towerdefense.model.world.gameobject;

import ru.nsu.fit.towerdefense.model.world.Vector2;
import ru.nsu.fit.towerdefense.model.world.types.ProjectileType;

public class Projectile implements Renderable {
  private Enemy target;
  private float remainingRange;
  private ProjectileType type;
  private Vector2<Double> position;
  private Vector2<Double> velocity;

  public Vector2<Double> getVelocity() {
    return velocity;
  }

  public void setVelocity(Vector2<Double> velocity) {
    this.velocity = velocity;
  }

  public void setRemainingRange(float remainingRange) {
    this.remainingRange = remainingRange;
  }

  public Enemy getTarget() {
    return target;
  }

  public float getRemainingRange() {
    return remainingRange;
  }

  public ProjectileType getType() {
    return type;
  }

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

  public void setPosition(Vector2<Double> position) {
    this.position = position;
  }

  public Projectile(Enemy target, float range, ProjectileType type, Vector2<Double> position, Vector2<Double> velocity) {
    remainingRange = range;
    this.target = target;
    this.type = type;
    this.position = position;
    this.velocity = velocity;
  }
}
