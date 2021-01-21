package ru.nsu.fit.towerdefense.simulator.world.gameobject;

import ru.nsu.fit.towerdefense.metadata.gameobjecttypes.EffectType;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.visitor.Visitor;
import ru.nsu.fit.towerdefense.util.Vector2;

public class Effect extends GameObject implements Renderable {
  private final Enemy host;
  private final EffectType type;
  private int remainingTicks;

  public Effect(Enemy host, EffectType type) {
    this.host = host;
    this.type = type;
    remainingTicks = type.getDuration();
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
  public void accept(Visitor visitor) {
    visitor.visit(this);
  }
}
