package ru.nsu.fit.towerdefense.simulator.world.gameobject;

public class GameObject {
  protected int id;

  public GameObject(int id) {
    this.id = id;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }
}
