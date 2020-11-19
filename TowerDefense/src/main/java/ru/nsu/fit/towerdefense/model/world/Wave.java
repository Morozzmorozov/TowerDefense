package ru.nsu.fit.towerdefense.model.world;

import ru.nsu.fit.towerdefense.model.world.map.WaveDescription;

public class Wave {
  private int remainingEnemiesCount;
  private int currentEnemyNumber = 0;
  private WaveDescription description;

  public int getRemainingEnemiesCount() {
    return remainingEnemiesCount;
  }

  public void setRemainingEnemiesCount(int remainingEnemiesCount) {
    this.remainingEnemiesCount = remainingEnemiesCount;
  }

  public WaveDescription getDescription() {
    return description;
  }
}
