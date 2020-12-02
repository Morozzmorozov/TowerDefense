package ru.nsu.fit.towerdefense.model.world.gameobject;

import ru.nsu.fit.towerdefense.model.util.Vector2;

public class TowerPlatform implements Renderable {
  private Vector2<Integer> position;
  private String image;

  public TowerPlatform(Vector2<Integer> position, String image) {
    this.position = position;
    this.image = image;
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
}
