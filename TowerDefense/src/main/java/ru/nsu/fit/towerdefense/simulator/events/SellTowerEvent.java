package ru.nsu.fit.towerdefense.simulator.events;

import ru.nsu.fit.towerdefense.simulator.world.World;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.Tower;

public class SellTowerEvent implements Event {

  private final long frameNumber;
  private final Tower tower;

  public SellTowerEvent(long frameNumber,
      Tower tower) {
    this.frameNumber = frameNumber;
    this.tower = tower;
  }

  @Override
  public EventType getType() {
    return EventType.SELL_TOWER;
  }

  @Override
  public long getFrameNumber() {
    return frameNumber;
  }

  @Override
  public void fire(World world) {
    world.setMoney(world.getMoney() + tower.getSellPrice());
    world.getTowers().remove(tower);
  }
}
