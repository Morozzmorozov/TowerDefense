package ru.nsu.fit.towerdefense.simulator.world.gameobject;

import ru.nsu.fit.towerdefense.util.Vector2;

public class TowerPlatform extends GameObject implements Renderable {
  private final Vector2<Integer> position;
  private final String image;

  public TowerPlatform(Vector2<Integer> position, String image) {
    this.position = position;
    this.image = image;
  }

  public TowerPlatform(TowerPlatform oldPlatform) {
    position = new Vector2<>(oldPlatform.position);
    image = oldPlatform.image;
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
    return 2;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void accept(ClickVisitor visitor) {
    visitor.onClicked(this);
  }
}
