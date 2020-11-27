package ru.nsu.fit.towerdefense.model.world.gameobject;

import ru.nsu.fit.towerdefense.model.util.Vector2;
import ru.nsu.fit.towerdefense.model.world.types.TowerType;

public class Tower implements Renderable {
  private TowerType type;
  private int cooldown = 0;
  private double rotation = 0;
  private Enemy target;
  private Vector2<Integer> position;

  public Vector2<Integer> getCell() {
    return position;
  }

  public Enemy getTarget() {
    return target;
  }

  public void setTarget(Enemy target) {
    this.target = target;
  }

  public TowerType getType() {
    return type;
  }

  public void setType(TowerType type) {
    this.type = type;
  }

  public double getRotation() {
    return rotation;
  }

  public void setRotation(double rotation) {
    this.rotation = rotation;
  }

  public int getCooldown() {
    return cooldown;
  }

  public void setCooldown(int cooldown) {
    this.cooldown = cooldown;
  }

  @Override
  public Vector2<Double> getPosition() {
    return new Vector2<>((double)position.getX(), (double)position.getY());
  }

  @Override
  public Vector2<Double> getSize() {
    return new Vector2<>(1d, 1d);
  }

  @Override
  public String getImageName() {
    return type.getImage();
  }
}
