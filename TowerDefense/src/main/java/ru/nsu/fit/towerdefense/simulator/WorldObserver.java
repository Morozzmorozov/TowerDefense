package ru.nsu.fit.towerdefense.simulator;

import ru.nsu.fit.towerdefense.simulator.world.gameobject.Base;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.Effect;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.Enemy;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.Portal;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.Projectile;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.RoadTile;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.Tower;
import ru.nsu.fit.towerdefense.simulator.world.gameobject.TowerPlatform;

/**
 * WorldObserver interface is used for reacting on the events occurred in the game world.
 *
 * @author Oleg Markelov
 */
public interface WorldObserver {
    /**
     * An event occurring on game defeat.
     */
    void onDefeat();

    /**
     * An event occurring on game victory.
     */
    void onVictory();

    void onBaseClicked(Base base);
    void onEnemyClicked(Enemy enemy);
    void onEffectClicked(Effect effect);
    void onPortalClicked(Portal portal);
    void onProjectileClicked(Projectile projectile);
    void onRoadTileClicked(RoadTile roadTile);
    void onTowerClicked(Tower tower);
    void onTowerPlatformClicked(TowerPlatform towerPlatform);
}
