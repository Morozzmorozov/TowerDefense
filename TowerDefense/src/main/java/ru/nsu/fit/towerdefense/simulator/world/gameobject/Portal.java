package ru.nsu.fit.towerdefense.simulator.world.gameobject;

import ru.nsu.fit.towerdefense.util.Vector2;

public class Portal extends GameObject implements Renderable {
  private final String image;

  private final Vector2<Integer> position;

  public Portal(Portal oldPortal) {
    super(oldPortal.id);
    image = oldPortal.image;
    position = new Vector2<>(oldPortal.position);
  }

  public Portal(String image, Vector2<Integer> position, int id) {
    super(id);
    this.image = image;
    this.position = position;
  }

  public Vector2<Integer> getCell() {
    return position;
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
