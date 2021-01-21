package ru.nsu.fit.towerdefense.simulator.world.gameobject;

import ru.nsu.fit.towerdefense.simulator.world.gameobject.visitor.Visitor;
import ru.nsu.fit.towerdefense.util.Vector2;

public interface Renderable {

  Vector2<Double> getPosition();
  Vector2<Double> getSize();
  String getImageName();

  /**
   * Returns rotation in degrees.
   *
   * @return rotation in degrees.
   */
  default double getRotation() {
    return 0;
  }

  /**
   * Returns z-index (it must be >= 0). The bigger z-index the later this object will be rendered.
   *
   * @return z-index.
   */
  double getZIndex();

  /**
   * Applies a visitor to this renderable.
   *
   * @param visitor a visitor to be applied.
   */
  void accept(Visitor visitor);
}
