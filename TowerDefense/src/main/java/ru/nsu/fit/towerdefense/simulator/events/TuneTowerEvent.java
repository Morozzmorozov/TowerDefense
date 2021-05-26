package ru.nsu.fit.towerdefense.simulator.events;

import com.google.gson.Gson;
import ru.nsu.fit.towerdefense.replay.GameStateWriter;
import ru.nsu.fit.towerdefense.simulator.world.World;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.Tower;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.Tower.Mode;

public class TuneTowerEvent implements Event {

  private final EventType type = EventType.TUNE_TOWER;

  private final long frameNumber;
  private final Tower.Mode towerMode;
  private final int x;
  private final int y;
  private final String player;

  public TuneTowerEvent(long frameNumber,
      Mode towerMode, Tower tower, String player) {
    this.frameNumber = frameNumber;
    this.towerMode = towerMode;
    this.player = player;
    x = tower.getCell().getX();
    y = tower.getCell().getY();
  }

  @Override
  public String serialize() {
    return new Gson().toJson(this);
  }

  @Override
  public EventType getType() {
    return EventType.TUNE_TOWER;
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
    tower.setMode(towerMode);
    tower.setTarget(null);
    GameStateWriter.getInstance().switchMode(tower, towerMode);
  }
}
