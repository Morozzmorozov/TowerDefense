package ru.nsu.fit.towerdefense.model.world.gameobject;

import ru.nsu.fit.towerdefense.model.util.Vector2;
import ru.nsu.fit.towerdefense.model.world.types.TowerType;

public class Tower extends GameObject implements Renderable {

  public enum Mode { First, Last, Nearest, Farthest, Weakest, Strongest, Random }

  private TowerType type;
  private int cooldown = 0;
  private double rotation = 0;
  private Enemy target;
  private Vector2<Integer> position;
  private Mode mode = Mode.First;

  public int getSellPrice() { // todo e.g. 40% of all the money user payed for building and upgrading this tower
    return 12;
  }

  public Mode getMode() {
    return mode;
  }

  public void setMode(Mode mode) {
    this.mode = mode;
  }

  public void setPosition(Vector2<Integer> position) {
    this.position = position;
  }

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

  @Override
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
  public Type getGameObjectType() {
    return Type.TOWER;
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

  @Override
  public double getZIndex() {
    return 3;
  }
}
