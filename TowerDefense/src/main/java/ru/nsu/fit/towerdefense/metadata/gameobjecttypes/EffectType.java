package ru.nsu.fit.towerdefense.metadata.gameobjecttypes;

public class EffectType {
  private String name;
  private double speedMultiplier;
  private String image;
  private int damagePerTick;
  private int duration;

  private EffectType() {}

  public static final EffectType BURN = new EffectType() {
    @Override
    public double getSpeedMultiplier() {
      return 1;
    }
    @Override
    public int getDamagePerTick() {
      return 2;
    }
    @Override
    public String getImage() {
      return "flame.png";
    }

    @Override
    public int getDuration() {
      return 60;
    }
  };

  public static final EffectType FREEZE = new EffectType() {
    @Override
    public double getSpeedMultiplier() {
      return 0.6;
    }
    @Override
    public int getDamagePerTick() {
      return 0;
    }
    @Override
    public String getImage() {
      return "frost.png";
    }

    @Override
    public int getDuration() {
      return 60;
    }
  };

  public static final EffectType POISON = new EffectType() {
    @Override
    public double getSpeedMultiplier() {
      return 0.5;
    }
    @Override
    public int getDamagePerTick() {
      return 1;
    }
    @Override
    public String getImage() {
      return "poison.png";
    }

    @Override
    public int getDuration() {
      return 200;
    }
  };

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
