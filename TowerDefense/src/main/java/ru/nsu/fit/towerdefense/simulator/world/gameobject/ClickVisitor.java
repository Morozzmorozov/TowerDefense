package ru.nsu.fit.towerdefense.simulator.world.gameobject;

/**
 * ClickVisitor interface is a simple visitor used for firing click events.
 *
 * @author Oleg Markelov
 */
public interface ClickVisitor {
    void onClicked(Base base);
    void onClicked(Enemy enemy);
    void onClicked(Effect effect);
    void onClicked(Portal portal);
    void onClicked(Projectile projectile);
    void onClicked(RoadTile roadTile);
    void onClicked(Tower tower);
    void onClicked(TowerPlatform towerPlatform);
}
