package ru.nsu.fit.towerdefense.simulator.world.gameobject;

import java.util.UUID;

public class GameObject {
  protected UUID id;

  public GameObject() {
    id = UUID.randomUUID();
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }
}
