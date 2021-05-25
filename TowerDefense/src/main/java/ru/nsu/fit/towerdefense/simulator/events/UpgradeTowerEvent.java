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
  private final long frameNumber;
  private final String player;
  private final String upgradeName;
  private final int x;
  private final int y;

  public UpgradeTowerEvent(Upgrade upgrade,
      Tower tower, long frameNumber, String player) {
    this.upgrade = upgrade;
    this.frameNumber = frameNumber;
    this.player = player;
    upgradeName = upgrade.getName();
    x = tower.getCell().getX();
    y = tower.getCell().getY();
  }

  @Override
  public EventType getType() {
    return EventType.UPGRADE_TOWER;
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
    Tower tower = null;
    for (Tower candidate : world.getTowers()) {
      if (candidate.getCell().getX() == x && candidate.getCell().getY() == y) {
        tower = candidate;
        break;
      }
    }
    if (tower == null) {
      System.err.println("Tower not found");
      return;
    }


    if (world.getMoney(player) < upgrade.getCost()) {
      throw new GameplayException("Not enough money to upgrade the tower");
    }
    if (!GameMetaData.getInstance().getTechTree().getIsTypeAvailable(upgrade.getName())) {
      throw new GameplayException("The tower is not yet researched");
    }
    world.setMoney(player, world.getMoney(player) - upgrade.getCost());
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
