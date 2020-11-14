package ru.nsu.fit.towerdefense.model.world.map;

import ru.nsu.fit.towerdefense.model.world.Vector2;

public class BaseDescription {

    private Vector2<Integer> position;
    private Integer health;
    private String image;


    public BaseDescription()
    {

    }

    public void setHealth(Integer health) {
        this.health = health;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setPosition(Vector2<Integer> position) {
        this.position = position;
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
