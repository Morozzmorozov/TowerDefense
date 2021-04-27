package ru.nsu.fit.towerdefense.simulator.events;

import static ru.nsu.fit.towerdefense.simulator.WorldControl.EPS;
import static ru.nsu.fit.towerdefense.simulator.WorldControl.SELL_MULTIPLIER;

import ru.nsu.fit.towerdefense.metadata.GameMetaData;
import ru.nsu.fit.towerdefense.metadata.gameobjecttypes.TowerType;
import ru.nsu.fit.towerdefense.metadata.gameobjecttypes.TowerType.Upgrade;
import ru.nsu.fit.towerdefense.replay.GameStateWriter;
import ru.nsu.fit.towerdefense.simulator.exceptions.GameplayException;
import ru.nsu.fit.towerdefense.simulator.world.World;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.Tower;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.TowerPlatform;
import ru.nsu.fit.towerdefense.util.Vector2;

public class UpgradeTowerEvent implements Event {

  private final Upgrade upgrade;
  private final Tower tower;
  private final long frameNumber;

  public UpgradeTowerEvent(Upgrade upgrade,
      Tower tower, long frameNumber) {
    this.upgrade = upgrade;
    this.tower = tower;
    this.frameNumber = frameNumber;
  }

  @Override
  public EventType getType() {
    return EventType.UPGRADE_TOWER;
  }

  @Override
  public long getFrameNumber() {
    return frameNumber;
  }

  @Override
  public void fire(World world) throws GameplayException {
    if (world.getMoney() < upgrade.getCost()) {
      throw new GameplayException("Not enough money to upgrade the tower");
    }
    if (!GameMetaData.getInstance().getTechTree().getIsTypeAvailable(upgrade.getName())) {
      throw new GameplayException("The tower is not yet researched");
    }
    world.setMoney(world.getMoney() - upgrade.getCost());
    TowerType type = GameMetaData.getInstance().getTowerType(upgrade.getName());
    tower.setType(type);
    tower.setCooldown(type.getFireRate());
    tower.setTarget(null);
    tower.setSellPrice(tower.getSellPrice() + Math.round(upgrade.getCost() + SELL_MULTIPLIER));

    for (int i = 0; i < world.getTowerPlatforms().size(); ++i) {
      TowerPlatform platform = world.getTowerPlatforms().get(i);
      if (Vector2.distance(platform.getPosition(), tower.getPosition()) < EPS) {
        GameStateWriter.getInstance().upgradeTower(i, upgrade.getName()); // todo ask about platform
        break;
      }
    }
  }
}
