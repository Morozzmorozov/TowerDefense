package ru.nsu.fit.towerdefense.model.world.gameobject;

import ru.nsu.fit.towerdefense.model.util.Vector2;

public interface Renderable {
  Vector2<Double> getPosition();
  Vector2<Double> getSize();
  String getImageName();

  default double getRotation() {
    return 0;
  }
}
