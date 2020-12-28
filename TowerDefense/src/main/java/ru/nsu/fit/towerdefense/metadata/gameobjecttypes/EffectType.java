package ru.nsu.fit.towerdefense.metadata.gameobjecttypes;

public class EffectType {
  private String name;
  private double speedMultiplier;
  private String image;
  private int damagePerTick;
  private int duration;

  private EffectType() {}

  public double getSpeedMultiplier() {
    return speedMultiplier;
  }

  public String getImage() {
    return image;
  }

  public int getDamagePerTick() {
    return damagePerTick;
  }

  public int getDuration() {
    return duration;
  }

  public String getName()
  {
    return name;
  }

  public static class Builder
  {
    private String name;
    private double speedMultiplier;
    private String image;
    private int damagePerTick;
    private int duration;
    public Builder(String name)
    {
      this.name = name;
    }

    public void setImage(String image)
    {
      this.image = image;
    }

    public void setDamagePerTick(int damagePerTick)
    {
      this.damagePerTick = damagePerTick;
    }

    public void setDuration(int duration)
    {
      this.duration = duration;
    }

    public void setSpeedMultiplier(double speedMultiplier)
    {
      this.speedMultiplier = speedMultiplier;
    }

    public EffectType build()
    {
      EffectType effectType = new EffectType();
      effectType.name = name;
      effectType.damagePerTick = damagePerTick;
      effectType.duration = duration;
      effectType.image = image;
      effectType.speedMultiplier = speedMultiplier;

      return effectType;
    }
  }
}
