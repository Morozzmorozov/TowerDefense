package ru.nsu.fit.towerdefense.model.world.gameobject;

import ru.nsu.fit.towerdefense.model.util.Vector2;

public class RoadTile implements Renderable {
  private String image;
  private Vector2<Integer> position;

  public RoadTile(String image,
      Vector2<Integer> position) {
    this.image = image;
    this.position = position;
  }

  @Override
  public Type getGameObjectType() {
    return Type.ROAD_TILE;
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
    return 0;
  }
}
