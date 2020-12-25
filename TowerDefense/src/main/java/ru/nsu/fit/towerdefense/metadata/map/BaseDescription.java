package ru.nsu.fit.towerdefense.metadata.map;

import ru.nsu.fit.towerdefense.util.Vector2;

/**
 * This object should be immutable (read-only).
 */
public class BaseDescription {

    private Vector2<Integer> position;
    private Integer health;
    private String image;


    private BaseDescription()
    {
    }

    /*public void setHealth(Integer health) {
        this.health = health;
    }*/

    /*public void setImage(String image) {
        this.image = image;
    }*/

    /*public void setPosition(Vector2<Integer> position) {
        this.position = position;
    }*/

    public static class Builder
    {
        private Vector2<Integer> position;
        private Integer health;
        private String image;

        public Builder()
        {
            position = new Vector2<>(0, 0);
            health = 0;
            image = "";
        }

        public void setPosition(int x, int y) {
            this.position.setX(x);
            this.position.setY(y);
        }

        public void setImage(String image) {
            this.image = image;
        }

        public void setHealth(Integer health) {
            this.health = health;
        }

        public BaseDescription build()
        {
            BaseDescription description = new BaseDescription();
            description.image = image;
            description.health = health;
            description.position = position;
            return description;
        }
    }

    public String getImage() {
        return image;
    }

    public Vector2<Integer> getPosition() {
        return position;
    }

    public Integer getHealth() {
        return health;
    }

}
