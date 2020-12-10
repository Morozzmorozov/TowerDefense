package ru.nsu.fit.towerdefense.fx.controllers.game;

import ru.nsu.fit.towerdefense.model.world.gameobject.Renderable;

public interface WorldRendererObserver {
    void onGameObjectClicked(Renderable renderable);
}
