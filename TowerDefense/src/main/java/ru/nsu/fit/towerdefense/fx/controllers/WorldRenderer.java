package ru.nsu.fit.towerdefense.fx.controllers;

import ru.nsu.fit.towerdefense.model.world.gameobject.Renderable;

public class WorldRenderer {



    public void render(Iterable<Renderable> renderables) {
        for (Renderable renderable : renderables) {
            System.out.println(renderable.getPosition());
        }
        System.out.println();
    }
}
