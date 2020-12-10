package ru.nsu.fit.towerdefense.model.world.gameobject;

import ru.nsu.fit.towerdefense.model.util.Vector2;

public class Base implements Renderable {
  private int health;

  private String image;

  private Vector2<Integer> position;

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
  public Type getGameObjectType() {
    return Type.BASE;
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
}
