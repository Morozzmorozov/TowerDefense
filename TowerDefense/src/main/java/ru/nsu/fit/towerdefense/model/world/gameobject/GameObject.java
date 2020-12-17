package ru.nsu.fit.towerdefense.model.world.gameobject;

import java.util.UUID;

public class GameObject {
  private final UUID id;

  public GameObject() {
    id = UUID.randomUUID();
  }

  public UUID getId() {
    return id;
  }
}
