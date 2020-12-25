package ru.nsu.fit.towerdefense.metadata.gameobjecttypes;

import java.util.HashMap;
import java.util.Map;
import ru.nsu.fit.towerdefense.util.Vector2;

public class ProjectileType {
    private String typeName;
    private float speed;
    private boolean selfGuided;
    private int basicDamage;
    private Map<String, Integer> enemyTypeDamageMap;
    private int hitBox;
    private Vector2<Double> size;
    private String image;
    private String displayInfo;
    private double angularVelocity;

    public double getAngularVelocity()
    {
        return angularVelocity;
    }

    public Vector2<Double> getSize() {
    return size;
  }

    public String getImage() {
    return image;
  }

    public float getSpeed() {
    return speed;
  }

    public boolean isSelfGuided() {
    return selfGuided;
  }

    public Map<String, Integer> getEnemyTypeDamageMap() {
    return enemyTypeDamageMap;
  }

    public int getHitBox() {
    return hitBox;
  }

    public int getBasicDamage()
    {
        return basicDamage;
    }

    public String getTypeName()
    {
        return typeName;
    }

    public String getDisplayInfo () { return displayInfo ; }

    public static class Builder
    {
        private String typeName;
        private float speed;
        private boolean selfGuided;
        private Map<String, Integer> enemyTypeDamageMap;
        private int hitBox;
        private Vector2<Double> size;
        private int basicDamage;
        private String image;
        private String displayInfo ;
        private double angularVelocity = 0.0;
        public Builder(String name)
        {
            typeName = name;
            enemyTypeDamageMap = new HashMap<>();
            size = new Vector2<>(0.0, 0.0);
        }

        public void setImage(String image)
        {
            this.image = image;
        }

        public void setSpeed(float speed)
        {
            this.speed = speed;
        }

        public void setHitBox(int hitBox)
        {
            this.hitBox = hitBox;
        }

        public void setAngularVelocity(double angularVelocity)
        {
            this.angularVelocity = angularVelocity;
        }

        public void setSize(Double x, Double y)
        {
            this.size.setX(x);
            this.size.setY(y);
        }

        public void setDisplayInfo(String displayInfo )
        {

            this.displayInfo  = displayInfo ;
        }

        public void setBasicDamage(int basicDamage)
        {
            this.basicDamage = basicDamage;
        }

        public void setSelfGuided(boolean selfGuided)
        {
            this.selfGuided = selfGuided;
        }

        public void setTypeName(String typeName)
        {
            this.typeName = typeName;
        }

        public void add(String type, Integer basicDamage)
        {
            enemyTypeDamageMap.put(type, basicDamage);
        }

        public ProjectileType build()
        {
            ProjectileType type = new ProjectileType();
            type.typeName = typeName;
            type.speed = speed;
            type.size = size;
            type.selfGuided = selfGuided;
            type.image = image;
            type.hitBox = hitBox;
            type.enemyTypeDamageMap = enemyTypeDamageMap;
            type.basicDamage = basicDamage;
            type.displayInfo  = displayInfo;
            type.angularVelocity = angularVelocity;
            return type;
        }
    }

}
