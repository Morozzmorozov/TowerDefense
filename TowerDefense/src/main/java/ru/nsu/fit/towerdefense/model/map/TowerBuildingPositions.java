package ru.nsu.fit.towerdefense.model.map;

import ru.nsu.fit.towerdefense.model.util.Vector2;

import java.util.LinkedList;
import java.util.List;

public class TowerBuildingPositions {
    private List<Vector2<Integer>> positions;

    private TowerBuildingPositions()
    {
        positions = new LinkedList<>();
    }

    public List<Vector2<Integer>> getPositions() {
        return positions;
    }

    public static class Builder
    {
        List<Vector2<Integer>> positions;
        public Builder()
        {
            positions = new LinkedList<>();

        }

        public void addPosition(int x, int y)
        {
            positions.add(new Vector2<Integer>(x, y));
        }

        public TowerBuildingPositions build()
        {
            TowerBuildingPositions pos = new TowerBuildingPositions();
            pos.positions = positions;
            return pos;
        }
    }

}
