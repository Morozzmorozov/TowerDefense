package ru.nsu.fit.towerdefense.simulator.world.gameobject.visitor;

import ru.nsu.fit.towerdefense.simulator.world.gameobject.Base;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.Effect;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.Enemy;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.Portal;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.Projectile;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.RoadTile;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.Tower;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.TowerPlatform;

/**
 * A visitor of renderables, in the style of the visitor design pattern. Classes implementing this
 * interface are used to operate on a Renderable when the kind of renderable is unknown at compile
 * time. When a visitor is passed to an element's accept method, the visitXYZ method most applicable
 * to that type is invoked.
 *
 * @author Oleg Markelov
 */

public interface Visitor {
    void visit(Base base);
    void visit(Enemy enemy);
    void visit(Effect effect);
    void visit(Portal portal);
    void visit(Projectile projectile);
    void visit(RoadTile roadTile);
    void visit(Tower tower);
    void visit(TowerPlatform towerPlatform);
}
