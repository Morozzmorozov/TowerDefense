package ru.nsu.fit.towerdefense.simulator.world.gameobject;

import ru.nsu.fit.towerdefense.metadata.gameobjecttypes.EffectType;
import ru.nsu.fit.towerdefense.util.Vector2;

public class Effect extends GameObject implements Renderable {
  private final Enemy host;
  private final EffectType type;
  private int remainingTicks;
  private String owner;

  public String getOwner() {
    return owner;
  }

  public void setOwner(String owner) {
    this.owner = owner;
  }

  public Effect(Enemy host, EffectType type, String owner, int id) {
    super(id);
    this.host = host;
    this.type = type;
    remainingTicks = type.getDuration();
    this.owner = owner;
  }

  public Effect(Effect oldEffect, Enemy host) {
    super(oldEffect.id);
    this.host = host;
    this.type = oldEffect.type;
    this.remainingTicks = oldEffect.remainingTicks;
    this.owner = oldEffect.owner;
  }

  public Enemy getHost() {
    return host;
  }

  public int getRemainingTicks() {
    return remainingTicks;
  }

  public void setRemainingTicks(int remainingTicks) {
    this.remainingTicks = remainingTicks;
  }

  public EffectType getType() {
    return type;
  }

  @Override
  public Vector2<Double> getPosition() {
    return host.getPosition();
  }

  @Override
  public Vector2<Double> getSize() {
    return new Vector2<>(1d, 1d);
  }

  @Override
  public String getImageName() {
    return type.getImage();
  }

  @Override
  public double getZIndex() {
    return 5;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void accept(ClickVisitor visitor) {
    visitor.onClicked(this);
  }
}
