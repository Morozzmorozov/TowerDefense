package ru.nsu.fit.towerdefense.simulator.world.gameobject;

import ru.nsu.fit.towerdefense.util.Vector2;

public class Base extends GameObject implements Renderable {
  private int health;

  private final String image;

  private final Vector2<Integer> position;

  public Base(Base oldBase) {
    health = oldBase.health;
    image = oldBase.image;
    position = new Vector2<>(oldBase.position);
  }

  public Base(int health, String image,
      Vector2<Integer> position) {
    this.health = health;
    this.image = image;
    this.position = position;
  }

  public int getHealth() {
    return health;
  }

  public void setHealth(int health) {
    this.health = health;
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
    return image;
  }

  @Override
  public double getZIndex() {
    return 1;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void accept(ClickVisitor visitor) {
    visitor.onClicked(this);
  }
}
