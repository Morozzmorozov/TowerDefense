package ru.nsu.fit.towerdefense.simulator.world.gameobject;

import ru.nsu.fit.towerdefense.util.Vector2;
import ru.nsu.fit.towerdefense.metadata.gameobjecttypes.TowerType;

public class Tower extends GameObject implements Renderable {

  public Tower(int id) {
    super(id);
  }

  public Tower(Tower oldTower, Enemy target) {
    super(oldTower.id);
    type = oldTower.type;
    cooldown = oldTower.cooldown;
    rotation = oldTower.rotation;
    this.target = target;
    position = new Vector2<>(oldTower.position);
    mode = oldTower.mode;
    sellPrice = oldTower.sellPrice;
    owner = oldTower.owner;
  }

  public enum Mode { First, Last, Nearest, Farthest, Weakest, Strongest, Random }

  private TowerType type;
  private int cooldown = 0;
  private double rotation = 0;
  private Enemy target;
  private Vector2<Integer> position;
  private Mode mode = Mode.First;
  private int sellPrice;
  private String owner;

  public String getOwner() {
    return owner;
  }

  public Tower setOwner(String owner) {
    this.owner = owner;
    return this;
  }

  public int getSellPrice() {
    return sellPrice;
  }

  public Tower setSellPrice(int sellPrice) {
    this.sellPrice = sellPrice;
    return this;
  }

  public Mode getMode() {
    return mode;
  }

  public Tower setMode(Mode mode) {
    this.mode = mode;
    return this;
  }

  public Tower setPosition(Vector2<Integer> position) {
    this.position = position;
    return this;
  }

  public Vector2<Integer> getCell() {
    return position;
  }

  public Enemy getTarget() {
    return target;
  }

  public Tower setTarget(Enemy target) {
    this.target = target;
    return this;
  }

  public TowerType getType() {
    return type;
  }

  public Tower setType(TowerType type) {
    this.type = type;
    return this;
  }

  @Override
  public double getRotation() {
    return rotation;
  }

  public Tower setRotation(double rotation) {
    this.rotation = rotation;
    return this;
  }

  public int getCooldown() {
    return cooldown;
  }

  public Tower setCooldown(int cooldown) {
    this.cooldown = cooldown;
    return this;
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
