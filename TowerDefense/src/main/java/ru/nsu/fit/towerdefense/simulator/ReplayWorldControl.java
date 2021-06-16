package ru.nsu.fit.towerdefense.simulator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import ru.nsu.fit.towerdefense.metadata.GameMetaData;
import ru.nsu.fit.towerdefense.metadata.gameobjecttypes.TowerType.FireType;
import ru.nsu.fit.towerdefense.metadata.map.GameMap;
import ru.nsu.fit.towerdefense.metadata.map.WaveEnemies;
import ru.nsu.fit.towerdefense.replay.EventRecord;
import ru.nsu.fit.towerdefense.replay.Replay;
import ru.nsu.fit.towerdefense.replay.WorldState;
import ru.nsu.fit.towerdefense.simulator.world.Wave;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.Effect;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.Enemy;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.Projectile;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.Tower;
import ru.nsu.fit.towerdefense.util.Vector2;

public class ReplayWorldControl extends WorldControl {

  private Replay replay;

  private int currentWorldStateIndex = 0;

  private Map<Integer, EventRecord> idEventMap = new HashMap<>();

  public ReplayWorldControl(GameMap gameMap, int deltaTime, WorldObserver observer, Replay replay) {
    super(gameMap, deltaTime, observer, List.of("player")); // todo fix
    this.replay = replay;
    isReplay = true;

    for (var event : replay.getEventRecords()) {
      idEventMap.put(event.getId(), event);
    }
  }

  @Override
  public void simulateTick() {
    skipToTick((int) getTick());
  }

  /**
   * Skips to the state after <code>tickIndex</code> tick
   *
   * @param tickIndex the tick before the desired state
   */
  public void skipToTick(int tickIndex) {
    int worldStateIndex = tickIndex / replay.getTickRate();
    if (!(worldStateIndex == currentWorldStateIndex && tickIndex >= getTick())) {
      if (replay.getWorldStates().size() > worldStateIndex) {
        setWorldState(
            replay.getWorldStates().get(worldStateIndex)); // move to state after tick No. tickIndex
      }
      currentWorldStateIndex = worldStateIndex;
      world.setTick(worldStateIndex * replay.getTickRate() + 1);
    }
    while (getTick() != tickIndex + 1) {
      super.simulateTick();
      fireEvents(
          getTick() - 1); // we want to access events that happened at the tick we have now simulated
    }
  }

  private void setWorldState(WorldState state) {
  }

  private void fireEvents(long tickIndex) {
  }
}
