package ru.nsu.fit.towerdefense.simulator.events;

import static ru.nsu.fit.towerdefense.simulator.WorldControl.EPS;

import ru.nsu.fit.towerdefense.simulator.world.World;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.Tower;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.TowerPlatform;
import ru.nsu.fit.towerdefense.util.Vector2;

public class SellTowerEvent implements Event {

  private final long frameNumber;
  private final Tower tower;

  private final String player;

  private TowerPlatform platform;

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
    for (TowerPlatform platform : world.getTowerPlatforms()) {
      if (Vector2.distance(tower.getPosition(), platform.getPosition()) < EPS) {
        this.platform = platform;
        break;
      }
    }
  }

  public TowerPlatform getPlatform() {
    return platform;
  }
}
