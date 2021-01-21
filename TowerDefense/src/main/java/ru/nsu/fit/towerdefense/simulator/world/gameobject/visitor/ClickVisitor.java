package ru.nsu.fit.towerdefense.simulator.world.gameobject.visitor;

import ru.nsu.fit.towerdefense.simulator.WorldObserver;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.Base;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.Effect;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.Enemy;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.Portal;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.Projectile;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.RoadTile;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.Tower;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.TowerPlatform;

/**
 * ClickVisitor class is a simple visitor used for firing click event of WorldObserver.
 *
 * @author Oleg Markelov
 */
public class ClickVisitor implements Visitor {

    private final WorldObserver worldObserver;

    /**
     * Creates new ClickVisitor with specified WorldObserver.
     *
     * @param worldObserver world observer.
     */
    public ClickVisitor(WorldObserver worldObserver) {
        this.worldObserver = worldObserver;
    }

    @Override
    public void visit(Base base) {
        worldObserver.onBaseClicked(base);
    }

    @Override
    public void visit(Enemy enemy) {
        worldObserver.onEnemyClicked(enemy);
    }

    @Override
    public void visit(Effect effect) {
        worldObserver.onEffectClicked(effect);
    }

    @Override
    public void visit(Portal portal) {
        worldObserver.onPortalClicked(portal);
    }

    @Override
    public void visit(Projectile projectile) {
        worldObserver.onProjectileClicked(projectile);
    }

    @Override
    public void visit(RoadTile roadTile) {
        worldObserver.onRoadTileClicked(roadTile);
    }

    @Override
    public void visit(Tower tower) {
        worldObserver.onTowerClicked(tower);
    }

    @Override
    public void visit(TowerPlatform towerPlatform) {
        worldObserver.onTowerPlatformClicked(towerPlatform);
    }
}
