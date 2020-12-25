package ru.nsu.fit.towerdefense.simulator.world.types;

import java.util.LinkedList;
import java.util.List;

public class TowerType {

    public static class Upgrade
    {
        private String name;
        private int cost;
        public Upgrade(String name, int cost)
        {
            this.name = name;
            this.cost = cost;
        }

        public int getCost() {
            return cost;
        }

        public String getName() {
            return name;
        }
    }

    private String displayInfo ;
    private String typeName;
    private int price;
    private List<Upgrade> upgrades;
    private int range;
    private int fireRate;
    private FireType fireType;
    private String projectileType;
    private String image;
    private double rotationSpeed;

    public String getDisplayInfo ()
    {
        return displayInfo ;
    }

    public String getTypeName()
    {
        return typeName;
    }

    public String getImage() {
    return image;
  }

    public int getPrice() {
    return price;
  }

    public List<Upgrade> getUpgrades() {
    return upgrades;
  }

    public int getRange() {
    return range;
  }

    public int getFireRate() {
    return fireRate;
  }

    public FireType getFireType() {
    return fireType;
  }

    public String getProjectileType() {
    return projectileType;
  }

    public double getRotationSpeed()
    {
        return rotationSpeed;
    }

    public enum FireType {
        UNIDIRECTIONAL, OMNIDIRECTIONAL
    }

    public static class Builder
    {
        private String displayInfo ;
        private int price;
        private String typeName;
        private List<Upgrade> upgrades;
        private int range;
        private int fireRate;
        private double rotationSpeed;
        private String fireType;
        private String projectileType;
        private String image;

        public Builder(String typeName)
        {
            this.typeName = typeName;
            upgrades = new LinkedList<>();
        }

        public void setDisplayInfo (String displayInfo )
        {
            this.displayInfo  = displayInfo ;
        }

        public void setImage(String image)
        {
            this.image = image;
        }

        public void setFireRate(int fireRate)
        {
            this.fireRate = fireRate;
        }

        public void setFireType(String fireType)
        {
            this.fireType = fireType;
        }

        public void setPrice(int price)
        {
            this.price = price;
        }

        public void setProjectileType(String projectileType)
        {
            this.projectileType = projectileType;
        }

        public void setRange(int range)
        {
            this.range = range;
        }

        public void setRotationSpeed(double speed)
        {
            this.rotationSpeed = speed;
        }


        public void addUpgrade(Upgrade upgrade)
        {
            upgrades.add(upgrade);
        }

        public TowerType build()
        {
            TowerType type = new TowerType();
            type.typeName = typeName;
            type.fireRate = fireRate;
            if (fireType.compareTo("Unidirectional") == 0)
            {
                type.fireType = FireType.UNIDIRECTIONAL;
            }
            else
            {
                type.fireType = FireType.OMNIDIRECTIONAL;
            }
            type.image = image;
            type.price = price;
            type.projectileType = projectileType;
            type.range = range;
            type.upgrades = upgrades;
            type.displayInfo  = displayInfo;
            type.rotationSpeed = rotationSpeed;
            return type;
        }
    }
}
