package ru.nsu.fit.towerdefense.simulator;

import java.util.HashMap;
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
    super(gameMap, deltaTime, observer);
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
    world.setMoney("player", state.getMoney()); // todo make replays for multiplayer
    enemiesKilled = state.getKilledEnemies();
    scienceEarned = state.getScience();

    world.getEnemies().removeAll(world.getEnemies().stream()
        .filter(enemy -> state.getEnemies().stream()
            .noneMatch(candidate -> candidate.getId().equals(enemy.getId().toString())))
        .collect(Collectors.toList()));

    world.setCountdown(state.getCountdown());
    world.clearWaves();
    if (gameMap.getWaves().size() > state.getWaveNumber()) {
      Wave currentWave = new Wave();
      currentWave.setCurrentEnemyNumber(state.getCurrentEnemyNumber());
      currentWave.setNumber(state.getWaveNumber());
      currentWave.setRemainingEnemiesCount(
          gameMap.getWaves().get(currentWave.getNumber()).getEnemiesList().stream().mapToInt(
              WaveEnemies::getCount).sum() - currentWave.getCurrentEnemyNumber());
      world.setCurrentWave(currentWave);
    } else {
      Wave wave = new Wave();
      wave.setCurrentEnemyNumber(Integer.MAX_VALUE);
      world.setCurrentWave(wave);
      world.setCurrentWaveNumber(state.getWaveNumber());
    }

    world.getBase().setHealth(state.getBaseHealth());

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
        enemy.setPosition(new Vector2<>(enemyInfo.getPosition()));
        enemy.getTrajectory().clear();
        enemy.getTrajectory().clear();
        for (var vertex : enemyInfo.getTrajectory()) {
          enemy.getTrajectory().add(new Vector2<>(vertex));
        }
        enemy.getEffects().clear();
        for (var effectInfo : enemyInfo.getEffects()) {
          Effect effect = new Effect(enemy, GameMetaData.getInstance().getEffectType(
              effectInfo.getKey()));
          effect.setRemainingTicks(effectInfo.getValue());
          enemy.getEffects().add(effect);
        }
      } else {
        Enemy enemy = new Enemy(
            GameMetaData.getInstance().getEnemyType(enemyInfo.getType()),
            enemyInfo.getWave(),
            new Vector2<>(enemyInfo.getPosition()),
            enemyInfo.getReward()
        );
        enemy.getTrajectory().clear();
        for (var vertex : enemyInfo.getTrajectory()) {
          enemy.getTrajectory().add(new Vector2<>(vertex));
        }
        for (var effectInfo : enemyInfo.getEffects()) {
          Effect effect = new Effect(enemy, GameMetaData.getInstance().getEffectType(
              effectInfo.getKey()));
          effect.setRemainingTicks(effectInfo.getValue());
          enemy.getEffects().add(effect);
        }
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
        projectile.setPosition(new Vector2<>(projectileInfo.getPosition().getX(),
            projectileInfo.getPosition().getY()));
        projectile.setRemainingRange(projectileInfo.getRange().floatValue());
        projectile.setVelocity(new Vector2<>(projectileInfo.getVelocity().getX(),
            projectileInfo.getVelocity().getY()));
        projectile.setRotation((Math
            .toDegrees(Math.atan2(projectile.getVelocity().getY(), projectile.getVelocity().getX()))
            + 450) % 360);
        projectile.setRotationSpeed(
            GameMetaData.getInstance().getProjectileType(projectileInfo.getType())
                .getAngularVelocity());
        projectile.setScale(projectileInfo.getScale());
        projectile.setTarget(
            world.getEnemies().stream()
                .dropWhile(enemy -> enemy.getId().toString().equals(projectileInfo.getTarget()))
                .findFirst()
                .orElse(null));
        projectile.setParentPosition(new Vector2<>(projectileInfo.getParentPosition()));
        if (projectile.getFireType().equals(FireType.OMNIDIRECTIONAL)) {
          projectile.setPosition(projectileInfo.getParentPosition());
        }
      } else {
        Projectile projectile = new Projectile(
            world.getEnemies().stream()
                .dropWhile(enemy -> enemy.getId().toString().equals(projectileInfo.getTarget()))
                .findFirst()
                .orElse(null),
            projectileInfo.getRange().floatValue(),
            GameMetaData.getInstance().getProjectileType(projectileInfo.getType()),
            new Vector2<>(projectileInfo.getPosition().getX(), projectileInfo.getPosition().getY()),
            new Vector2<>(projectileInfo.getVelocity().getX(), projectileInfo.getVelocity().getY()),
            null);
        projectile.setFireType(projectileInfo.getFireType());
        projectile.setRotationSpeed(
            GameMetaData.getInstance().getProjectileType(projectileInfo.getType())
                .getAngularVelocity());
        projectile.setRotationSpeed(
            GameMetaData.getInstance().getProjectileType(projectileInfo.getType())
                .getAngularVelocity());
        projectile.setRemainingRange(projectileInfo.getRange().floatValue());
        projectile.setRotation((Math
            .toDegrees(Math.atan2(projectile.getVelocity().getY(), projectile.getVelocity().getX()))
            + 450) % 360);
        projectile.setParentPosition(new Vector2<>(projectileInfo.getParentPosition()));
        projectile.setScale(projectileInfo.getScale());
        if (projectile.getFireType().equals(FireType.OMNIDIRECTIONAL)) {
          projectile.setPosition(projectileInfo.getParentPosition());
        }
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
        world.getTowers().add(tower);
      }
    }
  }

  private void fireEvents(long tickIndex) {
    var event = idEventMap.get((int) tickIndex);

    if (event == null) {
      return;
    }
    for (var buildTower : event.getBuildTower()) {
      Tower tower = new Tower();
      tower.setType(GameMetaData.getInstance().getTowerType(buildTower.getValue().getKey()));
      tower.setPosition(new Vector2<>(
          (int) Math.round(world.getTowerPlatforms().get(buildTower.getKey()).getPosition().getX()),
          (int) Math
              .round(world.getTowerPlatforms().get(buildTower.getKey()).getPosition().getY())));
      tower.setId(UUID.fromString(buildTower.getValue().getValue()));
      world.getTowers().add(tower);
      world.setMoney("player", world.getMoney("player") - tower.getType().getPrice());
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
          .ifPresent(upgrade -> world.setMoney("player", world.getMoney("player") - upgrade.getCost()));
      tower.setType(GameMetaData.getInstance().getTowerType(upgradeTower.getValue()));
      tower.setCooldown(tower.getType().getFireRate());
    }
  }
}
