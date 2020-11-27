package ru.nsu.fit.towerdefense.model.map;

import ru.nsu.fit.towerdefense.model.util.Vector2;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class RoadDescription {
    private List<List<Vector2<Double>>> roads;

    private RoadDescription()
    {
        roads = new ArrayList<>();
    }

    public List<Vector2<Double>> getRoad(int id) {
        return roads.get(id);
    }

    public static class Builder
    {
        List<List<Vector2<Double>>> roads;
        public Builder()
        {
            roads = new LinkedList<>();
        }
        public void addRoad()
        {
            roads.add(new LinkedList<>());
        }

        public void addTile(Vector2<Double> tile)
        {
            roads.get(roads.size() - 1).add(tile);
        }

        public RoadDescription build()
        {
            RoadDescription description = new RoadDescription();
            description.roads = roads;
            return description;
        }
    }

}
