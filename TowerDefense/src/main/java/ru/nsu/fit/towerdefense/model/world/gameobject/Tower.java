package ru.nsu.fit.towerdefense.model.world.gameobject;

import ru.nsu.fit.towerdefense.model.world.Vector2;
import ru.nsu.fit.towerdefense.model.world.types.TowerType;

public class Tower extends GameObject {
  private TowerType type;
  private int cooldown = 0;
  private double rotation = 0;
  private Enemy target;
  private Vector2<Integer> position;

  public Vector2<Integer> getPosition() {
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
}
