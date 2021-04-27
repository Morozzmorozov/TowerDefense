package ru.nsu.fit.towerdefense.simulator.events;

import ru.nsu.fit.towerdefense.replay.GameStateWriter;
import ru.nsu.fit.towerdefense.simulator.world.World;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.Tower;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.Tower.Mode;

public class TuneTowerEvent implements Event {

  private final long frameNumber;
  private final Tower.Mode towerMode;
  private final Tower tower;

  public TuneTowerEvent(long frameNumber,
      Mode towerMode, Tower tower) {
    this.frameNumber = frameNumber;
    this.towerMode = towerMode;
    this.tower = tower;
  }

  @Override
  public EventType getType() {
    return EventType.TUNE_TOWER;
  }

  @Override
  public long getFrameNumber() {
    return frameNumber;
  }

  @Override
  public void fire(World world) {
    tower.setMode(towerMode);
    tower.setTarget(null);
    GameStateWriter.getInstance().switchMode(tower, towerMode);
  }
}
