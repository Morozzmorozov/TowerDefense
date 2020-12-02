package ru.nsu.fit.towerdefense.model;

/**
 * WorldObserver interface is used for reacting on the events occurred in the game world.
 *
 * @author Oleg Markelov
 */
public interface WorldObserver {
    /**
     * An event occurring on player loosing the game.
     */
    void onGameLoosing();

    /**
     * An event occurring on player winning the game.
     */
    void onGameWinning();
}
