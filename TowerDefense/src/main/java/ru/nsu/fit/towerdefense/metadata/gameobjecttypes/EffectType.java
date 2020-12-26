package ru.nsu.fit.towerdefense.metadata.gameobjecttypes;

public class EffectType {
  private double speedMultiplier;
  private String image;
  private int damagePerTick;
  private int duration;

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
}
