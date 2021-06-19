package ru.nsu.fit.towerdefense.simulator.world.gameobject;

import ru.nsu.fit.towerdefense.util.Vector2;
import ru.nsu.fit.towerdefense.metadata.gameobjecttypes.ProjectileType;
import ru.nsu.fit.towerdefense.metadata.gameobjecttypes.TowerType.FireType;

public class Projectile extends GameObject implements Renderable {
  private Enemy target;
  private float remainingRange;
  private ProjectileType type;
  private Vector2<Double> position;
  private Vector2<Double> velocity;
  private double rotation;
  private double scale = 1.0;
  private double rotationSpeed = 3.0; // DEBUG
  private FireType fireType;
  private Vector2<Double> parentPosition;
  private String owner;

  public String getOwner() {
    return owner;
  }

  public Projectile setOwner(String owner) {
    this.owner = owner;
    return this;
  }

  public Projectile(int id) {
    super(id);
  }

  public Projectile(Projectile oldProjectile, Enemy target) {
    super(oldProjectile.id);
    this.target = target;
    remainingRange = oldProjectile.remainingRange;
    type = oldProjectile.type;
    position = new Vector2<>(oldProjectile.position);
    velocity = new Vector2<>(oldProjectile.velocity);
    rotation = oldProjectile.rotation;
    scale = oldProjectile.scale;
    rotationSpeed = oldProjectile.rotationSpeed;
    fireType = oldProjectile.fireType;
    parentPosition = new Vector2<>(oldProjectile.parentPosition);
    owner = oldProjectile.owner;
  }

  public FireType getFireType() {
    return fireType;
  }

  public Projectile setFireType(FireType fireType) {
    this.fireType = fireType;
    return this;
  }

  public double getRotationSpeed() {
    return rotationSpeed;
  }

  public Projectile setRotationSpeed(double rotationSpeed) {
    this.rotationSpeed = rotationSpeed;
    return this;
  }

  public double getScale() {
    return scale;
  }

  public Projectile setScale(double scale) {
    this.scale = scale;
    return this;
  }

  public Vector2<Double> getVelocity() {
    return velocity;
  }

  public Projectile setVelocity(Vector2<Double> velocity) {
    this.velocity = velocity;
    return this;
  }

  public Projectile setRemainingRange(float remainingRange) {
    this.remainingRange = remainingRange;
    return this;
  }

  public Enemy getTarget() {
    return target;
  }

  public Projectile setTarget(Enemy target) {
    this.target = target;
    return this;
  }

  public float getRemainingRange() {
    return remainingRange;
  }

  public ProjectileType getType() {
    return type;
  }

  @Override
  public double getRotation() {
    return rotation;
  }

  public Projectile setRotation(double rotation) {
    this.rotation = rotation;
    return this;
  }

  @Override
  public Vector2<Double> getPosition() {
    if (fireType.equals(FireType.UNIDIRECTIONAL))
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

  public Projectile setPosition(Vector2<Double> position) {
    this.position = position;
    return this;
  }

  public Projectile(Enemy target, float range, ProjectileType type, Vector2<Double> position, Vector2<Double> velocity, Tower parent, int id) {
    super(id);
    remainingRange = range;
    this.target = target;
    this.type = type;
    this.position = position;
    this.velocity = velocity;
    fireType = parent.getType().getFireType();
    parentPosition = new Vector2<>(parent.getPosition());

  }

  public Projectile setType(ProjectileType type) {
    this.type = type;
    return this;
  }

  @Override
  public double getZIndex() {
    return 6;
  }

  public Vector2<Double> getParentPosition() {
    return parentPosition;
  }

  public Projectile setParentPosition(Vector2<Double> parentPosition) {
    this.parentPosition = parentPosition;
    return this;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void accept(ClickVisitor visitor) {
    visitor.onClicked(this);
  }
}
