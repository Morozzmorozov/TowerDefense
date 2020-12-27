package ru.nsu.fit.towerdefense.simulator;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import ru.nsu.fit.towerdefense.metadata.GameMetaData;
import ru.nsu.fit.towerdefense.metadata.map.GameMap;
import ru.nsu.fit.towerdefense.replay.EventRecord;
import ru.nsu.fit.towerdefense.replay.Replay;
import ru.nsu.fit.towerdefense.replay.WorldState;
import ru.nsu.fit.towerdefense.simulator.world.World;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.Enemy;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.Projectile;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.Tower;
import ru.nsu.fit.towerdefense.util.Vector2;

public class ReplayWorldControl extends WorldControl {
  private Replay replay;

  private int currentWorldStateIndex = 0;

  private Map<Integer, EventRecord> idEventMap = new HashMap<>();

  public ReplayWorldControl(GameMap gameMap, int deltaTime, WorldObserver observer, Replay replay) {
    super(gameMap, deltaTime, observer);
    this.replay = replay;

    for (var event : replay.getEventRecords()) {
      idEventMap.put(event.getId(), event);
    }
  }

  /**
   * Skips to the state after <code>tickIndex</code> tick
   *
   * @param tickIndex the tick before the desired state
   */
  public void skipToTick(int tickIndex) {
    // todo todo
    int worldStateIndex = tickIndex / replay.getTickRate();
    if (!(worldStateIndex == currentWorldStateIndex && tickIndex >= tick)) {
      setWorldState(replay.getWorldStates().get(worldStateIndex)); // move to state after tick No. tickIndex
    }

    while (tick != tickIndex + 1) {
      simulateTick();

      fireEvents(tick - 1); // we want to access events that happened at the tick we have now simulated
    }
  }

  private void setWorldState(WorldState state) {
    world.setMoney(state.getMoney());



    world.getEnemies().removeAll(world.getEnemies().stream()
        .filter(enemy -> state.getEnemies().stream()
            .noneMatch(candidate -> candidate.getId().equals(enemy.getId().toString())))
        .collect(Collectors.toList()));


    for (var enemyInfo : state.getEnemies()) {
      var savedEnemy = world.getEnemies().stream()
          .dropWhile(enemy -> !enemy.getId().toString().equals(enemyInfo.getId()))
          .findFirst();

      if (savedEnemy.isPresent()) {
        Enemy enemy = savedEnemy.get();
        enemy.setHealth(enemyInfo.getHealth());
        enemy.setPosition(enemyInfo.getPosition());
        enemy.getTrajectory().clear();
        enemy.getTrajectory().addAll(enemyInfo.getTrajectory());
        // todo effects
      } else {
        Enemy enemy = new Enemy(
            GameMetaData.getInstance().getEnemyType(enemyInfo.getType()),
            enemyInfo.getWave(),
            enemyInfo.getPosition(),
            100 // todo something with it
            );

        world.getEnemies().add(enemy);
      }
    }

    // remove all projectiles with ids not contained in state
    world.getProjectiles().removeAll(world.getProjectiles().stream()
        .filter(projectile -> state.getProjectiles().stream()
            .noneMatch(candidate -> candidate.getId().equals(projectile.getId().toString())))
        .collect(Collectors.toList()));

    for (var projectileInfo : state.getProjectiles()) {
      var savedProjectile = world.getProjectiles().stream()
          .dropWhile(projectile -> !projectile.getId().toString().equals(projectileInfo.getId()))
          .findFirst();

      if (savedProjectile.isPresent()) {
        Projectile projectile = savedProjectile.get();
        projectile.setPosition(new Vector2<>(projectileInfo.getPosition()));
        projectile.setRemainingRange(projectileInfo.getRange().floatValue());
        projectile.setRotation(100500); // todo come up with this
        projectile.setRotationSpeed(GameMetaData.getInstance().getProjectileType(projectileInfo.getType()).getAngularVelocity());
        //projectile.setScale();
        projectile.setTarget(
            world.getEnemies().stream()
                .dropWhile(enemy -> enemy.getId().toString().equals(projectileInfo.getTarget()))
                .findFirst()
                .orElse(null));
      }
    }
  }

  private void fireEvents(long tickIndex) {
    var event = idEventMap.get((int)tickIndex);
    if (event == null) {
      return;
    }
    for (var buildTower : event.getBuildTower()) {
      Tower tower = new Tower();
      tower.setType(GameMetaData.getInstance().getTowerType(buildTower.getValue()));
      tower.setPosition(new Vector2<>(
          (int)Math.round(world.getTowerPlatforms().get(buildTower.getKey()).getPosition().getX()),
          (int)Math.round(world.getTowerPlatforms().get(buildTower.getKey()).getPosition().getY())));
      // todo id
      world.getTowers().add(tower);
      world.setMoney(world.getMoney() - tower.getType().getPrice());
    }

    for (var tuneTower : event.getTuneTower()) {
      var towerOptional = world.getTowers().stream()
          .dropWhile(tower -> !tower.getId().toString().equals(tuneTower.getKey()))
          .findFirst();

      if (towerOptional.isEmpty()) {
        System.out.println("Tune tower with unknown id: " + tuneTower.getKey());
      } else {
        Tower tower = towerOptional.get();
        tower.setMode(tuneTower.getValue());
      }
    }

    for (var upgradeTower : event.getUpgradeTower()) {
      Tower tower = getTowerOnPlatform(world.getTowerPlatforms().get(upgradeTower.getKey()));

      tower.getType().getUpgrades().stream()
          .dropWhile(upgrade -> upgrade.getName().equals(upgradeTower.getValue()))
          .findFirst()
          .ifPresent(upgrade -> world.setMoney(world.getMoney() - upgrade.getCost()));
      tower.setType(GameMetaData.getInstance().getTowerType(upgradeTower.getValue()));
    }
  }
}
