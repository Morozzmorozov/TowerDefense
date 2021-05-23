package ru.nsu.fit.towerdefense.simulator.world.gameobject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import java.util.Map;
import java.util.stream.Collectors;
import ru.nsu.fit.towerdefense.util.Vector2;
import ru.nsu.fit.towerdefense.metadata.gameobjecttypes.EnemyType;

public class Enemy extends GameObject implements Renderable {
  private int health;
  private EnemyType type;
  private float velocity;
  private int waveNumber;
  private boolean isDead = false;
  private Vector2<Double> position;
  private List<Vector2<Double>> trajectory = new ArrayList<>();
  private int moneyReward;
  private List<Effect> effects = new ArrayList<>();
  private Map<String, Integer> damageMap = new HashMap<>();

  public Enemy(EnemyType type, int waveNumber, Vector2<Double> position, int moneyReward) {
    this.type = type;
    this.waveNumber = waveNumber;
    this.position = position;
    this.moneyReward = moneyReward;
    health = type.getHealth();
    velocity = type.getSpeed();
  }

  public Enemy(Enemy oldEnemy) {
    health = oldEnemy.health;
    type = oldEnemy.type;
    velocity = oldEnemy.velocity;
    waveNumber = oldEnemy.waveNumber;
    isDead = oldEnemy.isDead;
    position = new Vector2<>(oldEnemy.position);
    trajectory = oldEnemy.trajectory.stream().map(Vector2::new).collect(Collectors.toList());
    moneyReward = oldEnemy.moneyReward;
    effects = oldEnemy.effects.stream().map(effect -> new Effect(effect, this)).collect(Collectors.toList());
    damageMap = new HashMap<>(oldEnemy.damageMap);
  }

  public List<Effect> getEffects() {
    return effects;
  }

  public void addDamageToMap(String player, int amount) {
    if (damageMap.containsKey(player)) {
      damageMap.put(player, damageMap.get(player) + amount);
    } else {
      damageMap.put(player, amount);
    }
  }

  public Map<String, Integer> getDamageMap() {
    return damageMap;
  }

  public int getMoneyReward() {
    return moneyReward;
  }

  public Vector2<Double> getCell() {
    return position;
  }

  public void setPosition(Vector2<Double> position) {
    this.position = position;
  }

  public boolean isDead() {
    return isDead;
  }

  public void setDead(boolean dead) {
    isDead = dead;
  }

  public int getWaveNumber() {
    return waveNumber;
  }

  public int getHealth() {
    return health;
  }

  public void setHealth(int health) {
    this.health = health;
  }

  public EnemyType getType() {
    return type;
  }

  public float getVelocity() {
    return velocity;
  }

  @Override
  public Vector2<Double> getPosition() {
    return position;
  }

  @Override
  public Vector2<Double> getSize() {
    return type.getSize();
  }

  @Override
  public String getImageName() {
    return type.getImage();
  }

  public List<Vector2<Double>> getTrajectory() {
    return trajectory;
  }

  @Override
  public double getZIndex() {
    return 4;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void accept(ClickVisitor visitor) {
    visitor.onClicked(this);
  }
}
