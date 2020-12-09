package ru.nsu.fit.towerdefense.model;

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
}
