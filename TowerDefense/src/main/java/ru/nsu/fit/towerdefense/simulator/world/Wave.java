package ru.nsu.fit.towerdefense.simulator.world;

import ru.nsu.fit.towerdefense.simulator.map.WaveDescription;

public class Wave {
  private int number;
  private int remainingEnemiesCount;
  private int currentEnemyNumber = 0;
  private WaveDescription description;

  public int getNumber() {
    return number;
  }

  public void setNumber(int number) {
    this.number = number;
  }

  public int getCurrentEnemyNumber() {
    return currentEnemyNumber;
  }

  public void setCurrentEnemyNumber(int currentEnemyNumber) {
    this.currentEnemyNumber = currentEnemyNumber;
  }

  public int getRemainingEnemiesCount() {
    return remainingEnemiesCount;
  }

  public void setRemainingEnemiesCount(int remainingEnemiesCount) {
    this.remainingEnemiesCount = remainingEnemiesCount;
  }

  public WaveDescription getDescription() {
    return description;
  }

  public void setDescription(WaveDescription description) {
    this.description = description;
  }
}
