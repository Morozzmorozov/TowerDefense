package ru.nsu.fit.towerdefense.simulator.events;

import ru.nsu.fit.towerdefense.simulator.world.World;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.Tower;

public class SellTowerEvent implements Event {

  private final long frameNumber;
  private final Tower tower;

  private final String player;

  public SellTowerEvent(long frameNumber,
      Tower tower, String player) {
    this.frameNumber = frameNumber;
    this.tower = tower;
    this.player = player;
  }

  @Override
  public EventType getType() {
    return EventType.SELL_TOWER;
  }

  @Override
  public String getPlayer() {
    return player;
  }

  @Override
  public long getFrameNumber() {
    return frameNumber;
  }

  @Override
  public void fire(World world) {
    world.setMoney(player, world.getMoney(player) + tower.getSellPrice());
    world.getTowers().remove(tower);
  }
}
