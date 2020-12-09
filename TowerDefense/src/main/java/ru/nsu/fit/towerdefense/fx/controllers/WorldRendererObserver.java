package ru.nsu.fit.towerdefense.fx.controllers;

import ru.nsu.fit.towerdefense.model.world.gameobject.Renderable;

public interface WorldRendererObserver {
    void onGameObjectClicked(Renderable renderable);
}
