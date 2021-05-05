package ru.nsu.fit.towerdefense.simulator.world.gameobject;

import ru.nsu.fit.towerdefense.util.Vector2;

public class RoadTile extends GameObject implements Renderable {
  private final String image;
  private final Vector2<Integer> position;

  public RoadTile(RoadTile oldTile) {
    image = oldTile.image;
    position = new Vector2<>(oldTile.position);
  }

  public RoadTile(String image,
      Vector2<Integer> position) {
    this.image = image;
    this.position = position;
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

  /**
   * {@inheritDoc}
   */
  @Override
  public void accept(ClickVisitor visitor) {
    visitor.onClicked(this);
  }
}
