package ru.nsu.fit.towerdefense.simulator.world.gameobject;

/**
 * A visitor of renderables, in the style of the visitor design pattern. Classes implementing this
 * interface are used to operate on a Renderable when the kind of renderable is unknown at compile
 * time. When a visitor is passed to an element's accept method, the visitXYZ method most applicable
 * to that type is invoked.
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
