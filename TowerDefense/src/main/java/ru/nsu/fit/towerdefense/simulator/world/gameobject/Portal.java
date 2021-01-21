package ru.nsu.fit.towerdefense.simulator.world.gameobject;

import ru.nsu.fit.towerdefense.util.Vector2;

public class Portal extends GameObject implements Renderable {
  private String image;

  private Vector2<Integer> position;

  public Portal(String image, Vector2<Integer> position) {
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
