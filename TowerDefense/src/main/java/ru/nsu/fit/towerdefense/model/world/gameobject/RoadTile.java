package ru.nsu.fit.towerdefense.model.world.gameobject;

import ru.nsu.fit.towerdefense.model.world.Vector2;

public class RoadTile implements Renderable {
  private String image;
  private Vector2<Integer> position;

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
