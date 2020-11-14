package ru.nsu.fit.towerdefense.model.world.map;

import ru.nsu.fit.towerdefense.model.world.Vector2;

import java.util.ArrayList;
import java.util.List;

public class RoadDescription {
    private List<List<Vector2<Double>>> roads;

    RoadDescription()
    {
        roads = new ArrayList<>();
    }

    public List<Vector2<Double>> getRoad(int id) {
        return roads.get(id);
    }



}
