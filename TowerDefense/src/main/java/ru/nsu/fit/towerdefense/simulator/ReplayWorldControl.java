package ru.nsu.fit.towerdefense.simulator;

import java.util.List;
import ru.nsu.fit.towerdefense.metadata.map.GameMap;
import ru.nsu.fit.towerdefense.replay.Replay;
import ru.nsu.fit.towerdefense.simulator.events.Event;
import ru.nsu.fit.towerdefense.simulator.exceptions.GameplayException;
import ru.nsu.fit.towerdefense.simulator.world.World;

public class ReplayWorldControl extends WorldControl {

  private final Replay replayPlayback;

  public ReplayWorldControl(GameMap gameMap, int deltaTime, WorldObserver observer,
      Replay replayPlayback) {
    super(gameMap, deltaTime, observer, replayPlayback.getPlayers(), null);
    this.replayPlayback = replayPlayback;
  }

  @Override
  public void simulateTick() {
    synchronized (this) {
      if (replayPlayback.getEvents().get((int) world.getTick()) != null) {
        for (Event event : replayPlayback.getEvents().get((int) world.getTick())) {
          try {
            event.fire(world);
          } catch (GameplayException e) {
            e.printStackTrace();
          }
        }
      }
      super.simulateTick();
    }
  }

  /**
   * Skips to the state after <code>tickIndex</code> tick
   *
   * @param tickIndex the tick before the desired state
   */
  public void skipToTick(int tickIndex) {
    synchronized (this) {
      int targetPivotTick =
          tickIndex / replayPlayback.getStateRate() * replayPlayback.getStateRate();
      int currentPivotTick =
          (int) world.getTick() / replayPlayback.getStateRate() * replayPlayback.getStateRate();

      if (tickIndex <= world.getTick() || targetPivotTick != currentPivotTick) {
        world = replayPlayback.getStates().get(targetPivotTick).generateWorld(world);
      }

      while (world.getTick() < tickIndex) {
        simulateTick();
      }
    }
  }

  @Override
  protected void saveStateToReplay() {
    // ignore
  }

  @Override
  protected void addRewards(int scienceReward, int multiplayerReward) {
    // ignore
  }

  @Override
  protected void saveEventToReplay(Event event) {
    // ignore
  }

  @Override
  protected void saveReplay() {
    // ignore
  }

  @Override
  protected void simulateForNewEvents(long from) throws GameplayException {
    // ignore
  }

  @Override
  protected void putState(World world) {
    // ignore
  }
}
