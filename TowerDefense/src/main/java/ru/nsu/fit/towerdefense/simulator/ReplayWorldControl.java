package ru.nsu.fit.towerdefense.simulator;

import java.util.UUID;
import java.util.stream.Collectors;
import ru.nsu.fit.towerdefense.metadata.map.GameMap;
import ru.nsu.fit.towerdefense.replay.Replay;
import ru.nsu.fit.towerdefense.replay.WorldState;
import ru.nsu.fit.towerdefense.simulator.world.World;

public class ReplayWorldControl extends WorldControl {
  private Replay replay;

  private int currentWorldStateIndex = 0;

  public ReplayWorldControl(GameMap gameMap, int deltaTime, WorldObserver observer, Replay replay) {
    super(gameMap, deltaTime, observer);
    this.replay = replay;
  }


  public void skipToTick(int tickIndex) {
    // todo todo
    int worldStateIndex = tickIndex / replay.getTickRate();
    if (worldStateIndex == currentWorldStateIndex && tickIndex >= tick) {

    } else {

    }
  }

  private void setWorldState(WorldState state) {
    world.setMoney(state.getMoney());
   // world.getProjectiles().removeAll(world.get)
  }


}
