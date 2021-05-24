package ru.nsu.fit.towerdefense.simulator.events;

import ru.nsu.fit.towerdefense.metadata.GameMetaData;
import ru.nsu.fit.towerdefense.metadata.gameobjecttypes.TowerType;
import ru.nsu.fit.towerdefense.replay.GameStateWriter;
import ru.nsu.fit.towerdefense.simulator.WorldControl;
import ru.nsu.fit.towerdefense.simulator.exceptions.GameplayException;
import ru.nsu.fit.towerdefense.simulator.world.World;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.Tower;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.TowerPlatform;
import ru.nsu.fit.towerdefense.util.Vector2;

public class BuildTowerEvent implements Event {

  private final long frameNumber;

  private final TowerPlatform towerPlatform;

  private final TowerType towerType;

  private final String player;

  private Tower tower;

  public BuildTowerEvent(long frameNumber,
      TowerPlatform towerPlatform, TowerType towerType, String player) {
    this.frameNumber = frameNumber;
    this.towerPlatform = towerPlatform;
    this.towerType = towerType;
    this.player = player;
  }

  @Override
  public EventType getType() {
    return EventType.BUILD_TOWER;
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
  public void fire(World world) throws GameplayException {
    if (world.getMoney(player) < towerType.getPrice()) {
      throw new GameplayException("Not enough money to build the tower");
    }
    if (!GameMetaData.getInstance().getTechTree().getIsTypeAvailable(towerType.getTypeName())) {
      throw new GameplayException("The tower is not yet researched");
    }
    world.setMoney(player, world.getMoney(player) - towerType.getPrice());
    tower = new Tower();
    tower.setPosition(new Vector2<>((int) Math.round(towerPlatform.getPosition().getX()),
        (int) Math.round(towerPlatform.getPosition().getY())));
    tower.setType(towerType);
    tower.setRotation(0);
    tower.setCooldown(towerType.getFireRate());
    tower.setSellPrice(Math.round(towerType.getPrice() * WorldControl.SELL_MULTIPLIER));
    tower.setOwner(player);
    world.getTowers().add(tower);
    GameStateWriter
        .getInstance()
        .buildTower(world.getTowerPlatforms().indexOf(towerPlatform), towerType.getTypeName(),
            tower.getId().toString());
  }

  public Tower getTower() {
    return tower;
  }
}
