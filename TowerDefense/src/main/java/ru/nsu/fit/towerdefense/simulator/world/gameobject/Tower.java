package ru.nsu.fit.towerdefense.simulator.world.gameobject;

import ru.nsu.fit.towerdefense.util.Vector2;
import ru.nsu.fit.towerdefense.metadata.gameobjecttypes.TowerType;

public class Tower extends GameObject implements Renderable {

  public Tower() {

  }

  public Tower(Tower oldTower, Enemy target) {
    type = oldTower.type;
    cooldown = oldTower.cooldown;
    rotation = oldTower.rotation;
    this.target = target;
    position = new Vector2<>(oldTower.position);
    mode = oldTower.mode;
    sellPrice = oldTower.sellPrice;
  }

  public enum Mode { First, Last, Nearest, Farthest, Weakest, Strongest, Random }

  private TowerType type;
  private int cooldown = 0;
  private double rotation = 0;
  private Enemy target;
  private Vector2<Integer> position;
  private Mode mode = Mode.First;
  private int sellPrice;

  public int getSellPrice() {
    return sellPrice;
  }

  public void setSellPrice(int sellPrice) {
    this.sellPrice = sellPrice;
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

  /**
   * {@inheritDoc}
   */
  @Override
  public void accept(ClickVisitor visitor) {
    visitor.onClicked(this);
  }
}
