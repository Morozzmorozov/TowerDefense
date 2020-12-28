package ru.nsu.fit.towerdefense.simulator;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import ru.nsu.fit.towerdefense.metadata.GameMetaData;
import ru.nsu.fit.towerdefense.metadata.map.GameMap;
import ru.nsu.fit.towerdefense.metadata.map.WaveEnemies;
import ru.nsu.fit.towerdefense.replay.EventRecord;
import ru.nsu.fit.towerdefense.replay.Replay;
import ru.nsu.fit.towerdefense.replay.WorldState;
import ru.nsu.fit.towerdefense.simulator.world.Wave;
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
    isReplay = true;

    for (var event : replay.getEventRecords()) {
      idEventMap.put(event.getId(), event);
    }
  }

  @Override
  public void simulateTick() {
    skipToTick((int)tick);
  }

  private void simulateTick2() {
    super.simulateTick();
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
      currentWorldStateIndex = worldStateIndex;
    }

    while (tick != tickIndex + 1) {
      simulateTick2();
      fireEvents(tick - 1); // we want to access events that happened at the tick we have now simulated
    }
  }

  private void setWorldState(WorldState state) {
    world.setMoney(state.getMoney());

    world.getEnemies().removeAll(world.getEnemies().stream()
        .filter(enemy -> state.getEnemies().stream()
            .noneMatch(candidate -> candidate.getId().equals(enemy.getId().toString())))
        .collect(Collectors.toList()));

    world.setCountdown(state.getCountdown());
    world.clearWaves();
    Wave currentWave = new Wave();
    currentWave.setCurrentEnemyNumber(state.getCurrentEnemyNumber());
    currentWave.setNumber(state.getWaveNumber());
    currentWave.setRemainingEnemiesCount(gameMap.getWaves().get(currentWave.getNumber()).getEnemiesList().stream().mapToInt(
        WaveEnemies::getCount).sum() - currentWave.getCurrentEnemyNumber());
    world.setCurrentWave(currentWave);

    for (var enemyInfo : state.getEnemies()) {

      if (world.getWaveByNumber(enemyInfo.getWave()) == null) {
        Wave wave = new Wave();
        wave.setNumber(enemyInfo.getWave());
        wave.setRemainingEnemiesCount(1);
        world.addWave(wave);
      } else {
        Wave wave = world.getWaveByNumber(enemyInfo.getWave());
        wave.setRemainingEnemiesCount(wave.getRemainingEnemiesCount() + 1);
      }

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
        enemy.getTrajectory().addAll(enemyInfo.getTrajectory());
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
        projectile.setVelocity(new Vector2<>(projectileInfo.getVelocity()));
        projectile.setRotation(Math.atan2(projectile.getVelocity().getY(), projectile.getVelocity().getX()));
        projectile.setRotationSpeed(GameMetaData.getInstance().getProjectileType(projectileInfo.getType()).getAngularVelocity());
        //projectile.setScale();
        projectile.setTarget(
            world.getEnemies().stream()
                .dropWhile(enemy -> enemy.getId().toString().equals(projectileInfo.getTarget()))
                .findFirst()
                .orElse(null));
      } else {
        Projectile projectile = new Projectile(
            world.getEnemies().stream()
              .dropWhile(enemy -> enemy.getId().toString().equals(projectileInfo.getTarget()))
              .findFirst()
              .orElse(null),
            projectileInfo.getRange().floatValue(),
            GameMetaData.getInstance().getProjectileType(projectileInfo.getType()),
            new Vector2<>(projectileInfo.getPosition()),
            new Vector2<>(projectileInfo.getVelocity()),
            null);
        projectile.setFireType(projectileInfo.getFireType());
        projectile.setRotationSpeed(GameMetaData.getInstance().getProjectileType(projectileInfo.getType()).getAngularVelocity());
        projectile.setRotationSpeed(GameMetaData.getInstance().getProjectileType(projectileInfo.getType()).getAngularVelocity());
        projectile.setRemainingRange(projectileInfo.getRange().floatValue());
        world.getProjectiles().add(projectile);
      }
    }


    world.getTowers().removeAll(world.getTowers().stream()
        .filter(tower -> state.getTowers().stream()
            .noneMatch(candidate -> candidate.getId().equals(tower.getId().toString())))
        .collect(Collectors.toList()));

    for (var towerInfo : state.getTowers()) {
      var savedTower = world.getTowers().stream()
          .dropWhile(tower -> !tower.getId().toString().equals(towerInfo.getId()))
          .findFirst();

      if (savedTower.isPresent()) {
        Tower tower = savedTower.get();
        tower.setType(GameMetaData.getInstance().getTowerType(towerInfo.getType()));
        tower.setMode(towerInfo.getMode());
        tower.setSellPrice(towerInfo.getSellPrice());
        tower.setRotation(towerInfo.getRotation());
        tower.setCooldown(towerInfo.getCooldown());
        tower.setTarget(world.getEnemies().stream()
            .dropWhile(enemy -> enemy.getId().toString().equals(towerInfo.getTarget()))
            .findFirst()
            .orElse(null));
      } else {
        Tower tower = new Tower();
        tower.setType(GameMetaData.getInstance().getTowerType(towerInfo.getType()));
        tower.setMode(towerInfo.getMode());
        tower.setSellPrice(towerInfo.getSellPrice());
        tower.setRotation(towerInfo.getRotation());
        tower.setCooldown(towerInfo.getCooldown());
        tower.setTarget(world.getEnemies().stream()
            .dropWhile(enemy -> enemy.getId().toString().equals(towerInfo.getTarget()))
            .findFirst()
            .orElse(null));
        tower.setPosition(new Vector2<>(
            Math.round(towerInfo.getPosition().getX().floatValue()),
            Math.round(towerInfo.getPosition().getY().floatValue())
            ));
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
      tower.setType(GameMetaData.getInstance().getTowerType(buildTower.getValue().getKey()));
      tower.setPosition(new Vector2<>(
          (int)Math.round(world.getTowerPlatforms().get(buildTower.getKey()).getPosition().getX()),
          (int)Math.round(world.getTowerPlatforms().get(buildTower.getKey()).getPosition().getY())));
      tower.setId(UUID.fromString(buildTower.getValue().getValue()));
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
