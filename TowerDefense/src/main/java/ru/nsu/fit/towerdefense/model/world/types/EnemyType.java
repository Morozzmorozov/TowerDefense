package ru.nsu.fit.towerdefense.model.world.types;

import ru.nsu.fit.towerdefense.model.world.Vector2;

public class EnemyType {
    private String typeName;
    private int health;
    private float speed;
    private float hitBox;
    private Vector2<Double> size;
    private String image;
    private int damage;
    public String getImage() {
    return image;
  }

    public Vector2<Double> getSize() {
    return size;
  }

    public int getHealth() {
    return health;
  }

    public float getSpeed() {
    return speed;
  }

    public float getHitBox() {
    return hitBox;
  }

    public int getDamage() {
    return damage;
  }

    public String getTypeName()
    {
        return typeName;
    }

    public static class Builder
    {
        private int health;
        private float speed;
        private float hitBox;
        private Vector2<Double> size;
        private String image;
        private int damage;
        private String typeName;
        public Builder(String typeName)
        {
            this.typeName = typeName;
            size = new Vector2<>(0.0, 0.0);
        }

        public void setHealth(int health)
        {
            this.health = health;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public void setSize(double x, double y) {
            this.size.setX(x);
            this.size.setY(y);
        }

        public void setDamage(int damage) {
            this.damage = damage;
        }

        public void setHitBox(float hitBox) {
            this.hitBox = hitBox;
        }


        public void setSpeed(float speed) {
            this.speed = speed;
        }

        public EnemyType build()
        {
            EnemyType type = new EnemyType();
            type.typeName = typeName;
            type.damage = damage;
            type.health = health;
            type.hitBox = hitBox;
            type.image = image;
            type.size = size;
            type.speed = speed;
            return type;
        }
    }

}
