package ru.nsu.fit.towerdefense.model.world.gameobject;

import ru.nsu.fit.towerdefense.model.util.Vector2;
import ru.nsu.fit.towerdefense.model.world.types.ProjectileType;
import ru.nsu.fit.towerdefense.model.world.types.TowerType.FireType;

public class Projectile extends GameObject implements Renderable {
  private Enemy target;
  private float remainingRange;
  private ProjectileType type;
  private Vector2<Double> position;
  private Vector2<Double> velocity;
  private double rotation;
  private Tower parent;
  private double scale = 1.0;
  private double rotationSpeed = 3.0; // DEBUG

  public double getRotationSpeed() {
    return rotationSpeed;
  }

  public void setRotationSpeed(double rotationSpeed) {
    this.rotationSpeed = rotationSpeed;
  }

  public double getScale() {
    return scale;
  }

  public void setScale(double scale) {
    this.scale = scale;
  }

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

  public void setTarget(Enemy target) {
    this.target = target;
  }

  public float getRemainingRange() {
    return remainingRange;
  }

  public ProjectileType getType() {
    return type;
  }

  public Tower getParent() {
    return parent;
  }

  @Override
  public double getRotation() {
    return rotation;
  }

  public void setRotation(double rotation) {
    this.rotation = rotation;
  }

  @Override
  public Type getGameObjectType() {
    return Type.PROJECTILE;
  }

  public Vector2<Double> getPosition() { // todo fix temporary solution
    if (parent.getType().getFireType().equals(FireType.UNIDIRECTIONAL))
    return position;
    else return new Vector2<>(position.getX() + 0.5 - 0.5 * scale, position.getY() + 0.5 - 0.5 * scale);
  }

  @Override
  public Vector2<Double> getSize() {
    return new Vector2<>(type.getSize().getX() * scale, type.getSize().getY() * scale);
  }

  @Override
  public String getImageName() {
    return type.getImage();
  }

  public void setPosition(Vector2<Double> position) {
    this.position = position;
  }

  public Projectile(Enemy target, float range, ProjectileType type, Vector2<Double> position, Vector2<Double> velocity, Tower parent) {
    remainingRange = range;
    this.target = target;
    this.type = type;
    this.position = position;
    this.velocity = velocity;
    this.parent = parent;
  }

  @Override
  public double getZIndex() {
    return 5;
  }
}
